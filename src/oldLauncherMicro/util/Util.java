package oldLauncherMicro.util;

import battlecode.common.*;
import oldLauncherMicro.pathfinder.Pathfinding;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static oldLauncherMicro.util.Constants.rc;

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

    public static boolean tryPathfindingMove(MapLocation loc) {
        return Pathfinding.execute(loc);
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

    public static void tryKiteFrom(MapLocation location) throws GameActionException {
        throw new IllegalStateException("Unimplemented");
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
}