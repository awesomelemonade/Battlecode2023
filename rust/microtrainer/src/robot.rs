use std::collections::BTreeMap;

use itertools::Itertools;

use crate::{direction::Direction, game::Board, grid::Grid, Position};

pub struct RobotController<'a> {
    board: &'a mut Board,
    robot_id: RobotId,
}

impl<'a> RobotController<'a> {
    pub fn new(board: &'a mut Board, robot_id: RobotId) -> Self {
        Self { board, robot_id }
    }

    pub fn robot_id(&self) -> RobotId {
        self.robot_id
    }

    pub fn can_move(&self, direction: Direction) -> bool {
        if let Some(position) =
            self.current_position()
                .add_checked(direction, self.board.width(), self.board.height())
        {
            let robot = self.current_robot();
            robot.is_move_ready()
                && (robot.position() == position || !self.board.robots().is_occupied(position))
        } else {
            false
        }
    }
    pub fn move_exn(&mut self, direction: Direction) {
        assert!(self.can_move(direction));
        let position = self.current_position().add_exn(direction);
        let robots = self.board.robots_mut();
        let robot = robots
            .get_robot_if_alive_mut(self.robot_id)
            .expect("Controlled robot is not alive");
        robot.move_cooldown += 20;
        let old_position = robot.position();
        *robot.position_mut() = position;
        robots.robots_by_position[old_position] = None;
        robots.robots_by_position[position] = Some(self.robot_id);
    }
    pub fn can_attack(&self, position: impl Into<Position>) -> bool {
        let position = position.into();
        let robot = self.current_robot();
        robot.is_action_ready()
            && robot.position.distance_squared(position) <= robot.kind.action_radius_squared()
    }

    pub fn attack_exn(&mut self, position: impl Into<Position>) {
        let position = position.into();
        assert!(self.can_attack(position));
        let robot = self.current_robot_mut();
        robot.action_cooldown += 10;
        let team = robot.team;
        let damage = robot.kind.damage();

        let robots = self.board.robots_mut();
        if let Some(dead_robot_id) = robots
            .find_robot_by_position_mut(position)
            .filter(|r| r.team() != team)
            .and_then(|target| {
                target.health = target.health.saturating_sub(damage);
                if target.health == 0 {
                    Some(target.id)
                } else {
                    None
                }
            })
        {
            robots.despawn_robot(dead_robot_id);
        }
    }

    pub fn get_nearest_enemy_omnipotent(&self) -> Option<&Robot> {
        let robot = self.current_robot();
        self.board
            .robots()
            .iter()
            .filter(|r| r.team != robot.team)
            .min_by_key(|r| r.position.distance_squared(robot.position))
    }

    pub fn get_all_positions(&self, distance_squared: u32) -> Vec<Position> {
        let sqrt = ((distance_squared as f64).sqrt() as i32) + 1;
        let current_position = self.current_position();
        let mut positions = Vec::new();
        let board_width = self.board.width();
        let board_height = self.board.height();
        for dx in -sqrt..=sqrt {
            for dy in -sqrt..=sqrt {
                if (dx * dx + dy * dy) as u32 <= distance_squared {
                    if let Some(new_position) =
                        current_position.add_checked((dx, dy), board_width, board_height)
                    {
                        positions.push(new_position);
                    }
                }
            }
        }
        positions
    }
    pub fn on_the_map(&self, position: Position) -> bool {
        position.x < self.board.width() && position.y < self.board.height()
    }
    pub fn current_robot(&self) -> &Robot {
        self.board
            .robots()
            .get_robot_if_alive(self.robot_id)
            .expect("Controlled robot is not alive")
    }
    fn current_robot_mut(&mut self) -> &mut Robot {
        self.board
            .robots_mut()
            .get_robot_if_alive_mut(self.robot_id)
            .expect("Controlled robot is not alive")
    }
    pub fn current_position(&self) -> Position {
        self.current_robot().position
    }

    pub fn sense_nearby_robots_in_vision(&self) -> Vec<&Robot> {
        self.sense_nearby_robots(self.current_robot().kind.vision_radius_squared())
    }
    pub fn sense_nearby_robots(&self, distance_squared: u32) -> Vec<&Robot> {
        // TODO: check vision radius?
        let current_position = self.current_position();
        self.board
            .robots()
            .iter()
            .filter(|r| {
                r.id != self.robot_id
                    && r.position.distance_squared(current_position) <= distance_squared
            })
            .collect_vec()
    }
    pub fn get_round_num(&self) -> u32 {
        self.board.round_num()
    }
}

#[derive(Debug, Copy, Clone, PartialEq, Eq)]
pub enum RobotKind {
    // Headquarter,
    // Carrier,
    Launcher,
}

impl RobotKind {
    pub fn vision_radius_squared(&self) -> u32 {
        20
    }
    pub fn action_radius_squared(&self) -> u32 {
        16
    }
    pub fn damage(&self) -> u32 {
        20
    }
    pub fn starting_health(&self) -> u32 {
        200
    }
}

