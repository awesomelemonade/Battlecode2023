package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

import static sprintBot.util.Constants.rc;

public class Cache { // Cache variables that are constant throughout a turn
    public static RobotInfo[] ALLY_ROBOTS, ENEMY_ROBOTS;
    public static int TURN_COUNT;
    public static MapLocation MY_LOCATION;

    public static void init() {
        TURN_COUNT = 0;
        invalidate();
    }

    public static void loop() {
        if (TURN_COUNT > 0) {
            invalidate();
        }
        TURN_COUNT++;
    }

    public static void invalidate() {
        try {
            ALLY_ROBOTS = rc.senseNearbyRobots(-1, Constants.ALLY_TEAM);
            ENEMY_ROBOTS = rc.senseNearbyRobots(-1, Constants.ENEMY_TEAM);
        } catch (GameActionException ex) {
            throw new IllegalStateException(ex);
        }
        MY_LOCATION = rc.getLocation();
    }
}