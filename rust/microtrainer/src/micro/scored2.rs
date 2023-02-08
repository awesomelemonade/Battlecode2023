core!();

use std::ops::{BitAnd, BitOrAssign, BitXor, BitXorAssign, Shl};

use itertools::Itertools;
use rand::{seq::IteratorRandom, Rng};

use crate::{
    bot::{Bot, BotProvider},
    bot_ext::BotExt,
    direction::Direction,
    position::Position,
    robot::{Robot, RobotController},
};

#[derive(Debug, Clone, Copy, PartialEq, PartialOrd)]
struct Score(f32);

impl Eq for Score {}

impl Ord for Score {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.partial_cmp(other).unwrap()
    }
}

#[derive(Debug, Clone)]
pub struct Parameters<const NUM_PARAMETERS: usize, const PARAM_LEN: usize> {
    parameters: [SubParameters<PARAM_LEN>; NUM_PARAMETERS],
}

impl<const NUM_PARAMETERS: usize, const PARAM_LEN: usize> Default
    for Parameters<NUM_PARAMETERS, PARAM_LEN>
{
    fn default() -> Self {
        Self {
            parameters: [Default::default(); NUM_PARAMETERS],
        }
    }
}

impl<const NUM_PARAMETERS: usize, const PARAM_LEN: usize> Parameters<NUM_PARAMETERS, PARAM_LEN> {
    pub fn new(parameters: [SubParameters<PARAM_LEN>; NUM_PARAMETERS]) -> Self {
        Self { parameters }
    }
    pub fn random_neighbor(&self) -> Parameters<NUM_PARAMETERS, PARAM_LEN> {
        let mut rng = rand::thread_rng();
        let index = rng.gen_range(0..NUM_PARAMETERS);
        let mut ret = self.clone();
        ret.parameters[index] = ret.parameters[index].random_neighbor();
        ret
    }
    pub fn squashed_parameters(&self) -> Vec<(usize, [usize; PARAM_LEN])> {
        self.parameters
            .map(|parameter| (parameter.max_or_min, parameter.permutation.data))
            .into_iter()
            .collect_vec()
    }
}

#[derive(Debug, Clone, Copy)]
pub struct Permutation<const LEN: usize> {
    data: [usize; LEN],
}

impl<const LEN: usize> Permutation<LEN> {
    pub fn new(data: [usize; LEN]) -> OrError<Self> {
        // verify they are all in range and unique
        if data.iter().all(|&x| x < LEN) && data.iter().all_unique() {
            Ok(Self { data })
        } else {
            Err(Error!("Data is not a permutation: {:?}", data))
        }
    }
    pub fn apply<T: Copy>(&self, data: [T; LEN]) -> [T; LEN] {
        self.data.map(|i| data[i])
    }
}

impl<const LEN: usize> Default for Permutation<LEN> {
    fn default() -> Self {
        let mut data = [0; LEN];
        for i in 0..LEN {
            data[i] = i;
        }
        Self { data }
    }
}

#[derive(Debug, Default, Clone, Copy)]
pub struct SubParameters<const LEN: usize> {
    max_or_min: usize, // bitfield
    permutation: Permutation<LEN>,
}

impl<const LEN: usize> SubParameters<LEN> {
    pub fn new(max_or_min: usize, permutation: Permutation<LEN>) -> Self {
        Self {
            max_or_min,
            permutation,
        }
    }
    fn apply(&self, mut data: [f32; LEN]) -> [f32; LEN] {
        for i in 0..LEN {
            if self.max_or_min.bitand(1usize.shl(i)) != 0 {
                data[i] = -data[i];
            }
        }
        self.permutation.apply(data)
    }
    pub fn random_neighbor(&self) -> SubParameters<LEN> {
        let mut rng = rand::thread_rng();
        let mut ret = self.clone();
        if rng.gen_bool(0.5) {
            // flip max_or_min bit
            ret.max_or_min
                .bitxor_assign(1usize.shl(rng.gen_range(0..LEN)));
        } else {
            // swap 2 elements in the permutation
            let indices = (0..LEN).choose_multiple(&mut rng, 2);
            ret.permutation.data.swap(indices[0], indices[1]);
        }
        ret
    }
}

