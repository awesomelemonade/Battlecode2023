use rand::seq::SliceRandom;

use crate::position::Position;
use crate::robot::{RobotController, Robot};
use crate::Direction;

pub fn micro() -> impl Fn(&mut RobotController) {
    |controller| {
        let mut rng = rand::thread_rng();
        let direction = *Direction::ordinal_directions().choose(&mut rng).unwrap();
        if controller.can_move(direction) {
            controller.move_exn(direction);
        }
    }
}
