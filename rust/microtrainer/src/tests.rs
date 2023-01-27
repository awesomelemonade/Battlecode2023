core!();

use super::*;

#[test]
fn test_board_new() {
    let mut board = Board::new(5, 5);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (1, 3));
    expect!(
        board,
        r#"
        Board {
            round_num: 0,
            width: 5,
            height: 5,
            robots: Robots {
                id_generator: RobotIdGenerator {
                    next_id: 1,
                },
                robot_turn_order: [
                    0,
                ],
                robots_by_id: {
                    0: Robot {
                        id: 0,
                        position: Position {
                            x: 1,
                            y: 3,
                        },
                        move_cooldown: 2,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Red,
                        health: 200,
                    },
                },
                robots_by_position: Grid {
                    width: 5,
                    height: 5,
                    data: [
                        [
                            None,
                            None,
                            None,
                            None,
                            None,
                        ],
                        [
                            None,
                            None,
                            None,
                            Some(
                                0,
                            ),
                            None,
                        ],
                        [
                            None,
                            None,
                            None,
                            None,
                            None,
                        ],
                        [
                            None,
                            None,
                            None,
                            None,
                            None,
                        ],
                        [
                            None,
                            None,
                            None,
                            None,
                            None,
                        ],
                    ],
                },
            },
        }"#
    );
}

#[test]
fn test_board_step() {
    let mut board = Board::new(3, 3);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 1));
    board.step(&mut |mut controller| {
        controller.move_exn(Direction::North);
        controller.attack_exn((0, 0));
    });

    expect!(
        board,
        r#"
        Board {
            round_num: 1,
            width: 3,
            height: 3,
            robots: Robots {
                id_generator: RobotIdGenerator {
                    next_id: 1,
                },
                robot_turn_order: [
                    0,
                ],
                robots_by_id: {
                    0: Robot {
                        id: 0,
                        position: Position {
                            x: 0,
                            y: 2,
                        },
                        move_cooldown: 12,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Red,
                        health: 200,
                    },
                },
                robots_by_position: Grid {
                    width: 3,
                    height: 3,
                    data: [
                        [
                            None,
                            None,
                            Some(
                                0,
                            ),
                        ],
                        [
                            None,
                            None,
                            None,
                        ],
                        [
                            None,
                            None,
                            None,
                        ],
                    ],
                },
            },
        }"#
    )
}

#[test]
fn test_robot_cooldowns() {
    let mut board = Board::new(3, 3);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 1));
    board.step(&mut |mut controller| {
        controller.move_exn(Direction::North);
        controller.attack_exn((0, 0));
        let robot = controller.current_robot();
        expect!(robot.move_cooldown(), "22");
        expect!(robot.action_cooldown(), "10");
        expect!(robot.is_move_ready(), "false");
        expect!(robot.is_action_ready(), "false");
    });
}

fn attack_controller(mut controller: RobotController) {
    let team = controller.current_robot().team();
    if let Some(enemy_position) = controller
        .sense_nearby_robots_in_vision()
        .iter()
        .filter(|r| r.team() != team)
        .map(|r| r.position())
        .next()
    {
        controller.attack_exn(enemy_position);
    }
}

#[test]
fn test_robot_attack_friendly() {
    let mut board = Board::new(1, 2);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 0));
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 1));
    board.step(&mut attack_controller);
    expect!(
        board,
        r#"
        Board {
            round_num: 1,
            width: 1,
            height: 2,
            robots: Robots {
                id_generator: RobotIdGenerator {
                    next_id: 2,
                },
                robot_turn_order: [
                    0,
                    1,
                ],
                robots_by_id: {
                    0: Robot {
                        id: 0,
                        position: Position {
                            x: 0,
                            y: 0,
                        },
                        move_cooldown: 0,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Red,
                        health: 200,
                    },
                    1: Robot {
                        id: 1,
                        position: Position {
                            x: 0,
                            y: 1,
                        },
                        move_cooldown: 0,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Red,
                        health: 200,
                    },
                },
                robots_by_position: Grid {
                    width: 1,
                    height: 2,
                    data: [
                        [
                            Some(
                                0,
                            ),
                            Some(
                                1,
                            ),
                        ],
                    ],
                },
            },
        }"#
    )
}

#[test]
fn test_robot_attack_enemy() {
    let mut board = Board::new(1, 2);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 0));
    board
        .robots_mut()
        .spawn_robot(Team::Blue, RobotKind::Launcher, 2, (0, 1));
    board.step(&mut attack_controller);
    expect!(
        board,
        r#"
        Board {
            round_num: 1,
            width: 1,
            height: 2,
            robots: Robots {
                id_generator: RobotIdGenerator {
                    next_id: 2,
                },
                robot_turn_order: [
                    0,
                    1,
                ],
                robots_by_id: {
                    0: Robot {
                        id: 0,
                        position: Position {
                            x: 0,
                            y: 0,
                        },
                        move_cooldown: 0,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Red,
                        health: 180,
                    },
                    1: Robot {
                        id: 1,
                        position: Position {
                            x: 0,
                            y: 1,
                        },
                        move_cooldown: 0,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Blue,
                        health: 180,
                    },
                },
                robots_by_position: Grid {
                    width: 1,
                    height: 2,
                    data: [
                        [
                            Some(
                                0,
                            ),
                            Some(
                                1,
                            ),
                        ],
                    ],
                },
            },
        }"#
    );
}

#[test]
fn test_sense_robots_in_vision() {
    let mut board = Board::new(1, 100);
    board
        .robots_mut()
        .spawn_robot(Team::Red, RobotKind::Launcher, 2, (0, 0));
    board
        .robots_mut()
        .spawn_robot(Team::Blue, RobotKind::Launcher, 2, (0, 1));
    board
        .robots_mut()
        .spawn_robot(Team::Blue, RobotKind::Launcher, 2, (0, 99));
    board.step(&mut |controller| {
        if controller.current_position() == (0, 0).into() {
            expect!(
                controller.sense_nearby_robots_in_vision(),
                r#"
                [
                    Robot {
                        id: 1,
                        position: Position {
                            x: 0,
                            y: 1,
                        },
                        move_cooldown: 2,
                        action_cooldown: 0,
                        kind: Launcher,
                        team: Blue,
                        health: 200,
                    },
                ]"#
            );
        }
    });
}
