package sprintBot.util;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import sprintBot.fast.FastBooleanArray2D;

import static sprintBot.util.Constants.rc;

public class MapCache {
    private static FastBooleanArray2D cached;
    private static FastBooleanArray2D hasAdjacentUnpassable;

    public static void init() {
        cached = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        hasAdjacentUnpassable = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    public static boolean isPassable(MapLocation location) {
        try {
            if (rc.onTheMap(location)) {
                return rc.sensePassability(location);
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        return false;
    }

    public static boolean hasAdjacentUnpassable(MapLocation location) {
        // see if stored in cache
        if (cached.get(location)) {
            return hasAdjacentUnpassable.get(location);
        }
        boolean hasAdjacentUnpassable = false;
        for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
            Direction direction = Constants.ORDINAL_DIRECTIONS[i];
            MapLocation adjacentLocation = location.add(direction);
            if (!isPassable(adjacentLocation)) {
                hasAdjacentUnpassable = true;
                break;
            }
        }
        if (hasAdjacentUnpassable) {
            MapCache.hasAdjacentUnpassable.setTrue(location);
        }
        cached.setTrue(location);
        return hasAdjacentUnpassable;
    }
}
