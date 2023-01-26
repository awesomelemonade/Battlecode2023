use std::collections::HashMap;

#[derive(PartialEq, Debug, Copy, Clone)]
pub enum Team {
    Red,
    Blue,
}

#[derive(PartialEq, Copy, Clone)]
pub enum Direction {
    Center,
    North,
    Northeast,
    East,
    Southeast,
    South,
    Southwest,
    West,
    Northwest,
}

impl Direction {
    pub fn dx(&self) -> i32 {
        match *self {
            Self::Center => 0,
            Self::North => 0,
            Self::Northeast => 1,
            Self::East => 1,
            Self::Southeast => 1,
            Self::South => 0,
            Self::Southwest => -1,
            Self::West => -1,
            Self::Northwest => -1,
        }
    }

    pub fn dy(&self) -> i32 {
        match *self {
            Self::Center => 0,
            Self::North => 1,
            Self::Northeast => 1,
            Self::East => 0,
            Self::Southeast => -1,
            Self::South => -1,
            Self::Southwest => -1,
            Self::West => 0,
            Self::Northwest => 1,
        }
    }
}

#[derive(Copy, Clone, PartialEq)]
pub struct Position {
    pub x: i32,
    pub y: i32,
}

impl Position {
    pub fn add(&self, dir: Direction) -> Self {
        Position {
            x: self.x + dir.dx(),
            y: self.y + dir.dy(),
        }
    }

    pub fn distance_squared(&self, other: Position) -> i32 {
        (self.x - other.x).pow(2) + (self.y - other.y).pow(2)
    }
}

pub struct GameManager<F1, F2>
where
    F1: Fn(&mut GameState, u32) -> (),
    F2: Fn(&mut GameState, u32) -> (),
{
    pub state: GameState,
    red_controller: F1,
    blue_controller: F2,
}

impl<F1, F2> GameManager<F1, F2>
where
    F1: Fn(&mut GameState, u32) -> (),
    F2: Fn(&mut GameState, u32) -> (),
{
    pub fn new(state: GameState, red_controller: F1, blue_controller: F2) -> Self {
        GameManager {
            state,
            red_controller,
            blue_controller,
        }
    }

    pub fn step_game(&mut self) {
        if self.state.is_game_over() {
            panic!("Tried stepping game when game is over");
        }

        self.state.subturn_count += 1;
        if self.state.turn_queue_index == self.state.turn_queue.len() {
            self.state.turn_queue_index = 0;
            self.state.turn_count += 1;
            for (_, robot) in &mut self.state.robots {
                robot.action_cooldown = robot.action_cooldown.saturating_sub(10);
                robot.move_cooldown = robot.move_cooldown.saturating_sub(10);
            }
            self.state.attack_vectors.clear();
        }

        let id = self.state.turn_queue[self.state.turn_queue_index];
        let team = &self.state.robots.get(&id).expect("Invalid id").team;
        if *team == Team::Red {
            (self.red_controller)(&mut self.state, id);
        } else {
            (self.blue_controller)(&mut self.state, id);
        }

        self.state.turn_queue_index += 1;
    }
}

#[derive(Clone)]
pub struct RobotInfo {
    pub id: u32,
    pub health: u32,
    pub action_cooldown: u32,
    pub move_cooldown: u32,
    pub team: Team,
    pub pos: Position,
}

#[derive(Clone)]
pub struct GameState {
    pub width: i32,
    pub height: i32,
    next_robot_id: u32,
    pub turn_queue: Vec<u32>,
    pub turn_queue_index: usize,
    pub turn_count: u32,
    pub subturn_count: u32,
    pub robots: HashMap<u32, RobotInfo>,
    pub map: [[i32; 64]; 64],
    pub attack_vectors: Vec<(Team, Position, Position)>,
}

impl GameState {
    pub fn new(width: i32, height: i32) -> Self {
        GameState {
            width,
            height,
            next_robot_id: 0,
            turn_queue: Vec::new(),
            turn_queue_index: 0,
            turn_count: 0,
            subturn_count: 0,
            robots: HashMap::new(),
            map: [[-1; 64]; 64],
            attack_vectors: Vec::new(),
        }
    }

