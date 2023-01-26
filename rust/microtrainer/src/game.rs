core!();

use crate::robot::{RobotController, Robots, Team};

pub struct GameManager<F1, F2> {
    board: Board,
    red_controller: F1,
    blue_controller: F2,
}

impl<F1, F2> GameManager<F1, F2>
where
    F1: Fn(&mut RobotController) -> (),
    F2: Fn(&mut RobotController) -> (),
{
    pub fn new(state: Board, red_controller: F1, blue_controller: F2) -> Self {
        GameManager {
            board: state,
            red_controller,
            blue_controller,
        }
    }

    fn step_exn(&mut self) {
        self.board
            .step(|mut controller| match controller.current_robot().team() {
                Team::Red => (self.red_controller)(&mut controller),
                Team::Blue => (self.blue_controller)(&mut controller),
            });
    }

    pub fn step(&mut self) -> OrError<()> {
        if self.board.is_game_over() {
            Err(Error!("Game is already over"))
        } else {
            self.step_exn();
            Ok(())
        }
    }

    pub fn step_until_game_over(&mut self, max_turns: u32) {
        while !self.board.is_game_over() && self.board.turn_count < max_turns {
            self.step_exn();
        }
    }

    pub fn board(&self) -> &Board {
        &self.board
    }
}

#[derive(Debug, Clone)]
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
        for robot_id in &robots {
            if !self.robots.is_alive(*robot_id) {
                continue;
            }
            let controller = RobotController::new(self, *robot_id);
            f(controller);
        }
        // decrement cooldowns
        for robot_id in robots {
            if let Some(robot) = self.robots.get_robot_if_alive_mut(robot_id) {
                robot.decrement_cooldowns();
            } else {
                continue;
            }
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
}
