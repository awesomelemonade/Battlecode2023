package manaOnly.fast;

public class FastIntMap {
    private static final int CHUNK_SIZE = 64;
    private int[][] data;

    public FastIntMap(int maxSize) {
        this.data = new int[maxSize / 64 + 1][];
    }

    public void set(int index, int data) {
        int chunkIndex = index / CHUNK_SIZE;
        int[] array = this.data[chunkIndex];
        if (array == null) {
            array = new int[CHUNK_SIZE];
            this.data[chunkIndex] = array;
        }
        array[index % CHUNK_SIZE] = data;
    }

    public int get(int index) {
        int chunkIndex = index / CHUNK_SIZE;
        int[] array = this.data[chunkIndex];
        if (array == null) {
            array = new int[CHUNK_SIZE];
            this.data[chunkIndex] = array;
        }
        return array[index % CHUNK_SIZE];
    }
}
