package beforeBetterMicro.util;

import battlecode.common.*;

import static beforeBetterMicro.util.Constants.rc;

public class Cache { // Cache variables that are constant throughout a turn
    public static RobotInfo[] ALLY_ROBOTS, ENEMY_ROBOTS;
    public static int TURN_COUNT;
    public static MapLocation MY_LOCATION;
    public static WellInfo[] ADAMANTIUM_WELLS;
    public static WellInfo[] MANA_WELLS;
    public static WellInfo[] ELIXIR_WELLS;
    public static MapLocation NEAREST_ALLY_HQ;

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
            ADAMANTIUM_WELLS =  rc.senseNearbyWells(ResourceType.ADAMANTIUM);
            MANA_WELLS =  rc.senseNearbyWells(ResourceType.MANA);
            ELIXIR_WELLS =  rc.senseNearbyWells(ResourceType.ELIXIR);
        } catch (GameActionException ex) {
            throw new IllegalStateException(ex);
        }
        MY_LOCATION = rc.getLocation();
        if (Constants.ROBOT_TYPE == RobotType.CARRIER) {
            NEAREST_ALLY_HQ = Util.getClosestAllyHeadquartersLocation();
        }
    }
}