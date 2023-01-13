package oldLauncherMicro.robots;

import battlecode.common.*;
import oldLauncherMicro.util.*;

import java.util.Comparator;

import static oldLauncherMicro.util.Constants.rc;

public class Carrier implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        // let's try to pick up an anchor from hq
        if (tryPickupAnchorFromHQ()) {
            return;
        }
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
                    Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, wellLocation, 0, 128, 0); // dark green
                    if (Cache.MY_LOCATION.isAdjacentTo(wellLocation)) {
                        // try mine
                        tryCollectResource(wellLocation, Math.min(well.getRate(), capacityLeft));
                    } else {
                        // move towards well
                        Util.tryPathfindingMove(well.getMapLocation());
                    }
                }
            } else {
                // deposit to hq
                Debug.setIndicatorDot(Profile.MINING, Cache.MY_LOCATION, 0, 128, 0); // dark green
                if (tryTransferToHQ()) {
                    return;
                }
                // return to hq
                tryMoveToOurHQ();
            }
        } else {
            // TODO: we don't want to go to an island that already has an anchor already assigned to it
            MapLocation islandLocation = findClosestUnoccupiedNonAllyIsland();
            if (islandLocation == null) {
                // TODO: go to commed islands?
                Util.tryExplore();
            } else {
                if (islandLocation.equals(Cache.MY_LOCATION)) {
                    tryPlaceAnchor();
                } else {
                    Util.tryPathfindingMove(islandLocation);
                }
            }
        }
    }

    public static boolean tryPickupAnchorFromHQ() {
        if (getWeight() > 0) { // we need space for an anchor
            return false;
        }
        Communication.CarrierTask task = Communication.getTaskAsCarrier();
        if (task != null) {
            if (task.type == Communication.CarrierTaskType.PICKUP_ANCHOR) {
                MapLocation location = task.hqLocation;
                if (Cache.MY_LOCATION.isAdjacentTo(location)) {
                    tryTakeAnchor(location, Anchor.ACCELERATING);
                    tryTakeAnchor(location, Anchor.STANDARD);
                } else {
                    Util.tryPathfindingMove(location);
                }
                return true;
            }
        }
        return false;

        // naive - also doesn't work cuz we can't use the Inventory task
//        // get all ally headquarters
//        RobotInfo closestAnchorPickupTarget = Util.getClosestRobot(Cache.ALLY_ROBOTS,
//                robot -> robot.type == RobotType.HEADQUARTERS && robot.inventory.getTotalAnchors() > 0);
//        if (closestAnchorPickupTarget != null) {
//            MapLocation location = closestAnchorPickupTarget.getLocation();
//            if (Cache.MY_LOCATION.isAdjacentTo(location)) {
//                Anchor anchorType = closestAnchorPickupTarget.inventory.getNumAnchors(Anchor.ACCELERATING) > 0
//                        ? Anchor.ACCELERATING : Anchor.STANDARD;
//                tryTakeAnchor(location, anchorType);
//            } else {
//                Util.tryPathfindingMove(location);
//            }
//            return true;
//        }
//        return false;
    }

    public static boolean tryTakeAnchor(MapLocation location, Anchor anchorType) {
        if (rc.canTakeAnchor(location, anchorType)) {
            try {
                rc.takeAnchor(location, anchorType);
                return true;
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
    }

    public static boolean tryPlaceAnchor() {
        if (rc.canPlaceAnchor()) {
            try {
                rc.placeAnchor();
                return true;
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
    }

    public static MapLocation findClosestUnoccupiedNonAllyIsland() {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        int[] islands = rc.senseNearbyIslands();
        for (int i = islands.length; --i >= 0; ) {
            int islandId = islands[i];
            try {
                if (rc.senseTeamOccupyingIsland(islandId) != Constants.ALLY_TEAM) {
                    // TODO-someday: can likely save bytecodes by using rc.senseNearbyIslandLocations(distanceSquared, idx)
                    MapLocation[] locations = rc.senseNearbyIslandLocations(islandId);
                    for (int j = locations.length; --j >= 0; ) {
                        MapLocation location = locations[j];
                        if (location.equals(Cache.MY_LOCATION) || rc.isLocationOccupied(location)) {
                            int distanceSquared = location.distanceSquaredTo(Cache.MY_LOCATION);
                            if (distanceSquared < bestDistanceSquared) {
                                bestDistanceSquared = distanceSquared;
                                bestLocation = location;
                            }
                        }
                    }
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return bestLocation;
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
        Util.tryPathfindingMove(Communication.getClosestSafeAllyHQ());
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
        if (!Cache.MY_LOCATION.isAdjacentTo(hqLocation)) {
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
        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, location, 0, 255, 0); // green
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
        return rc.getWeight();
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
