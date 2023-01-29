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
mod bot_ext;
mod game;
mod micro;
mod simulated_annealing;

mod direction;
use direction::*;
mod position;
use position::*;
mod robot;

use crate::{micro::scored2::Scored2Parameters, plot::histogram};
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
    // let saved_params2 = [
    //     3.8238728f32,
    //     -13.151678,
    //     -5.840171,
    //     3.828451,
    //     4.342893,
    //     1.4151905,
    //     -33.45562,
    //     1.8735235,
    //     -26.521437,
    //     20.977238,
    //     -27.63979,
    //     25.943026,
    // ]
    // .map(|x| x.signum() * (x.abs().exp() - 1.0f32)); // 0.03 vs sprint2
    // let parameters = saved_params2;
    // let scores = arena::get_scores(
    //     &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
    //     micro::sprint2::Sprint2Micro::provider(),
    //     // &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
    //     // &arena::wrap_micro(micro::random::RandomMicro::provider()),
    //     10000,
    // );
    // let scores_f64 = scores.iter().map(|&x| x as f64).collect_vec();
    // histogram(scores_f64.as_slice(), 0.01)?;

    // let average_score = scores.iter().sum::<f32>() / scores.len() as f32;
    // println!("score = {}", average_score);

    // show_game()?;

    // TRAINING
    // let mut parameters = Scored2Parameters::default();

    // let mut best_parameters = parameters.clone();
    // let mut best_score = {
    //     let scores = arena::get_scores(
    //         &micro::scored2::ScoredMicro2::provider(&best_parameters),
    //         micro::sprint2::Sprint2Micro::provider(),
    //         50000,
    //     );
    //     scores.iter().sum::<f32>() / scores.len() as f32
    // };
    // for i in 0..100000 {
    //     parameters = simulated_annealing::train(&parameters, 0.015, 10000);
    //     let score = {
    //         let scores = arena::get_scores(
    //             &micro::scored2::ScoredMicro2::provider(&parameters),
    //             micro::sprint2::Sprint2Micro::provider(),
    //             50000,
    //         );
    //         scores.iter().sum::<f32>() / scores.len() as f32
    //     };
    //     println!(
    //         "Score = {}, Parameters = {:?}",
    //         score,
    //         parameters.squashed_parameters()
    //     );
    //     if score > best_score {
    //         best_score = score;
    //         best_parameters = parameters.clone();
    //     }
    //     println!(
    //         "i = {}, Best Score = {}, Best Parameters = {:?}",
    //         i,
    //         best_score,
    //         best_parameters.squashed_parameters()
    //     );
    // }
    Ok(())
}
