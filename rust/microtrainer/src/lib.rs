macro_rules! core {
    () => {
        #[allow(unused_imports)]
        use crate::imports::*;
    };
}

core!();

use raylib::prelude::*;
use std::{thread, time};

mod arena;
mod game;
mod micro;
mod simulated_annealing;
use game::Board;

mod direction;
use direction::*;
mod position;
use position::*;
mod robot;
use robot::*;
mod grid;
use grid::*;

mod imports;

use crate::game::GameManager;

fn draw(rl: &mut RaylibHandle, thread: &RaylibThread, state: &Board) {
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
            cell_size * i as i32,
            0,
            cell_size * i as i32,
            cell_size * state.height as i32,
            Color::GRAY,
        );
    }
    for i in 1..state.height {
        d.draw_line(
            0,
            cell_size * i as i32,
            cell_size * state.width as i32,
            cell_size * i as i32,
            Color::GRAY,
        );
    }

    // Draw robots
    for (_, robot) in &state.robots {
        let color = if robot.team == Team::Red {
            Color::RED
        } else {
            Color::BLUE
        };
        d.draw_circle(
            robot.pos.x as i32 * cell_size + cell_size / 2,
            (state.height - 1 - robot.pos.y as i32) * cell_size + cell_size / 2,
            cell_size as f32 / 3.0,
            color,
        );
    }

    // Draw attack vectors
    for (team, pos1, pos2) in &state.attack_vectors {
        let color = if *team == Team::Red {
            Color::RED
        } else {
            Color::BLUE
        };
        d.draw_line(
            pos1.x * cell_size + cell_size / 2,
            (state.height - 1 - pos1.y) * cell_size + cell_size / 2,
            pos2.x * cell_size + cell_size / 2,
            (state.height - 1 - pos2.y) * cell_size + cell_size / 2,
            color,
        );
    }
}

fn show_game<F1, F2>(micro1: F1, micro2: F2)
where
    F1: Fn(&mut Board, u32) -> (),
    F2: Fn(&mut Board, u32) -> (),
{
    raylib::core::logging::set_trace_log(raylib::ffi::TraceLogLevel::LOG_ERROR);
    let (mut rl, thread) = raylib::init().title("Micro Trainer").build();

    let state = arena::gen_random_starting_state();
    let mut manager = GameManager::new(
        state,
        crate::arena::wrap_micro(micro1),
        crate::arena::wrap_micro(micro2),
    );

    while !rl.window_should_close() && !manager.state.is_game_over() {
        draw(&mut rl, &thread, &manager.state);
        manager.step_game();
        thread::sleep(time::Duration::from_millis(5));
    }
}

pub fn run() {
    // let scored_micro = get_scored_micro([-1.0, 1.0, 0.0, -1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]);
    // show_game(micro::sprint_micro, scored_micro);
    simulated_annealing::train(0.025, 1000);
}
