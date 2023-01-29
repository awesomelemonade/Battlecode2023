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
        HasAdjacentUnpassableCache.init();
    }

    private static void debug_render() {
        if (Profile.MINING.enabled()) {
            WellTracker.forEachPending(location -> Debug.setIndicatorDot(Profile.MINING, location.add(Direction.SOUTH), 0, 255, 255)); // cyan
            WellTracker.forEachKnown(location -> Debug.setIndicatorDot(Profile.MINING, location, 0, 0, 255)); // blue
            MapLocation knownManaWell = WellTracker.getKnownWell(ResourceType.MANA);
            if (knownManaWell != null) {
                Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, knownManaWell, 0, 0, 255); // blue
            }
            MapLocation pendingManaWell = WellTracker.getPendingWell(ResourceType.MANA);
            if (pendingManaWell != null) {
                Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, pendingManaWell, 0, 255, 255); // cyan
            }
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
        if (tryMoveToAttack()) {
            return;
        }
        if (tryKiteFromEnemies()) {
            return;
        }
//        if (tryMoveToCommunicatePendingManaWell()) {
//            return;
//        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 0 || HasAdjacentUnpassableCache.hasAdjacentUnpassable(location);
        if (tryMoveToPickupAnchor()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 1 || HasAdjacentUnpassableCache.hasAdjacentUnpassable(location);
        if (tryMoveToPlaceAnchorOnIsland()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 0 || HasAdjacentUnpassableCache.hasAdjacentUnpassable(location);
        if (tryMoveToTransferResourceToHQ()) {
            return;
        }
        Pathfinding.predicate = location -> (location.x + location.y) % 2 == 1 || HasAdjacentUnpassableCache.hasAdjacentUnpassable(location);
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
        if (tryAttack()) {
            return;
        }
        if (tryCollectResource()) {
            return;
        }
        if (tryTransferResourceToHQ()) {
            return;
        }
    }

    @Override
    public void postLoop() throws GameActionException {
//        // if we have bytecodes, just precalculate stuff
//        int x = Cache.MY_LOCATION.x;
//        int y = Cache.MY_LOCATION.y;
//        // each one takes ~250 bytecodes approximately
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 0, y + 0)); }
//        // ordinal directions
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 1, y + 0)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 0, y + 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 1, y + 0)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 0, y - 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 1, y + 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 1, y - 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 1, y - 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 1, y + 1)); }
//        // squares 2 away
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 2, y + 0)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 2, y + 0)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 0, y - 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 0, y + 2)); }
//
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 2, y + 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 2, y + 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 1, y - 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 1, y + 2)); }
//
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 2, y - 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 2, y - 1)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 1, y - 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 1, y + 2)); }
//
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 2, y - 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 2, y - 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x - 2, y + 2)); }
//        if (Clock.getBytecodesLeft() > 500) { MapCache.precalculate(new MapLocation(x + 2, y + 2)); }
    }

    public boolean tryMoveToCommunicatePendingManaWell() {
        if (rc.getWeight() > 20) {
            return false;
        }
        MapLocation pendingManaWell = WellTracker.getPendingWell(ResourceType.MANA);
        if (pendingManaWell == null) {
            return false;
        }
        int ourDistanceSquared = Cache.MY_LOCATION.distanceSquaredTo(pendingManaWell);

        // if there's a carrier that can see this well and is farther
        if (LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS, r -> {
            int distanceSquared = r.location.distanceSquaredTo(pendingManaWell);
            return r.type == RobotType.CARRIER && distanceSquared > ourDistanceSquared && distanceSquared <= RobotType.CARRIER.visionRadiusSquared;
        })) {
            return false;
        }
        Flags.flag(Flags.CARRIER_MOVE_TO_COMMUNICATE_PENDING_MANA_WELL);
        Util.tryPathfindingMoveAdjacent(Cache.NEAREST_ALLY_HQ);
        return true;
    }

    public static boolean willDie() {
        // look at enemy launchers, add up damage, and see if it equals or exceeds our health
        int totalDamage = 0;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (enemy.type == RobotType.LAUNCHER) {
                totalDamage += RobotType.LAUNCHER.damage;
            }
        }
        if (totalDamage < rc.getHealth()) {
            return false;
        }
        if (Cache.ALLY_ROBOTS.length >= 20
                || Util.numAllyAttackersWithin(Cache.MY_LOCATION, RobotType.CARRIER.visionRadiusSquared) >= 5) {
            // we probably won't die with this many allies
            return false;
        }
        return true;
    }

    public static MapLocation getImmediateAttackTarget() {
        double bestScore = Integer.MIN_VALUE;
        MapLocation bestEnemyLocation = null;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            MapLocation enemyLocation = enemy.location;
            if (!rc.canAttack(enemy.location)) {
                continue;
            }
            double score = getImmediateAttackScore(enemy);
            if (score > bestScore) {
                bestScore = score;
                bestEnemyLocation = enemyLocation;
            }
        }
        return bestEnemyLocation;
    }

    public static double getImmediateAttackScore(RobotInfo enemy) {
        int health = enemy.health;
        int attacksForLaunchersToKill = ((health - (5 * rc.getWeight() / 4)) + RobotType.LAUNCHER.damage - 1) / RobotType.LAUNCHER.damage;

        // prioritize attackers, then launchers vs destabilizers, then attacksForLaunchToKill, then health
        double score = 0;
        if (Util.isAttacker(enemy.type)) {
            score += 10000000;
        }
        if (enemy.type == RobotType.LAUNCHER) {
            score += 1000000;
        }
        score -= attacksForLaunchersToKill * 1000;
        score -= health;

        return score;
    }

    public static boolean shouldAttack() {
        // if we see a lot of enemy units
        return (Cache.ENEMY_ROBOTS.length >= 8 || willDie()) && rc.getWeight() > 0;
    }

    public static boolean tryMoveToAttack() {
        if (shouldAttack()) {
            // find any location that allows us to be in range to attack
            // TODO
            RobotInfo enemyRobot = Util.getClosestEnemyRobot(r -> r.type != RobotType.HEADQUARTERS);
            if (enemyRobot != null) {
                Util.tryPathfindingMove(enemyRobot.location);
                return true;
            }
//            MapLocation closestEnemyLocation = null;
//            int closestEnemyDistanceSquared = Integer.MAX_VALUE;
//            MapLocation closestEnemyLauncherLocation = null;
//            int closestEnemyLauncherDistanceSquared = Integer.MAX_VALUE;
//            for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
//                RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
//                MapLocation enemyLocation = enemy.location;
//                int distanceSquared = enemyLocation.distanceSquaredTo(Cache.MY_LOCATION);
//                switch (enemy.type) {
//                    case HEADQUARTERS:
//                        break;
//                    case LAUNCHER:
//                        if (distanceSquared < closestEnemyLauncherDistanceSquared) {
//                            closestEnemyLauncherDistanceSquared = distanceSquared;
//                            closestEnemyLauncherLocation = enemyLocation;
//                        }
//                        // don't break; on purpose
//                    default:
//                        if (distanceSquared < closestEnemyDistanceSquared) {
//                            closestEnemyDistanceSquared = distanceSquared;
//                            closestEnemyLocation = enemyLocation;
//                        }
//                }
//            }
        }
        return false;
    }

    public static boolean tryAttack() {
        if (shouldAttack()) {
            MapLocation target = getImmediateAttackTarget();
            if (target != null) {
                if (rc.canAttack(target)) {
                    try {
                        rc.attack(target);
                        return true;
                    } catch (GameActionException ex) {
                        Debug.failFast(ex);
                    }
                } else {
                    Debug.failFast("Cannot attack immediate target?");
                }
            }
        }
        return false;
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
        MapLocation commedWell;
        if (targetResource == null) {
            commedWell = WellTracker.getClosestWell(location -> !blacklist.contains(location));
        } else {
            commedWell = WellTracker.getClosestWell(targetResource, location -> !blacklist.contains(location));
            if (commedWell == null && targetResource == ResourceType.ADAMANTIUM) {
                commedWell = WellTracker.getClosestWell(location -> !blacklist.contains(location));
                Flags.flag(Flags.CARRIER_GAVE_UP_TARGET_RESOURCE);
            }
        }
        if (commedWell != null) {
            Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, commedWell, 0, 128, 0); // dark green
            if (!Cache.MY_LOCATION.isAdjacentTo(commedWell)) {
                if (Util.numAllyRobotsWithin(commedWell, 5) >= 12) {
                    // blacklist from future
                    blacklist.add(commedWell);
                }
            }
            Util.tryPathfindingMoveAdjacentCheckCurrents(commedWell);
            return true;
        }
        return false;
    }

    // bigger score = better
    public static double getImmediateWellMiningScore(WellInfo well) {
        ResourceType wellResourceType = well.getResourceType();
        double score = 0;
        // prefer the resource given by the task
        if (currentTask != null && Communication.CarrierTask.getMineResourceType(currentTask) == wellResourceType) {
            score += 1000000;
        }
        // prefer the resource you already have
        score += rc.getResourceAmount(wellResourceType) * 1000;
        // prefer adamantium > mana > elixir
        switch (wellResourceType) {
            case ADAMANTIUM:
                score += 3;
                break;
            case MANA:
                score += 2;
                break;
            case ELIXIR:
                score += 1;
                break;
            default:
                Debug.failFast("Unknown well resource type: " + wellResourceType);
        }
        return score;
    }

    public static boolean tryCollectResource() {
        if (capacityLeft() <= 0) {
            return false;
        }
        try {
            MapLocation allyHqLocation = Cache.NEAREST_ALLY_HQ;
            boolean adjacentToAllyHq = allyHqLocation != null && Cache.MY_LOCATION.isAdjacentTo(allyHqLocation);
            boolean isEmpty = rc.getWeight() == 0;
            boolean canMineAll = !adjacentToAllyHq || isEmpty;
            boolean canMineAdamantium = canMineAll || rc.getResourceAmount(ResourceType.ADAMANTIUM) > 0;
            boolean canMineMana = canMineAll || rc.getResourceAmount(ResourceType.MANA) > 0;
            boolean canMineElixir = canMineAll || rc.getResourceAmount(ResourceType.ELIXIR) > 0;
            // only look at wells adjacent to you
            WellInfo[] adjacentWells = rc.senseNearbyWells(2);
            // get best well
            double bestScore = -Double.MAX_VALUE;
            WellInfo bestWell = null;
            for (int i = adjacentWells.length; --i >= 0; ) {
                WellInfo well = adjacentWells[i];
                switch (well.getResourceType()) {
                    case ADAMANTIUM:
                        if (!canMineAdamantium) {
                            continue;
                        }
                        break;
                    case MANA:
                        if (!canMineMana) {
                            continue;
                        }
                        break;
                    case ELIXIR:
                        if (!canMineElixir) {
                            continue;
                        }
                        break;
                    default:
                        Debug.failFast("Unknown resource in well");
                }
                double score = getImmediateWellMiningScore(well);
                if (score > bestScore) {
                    bestScore = score;
                    bestWell = well;
                }
            }
            if (bestWell != null) {
                MapLocation wellLocation = bestWell.getMapLocation();
                tryCollectResource(wellLocation, Math.min(bestWell.getRate(), capacityLeft()));
                return true;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
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
            MapLocation hqLocation = currentTask.hqLocation;
            if (Cache.MY_LOCATION.isAdjacentTo(hqLocation)) {
                // check if there's actually an anchor there
                try {
                    RobotInfo hq = rc.senseRobotAtLocation(hqLocation);
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
                Util.tryPathfindingMoveAdjacent(hqLocation);
            }
            return true;
        }
        return false;
    }

    public static boolean tryMoveToPlaceAnchorOnIsland() {
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
            if (!islandLocation.equals(Cache.MY_LOCATION)) {
                Util.tryPathfindingMove(islandLocation);
            }
            return true;
        }
    }

    public static boolean tryPlaceAnchorOnIsland() {
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
                        if (location.equals(Cache.MY_LOCATION) || !rc.canSenseRobotAtLocation(location)) {
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
        return GameConstants.CARRIER_CAPACITY - rc.getWeight();
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
            if (rc.getAnchor() != null) {
                return false;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }
        MapLocation hqLocation = Cache.NEAREST_ALLY_HQ; // TODO: choose safe HQ?
        if (hqLocation == null) {
            return false;
        }
        if (Cache.MY_LOCATION.isAdjacentTo(hqLocation)) {
            if (getWeight() == 0) { // we might as well wait a turn to transfer what's there
                return false;
            }
        } else {
            if (capacityLeft() > 0) {
                return false;
            }
        }
        Util.tryPathfindingMoveAdjacent(hqLocation);
        Debug.setIndicatorLine(Profile.MINING, Cache.MY_LOCATION, hqLocation, 255, 255, 0); // yellow
        return true;
    }

    public static boolean tryTransferResourceToHQ() {
        MapLocation hqLocation = Cache.NEAREST_ALLY_HQ;
        if (hqLocation == null) {
            return false;
        }
        // see if in range
        if (!Cache.MY_LOCATION.isAdjacentTo(hqLocation)) {
            return false;
        }
        ResourceType resource = getTransferToHQResource();
        if (resource == ResourceType.NO_RESOURCE) {
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
}
