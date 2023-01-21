package beforeSpecChanges.fast;

public class FastIntCounter2D {
	private int[][] array;
	private int width;
	private int height;
	private int base;
	private int counter;
	public FastIntCounter2D(int width, int height) {
		this.array = new int[width][];
		this.base = 0;
		this.counter = 1;
		this.width = width;
		this.height = height;
	}
	public void updateBaseTrail(int n) {
		base = counter - n;
	}
	public void reset() {
		this.base = counter;
	}
	public void add(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		array[y] = counter++;
	}
	public boolean contains(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		return array[y] >= base;
	}
	public int get(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		int num = array[y] - base;
		return num < 0 ? -1 : num;
	}
	public int getCounter() {
		return counter - base;
	}
}