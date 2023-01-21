package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import sprintBot.fast.FastBooleanArray2D;

import static sprintBot.util.Constants.rc;

public class PassabilityCache {
    private static FastBooleanArray2D cached;
    private static FastBooleanArray2D passable;
    public static void init() {
        cached = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        passable = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    public static boolean isPassableOrFalse(MapLocation location) {
        if (cached.get(location)) {
            return passable.get(location);
        }
        if (rc.canSenseLocation(location)) {
            try {
                if (rc.sensePassability(location)) {
                    cached.setTrue(location);
                    passable.setTrue(location);
                    return true;
                } else {
                    cached.setTrue(location);
                    return false;
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
    }

    public static final int UNPASSABLE = 0;
    public static final int PASSABLE = 1;
    public static final int UNKNOWN = 2;
    // we gotta save bytecodes - so we return int instead of enum
    public static int isPassable(MapLocation location) {
        if (cached.get(location)) {
            return passable.get(location) ? 1 : 0;
        }
        if (rc.canSenseLocation(location)) {
            try {
                if (rc.sensePassability(location)) {
                    cached.setTrue(location);
                    passable.setTrue(location);
                    return PASSABLE;
                } else {
                    cached.setTrue(location);
                    return UNPASSABLE;
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return UNKNOWN;
    }
}
