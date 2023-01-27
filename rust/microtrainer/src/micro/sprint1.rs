use crate::{
    bot::Bot,
    direction::Direction,
    position::Position,
    robot::{Robot, RobotController},
};

// use crate::position::Position;
// use crate::robot::{RobotController, Robot};
// use crate::Direction;
#[derive(Debug, Default)]
pub struct Sprint1Micro {}

impl Bot for Sprint1Micro {
    fn step(&mut self, controller: &mut RobotController) {
        fn get_best_move_direction<F>(controller: &RobotController, scorer: F) -> Direction
        where
            F: Fn(&RobotController, Position) -> f32,
        {
            let mut best_direction = Direction::Center;
            let mut best_score = -1e18;
            let current_pos = controller.current_position();

            for direction in Direction::ordinal_directions() {
                if !controller.can_move(direction) {
                    continue;
                }
                let position = current_pos.add_exn(direction);
                let score = scorer(controller, position);
                if score > best_score {
                    best_score = score;
                    best_direction = direction;
                }
            }
            best_direction
        }

        fn get_score_for_kiting(controller: &RobotController, pos: Position) -> f32 {
            let sensed_robots = controller.sense_nearby_robots_in_vision();
            let our_robot = controller.current_robot();

            let mut score = 0.0;

            // prefer squares where attackers can't see you
            let mut num_enemies = 0;
            for other in sensed_robots {
                if our_robot.team() != other.team() && pos.distance_squared(other.position()) <= 20
                {
                    num_enemies += 1;
                }
            }
            score -= num_enemies as f32 * 100000.0;

            // prefer squares where you're further away from the closest enemy
            let closest_enemy = controller.get_nearest_enemy_omnipotent().unwrap();
            let dist = our_robot
                .position()
                .distance_squared(closest_enemy.position());
            assert!(dist <= 20);
            score += dist as f32 * 1000.0;

            // prefer diagonals over straight directions
            if our_robot.position().distance_squared(pos) == 2 {
                score += 50.0;
            }

            score
        }

        fn get_score_with_action_single_enemy_attacker(
            controller: &RobotController,
            pos: Position,
            enemy: &Robot,
        ) -> f32 {
            let mut score = 0.0;

            // todo: prefer non-clouds if we're not in a cloud

            // prefer squares that we can attack the enemy
            if pos.distance_squared(enemy.position()) <= 16 {
                score += 1000000.0;
            }

            // prefer straight moves
            if controller.current_position().distance_squared(pos) == 1 {
                score += 1000.0;
            }

            // prefer squares where you're further away from the enemy
            score += pos.distance_squared(enemy.position()) as f32;

            score
        }

        let our_robot = controller.current_robot();
        let sensed_robots = controller.sense_nearby_robots_in_vision();
        if our_robot.action_cooldown() < 10 {
            // getSingleAttackerOrNull
            let mut enemy = None;
            for other in sensed_robots {
                if our_robot.team() != other.team() {
                    if enemy.is_some() {
                        enemy = None;
                        break;
                    }
                    enemy = Some(other);
                }
            }

            if let Some(enemy) = enemy {
                // shouldAttackSingleEnemyWithAction
                let damage = 30;
                let num_attacks_to_enemy = (enemy.health() + damage - 1) / damage;
                let num_attacks_to_us = (our_robot.health() + damage - 1) / damage;
                let should_attack = num_attacks_to_us >= num_attacks_to_enemy;

                if should_attack {
                    let dir = get_best_move_direction(controller, |controller, pos| {
                        get_score_with_action_single_enemy_attacker(controller, pos, enemy)
                    });
                    if controller.can_move(dir) {
                        controller.move_exn(dir);
                    }
                    return;
                }
            }
        }
        let dir = get_best_move_direction(controller, get_score_for_kiting);
        if controller.can_move(dir) {
            controller.move_exn(dir);
        }
    }
}
