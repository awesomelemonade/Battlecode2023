#![allow(incomplete_features)]
#![feature(return_position_impl_trait_in_trait)]
#![feature(let_chains)]
macro_rules! core {
    () => {
        #[allow(unused_imports)]
        use crate::imports::*;
    };
}

core!();

use bot::{Bot, BotProvider};
use itertools::Itertools;
use raylib::prelude::*;
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

use crate::plot::histogram;
mod grid;

mod imports;

mod plot;

#[cfg(test)]
mod tests;

fn draw(rl: &mut RaylibHandle, thread: &RaylibThread, state: &Board, idx: usize) {
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

        // health bar
        let hp = robot.health() as f32 / robot.kind().starting_health() as f32;
        d.draw_rectangle(
            robot.position().x as i32 * cell_size,
            (state.height() as i32 - 1 - robot.position().y as i32) * cell_size + 4 * cell_size / 5,
            (hp * (cell_size as f32)) as i32,
            cell_size / 10,
            Color::PURPLE,
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

    d.draw_text(
        &idx.to_string(),
        (state.width() / 5) as i32,
        (state.height() / 10) as i32,
        30,
        Color::BLACK,
    );
}

fn show_game<F1: BotProvider<BotType = impl Bot>, F2: BotProvider<BotType = impl Bot>>(
    red_bot: &F1,
    blue_bot: &F2,
) -> OrError<()> {
    raylib::core::logging::set_trace_log(raylib::ffi::TraceLogLevel::LOG_ERROR);
    let (mut rl, thread) = raylib::init().title("Micro Trainer").build();

    let state = arena::gen_random_starting_board();
    let mut manager = new_game_manager_with_red_and_blue(state, red_bot, blue_bot);
    let mut boards = Vec::new();
    boards.push(manager.board().clone());

    while !rl.window_should_close() && !manager.board().is_game_over() {
        draw(&mut rl, &thread, boards.last().unwrap(), boards.len() - 1);
        manager.substep()?;
        boards.push(manager.board().clone());
    }

    let mut idx = boards.len() - 1;
    while !rl.window_should_close() {
        draw(&mut rl, &thread, &boards[idx], idx);
        if let Some(key) = rl.get_key_pressed() {
            let amount = if rl.is_key_down(KeyboardKey::KEY_LEFT_CONTROL) {
                100
            } else if rl.is_key_down(KeyboardKey::KEY_LEFT_SHIFT) {
                10
            } else {
                1
            };
            match key {
                KeyboardKey::KEY_LEFT => {
                    idx = idx.saturating_sub(amount);
                }
                KeyboardKey::KEY_RIGHT => {
                    idx += amount;
                    if idx >= boards.len() {
                        idx = boards.len() - 1;
                    }
                }
                _ => {}
            }
        }
    }
    Ok(())
}

pub fn run() -> OrError<()> {
    // plot::main()
    let saved_params2 = [
        -0.46400547f32,
        -10.105165,
        -46.85412,
        17.811329,
        1.4674165,
        -41.985172,
        -88.470436,
        -5.4255066,
        -35.05129,
        -48.359673,
        -14.175641,
        39.25703,
    ]
    .map(|x| x.signum() * (x.abs().exp() - 1.0f32)); // 0.0821876
    let saved_params = [
        1.3705931f32,
        -7.031256,
        -12.063573,
        1.1359694,
        8.576751,
        -11.193701,
        -7.579562,
        -2.2322478,
        -6.115107,
        -9.84329,
        -3.027422,
        8.143937,
    ]
    .map(|x| x.signum() * x.abs().exp());
    let parameters = saved_params2;
    let scores = arena::get_scores(
        &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
        micro::sprint2::Sprint2Micro::provider(),
        // &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
        // &arena::wrap_micro(micro::random::RandomMicro::provider()),
        10000,
    );
    let scores_f64 = scores.iter().map(|&x| x as f64).collect_vec();
    histogram(scores_f64.as_slice(), 0.01)?;

    let average_score = scores.iter().sum::<f32>() / scores.len() as f32;
    println!("score = {}", average_score);

    // show_game(
    //     // &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
    //     micro::sprint2::Sprint2Micro::provider(),
    //     &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
    // )?;

    // TRAINING
    // let mut parameters = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];

    // let mut best_parameters = parameters.clone();
    // let mut best_score = {
    //     let scores = arena::get_scores(
    //         &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&best_parameters)),
    //         &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
    //         50000,
    //     );
    //     scores.iter().sum::<f32>() / scores.len() as f32
    // };
    // for i in 0..100000 {
    //     parameters = simulated_annealing::train(parameters, 0.025, 1000);
    //     let score = {
    //         let scores = arena::get_scores(
    //             &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
    //             &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
    //             50000,
    //         );
    //         scores.iter().sum::<f32>() / scores.len() as f32
    //     };
    //     if score > best_score {
    //         best_score = score;
    //         best_parameters = parameters;
    //     }
    //     println!(
    //         "i = {}, Best Score = {}, Best Parameters = {:?}",
    //         i, best_score, best_parameters
    //     );
    // }
    Ok(())
}
