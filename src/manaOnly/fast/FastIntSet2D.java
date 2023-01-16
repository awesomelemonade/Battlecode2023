package manaOnly.fast;

import battlecode.common.MapLocation;

public class FastIntSet2D {
	private int[][] array;
	private int counter;
	private int width;
	private int height;

	public FastIntSet2D(int width, int height) {
		this.array = new int[width][];
		this.width = width;
		this.height = height;
		this.counter = 1;
	}
	public void reset() {
		this.counter++;
	}
	public void add(MapLocation location) {
		add(location.x, location.y);
	}
	public void add(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		array[y] = counter;
	}
	public boolean contains(MapLocation location) {
		return contains(location.x, location.y);
	}
	public boolean contains(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		return array[y] == counter;
	}
}