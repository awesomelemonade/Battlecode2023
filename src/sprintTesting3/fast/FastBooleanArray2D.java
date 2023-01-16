package sprintTesting3.fast;

import battlecode.common.MapLocation;

public class FastBooleanArray2D {
    private long[] data;

    public FastBooleanArray2D(int width, int height) {
        if (height > 64) {
            throw new IllegalStateException("Cannot store in long");
        }
        data = new long[width];
    }

    public boolean get(MapLocation location) {
        return (data[location.x] & (1L << location.y)) != 0;
    }

    public boolean get(int x, int y) {
        return (data[x] & (1L << y)) == 0;
    }

    public void setTrue(MapLocation location) {
        data[location.x] |= (1L << location.y);
    }

    public void setTrue(int x, int y) {
        data[x] |= (1L << y);
    }

    public void setFalse(MapLocation location) {
        data[location.x] &= (~(1L << location.y));
    }

    public void setFalse(int x, int y) {
        data[x] &= (~(1L << y));
    }
}
