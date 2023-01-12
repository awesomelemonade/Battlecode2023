package beforeWellTracker.fast;

public class FastIntSet2D {
	private int[][] array;
	private int counter;
	public FastIntSet2D(int width, int height) {
		this.array = new int[width][height];
	}
	public void reset() {
		this.counter++;
	}
	public void add(int x, int y) {
		this.array[x][y] = counter;
	}
	public boolean contains(int x, int y) {
		return this.array[x][y] == counter;
	}
}