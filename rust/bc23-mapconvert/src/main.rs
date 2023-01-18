use anyhow::anyhow as Error;
use anyhow::Error;
use anyhow::Ok;
use anyhow::Result as OrError;
use itertools::Itertools;
use rand::Rng;

use std::fs;
use std::io::Write;
use std::iter;

#[allow(unused_imports, non_snake_case)]
#[path = "../target/flatbuffers/battlecode22_generated.rs"]
pub mod battlecode22_generated;
pub use battlecode22_generated::battlecode::schema as schema22;

#[allow(unused_imports, non_snake_case)]
#[path = "../target/flatbuffers/battlecode23_generated.rs"]
pub mod battlecode23_generated;
pub use battlecode23_generated::battlecode::schema as schema23;

mod bc22;
use bc22::*;

mod bc23;
use bc23::*;

fn delete_previous_generated(directory: &str) -> OrError<()> {
    let paths: Result<Vec<_>, _> = fs::read_dir(directory)?.map(|p| Ok(p?.path())).collect();
    let paths = paths?;
    for path in paths {
        if path
            .file_name()
            .unwrap()
            .to_str()
            .unwrap()
            .contains("generated_")
        {
            println!("Removing: {}", path.display());
            if path.is_dir() {
                fs::remove_dir_all(path)?
            } else {
                fs::remove_file(path)?;
            }
        }
    }
    Ok(())
}

fn main() -> OrError<()> {
    let maps23_directory = "./maps23/";
    delete_previous_generated(maps23_directory)?;

    let maps22_directory = "./maps22/";
    let paths: Result<Vec<_>, _> = fs::read_dir(maps22_directory)?
        .map(|p| Ok(p?.path()))
        .collect();
    let paths = paths?;
    let maps22_bytes = paths.iter().map(|path| fs::read(path)).collect_vec();
    let maps22_bytes: Result<Vec<_>, _> = maps22_bytes.into_iter().collect();
    let maps22_bytes = maps22_bytes?;

    for (path, bc22_bytes) in paths.iter().zip_eq(maps22_bytes.iter()) {
        let name = path
            .file_name()
            .unwrap()
            .to_str()
            .unwrap()
            .replace(".map22", "");
        if name.contains("progress") || name.contains("tower") {
            // somehow broken
            continue;
        }
        print!("Generating from {}... ", name);
        std::io::stdout().flush().unwrap();
        let generated_name = format!("generated_{}", name);
        let bc22_map: BC22Map = bc22_bytes.as_slice().try_into()?;
        fs::create_dir_all(&format!("{}generated_{}/", maps23_directory, name))?;
        for i in 0..40 {
            for rng in (2..10).chain(95..100) {
                let bc23_map = convert(&bc22_map, i, (rng as f64) * 0.01);
                let bc23_bytes: Vec<u8> = bc23_map.into();
                fs::write(
                    &format!(
                        "{}generated_{}/{}{}_{}.map23",
                        maps23_directory, name, generated_name, i, rng
                    ),
                    bc23_bytes.as_slice(),
                )?;
            }
        }
        println!("Done: {}", generated_name);
    }

    // let bc22_bytes = fs::read("./maps22/charge.map22")?;
    // let bc22_map: BC22Map = bc22_bytes.as_slice().try_into()?;
    // let bc23_map = convert(bc22_map);
    // let bc23_bytes: Vec<u8> = bc23_map.into();
    // fs::write("./maps23/generated_charge.map23", bc23_bytes.as_slice())?;

    // let bytes = fs::read("./maps23/AllElements.map23")?;
    // let game_map = flatbuffers::root::<schema23::GameMap>(bytes.as_slice())?;
    // let max_corner = game_map.maxCorner().unwrap();
    // let min_corner = game_map.minCorner().unwrap();
    // let width = max_corner.x() - min_corner.x();
    // let height = max_corner.y() - min_corner.y();
    // let resources = game_map.resources().unwrap();

    // for j in (0..height).rev() {
    //     for i in 0..width {
    //         // let r = resources[j as usize][i as usize];
    //         let r = resources.get((i * width + j) as usize);
    //         if r == 0 {
    //             print!(" ");
    //         } else {
    //             print!("{}", r);
    //         }
    //     }
    //     println!();
    // }
    Ok(())
}

fn new_2d<T: Copy>(width: u32, height: u32, x: T) -> Vec<Vec<T>> {
    iter::repeat_with(|| iter::repeat(x).take(width as usize).collect_vec())
        .take(height as usize)
        .collect_vec()
}

