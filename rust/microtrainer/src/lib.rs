#![allow(incomplete_features)]
#![feature(return_position_impl_trait_in_trait)]
macro_rules! core {
    () => {
        #[allow(unused_imports)]
        use crate::imports::*;
    };
}

core!();

use bot::{Bot, BotProvider};
use raylib::prelude::*;
use std::{thread, time};

mod arena;
mod bot;
mod game;
mod micro;
mod simulated_annealing;
use game::{new_game_manager_with_red_and_blue, Board};

mod direction;
use direction::*;
mod position;
use position::*;
mod robot;
use robot::*;
mod grid;

mod imports;

fn draw(rl: &mut RaylibHandle, thread: &RaylibThread, state: &Board) {
    let cell_size: i32 = 20;
    rl.set_window_size(
        cell_size * state.width() as i32,
        cell_size * state.height() as i32,
    );

    let mut d = rl.begin_drawing(&thread);
    d.clear_background(Color::WHITE);

    // Draw grid
    for i in 1..state.width() {
        d.draw_line(
            cell_size * i as i32,
            0,
            cell_size * i as i32,
            cell_size * state.height() as i32,
            Color::GRAY,
        );
    }
    for i in 1..state.height() {
        d.draw_line(
            0,
            cell_size * i as i32,
            cell_size * state.width() as i32,
            cell_size * i as i32,
            Color::GRAY,
        );
    }

    // Draw robots
    for robot in state.robots().iter() {
        let color = match robot.team() {
            Team::Red => Color::RED,
            Team::Blue => Color::BLUE,
        };
        d.draw_circle(
            robot.position().x as i32 * cell_size + cell_size / 2,
            (state.height() as i32 - 1 - robot.position().y as i32) * cell_size + cell_size / 2,
            cell_size as f32 / 3.0,
            color,
        );
    }

    // Draw attack vectors
    // TODO
    // for (team, pos1, pos2) in &state.attack_vectors {
    //     let color = if *team == Team::Red {
    //         Color::RED
    //     } else {
    //         Color::BLUE
    //     };
    //     d.draw_line(
    //         pos1.x * cell_size + cell_size / 2,
    //         (state.height - 1 - pos1.y) * cell_size + cell_size / 2,
    //         pos2.x * cell_size + cell_size / 2,
    //         (state.height - 1 - pos2.y) * cell_size + cell_size / 2,
    //         color,
    //     );
    // }
}

fn show_game<F1: BotProvider<BotType = impl Bot>, F2: BotProvider<BotType = impl Bot>>(
    red_bot: &F1,
    blue_bot: &F2,
) {
    raylib::core::logging::set_trace_log(raylib::ffi::TraceLogLevel::LOG_ERROR);
    let (mut rl, thread) = raylib::init().title("Micro Trainer").build();

    let state = arena::gen_random_starting_board();
    let mut manager = new_game_manager_with_red_and_blue(state, red_bot, blue_bot);

    while !rl.window_should_close() && !manager.board().is_game_over() {
        draw(&mut rl, &thread, manager.board());
        manager.step().unwrap();
        thread::sleep(time::Duration::from_millis(100)); // TODO: probably need to adjust time
    }
    thread::sleep(time::Duration::from_millis(1000));
}

pub fn run() {
    let winrate = arena::get_score(
        &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
        &arena::wrap_micro(micro::random::RandomMicro::provider()),
        500,
    );
    println!("winrate = {}", winrate);
    show_game(
        &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
        &arena::wrap_micro(micro::random::RandomMicro::provider()),
        // arena::wrap_micro_persistant(micro::globalelite::micro()),
        // arena::wrap_micro(micro::sprint1::micro()),
    );
    // simulated_annealing::train(0.025, 1000);
}
