package sprintBot.robots;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import sprintBot.util.Cache;
import sprintBot.util.RunnableBot;
import sprintBot.util.Util;

import static sprintBot.util.Constants.rc;

public class Headquarters implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        if (tryBuildRandom(RobotType.CARRIER)) {
            return;
        }
        tryBuildRandom(RobotType.LAUNCHER);
    }

    public static boolean tryBuildRandom(RobotType type) {
        try {
            MapLocation[] locations = rc.getAllLocationsWithinRadiusSquared(Cache.MY_LOCATION, RobotType.HEADQUARTERS.actionRadiusSquared);
            Util.shuffle(locations);
            for (int i = locations.length; --i >= 0;) {
                if (Util.tryBuild(type, locations[i])) {
                    return true;
                }
            }
            return false;
        } catch (GameActionException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
