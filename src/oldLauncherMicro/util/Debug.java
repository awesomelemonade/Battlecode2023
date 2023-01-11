package oldLauncherMicro.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static oldLauncherMicro.util.Constants.rc;

public class Debug {
    public static final int id = -1;

    public static void failFast(GameActionException ex) {
        if (Constants.DEBUG_FAIL_FAST) {
            throw new IllegalStateException(ex);
        } else {
            Debug.setIndicatorDot(Profile.PATHFINDING, Cache.MY_LOCATION, 255, 0, 0);
        }
    }

    public static void failFast(String message) {
        if (Constants.DEBUG_FAIL_FAST) {
            throw new IllegalStateException(message);
        } else {
            Debug.setIndicatorDot(Profile.PATHFINDING, Cache.MY_LOCATION, 255, 0, 0);
        }
    }

    public static void println(Object o) {
        if (id == -1 || rc.getID() == id) {
            System.out.println(o);
        }
    }

    public static void println(String line) {
        if (id == -1 || rc.getID() == id) {
            System.out.println(line);
        }
    }

    public static void setIndicatorString(String string) {
        rc.setIndicatorString(string);
    }

    public static void setIndicatorDot(MapLocation location, int red, int green, int blue) {
        rc.setIndicatorDot(location, red, green, blue);
    }

    public static void setIndicatorLine(MapLocation a, MapLocation b, int red, int green, int blue) {
        rc.setIndicatorLine(a, b, red, green, blue);
    }

    public static void println(Profile profile, String line) {
        if (profile.enabled()) {
            if (id == -1 || rc.getID() == id) {
                System.out.println(line);
            }
        }
    }

    public static void setIndicatorString(Profile profile, String string) {
        if (profile.enabled()) {
            rc.setIndicatorString(string);
        }
    }

    public static void setIndicatorDot(Profile profile, MapLocation location, int red, int green, int blue) {
        if (profile.enabled()) {
            rc.setIndicatorDot(location, red, green, blue);
        }
    }

    public static void setIndicatorLine(Profile profile, MapLocation a, MapLocation b, int red, int green, int blue) {
        if (profile.enabled()) {
            rc.setIndicatorLine(a, b, red, green, blue);
        }
    }
}