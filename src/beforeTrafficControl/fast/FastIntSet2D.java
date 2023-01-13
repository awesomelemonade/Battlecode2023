package beforeTrafficControl.fast;

public class FastIntSet2D {
	private int[][] array;
	private int counter;
	private int width;
	private int height;

	public FastIntSet2D(int width, int height) {
		this.array = new int[width][];
		this.width = width;
		this.height = height;
	}
	public void reset() {
		this.counter++;
	}
	public void add(int x, int y) {
		int[] array = this.array[x];
		if (array == null) {
			array = new int[height];
			this.array[x] = array;
		}
		array[y] = counter;
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