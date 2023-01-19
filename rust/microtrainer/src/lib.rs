use micro::get_scored_micro;
use raylib::prelude::*;
use std::{thread, time};

mod game;
mod micro;
mod arena;
mod simulated_annealing;
use game::GameState;
use game::Team;

use crate::game::GameManager;

fn draw(rl: &mut RaylibHandle, thread: &RaylibThread, state: &GameState) {
    let cell_size: i32 = 20;
    rl.set_window_size(
        cell_size * state.width as i32,
        cell_size * state.height as i32,
    );

    let mut d = rl.begin_drawing(&thread);
    d.clear_background(Color::WHITE);
    
    // Draw grid
    for i in 1..state.width {
        d.draw_line(
            cell_size*i as i32,
            0,
            cell_size*i as i32,
            cell_size*state.height as i32,
            Color::GRAY,
        );
    }
    for i in 1..state.height {
        d.draw_line(
            0,
            cell_size*i as i32,
            cell_size*state.width as i32,
            cell_size*i as i32,
            Color::GRAY,
        );
    }

    // Draw robots
    for (_, robot) in &state.robots {
        let color = if robot.team == Team::Red {Color::RED} else {Color::BLUE};
        d.draw_circle(
            robot.pos.x as i32 * cell_size + cell_size / 2,
            (state.height-1 - robot.pos.y as i32) * cell_size + cell_size / 2,
            cell_size as f32 / 3.0,
            color,
        );
    }

    // Draw attack vectors
    for (team, pos1, pos2) in &state.attack_vectors {
        let color = if *team == Team::Red {Color::RED} else {Color::BLUE};
        d.draw_line(
            pos1.x * cell_size + cell_size / 2,
            (state.height-1 - pos1.y) * cell_size + cell_size / 2,
            pos2.x * cell_size + cell_size / 2,
            (state.height-1 - pos2.y) * cell_size + cell_size / 2,
            color
        );
    }
}

pub fn run() {
    // raylib::core::logging::set_trace_log(raylib::ffi::TraceLogLevel::LOG_ERROR);
    // let (mut rl, thread) = raylib::init()
    //     .title("Micro Trainer")
    //     .build();

    simulated_annealing::train(0.025, 1000);
    // let state = arena::gen_random_starting_state();
    // let scored_micro = get_scored_micro([-1.0, 1.0, 0.0, -1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]);
    // let mut manager = GameManager::new(state, crate::arena::wrap_micro(micro::sprint_micro), crate::arena::wrap_micro(scored_micro));
     
    // while !rl.window_should_close() && !manager.state.is_game_over() {
    //     draw(&mut rl, &thread, &manager.state);
    //     manager.step_game();
    //     thread::sleep(time::Duration::from_millis(5));
    // }
}
