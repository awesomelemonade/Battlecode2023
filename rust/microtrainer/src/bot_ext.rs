use crate::{bot::Bot, direction::Direction, robot::RobotController};

core!();

pub trait BotExt {
    fn try_attack(&self, controller: &mut RobotController);
    fn omnipotent_move(&self, controller: &mut RobotController);
}

impl<T: Bot> BotExt for T {
    fn try_attack(&self, controller: &mut RobotController) {
        let position = controller.current_position();
        let team = controller.current_robot().team();
        if let Some(target) = controller
            .sense_nearby_robots_in_vision()
            .iter()
            .filter(|r| r.team() != team && controller.can_attack(r.position()))
            .min_by_key(|r| (r.health(), r.position().distance_squared(position)))
        {
            controller.attack_exn(target.position());
        }
    }
    fn omnipotent_move(&self, controller: &mut RobotController) {
        if let Some(closest_enemy) = controller.get_nearest_enemy_omnipotent() {
            let target = closest_enemy.position();
            if let Some(&move_direction) =
                Direction::attempt_order(controller.current_position().direction_to(target))
                    .iter()
                    .filter(|&&dir| controller.can_move(dir))
                    .next()
            {
                controller.move_exn(move_direction);
            }
        }
    }
}
