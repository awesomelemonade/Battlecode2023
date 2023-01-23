package beforeBetterMicro.util;

import battlecode.common.MapLocation;

public class CurrentsCache {
    private static MapLocation[][] data;

    public static void init() {
        data = new MapLocation[Constants.MAP_WIDTH][];
    }

    public void set(MapLocation location, MapLocation data) {
        set(location.x, location.y, data);
    }

    public static void set(int x, int y, MapLocation location) {
        MapLocation[] array = data[x];
        if (array == null) {
            array = new MapLocation[Constants.MAP_HEIGHT];
            data[x] = array;
        }
        array[y] = location;
    }

    public static MapLocation get(MapLocation location) {
        MapLocation[] array = data[location.x];
        if (array == null) {
            array = new MapLocation[Constants.MAP_HEIGHT];
            data[location.x] = array;
        }
        MapLocation ret = array[location.y];
        if (ret == null) {
            ret = location;
            array[location.y] = location;
        }
        return ret;
    }
}
