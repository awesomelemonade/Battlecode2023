#[derive(PartialEq, Copy, Clone)]
pub enum Direction {
    Center,
    North,
    Northeast,
    East,
    Southeast,
    South,
    Southwest,
    West,
    Northwest,
}

impl Direction {
    pub fn dx(&self) -> i32 {
        match *self {
            Self::Center => 0,
            Self::North => 0,
            Self::Northeast => 1,
            Self::East => 1,
            Self::Southeast => 1,
            Self::South => 0,
            Self::Southwest => -1,
            Self::West => -1,
            Self::Northwest => -1,
        }
    }

    pub fn dy(&self) -> i32 {
        match *self {
            Self::Center => 0,
            Self::North => 1,
            Self::Northeast => 1,
            Self::East => 0,
            Self::Southeast => -1,
            Self::South => -1,
            Self::Southwest => -1,
            Self::West => 0,
            Self::Northwest => 1,
        }
    }
}

impl From<Direction> for (i32, i32) {
    fn from(direction: Direction) -> Self {
        (direction.dx(), direction.dy())
    }
}
