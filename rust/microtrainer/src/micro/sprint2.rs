use itertools::Itertools;

use crate::{
    arena::try_attack,
    bot::Bot,
    direction::Direction,
    position::Position,
    robot::{Robot, RobotController},
};

#[derive(Debug)]
struct Score(f32);

impl PartialEq for Score {
    fn eq(&self, other: &Self) -> bool {
        self.cmp(other) == std::cmp::Ordering::Equal
    }
}
impl Eq for Score {}

impl PartialOrd for Score {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        Some(self.cmp(other))
    }
}

impl Ord for Score {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.0.total_cmp(&other.0)
    }
}

#[derive(Debug, Default)]
pub struct Sprint2Micro {
    prev_closest_enemy_attacker: Option<Position>,
}

impl Bot for Sprint2Micro {
    fn step(&mut self, controller: &mut RobotController) {
        try_attack(controller);
        let our_robot = controller.current_robot();

        fn get_best_move_direction<F>(controller: &RobotController, scorer: F) -> Option<Direction>
        where
            F: Fn(&RobotController, Position) -> f32,
        {
            let current_pos = controller.current_position();
            Direction::all_directions()
                .into_iter()
                .filter(|&dir| dir == Direction::Center || controller.can_move(dir))
                .max_by_key(|&dir| Score(scorer(controller, current_pos.add_exn(dir))))
        }
        fn get_score_with_action_single_enemy_attacker(
            controller: &RobotController,
            pos: Position,
            enemy: &Robot,
        ) -> f32 {
            let our_robot = controller.current_robot();
            let mut score = 0.0;

            // prefer squares that we can attack the enemy
            if pos.distance_squared(enemy.position()) <= our_robot.kind().action_radius_squared() {
                score += 10_000_000.0;
            }

            // prefer not moving
            if controller.current_position() == pos {
                score += 1_000_000.0;
            }

            // prefer squares where you're closest to ally
            if let Some(closest_ally_robot) = controller
                .sense_nearby_robots_in_vision()
                .iter()
                .filter(|r| r.team() == our_robot.team())
                .min_by_key(|r| r.position().distance_squared(our_robot.position()))
            {
                score -= closest_ally_robot.position().distance_squared(pos) as f32 * 20_000.0;
            }

            // prefer straight moves
            if controller.current_position().distance_squared(pos) == 1 {
                score += 10_000.0;
            }

            // prefer squares where you're further away from the enemy
            score += pos.distance_squared(enemy.position()) as f32;

            score
        }
        fn get_score_for_kiting(controller: &RobotController, pos: Position) -> f32 {
            let sensed_robots = controller.sense_nearby_robots_in_vision();
            let our_robot = controller.current_robot();
            let enemy_robots = sensed_robots
                .iter()
                .filter(|r| r.team() != our_robot.team())
                .collect_vec();

            let mut score = 0.0;

            // prefer squares where attackers can't see you
            let num_enemies_in_vision = enemy_robots
                .iter()
                .filter(|r| r.position().distance_squared(pos) <= 20)
                .count();
            score -= num_enemies_in_vision as f32 * 2_000_000.0;

            // prefer squares where you're further away from the closest enemy
            let closest_enemy_distance_squared = enemy_robots
                .iter()
                .map(|r| r.position().distance_squared(pos))
                .min()
                .expect("No enemy robots nearby");
            score += closest_enemy_distance_squared as f32 * 10_000.0;

            // prefer squares where you're closest to ally
            if let Some(closest_ally_robot) = sensed_robots
                .iter()
                .filter(|r| r.team() == our_robot.team())
                .min_by_key(|r| r.position().distance_squared(our_robot.position()))
            {
                score -= closest_ally_robot.position().distance_squared(pos) as f32 * 100.0;
            }

            // prefer diagonals over straight directions
            if our_robot.position().distance_squared(pos) == 2 {
                score += 50.0;
            }

            score
        }
        let get_micro_direction = || -> Direction {
            let sensed_robots = controller.sense_nearby_robots_in_vision();
            let closest_enemy_attacker = sensed_robots
                .iter()
                .filter(|r| r.team() != our_robot.team())
                .min_by_key(|r| r.position().distance_squared(our_robot.position()))
                .expect("why are we microing with no enemies in vision");
            let closest_enemy_attacker_distance_squared = our_robot
                .position()
                .distance_squared(closest_enemy_attacker.position());
            let has_closer_ally = controller
                .sense_nearby_robots_in_vision()
                .iter()
                .filter(|r| r.team() == our_robot.team())
                .any(|r| {
                    closest_enemy_attacker
                        .position()
                        .distance_squared(r.position())
                        <= closest_enemy_attacker_distance_squared
                });
            if our_robot.is_action_ready() {
                // getSingleAttackerOrNull
                if let Some(single_enemy_attacker) = controller
                    .sense_nearby_robots_in_vision()
                    .iter()
                    .filter(|r| r.team() != our_robot.team())
                    .exactly_one()
                    .ok()
                {
                    let damage = single_enemy_attacker.kind().damage();
                    let num_attacks_to_enemy =
                        (single_enemy_attacker.health() + damage - 1) / damage;
                    let num_attacks_to_us = (our_robot.health() + damage - 1) / damage;
                    let should_attack = num_attacks_to_us >= num_attacks_to_enemy;
                    if should_attack {
                        // attack
                        get_best_move_direction(controller, |controller, location| {
                            get_score_with_action_single_enemy_attacker(
                                controller,
                                location,
                                single_enemy_attacker,
                            )
                        })
                        .unwrap_or(Direction::Center)
                    } else {
                        // kite
                        get_best_move_direction(controller, get_score_for_kiting)
                            .unwrap_or(Direction::Center)
                    }
                } else {
                    // must be seeing multiple enemies
                    if has_closer_ally {
                        // attack
                        get_best_move_direction(controller, |controller, location| {
                            get_score_with_action_single_enemy_attacker(
                                controller,
                                location,
                                closest_enemy_attacker,
                            )
                        })
                        .unwrap_or(Direction::Center)
                    } else {
                        // kite
                        get_best_move_direction(controller, get_score_for_kiting)
                            .unwrap_or(Direction::Center)
                    }
                }
            } else {
                if has_closer_ally {
                    Direction::Center
                } else {
                    // kite
                    get_best_move_direction(controller, get_score_for_kiting)
                        .unwrap_or(Direction::Center)
                }
            }
        };

        if let Some(closest_enemy) = controller.get_nearest_enemy_omnipotent() {
            let position = controller.current_position();
            let dist = position.distance_squared(closest_enemy.position());
            if dist > 20 {
                if self.prev_closest_enemy_attacker.is_some() {
                    // stay still
                } else {
                    // omnipotent move
                    let target = closest_enemy.position();
                    if let Some(&move_direction) =
                        Direction::attempt_order(position.direction_to(target))
                            .iter()
                            .filter(|&&dir| controller.can_move(dir))
                            .next()
                    {
                        controller.move_exn(move_direction);
                    }
                }
            } else {
                let dir = get_micro_direction();
                if dir != Direction::Center && controller.can_move(dir) {
                    controller.move_exn(dir);
                }
            }
        }
        try_attack(controller);
        self.prev_closest_enemy_attacker = controller
            .get_nearest_enemy_omnipotent()
            .filter(|r| r.position().distance_squared(controller.current_position()) <= 20)
            .map(|r| r.position());
    }
}
