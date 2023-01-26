// use crate::position::Position;
// use crate::robot::{RobotController, Robot};
// use crate::Direction;

// pub fn micro() -> impl Fn(&mut RobotController) {
//     |controller| {
//         micro_inner(controller);
//     }
// }

// pub fn micro_inner(controller: &mut RobotController) {
//     fn get_best_move_direction<F>(controller: &RobotController, scorer: F) -> Direction
//     where
//         F: Fn(&RobotController, Position) -> f32,
//     {
//         let mut best_direction = Direction::Center;
//         let mut best_score = -1e18;
//         let current_pos = controller.current_position();

//         for direction in Direction::ordinal_directions() {
//             if !controller.can_move(direction) {
//                 continue;
//             }
//             let position = current_pos.add_exn(direction);
//             let score = scorer(controller, position);
//             if score > best_score {
//                 best_score = score;
//                 best_direction = direction;
//             }
//         }
//         best_direction
//     }

//     fn get_score_for_kiting(controller: &RobotController, pos: Position) -> f32 {
//         let sensed_robots = controller.sense_nearby_robots_in_vision();
//         let our_robot = controller.current_robot();

//         let mut score = 0.0;

//         // prefer squares where attackers can't see you
//         let mut num_enemies = 0;
//         for other in sensed_robots {
//             if our_robot.team() != other.team() && pos.distance_squared(other.position()) <= 20 {
//                 num_enemies += 1;
//             }
//         }
//         score -= num_enemies as f32 * 100000.0;

//         // prefer squares where you're further away from the closest enemy
//         let closest_enemy = controller.get_nearest_enemy_omnipotent().unwrap();
//         let dist = our_robot.position().distance_squared(closest_enemy.position());
//         assert!(dist <= 20);
//         score += dist as f32 * 1000.0;

//         // prefer diagonals over straight directions
//         if our_robot.position().distance_squared(pos) == 2 {
//             score += 50.0;
//         }

//         score
//     }

//     let our_robot = controller.current_robot();

//     if let Some(target) = controller
//         .sense_nearby_robots_in_vision()
//         .iter()
//         .filter(|r| r.team() != our_robot.team())
//         .min_by_key(|r| (r.position().distance_squared(our_robot.position())))
//     {
//         if our_robot.action_cooldown() < 10 {
//             return;
//         } else {
//             let dir = get_best_move_direction(controller, get_score_for_kiting);
//             if controller.can_move(dir) {
//                 controller.move_exn(dir);
//             }
//         }
//     }
// }
