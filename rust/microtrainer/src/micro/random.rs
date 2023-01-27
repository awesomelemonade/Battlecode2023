use rand::seq::SliceRandom;

use crate::bot::{Bot, BotProvider};
use crate::robot::{Robot, RobotController};
use crate::Direction;

pub struct RandomMicro {}

impl Bot for RandomMicro {
    fn step(&mut self, controller: &mut RobotController) {
        let mut rng = rand::thread_rng();
        let direction = *Direction::ordinal_directions().choose(&mut rng).unwrap();
        if controller.can_move(direction) {
            controller.move_exn(direction);
        }
    }
}

impl RandomMicro {
    pub fn new() -> Self {
        RandomMicro {}
    }
    pub fn provider() -> &'static impl BotProvider<BotType = RandomMicro> {
        &|r: &Robot| Self::new()
    }
}
