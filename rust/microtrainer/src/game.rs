core!();

use std::collections::{BTreeMap, VecDeque};

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

#[derive(Debug)]
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

    fn substep_exn(&mut self) {
        assert!(!self.board.is_game_over());
        self.board.substep(&mut |mut controller| {
            self.controllers
                .entry(controller.robot_id())
                .or_insert_with(|| self.provider.get(controller.current_robot()))
                .step(&mut controller);
        });
        // remove all controllers that aren't alive
        self.controllers
            .retain(|&robot_id, _| self.board.robots().is_alive(robot_id));
    }

    pub fn substep(&mut self) -> OrError<()> {
        if self.board.is_game_over() {
            Err(Error!("Game is already over"))
        } else {
            self.substep_exn();
            Ok(())
        }
    }

    fn step_exn(&mut self) {
        assert!(!self.board.is_game_over());
        self.board.step_new_round_exn(&mut |mut controller| {
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
        while !self.board.is_game_over() && self.board.round_num() < max_turns {
            self.step_exn();
        }
    }

    pub fn board(&self) -> &Board {
        &self.board
    }
}

#[derive(Debug, Clone)]
pub struct Board {
    width: usize,
    height: usize,
    robots: Robots,
    round_num: u32,
    current_turn_queue: VecDeque<RobotId>,
}

impl Board {
    pub fn new(width: usize, height: usize) -> Self {
        Board {
            width,
            height,
            robots: Robots::new(width, height),
            round_num: 0,
            current_turn_queue: VecDeque::new(),
        }
    }

    pub fn width(&self) -> usize {
        self.width
    }

    pub fn height(&self) -> usize {
        self.height
    }

    pub fn dims(&self) -> (usize, usize) {
        (self.width, self.height)
    }

    // TODO-someday: may want to create a substepper object to eliminate exn in step_new_round
    // when the substepper object gets dropped, it should step_remaining_round
    pub fn substep(&mut self, f: &mut impl FnMut(RobotController)) {
        // check if it's a new round
        if self.current_turn_queue.is_empty() {
            let turn_order = self.robots().robot_turn_order().clone();
            self.current_turn_queue.extend(turn_order);
            self.round_num += 1;
        }
        // remove robots that are no longer alive
        while let Some(&robot_id) = self.current_turn_queue.front() && !self.robots.is_alive(robot_id) {
            self.current_turn_queue.pop_front();
        }
        // execute a robot's turn
        if let Some(robot_id) = self.current_turn_queue.pop_front() {
            let controller = RobotController::new(self, robot_id);
            f(controller);
            if let Some(robot) = self.robots.get_robot_if_alive_mut(robot_id) {
                robot.decrement_cooldowns();
            }
        }
    }

    pub fn step_new_round_exn(&mut self, f: &mut impl FnMut(RobotController)) {
        assert!(
            self.current_turn_queue.is_empty(),
            "Cannot step new round in the middle of an existing round"
        );
        self.round_num += 1;
        let turn_order = self.robots.robot_turn_order().clone();
        for robot_id in turn_order {
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

    // pub fn step_remaining_round(&mut self, f: &mut impl FnMut(RobotController)) {
    //     todo!()
    // }

    pub fn robots(&self) -> &Robots {
        &self.robots
    }

    pub fn robots_mut(&mut self) -> &mut Robots {
        &mut self.robots
    }

    pub fn is_game_over(&self) -> bool {
        self.robots.iter().map(|r| r.team()).all_equal()
    }

    pub fn round_num(&self) -> u32 {
        self.round_num
    }
}
