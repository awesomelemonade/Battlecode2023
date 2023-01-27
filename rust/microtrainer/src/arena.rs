use crate::{
    bot::{Bot, BotProvider},
    game::{new_game_manager_with_red_and_blue, Board},
    robot::{Robot, RobotController, RobotKind, Team},
    Direction,
};
use rand::Rng;
use rayon::prelude::{IntoParallelIterator, ParallelIterator};

pub fn try_attack(controller: &mut RobotController) {
    let position = controller.current_position();
    let team = controller.current_robot().team();
    if let Some(target) = controller
        .sense_nearby_robots_in_vision()
        .iter()
        .filter(|r| r.team() != team && controller.can_attack(r.position()))
        .min_by_key(|r| (r.health(), r.position().distance_squared(position)))
    {
        controller.attack_exn(target.position());
    }
}

pub fn wrap_micro<'a, T: Bot>(
    micro: &'a impl BotProvider<BotType = T>,
) -> impl BotProvider<BotType = impl Bot> + 'a {
    move |robot: &Robot| {
        let mut bot = micro.get(robot);
        move |controller: &mut RobotController| {
            try_attack(controller);
            if let Some(closest_enemy) = controller.get_nearest_enemy_omnipotent() {
                let position = controller.current_position();
                let dist = position.distance_squared(closest_enemy.position());
                if dist > 20 {
                    // omnipotent move
                    let target = closest_enemy.position();
                    if let Some(&move_direction) =
                        Direction::attempt_order(position.direction_to(target))
                            .iter()
                            .filter(|&&dir| controller.can_move(dir))
                            .next()
                    {
                        controller.move_exn(move_direction);
                    }
                } else {
                    bot.step(controller);
                }
            }
            try_attack(controller);
        }
    }
}

pub fn gen_random_starting_board() -> Board {
    let mut rng = rand::thread_rng();

    let mut board = Board::new(32, 32);
    let num_robots = rng.gen_range(3..=15);
    for _ in 0..num_robots {
        let mut x = rng.gen_range(0..board.width());
        let mut y = rng.gen_range(0..board.height() / 3);
        while board.robots().is_occupied((x, y)) {
            x = rng.gen_range(0..board.width());
            y = rng.gen_range(0..board.height() / 3);
        }
        board.robots_mut().spawn_robot(
            Team::Red,
            RobotKind::Launcher,
            rng.gen_range(0..20),
            (x, y),
        );
    }
    for _ in 0..num_robots {
        let mut x = rng.gen_range(0..board.width());
        let mut y = board.height() - 1 - rng.gen_range(0..board.height() / 3);
        while board.robots().is_occupied((x, y)) {
            x = rng.gen_range(0..board.width());
            y = board.height() - 1 - rng.gen_range(0..board.height() / 3);
        }
        board.robots_mut().spawn_robot(
            Team::Blue,
            RobotKind::Launcher,
            rng.gen_range(0..20),
            (x, y),
        );
    }

    board
}

pub fn get_score<F1, F2, T1, T2>(bot_provider1: &F1, bot_provider2: &F2, samples: u32) -> f32
where
    F1: BotProvider<BotType = T1> + Send + Sync,
    F2: BotProvider<BotType = T2> + Send + Sync,
    T1: Bot,
    T2: Bot,
{
    let total_healths: Vec<_> = (0..samples)
        .into_par_iter()
        .map(|_| {
            let mut total_health_1 = 0.0;
            let mut total_health_2 = 0.0;
            let starting_board = gen_random_starting_board();
            let mut manager = new_game_manager_with_red_and_blue(
                starting_board.clone(),
                bot_provider1,
                bot_provider2,
            );
            manager.step_until_game_over(200);
            for robot in manager.board().robots().iter() {
                if robot.team() == Team::Red {
                    total_health_1 += robot.health() as f32;
                } else {
                    total_health_2 += robot.health() as f32;
                }
            }
            let mut manager = new_game_manager_with_red_and_blue(
                starting_board.clone(),
                bot_provider2,
                bot_provider1,
            );
            manager.step_until_game_over(200);
            for robot in manager.board().robots().iter() {
                if robot.team() == Team::Blue {
                    total_health_1 += robot.health() as f32;
                } else {
                    total_health_2 += robot.health() as f32;
                }
            }
            (total_health_1, total_health_2)
        })
        .collect();
    let (total_health_1, total_health_2): (Vec<_>, Vec<_>) = total_healths.into_iter().unzip();
    let total_health_1: f32 = total_health_1.iter().sum();
    let total_health_2: f32 = total_health_2.iter().sum();
    total_health_1 / (total_health_1 + total_health_2)
}
