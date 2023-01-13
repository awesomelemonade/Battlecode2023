package beforeTrafficControl.fast;

public class FastDoubleTracker {
    private double[] data;
    private int index;
    private double sum;
    private int size;

    public FastDoubleTracker(int size) {
        this.data = new double[size];
        this.index = 0;
        this.sum = 0;
        this.size = size;
    }

    public void add(double data) {
        sum = sum + data - this.data[index];
        this.data[index] = data;
        index = (index + 1) % size;
    }

    public double sum() {
        return sum;
    }
    public double average() {
        return sum / ((double) size);
    }
}
