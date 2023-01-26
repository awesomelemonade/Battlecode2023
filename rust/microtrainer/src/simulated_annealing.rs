use rand::Rng;

use crate::{
    arena,
    micro::{scored_micro, sprint_micro},
};

fn get_energy(parameters: [f32; 12]) -> f32 {
    let mut winrates = Vec::new();
    let mut num_samples: f32 = 0.0;
    loop {
        let winrate = arena::get_score(&scored_micro(parameters), sprint_micro(), 500);
        winrates.push(winrate);
        num_samples += 1.0;
        if num_samples >= 2.0 {
            let stddev = statistical::standard_deviation(winrates.as_slice(), None);
            let stderr = stddev / num_samples.sqrt();
            if stderr < 0.005 {
                break;
            }
        }
    }
    let mean = statistical::mean(winrates.as_slice());
    1.0 - mean
}

pub fn train(initial_temperature: f32, num_steps: u32) {
    let mut rng = rand::thread_rng();
    let mut current_parameters = [
        -1.0, 1.0, 0.0, -1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    ];
    let mut current_energy = get_energy(current_parameters);
    println!("Parameters: {:?}", current_parameters);
    println!("Winrate: {}", 1.0 - current_energy);
    for k in 0..num_steps {
        let temperature = initial_temperature * (1.0 - ((k + 1) as f32) / num_steps as f32);
        println!("Temperature: {}", temperature);
        let mut new_parameters = [0.0; 12];
        for i in 0..12 {
            new_parameters[i] = current_parameters[i] + rng.gen_range(-0.2..0.2);
        }
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
        println!("Winrate: {}", 1.0 - current_energy);
        println!("");
    }
}
