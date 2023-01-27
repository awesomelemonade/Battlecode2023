use itertools::Itertools;

use crate::{
    bot::{Bot, BotProvider},
    direction::Direction,
    position::Position,
    robot::{Robot, RobotController},
};

#[derive(Debug)]
pub struct ScoredMicro {
    coeffs: [f32; 12],
}

impl ScoredMicro {
    pub fn provider<'a>(coeffs: &'a [f32; 12]) -> impl BotProvider<BotType = Self> + 'a {
        move |_: &Robot| ScoredMicro { coeffs: *coeffs }
    }
}

impl Bot for ScoredMicro {
    fn step(&mut self, controller: &mut RobotController) {
        let robot = controller.current_robot();
        let team = robot.team();
        let nearby_robots = controller.sense_nearby_robots_in_vision();
        let enemy_robots = nearby_robots
            .iter()
            .filter(|r| r.team() != team)
            .collect_vec();
        let ally_robots = nearby_robots
            .iter()
            .filter(|r| r.team() == team)
            .collect_vec();

        let get_score = |pos: Position| -> f32 {
            let mut dist_to_nearest_enemy = 1000;
            let mut lowest_enemy = None;
            let mut lowest_enemy_health = 200;
            let mut num_enemies = 0;
            for robot in &enemy_robots {
                let dist = robot.position().distance_squared(pos);

                dist_to_nearest_enemy = dist_to_nearest_enemy.min(dist);
                if dist <= 16 {
                    lowest_enemy_health = lowest_enemy_health.min(robot.health());
                    lowest_enemy = Some(robot);
                    num_enemies += 1;
                }
            }
            let enemy_close = if dist_to_nearest_enemy <= 9 { 1.0 } else { 0.0 };
            let enemy_middle = if 9 < dist_to_nearest_enemy && dist_to_nearest_enemy <= 16 {
                1.0
            } else {
                0.0
            };
            let enemy_far = if 16 < dist_to_nearest_enemy { 1.0 } else { 0.0 };

            let mut num_allies_close = 0;
            let mut num_allies_far = 0;
            if let Some(lowest_enemy) = lowest_enemy {
                for robot in &ally_robots {
                    if robot.action_cooldown() >= 10 {
                        continue;
                    }
                    let dist = robot.position().distance_squared(lowest_enemy.position());

                    if dist <= 16 {
                        num_allies_close += 1;
                    } else if dist <= 26 && robot.is_move_ready() {
                        num_allies_far += 1;
                    }
                }
            }

            let ready_to_attack = if robot.action_cooldown() < 10 {
                1.0
            } else {
                0.0
            };
            let new_movement_cooldown = if robot.position() == pos {
                robot.move_cooldown()
            } else {
                robot.move_cooldown() + 10
            };

            self.coeffs[0] * enemy_close * ready_to_attack
                + self.coeffs[1] * enemy_middle * ready_to_attack
                + self.coeffs[2] * enemy_far * ready_to_attack
                + self.coeffs[3] * enemy_close * (1.0 - ready_to_attack)
                + self.coeffs[4] * enemy_middle * (1.0 - ready_to_attack)
                + self.coeffs[5] * enemy_far * (1.0 - ready_to_attack)
                + self.coeffs[6] * lowest_enemy_health as f32 / 200.0
                + self.coeffs[7] * num_enemies as f32
                + self.coeffs[8] * num_allies_close as f32 / 5.0
                + self.coeffs[9] * num_allies_far as f32 / 5.0
                + self.coeffs[10] * ready_to_attack
                + self.coeffs[11] * new_movement_cooldown as f32 / 10.0
        };

        let mut best_dir = Direction::Center;
        let mut best_score = get_score(robot.position());
        for dir in Direction::ordinal_directions() {
            if !controller.can_move(dir) {
                continue;
            }
            if let Some(location) = robot.position().add_checked(dir, controller.board_dims()) {
                let score = get_score(location);
                if score > best_score {
                    best_score = score;
                    best_dir = dir;
                }
            }
        }
        if best_dir != Direction::Center && controller.can_move(best_dir) {
            controller.move_exn(best_dir);
        }
    }
}
