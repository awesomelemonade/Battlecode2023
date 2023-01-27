use itertools::Either;

use crate::robot::{Robot, RobotController};

pub trait Bot {
    fn step(&mut self, controller: &mut RobotController);
    fn provider() -> &'static impl BotProvider<BotType = Self>
    where
        Self: Default,
    {
        &|r: &Robot| Default::default()
    }
}

impl<T> Bot for T
where
    for<'a> T: FnMut(&'a mut RobotController),
{
    fn step(&mut self, controller: &mut RobotController) {
        self(controller);
    }
}

impl<T1, T2> Bot for Either<T1, T2>
where
    T1: Bot,
    T2: Bot,
{
    fn step(&mut self, controller: &mut RobotController) {
        match self {
            Either::Left(left) => left.step(controller),
            Either::Right(right) => right.step(controller),
        }
    }
}

// pub trait BotProvider<T: Bot> = Fn(&Robot) -> T;

pub trait BotProvider {
    type BotType: Bot;
    fn get(&self, robot: &Robot) -> Self::BotType;
}

impl<T: Bot, F: Fn(&Robot) -> T> BotProvider for F {
    type BotType = T;

    fn get(&self, robot: &Robot) -> Self::BotType {
        self(robot)
    }
}
