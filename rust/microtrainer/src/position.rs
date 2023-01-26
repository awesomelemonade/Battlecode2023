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
        width: usize,
        height: usize,
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
