package sprintBot.robots;

import battlecode.common.*;
import sprintBot.fast.FastIntSet2D;
import sprintBot.pathfinder.Pathfinding;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

public class Carrier implements RunnableBot {
    private static Communication.CarrierTask currentTask;
    private static FastIntSet2D blacklist;

    @Override
    public void init() throws GameActionException {
        blacklist = new FastIntSet2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        MapCache.init();
    }

    private static void debug_render() {
        if (Profile.MINING.enabled()) {
            WellTracker.forEachPending(location -> Debug.setIndicatorDot(Profile.MINING, location, 0, 255, 255)); // cyan
            WellTracker.forEachKnown(location -> Debug.setIndicatorDot(Profile.MINING, location, 0, 0, 255)); // blue
        }
    }

    @Override
    public void loop() throws GameActionException {
        debug_render();
        // update task (if needed)
        Communication.CarrierTask potentialTask = Communication.getTaskAsCarrier();
        if (potentialTask != null) {
            currentTask = potentialTask;
        }

        if (currentTask == null) {
            Debug.setIndicatorString(Profile.MINING, "None");
        } else {
            Debug.setIndicatorString(Profile.MINING, currentTask.type.toString());
        }

        if (getWeight() == GameConstants.CARRIER_CAPACITY) {
            blacklist.reset();
        }
    }

