use crate::robot::RobotController;

pub trait Bot {
    fn run(&mut self, controller: &mut RobotController) -> ();
}
