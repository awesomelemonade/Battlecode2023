use rand::Rng;

use crate::{arena, bot::Bot, micro};

fn get_energy(parameters: [f32; 12]) -> f32 {
    let parameters = parameters.map(|x| x.powi(3));
    let mut scores = Vec::new();
    let mut num_iterations = 0;
    loop {
        let current_scores = arena::get_scores(
            &arena::wrap_micro(&micro::scored::ScoredMicro::provider(&parameters)),
            &arena::wrap_micro(micro::sprint1::Sprint1Micro::provider()),
            500,
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
    1.0 - mean
}

pub fn train(initial_temperature: f32, num_steps: u32) {
    let mut rng = rand::thread_rng();
    // let mut current_parameters = [
    //     -1.0, 1.0, 0.0, -1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    // ];
    // action ready, within 9 of enemy? (0 or 1)
    // action ready, within 10-16 of enemy? (0 or 1)
    // action ready, >17 of enemy? (0 or 1)
    let mut current_parameters = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
    let mut current_energy = get_energy(current_parameters);
    println!("Parameters: {:?}", current_parameters);
    println!("Score: {}", 1.0 - current_energy);
    for k in 0..num_steps {
        let temperature = initial_temperature * (1.0 - ((k + 1) as f32) / num_steps as f32);
        println!("Temperature: {}", temperature);
        let mut new_parameters = current_parameters.clone();

        let index = rng.gen_range(0..6);
        new_parameters[index] += rng.gen_range(-0.5..0.5);

        let new_energy = get_energy(new_parameters);

        let mut acceptance_probability = 1.0;
        if new_energy > current_energy {
            acceptance_probability = (-(new_energy - current_energy) / temperature).exp();
        }

        if rng.gen_range(0.0..1.0) <= acceptance_probability {
            current_parameters = new_parameters;
            current_energy = new_energy;
        }
        println!("Parameters: {:?}", current_parameters);
        println!("Score: {}", 1.0 - current_energy);
        println!("");
    }
}