    @Override
    public void move() {
        Pathfinding.predicate = location -> true;
        if (tryKiteFromEnemies()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 0 || MapCache.hasAdjacentUnpassable(location);
        if (tryMoveToPickupAnchor()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 1 || MapCache.hasAdjacentUnpassable(location);
        if (tryMoveToPlaceAnchorOnIsland()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 0 || MapCache.hasAdjacentUnpassable(location);
        if (tryMoveToTransferResourceToHQ()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 1 || MapCache.hasAdjacentUnpassable(location);
        if (tryMoveToWell()) {
            return;
        }
        Pathfinding.predicate = location -> true;
        Util.tryExplore();
    }

    @Override
    public void action() {
        if (tryPickupAnchorFromHQ()) {
            return;
        }
        if (tryPlaceAnchorOnIsland()) {
            return;
        }
        if (tryCollectResource()) {
            return;
        }
        if (tryTransferResourceToHQ()) {
            return;
        }
    }

    public static boolean tryMoveToWell() {
        if (capacityLeft() <= 0) {
            return false;
        }
        // go to commed well?
        // look for well
        ResourceType targetResource = Communication.CarrierTask.getMineResourceType(currentTask);
//        WellInfo well = targetResource == null ? getClosestWell() : getClosestWell(targetResource);
        // go to commed well
        MapLocation commedWell = targetResource == null ?
                WellTracker.getClosestKnownWell(location -> !blacklist.contains(location)) :
                WellTracker.getClosestKnownWell(targetResource, location -> !blacklist.contains(location));
        if (commedWell != null) {
            if (!Cache.MY_LOCATION.isAdjacentTo(commedWell)) {
                if (Util.numAllyRobotsWithin(commedWell, 5) >= 12) {
                    // blacklist from future
                    blacklist.add(commedWell);
                }
            }
            Util.tryPathfindingMoveAdjacent(commedWell);
            return true;
        }
        return false;
//        WellInfo well = getWell();
//        if (well == null) {
//            return false;
//        } else {
//            MapLocation wellLocation = well.getMapLocation();
//            if (!Cache.MY_LOCATION.isAdjacentTo(wellLocation)) {
//                if (Util.numAllyRobotsWithin(wellLocation, 5) >= 12) {
//                    // blacklist from future
//                    blacklist.add(wellLocation);
//                }
//            }
//            Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, wellLocation, 0, 128, 0); // dark green
//            // move towards well
//            Util.tryPathfindingMoveAdjacent(well.getMapLocation());
//            return true;
//        }
    }

    public static boolean tryCollectResource() {
        if (capacityLeft() <= 0) {
            return false;
        }
        // try mine
        WellInfo well = getWell();
        if (well != null) {
            MapLocation wellLocation = well.getMapLocation();
            if (Cache.MY_LOCATION.isAdjacentTo(wellLocation)) {
                tryCollectResource(wellLocation, Math.min(well.getRate(), capacityLeft()));
                return true;
            }
        }
        return false;
    }

    public static boolean tryKiteFromEnemies() {
        RobotInfo closestAttacker = Util.getClosestEnemyRobot(r -> Util.isAttacker(r.type));
        if (closestAttacker == null) {
            return false;
        }
        Util.tryKiteFrom(closestAttacker.location);
        return true;
    }

    public static boolean tryPickupAnchorFromHQ() {
        if (getWeight() == 0 && currentTask != null
                && currentTask.type == Communication.CarrierTaskType.PICKUP_ANCHOR) {
            MapLocation location = currentTask.hqLocation;
            if (Cache.MY_LOCATION.isAdjacentTo(location)) {
                tryTakeAnchor(location, Anchor.ACCELERATING);
                tryTakeAnchor(location, Anchor.STANDARD);
            }
            return true;
        }
        return false;
    }

    public static boolean tryMoveToPickupAnchor() {
        if (getWeight() == 0 && currentTask != null
                && currentTask.type == Communication.CarrierTaskType.PICKUP_ANCHOR) {
            MapLocation location = currentTask.hqLocation;
            if (Cache.MY_LOCATION.isAdjacentTo(location)) {
                // check if there's actually an anchor there
                try {
                    RobotInfo hq = rc.senseRobotAtLocation(location);
                    if (hq == null) {
                        Debug.failFast("Cannot find hq?");
                    } else {
                        if (hq.getTotalAnchors() == 0) {
                            // no anchors?
                            currentTask = null;
                            return false;
                        }
                    }
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            } else {
                Util.tryPathfindingMoveAdjacent(location);
            }
            return true;
        }
        return false;
    }

    public static boolean tryPlaceAnchorOnIsland() {
        try {
            if (rc.getAnchor() == null) {
                return false;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        MapLocation islandLocation = findClosestUnoccupiedNonAllyIsland();
        if (islandLocation == null) {
            // TODO: go to commed islands?
            return false;
        } else {
            if (islandLocation.equals(Cache.MY_LOCATION)) {
                tryPlaceAnchor();
            } else {
                Util.tryPathfindingMove(islandLocation);
            }
            return true;
        }
    }

    public static boolean tryMoveToPlaceAnchorOnIsland() {
        try {
            if (rc.getAnchor() == null) {
                return false;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        try {
            int islandId = rc.senseIsland(Cache.MY_LOCATION);
            if (islandId != -1 && rc.senseTeamOccupyingIsland(islandId) == Team.NEUTRAL) {
                tryPlaceAnchor();
                return true;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        return false;
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
                if (rc.senseTeamOccupyingIsland(islandId) != Constants.ALLY_TEAM) { // TODO: only target neutral?
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

    public static boolean tryMoveToTransferResourceToHQ() {
        try {
            if (capacityLeft() > 0 || rc.getAnchor() != null) {
                return false;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        MapLocation hqLocation = Util.getClosestAllyHeadquartersLocation(); // TODO: choose safe HQ?
        if (hqLocation == null) {
            return false;
        }
        Util.tryPathfindingMoveAdjacent(hqLocation);
        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, hqLocation, 255, 255, 0); // yellow
        return true;
    }

    public static boolean tryTransferResourceToHQ() {
        MapLocation hqLocation = Util.getClosestAllyHeadquartersLocation();
        if (hqLocation == null) {
            return false;
        }
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

    public static WellInfo getClosestWell(ResourceType type) {
        return getClosestWell(rc.senseNearbyWells(type));
    }

    public static WellInfo getClosestWell() {
        return getClosestWell(rc.senseNearbyWells());
    }

    public static WellInfo getClosestWell(WellInfo[] wells) {
        WellInfo bestWell = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = wells.length; --i >= 0; ) {
            WellInfo well = wells[i];
            int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(well.getMapLocation());
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestWell = well;
            }
        }
        return bestWell;
    }

    // TODO-someday: to be removed
    public static WellInfo getWell() {
        WellInfo[] wells = Cache.ADAMANTIUM_WELLS;
        WellInfo bestWell = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = wells.length; --i >= 0; ) {
            WellInfo well = wells[i];
            MapLocation wellLocation = well.getMapLocation();
            if (!blacklist.contains(wellLocation)) {
                int distanceSquared = wellLocation.distanceSquaredTo(Cache.MY_LOCATION);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestWell = well;
                }
            }
        }
        if (bestWell != null) {
            return bestWell;
        }
        wells = Cache.MANA_WELLS;
        for (int i = wells.length; --i >= 0; ) {
            WellInfo well = wells[i];
            MapLocation wellLocation = well.getMapLocation();
            if (!blacklist.contains(wellLocation)) {
                int distanceSquared = wellLocation.distanceSquaredTo(Cache.MY_LOCATION);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestWell = well;
                }
            }
        }
        if (bestWell != null) {
            return bestWell;
        }
        wells = Cache.ELIXIR_WELLS;
        for (int i = wells.length; --i >= 0; ) {
            WellInfo well = wells[i];
            MapLocation wellLocation = well.getMapLocation();
            if (!blacklist.contains(wellLocation)) {
                int distanceSquared = wellLocation.distanceSquaredTo(Cache.MY_LOCATION);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestWell = well;
                }
            }
        }
        return bestWell;
    }
}