#[derive(PartialEq, Debug, Copy, Clone)]
pub enum Team {
    Red,
    Blue,
}

#[derive(Debug, Clone)]
pub struct Robot {
    id: RobotId,
    position: Position,
    move_cooldown: u32,
    action_cooldown: u32,
    kind: RobotKind,
    team: Team,
    health: u32,
}

impl Robot {
    pub fn decrement_cooldowns(&mut self) {
        self.move_cooldown = self.move_cooldown.saturating_sub(10);
        self.action_cooldown = self.action_cooldown.saturating_sub(10);
    }

    pub fn position(&self) -> Position {
        self.position
    }

    pub fn position_mut(&mut self) -> &mut Position {
        &mut self.position
    }

    pub fn team(&self) -> Team {
        self.team
    }

    pub fn health(&self) -> u32 {
        self.health
    }

    pub fn is_move_ready(&self) -> bool {
        self.move_cooldown < 10
    }

    pub fn move_cooldown(&self) -> u32 {
        self.move_cooldown
    }

    pub fn is_action_ready(&self) -> bool {
        self.action_cooldown < 10
    }

    pub fn action_cooldown(&self) -> u32 {
        self.action_cooldown
    }

    pub fn kind(&self) -> RobotKind {
        self.kind
    }
}

pub type RobotId = u32;

#[derive(Debug, Clone)]
pub struct RobotIdGenerator {
    next_id: RobotId,
}

impl RobotIdGenerator {
    fn new() -> Self {
        Self { next_id: 0 }
    }
    fn next(&mut self) -> RobotId {
        let ret = self.next_id;
        self.next_id += 1;
        ret
    }
}

#[derive(Debug, Clone)]
pub struct Robots {
    id_generator: RobotIdGenerator,
    robot_turn_order: Vec<RobotId>,
    robots_by_id: BTreeMap<RobotId, Robot>,
    robots_by_position: Grid<Option<RobotId>>,
}

impl Robots {
    pub fn new(width: usize, height: usize) -> Self {
        Self {
            id_generator: RobotIdGenerator::new(),
            robot_turn_order: Vec::new(),
            robots_by_id: BTreeMap::new(),
            robots_by_position: Grid::new(width, height),
        }
    }
    pub fn robot_turn_order(&self) -> &Vec<RobotId> {
        &self.robot_turn_order
    }
    pub fn spawn_robot(
        &mut self,
        team: Team,
        kind: RobotKind,
        move_cooldown: u32,
        position: impl Into<Position>,
    ) {
        let position = position.into();
        debug_assert!(self.robots_by_position.within_bounds(position));
        debug_assert!(!self.is_occupied(position));
        let robot_id = self.id_generator.next();
        let health = kind.starting_health();
        let robot = Robot {
            id: robot_id,
            position,
            move_cooldown,
            action_cooldown: 0,
            kind,
            team,
            health,
        };
        self.robot_turn_order.push(robot_id);
        self.robots_by_position[robot.position()] = Some(robot_id);
        self.robots_by_id.insert(robot_id, robot);
    }
    pub fn despawn_robot(&mut self, robot_id: RobotId) {
        // TODO-someday: consider changing robot_turn_order to a BTreeSet but with insertion order
        self.robot_turn_order.remove(
            self.robot_turn_order
                .iter()
                .position(|&r| r == robot_id)
                .expect("Robot id not in turn order"),
        );
        let robot = self
            .robots_by_id
            .remove(&robot_id)
            .expect("Robot is not alive");
        self.robots_by_position[robot.position()] = None;
    }
    pub fn is_occupied(&self, position: impl Into<Position>) -> bool {
        self.find_robot_id_by_position(position.into()).is_some()
    }
    pub fn find_robot_id_by_position(&self, position: Position) -> Option<RobotId> {
        self.robots_by_position[position]
    }
    pub fn find_robot_by_position(&self, position: Position) -> Option<&Robot> {
        let robot_id = self.find_robot_id_by_position(position)?;
        self.get_robot_if_alive(robot_id)
    }
    pub fn find_robot_by_position_mut(&mut self, position: Position) -> Option<&mut Robot> {
        let robot_id = self.find_robot_id_by_position(position)?;
        self.get_robot_if_alive_mut(robot_id)
    }
    pub fn get_robot_if_alive(&self, id: RobotId) -> Option<&Robot> {
        self.robots_by_id.get(&id)
    }
    pub fn get_robot_if_alive_mut(&mut self, id: RobotId) -> Option<&mut Robot> {
        self.robots_by_id.get_mut(&id)
    }
    pub fn is_alive(&self, id: RobotId) -> bool {
        self.get_robot_if_alive(id).is_some()
    }

    // no guarantees on turn order
    pub fn iter(&self) -> impl Iterator<Item = &Robot> {
        self.robots_by_id.values()
    }

    // no guarantees on turn order
    pub fn iter_mut(&mut self) -> impl Iterator<Item = &mut Robot> {
        self.robots_by_id.values_mut()
    }
}
