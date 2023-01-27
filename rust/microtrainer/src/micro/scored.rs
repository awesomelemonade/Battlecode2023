// use rand::seq::SliceRandom;

// use crate::position::Position;
// use crate::robot::{RobotController, Robot};
// use crate::Direction;

// pub fn get_scored_micro(coeffs: [f32; 12]) -> impl Fn(&mut Board, u32) -> () {
//     move |state, id| {
//         let robot = state.robots.get(&id).unwrap();

//         let sensed_robots = state.sense_nearby_robots(id);
//         let mut enemy_robots = Vec::new();
//         let mut ally_robots = Vec::new();
//         for other in sensed_robots {
//             if robot.team == other.team {
//                 ally_robots.push(other);
//             } else {
//                 enemy_robots.push(other);
//             }
//         }

//         let get_score = |pos: Position| -> f32 {
//             let mut dist_to_nearest_enemy = 1000;
//             let mut lowest_enemy = None;
//             let mut lowest_enemy_health = 200;
//             let mut num_enemies = 0;
//             for robot in &enemy_robots {
//                 let dist = robot.pos.distance_squared(pos);

//                 dist_to_nearest_enemy = std::cmp::min(dist_to_nearest_enemy, dist);
//                 if dist <= 16 {
//                     lowest_enemy_health = std::cmp::min(lowest_enemy_health, robot.health);
//                     lowest_enemy = Some(robot);
//                     num_enemies += 1;
//                 }
//             }
//             let enemy_close = if dist_to_nearest_enemy <= 9 { 1.0 } else { 0.0 };
//             let enemy_middle = if 9 < dist_to_nearest_enemy && dist_to_nearest_enemy <= 16 {
//                 1.0
//             } else {
//                 0.0
//             };
//             let enemy_far = if 16 < dist_to_nearest_enemy { 1.0 } else { 0.0 };

//             let mut num_allies_close = 0;
//             let mut num_allies_far = 0;
//             if lowest_enemy.is_some() {
//                 for robot in &ally_robots {
//                     if robot.action_cooldown >= 10 {
//                         continue;
//                     }
//                     let dist = robot.pos.distance_squared(lowest_enemy.unwrap().pos);

//                     if dist <= 16 {
//                         num_allies_close += 1;
//                     } else if dist <= 26 && robot.move_cooldown < 10 {
//                         num_allies_far += 1;
//                     }
//                 }
//             }

//             let ready_to_attack = if robot.action_cooldown < 10 { 1.0 } else { 0.0 };
//             let new_movement_cooldown = if robot.pos == pos {
//                 robot.move_cooldown
//             } else {
//                 robot.move_cooldown + 10
//             };

//             coeffs[0] * enemy_close * ready_to_attack
//                 + coeffs[1] * enemy_middle * ready_to_attack
//                 + coeffs[2] * enemy_far * ready_to_attack
//                 + coeffs[3] * enemy_close * (1.0 - ready_to_attack)
//                 + coeffs[4] * enemy_middle * (1.0 - ready_to_attack)
//                 + coeffs[5] * enemy_far * (1.0 - ready_to_attack)
//                 + coeffs[6] * lowest_enemy_health as f32 / 200.0
//                 + coeffs[7] * num_enemies as f32
//                 + coeffs[8] * num_allies_close as f32 / 5.0
//                 + coeffs[9] * num_allies_far as f32 / 5.0
//                 + coeffs[10] * ready_to_attack
//                 + coeffs[11] * new_movement_cooldown as f32 / 10.0
//         };

//         let mut best_dir = Direction::Center;
//         let mut best_score = get_score(robot.pos.add(Direction::Center));
//         for dir in ORDINAL_DIRECTIONS {
//             if !state.can_move(id, dir) {
//                 continue;
//             }
//             let score = get_score(robot.pos.add(dir));
//             if score > best_score {
//                 best_score = score;
//                 best_dir = dir;
//             }
//         }
//         if state.can_move(id, best_dir) {
//             state.do_move(id, best_dir);
//         }
//     }
// }
