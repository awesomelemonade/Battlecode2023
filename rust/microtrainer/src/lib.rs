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

use bot::Bot;
use itertools::Itertools;
mod arena;
mod bot;
mod game;
mod micro;
mod simulated_annealing;

mod direction;
use direction::*;
mod position;
use position::*;
mod robot;

use crate::plot::histogram;
mod grid;

mod imports;

mod plot;

#[cfg(test)]
mod tests;

#[cfg(feature = "visualizer")]
mod visualizer;

#[cfg(feature = "visualizer")]
pub fn show_game() -> OrError<()> {
    visualizer::show_game(
        // &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
        micro::sprint2::Sprint2Micro::provider(),
        &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
    )?;
    Ok(())
}

#[cfg(not(feature = "visualizer"))]
pub fn show_game() -> OrError<()> {
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

    show_game()?;

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
