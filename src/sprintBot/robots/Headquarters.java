package sprintBot.robots;

import battlecode.common.*;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

public class Headquarters implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        int numAnchors = rc.getNumAnchors(null);
        if (numAnchors > 0) { // check if we have anchors
            // TODO-someday: broadcast to nearest X empty miners to pickup anchors (where X = numAnchors)
            // Currently only broadcasts to nearest carrier
            RobotInfo carrier = Util.getClosestRobot(Cache.ALLY_ROBOTS,
                    robot -> robot.type == RobotType.CARRIER/* && robot.inventory.getWeight() == 0*/); // TODO - remove when inventory bug gets fixed
            if (carrier != null && carrier.location.isWithinDistanceSquared(Cache.MY_LOCATION, RobotType.CARRIER.visionRadiusSquared)) {
                Communication.addTask(carrier.location, Communication.CARRIER_TASK_ANCHOR_PICKUP_ID);
            }
        }
        if (rc.getRobotCount() > 250) {
            int adamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
            int mana = rc.getResourceAmount(ResourceType.MANA);
            if (adamantium >= Anchor.STANDARD.adamantiumCost
                    && mana >= Anchor.STANDARD.manaCost) { // simple random heuristic to build anchors
                if (tryBuildAnchor(Anchor.STANDARD)) {
                    return;
                }
            }
            // save to build anchor
        } else {
            if (tryBuildRandom(RobotType.CARRIER)) {
                return;
            }
            tryBuildRandom(RobotType.LAUNCHER);
        }
    }
    public static boolean tryBuildAnchor(Anchor anchorType) {
        if (rc.canBuildAnchor(anchorType)) {
            try {
                rc.buildAnchor(anchorType);
                return true;
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
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