fn generate_resources(symmetry: i32, lead: &Vec<Vec<i32>>, rng: f64) -> Vec<Vec<i32>> {
    let mut resources = lead
        .iter()
        .map(|r| {
            r.iter()
                .map(|lead_amount| if *lead_amount > 0 { 0 } else { 0 })
                .collect_vec()
        })
        .collect_vec();
    let height = lead.len() as i32;
    let width = lead[0].len() as i32;

    let symmetric_loc = |(y, x): (i32, i32)| match symmetry {
        0 => (height - y - 1, width - x - 1),
        1 => (height - y - 1, x),
        2 => (y, width - x - 1),
        _ => panic!("Unknown Symmetry"),
    };

    for j in 0..height {
        for i in 0..width {
            if lead[j as usize][i as usize] > 0 {
                // random well type
                let well_type = if rand::thread_rng().gen_bool(0.5) {
                    1
                } else {
                    2
                };
                if rand::thread_rng().gen_bool(1.0 - rng) {
                    continue;
                }

                let (sym_y, sym_x) = symmetric_loc((j, i));
                resources[j as usize][i as usize] = well_type;
                resources[sym_y as usize][sym_x as usize] = well_type;
            }
        }
    }

    resources
}

fn generate_islands(symmetry: i32, walls: &Vec<Vec<bool>>) -> Vec<Vec<i32>> {
    let mut islands = walls
        .iter()
        .map(|r| r.iter().map(|_| 0).collect_vec())
        .collect_vec();

    if walls.iter().flatten().all(|x| *x) {
        return islands;
    }
    let height = walls.len() as i32;
    let width = walls[0].len() as i32;

    let symmetric_loc = |(y, x): (i32, i32)| match symmetry {
        0 => (height - y - 1, width - x - 1),
        1 => (height - y - 1, x),
        2 => (y, width - x - 1),
        _ => panic!("Unknown Symmetry"),
    };

    let mut counter = 1;
    let mut generate_island = || {
        let mut y = rand::thread_rng().gen_range(1..height - 1);
        let mut x = rand::thread_rng().gen_range(1..width - 1);
        while walls[y as usize][x as usize] {
            y = rand::thread_rng().gen_range(0..height);
            x = rand::thread_rng().gen_range(0..width);
        }
        let (sym_y, sym_x) = symmetric_loc((y, x));
        islands[y as usize][x as usize] = counter;
        islands[sym_y as usize][sym_x as usize] = counter + 1;
        counter += 2;
    };

    generate_island();
    generate_island();
    generate_island();

    islands
}

fn convert(
    BC22Map {
        name,
        width,
        height,
        symmetry,
        random_seed,
        bodies,
        rubble,
        lead,
    }: &BC22Map,
    storm_rubble_index: usize,
    rng: f64,
) -> BC23Map {
    let width = *width;
    let height = *height;
    let symmetry = *symmetry;
    let random_seed = *random_seed;
    let existing_rubble_values = rubble
        .iter()
        .flatten()
        .unique()
        .sorted()
        .rev()
        .copied()
        .collect_vec();
    let storm_rubble = existing_rubble_values
        .get(storm_rubble_index)
        .copied()
        .unwrap_or(0);
    // TODO: for when clouds are relevant after sprint 1
    // let cloud_rubble = existing_rubble_values
    //     .get(storm_rubble_index + 1)
    //     .copied()
    //     .unwrap_or(0);
    let mut walls = rubble
        .iter()
        .map(|r| r.iter().map(|x| *x >= storm_rubble).collect_vec())
        .collect_vec();
    let mut islands = generate_islands(symmetry, &walls);
    let mut resources = generate_resources(symmetry, &lead, rng);

    // ensure we don't have wells, islands, and walls on HQ
    for hq in bodies {
        let (x, y) = hq.location;
        let x = x as usize;
        let y = y as usize;
        walls[y][x] = false;
        islands[y][x] = 0;
        resources[y][x] = 0;
    }

    // don't have walls and islands on resources
    for j in 0..height {
        for i in 0..width {
            let j = j as usize;
            let i = i as usize;
            if resources[j][i] > 0 {
                walls[j][i] = false;
                islands[j][i] = 0;
            }
        }
    }

    // re-id the islands to be continuous
    let mut next_id = 1;
    for j in 0..height {
        for i in 0..width {
            let j = j as usize;
            let i = i as usize;
            if islands[j][i] > 0 {
                islands[j][i] = next_id;
                next_id += 1;
            }
        }
    }

    BC23Map {
        name: format!("generated_{}", name),
        width,
        height,
        symmetry,
        random_seed,
        bodies: bodies // converting archons to headquarters
            .into_iter()
            .map(|body| BC23Body {
                id: body.id,
                team_id: body.team_id,
                body_type: schema23::BodyType::HEADQUARTERS,
                location: body.location,
            })
            .collect_vec(),
        walls,
        clouds: new_2d(width, height, false),
        currents: new_2d(width, height, 0),
        // islands: new_2d(width, height, 0),
        islands,
        // resources: new_2d(width, height, 0),
        resources,
    }
}
