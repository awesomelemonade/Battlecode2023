package sprintBot.robots;

import battlecode.common.*;
import sprintBot.util.*;

import java.util.Comparator;

import static sprintBot.util.Constants.rc;

public class Carrier implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        Anchor anchor = rc.getAnchor();
        if (anchor == null) {
            int capacityLeft = capacityLeft();
            if (capacityLeft > 0) {
                // look for well
                WellInfo well = getWell();
                if (well == null) {
                    Util.tryExplore();
                } else {
                    MapLocation wellLocation = well.getMapLocation();
                    if (Cache.MY_LOCATION.isAdjacentTo(wellLocation)) {
                        // try mine
                        tryCollectResource(wellLocation, Math.min(well.getRate(), capacityLeft));
                        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, wellLocation, 255, 255, 0);
                    } else {
                        // move towards well
                        Util.tryPathfindingMove(well.getMapLocation());
                        Debug.setIndicatorDot(Profile.MINING, wellLocation, 255, 255, 0);
                    }
                }
            } else {
                Debug.setIndicatorDot(Profile.MINING, Cache.MY_LOCATION, 255, 128, 0);
                if (tryTransferToHQ()) {
                    return;
                }
                // return to hq
                tryMoveToOurHQ();
            }
        } else {
            // TODO: look for island
            Util.tryExplore();
        }
    }

    public static int capacityLeft() {
        return GameConstants.CARRIER_CAPACITY - getWeight();
    }

    public static boolean tryCollectResource(MapLocation location, int amount) {
        if (rc.canCollectResource(location, amount)) {
            try {
                rc.collectResource(location, amount);
                return true;
            } catch (GameActionException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return false;
    }

    public static void tryMoveToOurHQ() {
        RobotInfo hq = Util.getClosestRobot(Cache.ALLY_ROBOTS, robot -> robot.type == RobotType.HEADQUARTERS);
        if (hq == null) {
            Util.tryExplore();
        } else {
            Util.tryPathfindingMove(hq.location);
        }
    }

    public static boolean tryTransferToHQ() {
        RobotInfo hq = Util.getClosestRobot(Cache.ALLY_ROBOTS, robot -> robot.type == RobotType.HEADQUARTERS);
        if (hq == null) {
            return false;
        }
        MapLocation hqLocation = hq.location;
        ResourceType resource = getTransferToHQResource();
        if (resource == ResourceType.NO_RESOURCE) {
            return false;
        }
        // see if in range
        if (!Cache.MY_LOCATION.isWithinDistanceSquared(hqLocation, Constants.ROBOT_TYPE.actionRadiusSquared)) {
            return false;
        }
        int amount = rc.getResourceAmount(resource);
        tryTransfer(hqLocation, resource, amount);
        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, hqLocation, 255, 255, 0); // yellow
        return true;
    }

    public static ResourceType getTransferToHQResource() {
        int adamantiumAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        if (adamantiumAmount > 0) {
            return ResourceType.ADAMANTIUM;
        }
        int manaAmount = rc.getResourceAmount(ResourceType.MANA);
        if (manaAmount > 0) {
            return ResourceType.MANA;
        }
        int elixirAmount = rc.getResourceAmount(ResourceType.ELIXIR);
        if (elixirAmount > 0) {
            return ResourceType.ELIXIR;
        }
        return ResourceType.NO_RESOURCE;
    }

    public static boolean tryTransfer(MapLocation location, ResourceType type, int amount) {
        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, location, 0, 255, 0);
        if (rc.canTransferResource(location, type, amount)) {
            try {
                rc.transferResource(location, type, amount);
            } catch (GameActionException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return false;
    }

    public static int getWeight() {
        return (rc.getAnchor() == null ? 0 : GameConstants.ANCHOR_WEIGHT)
                + rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }

    public static WellInfo getWell() {
        WellInfo[] wells = getWells();
        WellInfo closestWell = LambdaUtil.
                arraysStreamMin(wells, Comparator.comparingInt(
                        well -> well.getMapLocation().distanceSquaredTo(Cache.MY_LOCATION))).orElse(null);
        return closestWell;
    }

    public static WellInfo[] getWells() {
        WellInfo[] wells = rc.senseNearbyWells(ResourceType.ADAMANTIUM);
        if (wells.length > 0) {
            return wells;
        }
        wells = rc.senseNearbyWells(ResourceType.MANA);
        if (wells.length > 0) {
            return wells;
        }
        return rc.senseNearbyWells(ResourceType.MANA);
    }
}
