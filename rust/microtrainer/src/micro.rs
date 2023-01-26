use rand::seq::SliceRandom;

use crate::game::Board;
use crate::robot::RobotController;
use crate::Direction;
use crate::Position;

pub fn random_micro() -> impl Fn(&mut RobotController) {
    |controller| {
        let mut rng = rand::thread_rng();
        let direction = *Direction::ordinal_directions().choose(&mut rng).unwrap();
        if controller.can_move(direction) {
            controller.move_exn(direction);
        }
    }
}

// TODO: FnMut instead of Fn?
pub fn scored_micro(_coeffs: [f32; 12]) -> impl Fn(&mut RobotController) {
    random_micro()
}

// TODO: FnMut instead of Fn?
pub fn sprint_micro() -> impl Fn(&mut RobotController) {
    random_micro()
}

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

// pub fn sprint_micro(state: &mut Board, id: u32) {
//     fn get_best_move_direction<F>(state: &Board, id: u32, scorer: F) -> Direction
//     where
//         F: Fn(&Board, u32, Position) -> f32,
//     {
//         let robot = state.robots.get(&id).unwrap();
//         let mut best_direction = Direction::Center;
//         let mut best_score = -1e18;

//         for direction in ORDINAL_DIRECTIONS {
//             if !state.can_move(robot.id, direction) {
//                 continue;
//             }
//             let position = robot.pos.add(direction);
//             let score = scorer(state, id, position);
//             if score > best_score {
//                 best_score = score;
//                 best_direction = direction;
//             }
//         }
//         best_direction
//     }

//     fn get_score_for_kiting(state: &Board, id: u32, pos: Position) -> f32 {
//         let robot = state.robots.get(&id).unwrap();
//         let sensed_robots = state.sense_nearby_robots(id);

//         let mut score = 0.0;

//         // prefer squares where attackers can't see you
//         let mut num_enemies = 0;
//         for other in sensed_robots {
//             if robot.team != other.team && pos.distance_squared(other.pos) <= 20 {
//                 num_enemies += 1;
//             }
//         }
//         score -= num_enemies as f32 * 100000.0;

//         // prefer squares where you're further away from the closest enemy
//         let closest_enemy = state.get_nearest_enemy_omnipotent(id).unwrap();
//         let dist = robot.pos.distance_squared(closest_enemy.pos);
//         assert!(dist <= 20);
//         score += dist as f32 * 1000.0;

//         // prefer diagonals over straight directions
//         if robot.pos.distance_squared(pos) == 2 {
//             score += 50.0;
//         }

//         score
//     }

//     fn get_score_with_action_single_enemy_attacker(
//         state: &Board,
//         id: u32,
//         pos: Position,
//         enemy: &RobotInfo,
//     ) -> f32 {
//         let robot = state.robots.get(&id).unwrap();

//         let mut score = 0.0;

//         // todo: prefer non-clouds if we're not in a cloud

//         // prefer squares that we can attack the enemy
//         if pos.distance_squared(enemy.pos) <= 16 {
//             score += 1000000.0;
//         }

//         // prefer straight moves
//         if robot.pos.distance_squared(pos) == 1 {
//             score += 1000.0;
//         }

//         // prefer squares where you're further away from the enemy
//         score += pos.distance_squared(enemy.pos) as f32;

//         score
//     }

//     let robot = state.robots.get(&id).unwrap();
//     let sensed_robots = state.sense_nearby_robots(id);
//     if robot.action_cooldown < 10 {
//         // getSingleAttackerOrNull
//         let mut enemy = None;
//         for other in sensed_robots {
//             if robot.team != other.team {
//                 if enemy.is_some() {
//                     enemy = None;
//                     break;
//                 }
//                 enemy = Some(other);
//             }
//         }

//         if let Some(enemy) = enemy {
//             // shouldAttackSingleEnemyWithAction
//             let damage = 30;
//             let num_attacks_to_enemy = (enemy.health + damage - 1) / damage;
//             let num_attacks_to_us = (robot.health + damage - 1) / damage;
//             let should_attack = num_attacks_to_us >= num_attacks_to_enemy;

//             if should_attack {
//                 let dir = get_best_move_direction(state, id, |state, id, pos| {
//                     get_score_with_action_single_enemy_attacker(state, id, pos, enemy)
//                 });
//                 if state.can_move(id, dir) {
//                     state.do_move(id, dir);
//                 }
//                 return;
//             }
//         }
//     }
//     let dir = get_best_move_direction(state, id, get_score_for_kiting);
//     if state.can_move(id, dir) {
//         state.do_move(id, dir);
//     }
// }