pub type Scored2Parameters = Parameters<2, 7>;

#[derive(Debug)]
pub struct ScoredMicro2 {
    parameters: Scored2Parameters,
    prev_enemy_location: Option<Position>,
    last_turn_with_enemy: Option<u32>,
}

impl ScoredMicro2 {
    pub fn provider<'a>(
        parameters: &'a Scored2Parameters,
    ) -> impl BotProvider<BotType = Self> + 'a {
        move |_: &Robot| ScoredMicro2 {
            prev_enemy_location: None,
            last_turn_with_enemy: None,
            parameters: parameters.clone(),
        }
    }

    fn try_micro_move(&mut self, controller: &mut RobotController) {
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
        let damage = robot.kind().damage();
        let action_radius_squared = controller.current_robot().kind().action_radius_squared();
        let vision_radius_squared = controller.current_robot().kind().vision_radius_squared();
        let closest_enemy_location = enemy_robots
            .iter()
            .min_by_key(|r| r.position().distance_squared(robot.position()))
            .map(|r| r.position())
            .or(self.prev_enemy_location);

        let has_closer_ally = closest_enemy_location
            .map(|enemy_location| {
                ally_robots.iter().any(|ally_robot| {
                    ally_robot.position().distance_squared(enemy_location)
                        < robot.position().distance_squared(enemy_location)
                })
            })
            .unwrap_or(false);

        let factors = [
            // controller.round_num() % 2 == 0,
            robot.is_action_ready(),
            // robot.health() <= damage,
            // enemy_robots.is_empty(),
            // has_closer_ally,
        ];
        let coeffs_index = factors
            .iter()
            .enumerate()
            .map(|(i, &value)| (value as usize).shl(i))
            .sum::<usize>();
        let parameters = self.parameters.parameters[coeffs_index];

        let get_score = |pos: Position| -> [Score; 7] {
            let action_enemies = enemy_robots
                .iter()
                .filter(|r| r.position().distance_squared(pos) <= action_radius_squared)
                .collect_vec();
            let action_enemies_not_empty = (!action_enemies.is_empty()) as i32;
            let vision_enemies = enemy_robots
                .iter()
                .filter(|r| r.position().distance_squared(pos) <= vision_radius_squared)
                .collect_vec();
            let vision_enemies_not_empty = (!vision_enemies.is_empty()) as i32;

            let is_center_move = (robot.position() == pos) as i32;
            let is_straight_move = (robot.position().distance_squared(pos) == 1) as i32;

            let closest_enemy_distance =
                closest_enemy_location.map(|loc| (loc.distance_squared(pos) as f32).sqrt());

            let data = [
                action_enemies.len() as f32,
                action_enemies_not_empty as f32,
                vision_enemies.len() as f32,
                vision_enemies_not_empty as f32,
                is_center_move as f32,
                is_straight_move as f32,
                closest_enemy_distance.unwrap_or(0.0),
            ];
            parameters.apply(data).map(|f| Score(f))
        };

        let best_direction = Direction::all_directions()
            .into_iter()
            .filter(|&dir| dir == Direction::Center || controller.can_move(dir))
            .min_by_key(|&dir| get_score(robot.position().add_exn(dir)));
        if let Some(best_dir) = best_direction && best_dir != Direction::Center {
            controller.move_exn(best_dir);
        }
    }
}

impl Bot for ScoredMicro2 {
    fn step(&mut self, controller: &mut RobotController) {
        if controller
            .sense_nearby_robots_in_vision()
            .iter()
            .any(|r| r.team() != controller.current_robot().team())
        {
            self.last_turn_with_enemy = Some(controller.round_num());
        }
        self.try_attack(controller);
        // have we seen an enemy this turn or last turn?
        if let Some(last_turn_with_enemy) = self.last_turn_with_enemy && last_turn_with_enemy >= controller.round_num() - 1 {
            self.try_micro_move(controller);
        } else {
            self.omnipotent_move(controller);
        }

        self.try_attack(controller);
        self.prev_enemy_location = controller
            .sense_nearby_robots_in_vision()
            .iter()
            .filter(|r| r.team() != controller.current_robot().team())
            .min_by_key(|r| r.position().distance_squared(controller.current_position()))
            .map(|r| r.position());
    }
}
