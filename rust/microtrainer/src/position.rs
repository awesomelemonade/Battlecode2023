use crate::direction::Direction;

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord)]
pub struct Position {
    pub x: usize,
    pub y: usize,
}

impl Position {
    pub fn distance_squared(&self, position: Position) -> u32 {
        let dx = position.x.abs_diff(self.x);
        let dy = position.y.abs_diff(self.y);
        (dx * dx + dy * dy) as u32
    }
    pub fn add_exn(&self, delta: impl Into<(i32, i32)>) -> Position {
        let (dx, dy) = delta.into();
        let x = (self.x as i32) + dx;
        let y = (self.y as i32) + dy;
        Position {
            x: x as usize,
            y: y as usize,
        }
    }
    pub fn add_checked(
        &self,
        delta: impl Into<(i32, i32)>,
        (width, height): (usize, usize),
    ) -> Option<Position> {
        let (dx, dy) = delta.into();
        let x = (self.x as i32) + dx;
        let y = (self.y as i32) + dy;
        if x >= 0 && y >= 0 && x < width as i32 && y < height as i32 {
            Some(Position {
                x: x as usize,
                y: y as usize,
            })
        } else {
            None
        }
    }
    pub fn direction_to(&self, position: impl Into<Position>) -> Direction {
        let position = position.into();
        let dx = position.x as f32 - self.x as f32;
        let dy = position.y as f32 - self.y as f32;

        if dx.abs() >= 2.414 * dy.abs() {
            if dx > 0f32 {
                Direction::East
            } else if dx < 0f32 {
                Direction::West
            } else {
                Direction::Center
            }
        } else if dy.abs() >= 2.414 * dx.abs() {
            if dy > 0f32 {
                Direction::North
            } else {
                Direction::South
            }
        } else {
            if dy > 0f32 {
                if dx > 0f32 {
                    Direction::Northeast
                } else {
                    Direction::Northwest
                }
            } else {
                if dx > 0f32 {
                    Direction::Southeast
                } else {
                    Direction::Southwest
                }
            }
        }
    }
}

impl From<(usize, usize)> for Position {
    fn from((x, y): (usize, usize)) -> Self {
        Self { x, y }
    }
}

impl From<Position> for (usize, usize) {
    fn from(position: Position) -> Self {
        (position.x, position.y)
    }
}

impl From<&Position> for (usize, usize) {
    fn from(position: &Position) -> Self {
        (*position).into()
    }
}
