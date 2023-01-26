use rand::seq::SliceRandom;

use crate::bot::Bot;
use crate::robot::{RobotController};
use crate::Direction;

pub struct RandomMicro {
}

impl Bot for RandomMicro {
    fn run(&mut self, controller: &mut RobotController) -> () {
        let mut rng = rand::thread_rng();
        let direction = *Direction::ordinal_directions().choose(&mut rng).unwrap();
        if controller.can_move(direction) {
            controller.move_exn(direction);
        }
    }
}
