package beforeSpecChanges.util;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import beforeSpecChanges.fast.FastBooleanArray2D;

import static beforeSpecChanges.util.Constants.rc;

public class HasAdjacentUnpassableCache {
    private static FastBooleanArray2D cachedHasAdjacentUnpassable;
    private static FastBooleanArray2D hasAdjacentUnpassable;

    public static void init() {
        cachedHasAdjacentUnpassable = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        hasAdjacentUnpassable = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    private static boolean becauseOfCloud = false; // ugly hack

    public static boolean isPassable(MapLocation location) {
        try {
            if (rc.canSenseLocation(location)) {
                return rc.sensePassability(location);
            } else {
                if (Util.onTheMap(location)) {
                    // locations outside of map are not passable
                    return false;
                } else {
                    becauseOfCloud = true;
                    // because of cloud
                    return false; // assume false
                }
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        return false;
    }

    public static void precalculate(MapLocation location) {
        if (Util.onTheMap(location)) {
            hasAdjacentUnpassable(location);
        }
    }

    public static boolean hasAdjacentUnpassable(MapLocation location) {
        // see if stored in cache
        if (cachedHasAdjacentUnpassable.get(location)) {
            return hasAdjacentUnpassable.get(location);
        }
        // save bytecodes!!!
        becauseOfCloud = false;
        boolean hasAdjacentUnpassable = !(isPassable(location.add(Direction.NORTH))
                && isPassable(location.add(Direction.SOUTH))
                && isPassable(location.add(Direction.WEST))
                && isPassable(location.add(Direction.EAST))
                && isPassable(location.add(Direction.NORTHEAST))
                && isPassable(location.add(Direction.NORTHWEST))
                && isPassable(location.add(Direction.SOUTHEAST))
                && isPassable(location.add(Direction.SOUTHWEST)));
        if (!becauseOfCloud) {
            if (hasAdjacentUnpassable) {
                HasAdjacentUnpassableCache.hasAdjacentUnpassable.setTrue(location);
            }
            cachedHasAdjacentUnpassable.setTrue(location);
        }
        return hasAdjacentUnpassable;
    }
}
