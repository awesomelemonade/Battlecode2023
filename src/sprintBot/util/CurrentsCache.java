package sprintBot.util;

import battlecode.common.MapLocation;

public class CurrentsCache {
    public static MapLocation[][] data;

    public static void init() {
        data = new MapLocation[Constants.MAP_WIDTH][];
    }

    public void set(MapLocation location, MapLocation data) {
        set(location.x, location.y, data);
    }

    // Does not check for null - make sure it is preallocated!
    public static void set(int x, int y, MapLocation location) {
        data[x][y] = location;
    }

    public static void preallocateCurrentVision() {
        int x = Cache.MY_LOCATION.x;
        int start = Math.max(0, x - 6);
        int end = Math.min(Constants.MAP_WIDTH - 1, x + 6);
        for (int i = start; i <= end; i++) {
            if (data[i] == null) {
                data[i] = new MapLocation[Constants.MAP_HEIGHT];
            }
        }
    }

    public static boolean hasNoKnownCurrent(MapLocation location) {
        MapLocation[] array = data[location.x];
        if (array == null) {
            return true;
        } else {
            return location.equals(array[location.y]);
        }
    }

    // Does not check for null - make sure it is preallocated!
    public static MapLocation get(MapLocation location) {
        MapLocation ret = data[location.x][location.y];
        if (ret == null) {
            ret = location;
            data[location.x][location.y] = location;
        }
        return ret;
    }
}
