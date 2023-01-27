core!();

use std::collections::BTreeMap;

use itertools::{Either, Itertools};

use crate::{
    bot::{Bot, BotProvider},
    robot::{Robot, RobotController, RobotId, Robots, Team},
};

pub fn new_game_manager_with_red_and_blue<
    'a,
    F1: BotProvider<BotType = T1>,
    T1: Bot,
    F2: BotProvider<BotType = T2>,
    T2: Bot,
>(
    board: Board,
    red_provider: &'a F1,
    blue_provider: &'a F2,
) -> GameManager<impl BotProvider<BotType = Either<T1, T2>> + 'a, Either<T1, T2>> {
    GameManager::new(board, move |robot: &Robot| match robot.team() {
        Team::Red => Either::Left((&red_provider).get(robot)),
        Team::Blue => Either::Right((&blue_provider).get(robot)),
    })
}

pub struct GameManager<F, T> {
    board: Board,
    provider: F,
    controllers: BTreeMap<RobotId, T>,
}

impl<F, T> GameManager<F, T>
where
    F: BotProvider<BotType = T>,
    T: Bot,
{
    pub fn new(board: Board, provider: F) -> Self {
        GameManager {
            board,
            provider,
            controllers: BTreeMap::new(),
        }
    }

    fn step_exn(&mut self) {
        assert!(!self.board.is_game_over());
        self.board.step(|mut controller| {
            self.controllers
                .entry(controller.robot_id())
                .or_insert_with(|| self.provider.get(controller.current_robot()))
                .step(&mut controller);
        });
        // remove all controllers that aren't alive
        self.controllers
            .retain(|&robot_id, _| self.board.robots().is_alive(robot_id));
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
        while !self.board.is_game_over() && self.board.round_num < max_turns {
            self.step_exn();
        }
    }

    pub fn board(&self) -> &Board {
        &self.board
    }
}

#[derive(Debug, Clone)]
pub struct Board {
    round_num: u32,
    width: usize,
    height: usize,
    robots: Robots,
}

impl Board {
    pub fn new(width: usize, height: usize) -> Self {
        Board {
            round_num: 0,
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

    pub fn round_num(&self) -> u32 {
        self.round_num
    }

    pub fn step(&mut self, mut f: impl FnMut(RobotController)) {
        self.round_num += 1;
        // move all robots
        let robots = self.robots.robot_turn_order().clone();
        for robot_id in robots {
            if !self.robots.is_alive(robot_id) {
                continue;
            }
            let controller = RobotController::new(self, robot_id);
            f(controller);
            if let Some(robot) = self.robots.get_robot_if_alive_mut(robot_id) {
                robot.decrement_cooldowns();
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
        self.robots.iter().map(|r| r.team()).all_equal()
    }
}
