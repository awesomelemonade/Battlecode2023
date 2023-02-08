#[derive(Debug, Copy, Clone, PartialEq, Eq)]
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
        match self {
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
        match self {
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

    pub fn ordinal_directions() -> [Direction; 8] {
        [
            Direction::North,
            Direction::Northeast,
            Direction::East,
            Direction::Southeast,
            Direction::South,
            Direction::Southwest,
            Direction::West,
            Direction::Northwest,
        ]
    }
    pub fn all_directions() -> [Direction; 9] {
        [
            Direction::Center,
            Direction::North,
            Direction::Northeast,
            Direction::East,
            Direction::Southeast,
            Direction::South,
            Direction::Southwest,
            Direction::West,
            Direction::Northwest,
        ]
    }
    pub fn attempt_order(direction: Direction) -> [Direction; 8] {
        match direction {
            Direction::North => [
                Direction::North,
                Direction::Northwest,
                Direction::Northeast,
                Direction::West,
                Direction::East,
                Direction::Southwest,
                Direction::Southeast,
                Direction::South,
            ],
            Direction::Northeast => [
                Direction::Northeast,
                Direction::North,
                Direction::East,
                Direction::Northwest,
                Direction::Southeast,
                Direction::West,
                Direction::South,
                Direction::Southwest,
            ],
            Direction::East => [
                Direction::East,
                Direction::Northeast,
                Direction::Southeast,
                Direction::North,
                Direction::South,
                Direction::Northwest,
                Direction::Southwest,
                Direction::West,
            ],
            Direction::Southeast => [
                Direction::Southeast,
                Direction::East,
                Direction::South,
                Direction::Northeast,
                Direction::Southwest,
                Direction::North,
                Direction::West,
                Direction::Northwest,
            ],
            Direction::South => [
                Direction::South,
                Direction::Southeast,
                Direction::Southwest,
                Direction::East,
                Direction::West,
                Direction::Northeast,
                Direction::Northwest,
                Direction::North,
            ],
            Direction::Southwest => [
                Direction::Southwest,
                Direction::South,
                Direction::West,
                Direction::Southeast,
                Direction::Northwest,
                Direction::East,
                Direction::North,
                Direction::Northeast,
            ],
            Direction::West => [
                Direction::West,
                Direction::Southwest,
                Direction::Northwest,
                Direction::South,
                Direction::North,
                Direction::Southeast,
                Direction::Northeast,
                Direction::East,
            ],
            Direction::Northwest => [
                Direction::Northwest,
                Direction::West,
                Direction::North,
                Direction::Southwest,
                Direction::Northeast,
                Direction::South,
                Direction::East,
                Direction::Southeast,
            ],
            Direction::Center => panic!("No attempt order for center"),
        }
    }
}

impl From<Direction> for (i32, i32) {
    fn from(direction: Direction) -> Self {
        (direction.dx(), direction.dy())
    }
}
