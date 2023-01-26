core!();

use crate::{
    robot::{RobotController, Robots, Team},
    Direction, Position,
};

pub struct GameManager<F1, F2>
where
    F1: Fn(RobotController) -> (),
    F2: Fn(RobotController) -> (),
{
    board: Board,
    red_controller: F1,
    blue_controller: F2,
}

impl<F1, F2> GameManager<F1, F2>
where
    F1: Fn(RobotController) -> (),
    F2: Fn(RobotController) -> (),
{
    pub fn new(state: Board, red_controller: F1, blue_controller: F2) -> Self {
        GameManager {
            board: state,
            red_controller,
            blue_controller,
        }
    }

    pub fn step_game(&mut self) -> OrError<()> {
        if self.board.is_game_over() {
            return Err(Error!("Tried stepping game when game is over"));
        }
        self.board
            .step(|controller| match controller.current_robot().team() {
                Team::Red => (self.red_controller)(controller),
                Team::Blue => (self.blue_controller)(controller),
            });
        Ok(())
    }
}

#[derive(Debug)]
pub struct Board {
    turn_count: u32,
    width: usize,
    height: usize,
    robots: Robots,
}

impl Board {
    pub fn new(width: usize, height: usize) -> Self {
        Board {
            turn_count: 0,
            width,
            height,
            robots: Robots::new(width, height),
        }
    }

    pub fn width(&self) -> usize {
        self.width
    }

    pub fn height(&self) -> usize {
        self.height
    }

    pub fn step(&mut self, mut f: impl FnMut(RobotController)) {
        self.turn_count += 1;
        // move all robots
        let robots = self.robots.robot_turn_order().clone();
        for robot_id in robots {
            if let Some(robot) = self.robots.get_robot_if_alive_mut(robot_id) {
                robot.decrement_cooldowns();
            } else {
                continue;
            }
            let controller = RobotController::new(self, robot_id);
            f(controller);
        }
    }

    pub fn robots(&self) -> &Robots {
        &self.robots
    }

    pub fn robots_mut(&mut self) -> &mut Robots {
        &mut self.robots
    }

    pub fn is_game_over(&self) -> bool {
        self.robots.iter().all(|r| r.team() == Team::Red)
            || self.robots.iter().all(|r| r.team() == Team::Blue)
    }

    pub fn in_bounds(&self, pos: Position) -> bool {
        0 <= pos.x && pos.x < self.width && 0 <= pos.y && pos.y < self.height
    }
}
