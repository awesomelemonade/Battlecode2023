package bot.pathfinder;

public class FastIntCounter2D {
	private int[][] array;
	private int base;
	private int counter;
	public FastIntCounter2D(int width, int height) {
		this.array = new int[width][height];
		this.base = 0;
		this.counter = 0;
	}
	public void updateBaseTrail(int n) {
		base = counter - n;
	}
	public void reset() {
		this.base = counter;
	}
	public void add(int x, int y) {
		this.array[x][y] = counter++;
	}
	public boolean contains(int x, int y) {
		return this.array[x][y] >= base;
	}
	public int get(int x, int y) {
		int num = this.array[x][y] - base;
		return num < 0 ? -1 : num;
	}
	public int getCounter() {
		return counter - base;
	}
}