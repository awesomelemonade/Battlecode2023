package beforeBuildOrder.fast;

public class FastIntTracker {
    private int[] data;
    private int index;
    private int sum;
    private int size;

    public FastIntTracker(int size) {
        this.data = new int[size];
        this.index = 0;
        this.sum = 0;
        this.size = size;
    }

    public void add(int data) {
        sum = sum + data - this.data[index];
        this.data[index] = data;
        index = (index + 1) % size;
    }

    public int sum() {
        return sum;
    }
    public double average() {
        return ((double) sum) / ((double) size);
    }
}
