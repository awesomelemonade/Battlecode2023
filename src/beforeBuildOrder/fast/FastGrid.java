package beforeBuildOrder.fast;

import battlecode.common.MapLocation;

public class FastGrid<T> {
    private T[][] array;
    private int width;
    private int height;

    @SuppressWarnings("unchecked")
    public FastGrid(int width, int height) {
        this.array = (T[][]) new Object[width][];
        this.width = width;
        this.height = height;
    }
    public void set(MapLocation location, T data) {
        set(location.x, location.y, data);
    }

    @SuppressWarnings("unchecked")
    public void set(int x, int y, T data) {
        T[] array = this.array[x];
        if (array == null) {
            array = (T[]) new Object[height];
            this.array[x] = array;
        }
        array[y] = data;
    }

    public T get(MapLocation location) {
        return get(location.x, location.y);
    }
    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        T[] array = this.array[x];
        if (array == null) {
            array = (T[]) new Object[height];
            this.array[x] = array;
        }
        return array[y];
    }
}
