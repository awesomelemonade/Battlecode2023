package noCheckerboardNoAdjacent.util;

import battlecode.common.*;
import noCheckerboardNoAdjacent.pathfinder.Pathfinding;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static noCheckerboardNoAdjacent.util.Constants.rc;

public class Util {

    public static void init(RobotController controller) throws GameActionException {
        Constants.init(controller);
        Random.init();
        Cache.init();
        Communication.init();
        Explorer.init();
        Pathfinding.init();
    }

    public static void loop() throws GameActionException {
        Cache.loop();
        Communication.loop();
    }

    public static void postLoop() throws GameActionException {
        Communication.postLoop();
    }

    public static Direction random(Direction[] directions) {
        return directions[(int) (Math.random() * directions.length)];
    }

    public static <T> T random(T[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    public static Direction randomAdjacentDirection() {
        return random(Constants.ORDINAL_DIRECTIONS);
    }

    public static boolean isAttacker(RobotType type) {
        switch (type) {
            case LAUNCHER:
            case DESTABILIZER:
                return true;
            default:
                return false;
        }
    }

    public static boolean isBuilding(RobotType type) {
        switch (type) {
            case HEADQUARTERS:
                return true;
            default:
                return false;
        }
    }

    public static MapLocation getClosestMapLocation(MapLocation[] locations) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        for (int i = locations.length; --i >= 0; ) {
            MapLocation location = locations[i];
            int distanceSquared = location.distanceSquaredTo(Cache.MY_LOCATION);
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    public static MapLocation getClosestMapLocation(MapLocation[] locations, BiPredicate<Integer, MapLocation> predicate) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        for (int i = locations.length; --i >= 0; ) {
            MapLocation location = locations[i];
            if (predicate.test(i, location)) {
                int distanceSquared = location.distanceSquaredTo(Cache.MY_LOCATION);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        return bestLocation;
    }

    public static RobotInfo getClosestRobot(RobotInfo[] robots, Predicate<RobotInfo> filter) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        RobotInfo bestRobot = null;
        for (RobotInfo robot : robots) {
            if (filter.test(robot)) {
                int distanceSquared = robot.getLocation().distanceSquaredTo(Cache.MY_LOCATION);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestRobot = robot;
                }
            }
        }
        return bestRobot;
    }

    public static RobotInfo getClosestEnemyRobot(MapLocation location, int limit, Predicate<RobotInfo> filter) {
        int bestDistanceSquared = limit + 1;
        RobotInfo bestRobot = null;
        for (RobotInfo enemy : Cache.ENEMY_ROBOTS) {
            if (filter.test(enemy)) {
                int distanceSquared = enemy.getLocation().distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestRobot = enemy;
                }
            }
        }
        return bestRobot;
    }

    public static RobotInfo getClosestEnemyRobot(Predicate<RobotInfo> filter) {
        return getClosestEnemyRobot(rc.getLocation(), Constants.MAX_DISTANCE_SQUARED, filter);
    }

    public static RobotInfo getClosestEnemyRobot() {
        int bestDistanceSquared = Integer.MAX_VALUE;
        RobotInfo bestRobot = null;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            int distanceSquared = enemy.getLocation().distanceSquaredTo(rc.getLocation());
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestRobot = enemy;
            }
        }
        return bestRobot;
    }

    public static MapLocation getClosestAllyHeadquartersLocation() {
        if (Communication.headquartersLocations == null) {
            RobotInfo hq = Util.getClosestRobot(Cache.ALLY_ROBOTS, robot -> robot.type == RobotType.HEADQUARTERS);
            return hq == null ? null : hq.location;
        } else {
            int bestDistanceSquared = Integer.MAX_VALUE;
            MapLocation bestLocation = null;
            for (int i = Communication.headquartersLocations.length; --i >= 0; ) {
                MapLocation location = Communication.headquartersLocations[i];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
            return bestLocation;
        }
    }

    public static void move(Direction direction) {
        try {
            rc.move(direction);
            Cache.invalidate();
        } catch (GameActionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static boolean tryMove(Direction direction) {
        if (rc.canMove(direction)) {
            move(direction);
            return true;
        } else {
            return false;
        }
    }

    public static void tryPathfindingMove(MapLocation loc) {
        Pathfinding.execute(loc);
    }

    // tries to move on the location OR any location adjacent to it
    public static void tryPathfindingMoveAdjacent(MapLocation location) {
        Pathfinding.execute(location);
//        if (Cache.MY_LOCATION.isAdjacentTo(location)) {
//            // try to move on the location
//            if (!Cache.MY_LOCATION.equals(location)) {
//                Util.tryMove(Cache.MY_LOCATION.directionTo(location));
//            }
//        } else {
//            // try to move to the nearest adjacent spot
//            int bestDistanceSquared = Integer.MAX_VALUE;
//            MapLocation bestLocation = null;
//            for (Direction direction: Constants.ORDINAL_DIRECTIONS) {
//                MapLocation adjacentLocation = location.add(direction);
//                if (!rc.canSenseRobotAtLocation(adjacentLocation)) {
//                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(adjacentLocation);
//                    if (distanceSquared < bestDistanceSquared) {
//                        bestDistanceSquared = distanceSquared;
//                        bestLocation = adjacentLocation;
//                    }
//                }
//            }
//            if (bestLocation != null) {
//                Pathfinding.execute(bestLocation);
//            }
//        }
    }

    public static boolean tryMoveTowards(Direction direction) {
        for (Direction moveDirection : Constants.getAttemptOrder(direction)) {
            if (tryMove(moveDirection)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tryRandomMove() throws GameActionException {
        return tryMove(randomAdjacentDirection());
    }

    public static void tryKiteFrom(MapLocation location) {
        int bestDist = Cache.MY_LOCATION.distanceSquaredTo(location);
        Direction bestDir = null;
        for (Direction d : Constants.ORDINAL_DIRECTIONS) {
            MapLocation candidate = Cache.MY_LOCATION.add(d);
            if (!isEmptyTerrain(candidate)) continue;
            int dist = candidate.distanceSquaredTo(location);
            if (dist > bestDist) {
                bestDist = dist;
                bestDir = d;
            }
        }
        if (bestDir != null) tryMove(bestDir);
    }

    public static boolean tryExplore() {
        return Explorer.smartExplore();
    }

    public static boolean tryBuild(RobotType type, MapLocation location) {
        if (rc.canBuildRobot(type, location)) {
            try {
                rc.buildRobot(type, location);
                return true;
            } catch (GameActionException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return false;
    }

    public static int numAllyRobotsWithin(MapLocation location, int distanceSquared) {
        if (Cache.ALLY_ROBOTS.length >= 20) {
            try {
                return rc.senseNearbyRobots(location, distanceSquared, Constants.ALLY_TEAM).length;
            } catch (GameActionException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            // loop through robot list
            int count = 0;
            for (int i = Cache.ALLY_ROBOTS.length; --i >= 0; ) {
                if (location.isWithinDistanceSquared(Cache.ALLY_ROBOTS[i].getLocation(), distanceSquared)) {
                    count++;
                }
            }
            return count;
        }
    }

    public static <T> void shuffle(T[] array) {
        for (int i = array.length; --i >= 0; ) {
            int index = (int) (Math.random() * i);
            T temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static boolean onTheMap(MapLocation location) {
        int x = location.x;
        int y = location.y;
        return x >= 0 && y >= 0 && x < Constants.MAP_WIDTH && y < Constants.MAP_HEIGHT;
    }

    public static boolean isEmptyTerrain(MapLocation loc) {
        if (!onTheMap(loc)) return false;
        if (rc.canSenseLocation(loc)) {
            try {
                if (rc.sensePassability(loc) == false) return false;
                if (rc.senseRobotAtLocation(loc) != null) return false;
            } catch (GameActionException e) {
                Debug.failFast(e);
            }
        }
        return true;
    }

    public static int getWeight(RobotInfo robot) {
        return robot.getResourceAmount(ResourceType.ADAMANTIUM)
                + robot.getResourceAmount(ResourceType.MANA)
                + robot.getResourceAmount(ResourceType.ELIXIR)
                + robot.getTotalAnchors() * GameConstants.ANCHOR_WEIGHT;
    }
}