    pub fn is_game_over(&self) -> bool {
        let mut has_red = false;
        let mut has_blue = false;
        for (_, robot) in &(self.robots) {
            if robot.team == Team::Red {
                has_red = true;
            } else {
                has_blue = true;
            }
        }
        !has_red || !has_blue
    }

    pub fn in_bounds(&self, pos: Position) -> bool {
        0 <= pos.x && pos.x < self.width && 0 <= pos.y && pos.y < self.height
    }

    pub fn place_robot(&mut self, team: Team, pos: Position) {
        if !self.in_bounds(pos) {
            panic!("Cannot place robot out of bounds");
        }

        let id = self.next_robot_id;
        self.next_robot_id += 1;

        let robot = RobotInfo {
            id,
            health: 150,
            action_cooldown: 0,
            move_cooldown: 0,
            team,
            pos,
        };

        self.turn_queue.push(id);
        self.robots.insert(id, robot);
        self.map[pos.x as usize][pos.y as usize] = id as i32;
    }

    pub fn can_move(&self, id: u32, direction: Direction) -> bool {
        let robot = self.robots.get(&id).expect("Invalid id");

        if direction == Direction::Center {
            return false;
        }

        if robot.move_cooldown >= 10 {
            return false;
        }

        let new_pos = robot.pos.add(direction);
        if !self.in_bounds(new_pos) {
            return false;
        }
        if self.map[new_pos.x as usize][new_pos.y as usize] != -1 {
            return false;
        }

        true
    }

    pub fn do_move(&mut self, id: u32, direction: Direction) {
        if !self.can_move(id, direction) {
            panic!("Cannot move");
        }

        let robot = self.robots.get_mut(&id).expect("Invalid id");
        let new_pos = robot.pos.add(direction);

        robot.move_cooldown += 15;
        self.map[robot.pos.x as usize][robot.pos.y as usize] = -1;
        robot.pos = new_pos;
        self.map[new_pos.x as usize][new_pos.y as usize] = robot.id as i32;
    }

    pub fn can_attack(&self, id: u32, target_id: u32) -> bool {
        let robot = self.robots.get(&id).expect("Invalid id");
        let enemy = self.robots.get(&target_id).expect("Invalid id");

        if robot.team == enemy.team {
            return false;
        }

        if robot.action_cooldown >= 10 {
            return false;
        }

        robot.pos.distance_squared(enemy.pos) <= 16
    }

    pub fn do_attack(&mut self, id: u32, target_id: u32) {
        if !self.can_attack(id, target_id) {
            panic!("Cannot attack");
        }

        let mut robot = self.robots.get_mut(&id).expect("Invalid id");
        robot.action_cooldown += 10;
        let robot_pos = robot.pos;
        let robot_team = robot.team;
        let mut enemy = self.robots.get_mut(&target_id).expect("Invalid id");
        self.attack_vectors.push((robot_team, robot_pos, enemy.pos));

        enemy.health = enemy.health.saturating_sub(30);
        if enemy.health == 0 {
            let idx = self
                .turn_queue
                .iter()
                .position(|x| *x == target_id)
                .unwrap();
            self.turn_queue.remove(idx);
            if idx < self.turn_queue_index {
                self.turn_queue_index -= 1;
            }
            self.map[enemy.pos.x as usize][enemy.pos.y as usize] = -1;
            self.robots.remove(&target_id);
        }
    }

    pub fn sense_nearby_robots(&self, id: u32) -> Vec<&RobotInfo> {
        let robot = self.robots.get(&id).expect("Invalid id");
        let mut res = Vec::new();
        for (_, other) in &self.robots {
            if id != other.id && robot.pos.distance_squared(other.pos) <= 20 {
                res.push(other);
            }
        }
        res
    }

    pub fn get_nearest_enemy_omnipotent(&self, id: u32) -> Option<&RobotInfo> {
        let robot = self.robots.get(&id).expect("Invalid id");

        let mut best_enemy: Option<&RobotInfo> = None;
        let mut best_dist = 1000000;
        for (_, other) in &self.robots {
            if robot.team != other.team {
                let dist = robot.pos.distance_squared(other.pos);
                if dist < best_dist {
                    best_dist = dist;
                    best_enemy = Some(other);
                }
            }
        }

        best_enemy
    }
}
