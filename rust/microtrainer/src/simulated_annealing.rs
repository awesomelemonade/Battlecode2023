use rand::Rng;

use crate::{
    arena,
    bot::Bot,
    micro::{self, scored2::Scored2Parameters},
};

// we want to minimize energy
fn get_energy(parameters: &Scored2Parameters) -> f32 {
    let mut scores = Vec::new();
    let mut num_iterations = 0;
    loop {
        let current_scores = arena::get_scores(
            &micro::scored2::ScoredMicro2::provider(&parameters),
            micro::sprint2::Sprint2Micro::provider(),
            100,
        );
        // let average_score = current_scores.iter().sum::<f32>() / scores.len() as f32;
        // scores.push(average_score);
        scores.extend(current_scores);
        num_iterations += 1;
        if num_iterations >= 2 {
            let stddev = statistical::standard_deviation(scores.as_slice(), None);
            let stderr = stddev / (scores.len() as f32).sqrt();
            if stderr < 0.005 {
                break;
            }
        }
    }
    let mean = statistical::mean(scores.as_slice());
    -mean // we want to maximize score
}

pub fn train(
    starting_parameters: &Scored2Parameters,
    initial_temperature: f32,
    num_steps: u32,
) -> Scored2Parameters {
    let mut rng = rand::thread_rng();
    let mut current_parameters = starting_parameters.clone();
    let mut current_energy = get_energy(&current_parameters);
    for k in 0..num_steps {
        let temperature = initial_temperature * (1.0 - ((k + 1) as f32) / num_steps as f32);
        let new_parameters = current_parameters.random_neighbor();
        let new_energy = get_energy(&new_parameters);

        let mut acceptance_probability = 1.0;
        if new_energy > current_energy {
            acceptance_probability = (-(new_energy - current_energy) / temperature).exp();
        }

        if rng.gen_range(0.0..1.0) <= acceptance_probability {
            current_parameters = new_parameters;
            current_energy = new_energy;
        }

        if k % 100 == 0 {
            println!("Score: {}, Temperature: {}", -current_energy, temperature);
            println!("Parameters: {:?}", current_parameters);
        }
    }
    current_parameters
}
