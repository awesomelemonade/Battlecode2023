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
        let damage = robot.kind().damage();
        let action_radius_squared = controller.current_robot().kind().action_radius_squared();
        let vision_radius_squared = controller.current_robot().kind().vision_radius_squared();
        let closest_enemy_location = enemy_robots
            .iter()
            .min_by_key(|r| r.position().distance_squared(robot.position()))
            .map(|r| r.position())
            .expect("Micro with no enemy?");

        // let has_closer_ally = ally_robots.iter().any(|r| todo!());
        // let factors = [
        //     controller.round_num() % 2 == 0,
        //     robot.is_action_ready(),
        //     robot.health() <= damage,
        //     enemy_robots.is_empty(),
        //     has_closer_ally,
        // ];

        let get_score = |pos: Position| -> f32 {
            let action_enemies = enemy_robots
                .iter()
                .filter(|r| r.position().distance_squared(pos) <= action_radius_squared)
                .collect_vec();
            let vision_enemies = enemy_robots
                .iter()
                .filter(|r| r.position().distance_squared(pos) <= vision_radius_squared)
                .collect_vec();
            let vision_enemy_health: u32 = vision_enemies.iter().map(|r| r.health()).sum();
            let vision_enemy_damage = vision_enemies.len() as u32 * damage;

            let health_factor = if vision_enemies.is_empty() {
                0
            } else {
                let num_attacks_to_enemy = (vision_enemy_health + damage - 1) / damage;
                let num_attacks_to_us =
                    (robot.health() + vision_enemy_damage - 1) / vision_enemy_damage;
                num_attacks_to_enemy as i32 - num_attacks_to_us as i32
            };

            let is_center_move = if robot.position() == pos { 1 } else { 0 };
            let is_straight_move = if robot.position().distance_squared(pos) == 1 {
                1
            } else {
                0
            };

            let closest_enemy_distance =
                (closest_enemy_location.distance_squared(pos) as f32).sqrt();

            if robot.is_action_ready() {
                self.coeffs[0] * action_enemies.len() as f32
                    + self.coeffs[1] * (vision_enemies.len() - action_enemies.len()) as f32
                    + self.coeffs[2] * health_factor as f32 / 10.0
                    + self.coeffs[3] * is_center_move as f32
                    + self.coeffs[4] * is_straight_move as f32
                    + self.coeffs[5] * closest_enemy_distance / 5.0
            } else {
                self.coeffs[6] * action_enemies.len() as f32
                    + self.coeffs[7] * (vision_enemies.len() - action_enemies.len()) as f32
                    + self.coeffs[8] * health_factor as f32 / 10.0
                    + self.coeffs[9] * is_center_move as f32
                    + self.coeffs[10] * is_straight_move as f32
                    + self.coeffs[11] * closest_enemy_distance / 5.0
            }

            // self.coeffs[0] * enemy_close * ready_to_attack
            //     + self.coeffs[1] * enemy_middle * ready_to_attack
            //     + self.coeffs[2] * enemy_far * ready_to_attack
            //     + self.coeffs[3] * enemy_close * (1.0 - ready_to_attack)
            //     + self.coeffs[4] * enemy_middle * (1.0 - ready_to_attack)
            //     + self.coeffs[5] * enemy_far * (1.0 - ready_to_attack)
            //     + self.coeffs[6] * lowest_enemy_health as f32 / 200.0
            //     + self.coeffs[7] * num_enemies as f32
            //     + self.coeffs[8] * num_allies_close as f32 / 5.0
            //     + self.coeffs[9] * num_allies_far as f32 / 5.0
            //     + self.coeffs[10] * ready_to_attack // useless
            //     + self.coeffs[11] * new_movement_cooldown as f32 / 10.0
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
