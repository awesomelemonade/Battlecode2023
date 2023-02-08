core!();

use raylib::prelude::*;

use crate::{
    arena,
    bot::{Bot, BotProvider},
    game::{new_game_manager_with_red_and_blue, Board},
    robot::Team,
};

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

pub fn show_game<F1: BotProvider<BotType = impl Bot>, F2: BotProvider<BotType = impl Bot>>(
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
