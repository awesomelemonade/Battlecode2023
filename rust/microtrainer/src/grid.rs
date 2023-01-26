use std::ops::{Index, IndexMut};

use itertools::Itertools;

use crate::position::Position;

#[derive(Debug)]
pub struct Grid<T> {
    width: usize,
    height: usize,
    data: Vec<Vec<T>>,
}

impl<T: Clone> Clone for Grid<T> {
    fn clone(&self) -> Self {
        Self {
            width: self.width,
            height: self.height,
            data: self.data.clone(),
        }
    }
}

impl<T: Default> Grid<T> {
    pub fn new(width: usize, height: usize) -> Self {
        Self::new_with_func(width, height, |_, _| Default::default())
    }
}
impl<T> Grid<T>
where
    T: Clone,
{
    pub fn new_with_constant(width: usize, height: usize, item: T) -> Self {
        Self::new_with_func(width, height, |_, _| item.clone())
    }
}
impl<T> Grid<T> {
    pub fn new_with_func(
        width: usize,
        height: usize,
        mut func: impl FnMut(usize, usize) -> T,
    ) -> Self {
        let data = (0..width)
            .map(|x| (0..height).map(|y| func(x, y)).collect_vec())
            .collect_vec();
        Self {
            data,
            width,
            height,
        }
    }
    pub fn data(&self) -> &Vec<Vec<T>> {
        &self.data
    }
    pub fn data_mut(&mut self) -> &mut Vec<Vec<T>> {
        &mut self.data
    }
    pub fn within_bounds(&self, Position { x, y }: Position) -> bool {
        x >= 0 && y >= 0 && x < self.width && y < self.height
    }
}

impl<T, U> Index<U> for Grid<T>
where
    U: Into<(usize, usize)>,
{
    type Output = T;

    fn index(&self, index: U) -> &Self::Output {
        let (x, y) = index.into();
        &self.data[x][y]
    }
}

impl<T, U> IndexMut<U> for Grid<T>
where
    U: Into<(usize, usize)>,
{
    fn index_mut(&mut self, index: U) -> &mut Self::Output {
        let (x, y) = index.into();
        &mut self.data[x][y]
    }
}
