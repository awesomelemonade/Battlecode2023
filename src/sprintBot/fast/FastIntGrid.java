package sprintBot.fast;

import battlecode.common.MapLocation;

public class FastIntGrid {
    private int[][] array;
    private int width;
    private int height;
    public FastIntGrid(int width, int height) {
        this.array = new int[width][];
        this.width = width;
        this.height = height;
    }
    public void set(MapLocation location, int data) {
        set(location.x, location.y, data);
    }
    public void set(int x, int y, int data) {
        int[] array = this.array[x];
        if (array == null) {
            array = new int[height];
            this.array[x] = array;
        }
        array[y] = data;
    }
    public void bitwiseOr(MapLocation location, int data) {
        bitwiseOr(location.x, location.y, data);
    }
    public void bitwiseOr(int x, int y, int data) {
        int[] array = this.array[x];
        if (array == null) {
            array = new int[height];
            this.array[x] = array;
        }
        array[y] |= data;
    }
    public int get(MapLocation location) {
        return get(location.x, location.y);
    }
    public int get(int x, int y) {
        int[] array = this.array[x];
        if (array == null) {
            array = new int[height];
            this.array[x] = array;
        }
        return array[y];
    }
}
