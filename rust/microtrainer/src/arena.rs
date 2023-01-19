use rand::Rng;
use crate::game::{GameState, Direction, Team, Position, GameManager};

pub fn wrap_micro<F>(micro: F) -> impl Fn(&mut GameState, u32) -> ()
where F: Fn(&mut GameState, u32) -> () {
    fn try_attack(state: &mut GameState, id: u32) {
        let robot = state.robots.get(&id).expect("Invalid id");

        let nearby = state.sense_nearby_robots(id);
        let mut best_health = 10000;
        let mut best_id = robot.id;
        for other in nearby {
            if state.can_attack(id, other.id) {
                if other.health < best_health {
                    best_health = other.health;
                    best_id = other.id;
                }
            }
        }
        if state.can_attack(id, best_id) {
            state.do_attack(id, best_id);
        }
    }

    move |state, id| {
        try_attack(state, id);
        let closest_enemy = state.get_nearest_enemy_omnipotent(id);
        if closest_enemy.is_none() {
            return ();
        }
        let closest_enemy = closest_enemy.unwrap();
        let robot = state.robots.get(&id).unwrap();
        let dist = robot.pos.distance_squared(closest_enemy.pos);
        if dist > 20 {
            let all_dirs = [
                Direction::North,
                Direction::Northeast,
                Direction::East,
                Direction::Southeast,
                Direction::South,
                Direction::Southwest,
                Direction::West,
                Direction::Northwest,
            ];
            let mut best_dir = Direction::Center;
            let mut best_dist = dist;
            for dir in all_dirs {
                if state.can_move(id, dir) {
                    let new_pos = robot.pos.add(dir);
                    let dist = new_pos.distance_squared(closest_enemy.pos);
                    if dist < best_dist {
                        best_dist = dist;
                        best_dir = dir;
                    }
                }
            }
            if state.can_move(id, best_dir) {
                state.do_move(id, best_dir);
            }
        } else {
            micro(state, id);
        }
        try_attack(state, id);
    }
}

pub fn gen_random_starting_state() -> GameState {
    let mut rng = rand::thread_rng();

    let mut state = GameState::new(32, 32);
    let num_robots = rng.gen_range(3..=15);
    for _ in 0..num_robots {
        let mut x = rng.gen_range(0..state.width);
        let mut y = rng.gen_range(0..state.height/3);
        while state.map[x as usize][y as usize] != -1 {
            x = rng.gen_range(0..state.width);
            y = rng.gen_range(0..state.height/3);
        }
        state.place_robot(Team::Red, Position{x, y});
    }
    for _ in 0..num_robots {
        let mut x = rng.gen_range(0..state.width);
        let mut y = state.height-1 - rng.gen_range(0..state.height/3);
        while state.map[x as usize][y as usize] != -1 {
            x = rng.gen_range(0..state.width);
            y = state.height-1 - rng.gen_range(0..state.height/3);
        }
        state.place_robot(Team::Blue, Position{x, y});
    }
        
    state
}

pub fn get_score<F1, F2>(micro1: F1, micro2: F2, samples: u32) -> f32
where F1: Fn(&mut GameState, u32) -> (),
      F2: Fn(&mut GameState, u32) -> () {
    let mut total_health_1 = 0.0;
    let mut total_health_2 = 0.0;
    for _ in 0..samples {
        let state = gen_random_starting_state();
        let mut manager = GameManager::new(state.clone(), wrap_micro(&micro1), wrap_micro(&micro2));
        while !manager.state.is_game_over() && manager.state.turn_count < 200 {
            manager.step_game();
        }
        let winner = manager.state.get_outcome();
        let mut total_health = 0.0;
        for (_, robot) in &manager.state.robots {
            total_health += robot.health as f32;
        }
        if winner == Team::Red {
            total_health_1 += total_health;
        } else {
            total_health_2 += total_health;
        }

        let mut manager = GameManager::new(state.clone(), wrap_micro(&micro2), wrap_micro(&micro1));
        while !manager.state.is_game_over() {
            manager.step_game();
        }
        let winner = manager.state.get_outcome();
        let mut total_health = 0.0;
        for (_, robot) in &manager.state.robots {
            total_health += robot.health as f32;
        }
        if winner == Team::Red {
            total_health_2 += total_health;
        } else {
            total_health_1 += total_health;
        }
    }

    total_health_1 / (total_health_1 + total_health_2)
}
