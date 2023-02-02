package beforeAvoidCloud.util;

import battlecode.common.*;
import beforeAvoidCloud.pathfinder.Nav;

import static beforeAvoidCloud.util.Constants.rc;

public class Cache { // Cache variables that are constant throughout a turn
    public static RobotInfo[] ALLY_ROBOTS, ENEMY_ROBOTS;
    public static int TURN_COUNT;
    public static MapLocation MY_LOCATION;
    public static MapLocation NEAREST_ALLY_HQ;
    public static WellInfo[] NEARBY_WELLS;

    public static RobotInfo prevClosestEnemyAttacker = null;

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

    public static void postLoop() {
        prevClosestEnemyAttacker = Util.getClosestEnemyRobot(r -> Util.isAttacker(r.type));
    }

    public static void invalidate() {
        try {
            ALLY_ROBOTS = rc.senseNearbyRobots(-1, Constants.ALLY_TEAM);
            ENEMY_ROBOTS = rc.senseNearbyRobots(-1, Constants.ENEMY_TEAM);
        } catch (GameActionException ex) {
            throw new IllegalStateException(ex);
        }
        MY_LOCATION = rc.getLocation();
        if (Constants.ROBOT_TYPE == RobotType.CARRIER) {
            NEAREST_ALLY_HQ = Util.getClosestAllyHeadquartersLocation();
        }
        if (Constants.ROBOT_TYPE != RobotType.LAUNCHER) {
            NEARBY_WELLS = rc.senseNearbyWells();
            WellTracker.onNewWells();
        }
    }
}