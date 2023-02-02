package beforeStuckBlacklist.fast;

import battlecode.common.MapLocation;

// defaults to (i, j)
public class FastMapLocationGridWithDefault {
    private MapLocation[][] array;
    private int width;
    private int height;

    public FastMapLocationGridWithDefault(int width, int height) {
        this.array = new MapLocation[width][];
        this.width = width;
        this.height = height;
    }
    public void set(MapLocation location, MapLocation data) {
        set(location.x, location.y, data);
    }

    public void set(int x, int y, MapLocation data) {
        MapLocation[] array = this.array[x];
        if (array == null) {
            array = new MapLocation[height];
            this.array[x] = array;
        }
        array[y] = data;
    }

    public MapLocation get(MapLocation location) {
        MapLocation[] array = this.array[location.x];
        if (array == null) {
            array = new MapLocation[height];
            this.array[location.x] = array;
        }
        MapLocation ret = array[location.y];
        if (ret == null) {
            ret = location;
            array[location.y] = location;
        }
        return ret;
    }
}
