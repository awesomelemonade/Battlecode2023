package sprintBot.util;

import battlecode.common.*;
import sprintBot.pathfinder.Pathfinding;

import static sprintBot.util.Constants.rc;

public class Explorer {
    private static Direction previousDirection = Util.randomAdjacentDirection();

    public static double currentExploreDirection = -1.0;

    public static void init() throws GameActionException {
        if (!Util.isBuilding(Constants.ROBOT_TYPE)) {
            currentExploreDirection = getInitialExploreDirection();
        }
    }

    public static double getInitialExploreDirection() {
        int x = (int) (Math.random() * Constants.MAP_WIDTH);
        int y = (int) (Math.random() * Constants.MAP_HEIGHT);
        return Math.atan2(y - Cache.MY_LOCATION.y, x - Cache.MY_LOCATION.x);
    }

    public static double getNewExploreDirection() {
        return 2.0 * Math.PI * Math.random();
    }

    public static boolean randomExplore() {
        Debug.setIndicatorDot(Profile.EXPLORER, Cache.MY_LOCATION, 255, 128, 0); // orange
        Direction bestDirection = null;
        int minAllies = Integer.MAX_VALUE;
        for (Direction direction : Constants.getAttemptOrder(previousDirection)) {
            if (Util.canMoveAndCheckCurrents(direction)) {
                MapLocation next = Cache.MY_LOCATION.add(direction);
                int numAllies = Util.numAllyRobotsWithin(next, 10);
                if (numAllies < minAllies) {
                    bestDirection = direction;
                    minAllies = numAllies;
                }
            }
        }
        if (bestDirection != null) {
            Util.move(bestDirection);
            previousDirection = bestDirection;
            return true;
        }
        // traffic jam
        return false;
    }


    public static boolean smartExplore() {
        Debug.setIndicatorDot(Profile.EXPLORER, Cache.MY_LOCATION, 255, 128, 0); // orange
        if (currentExploreDirection < 0 || reachedBorder(currentExploreDirection)) {
            boolean noNewDirection = true;
            for (int i = 20; --i >= 0;) { // Only attempt 20 times
                double potentialDirection = getNewExploreDirection();
                if (currentExploreDirection >= 0) {
                    double angleBetween = angleBetween(currentExploreDirection, potentialDirection);
                    // checks that the potentialDirection is not in the opposite direction as exploreDirection
                    if (angleBetween > Math.PI - Math.PI / 6.0) {
                        continue;
                    }
                }
                if (reachedBorder(potentialDirection)) {
                    continue;
                }
                currentExploreDirection = potentialDirection;
                noNewDirection = false;
                break;
            }
            if (noNewDirection) {
                currentExploreDirection = -1.0;
            }
        }
        if (currentExploreDirection < 0) {
            return randomExplore();
        } else {
            MapLocation target = getExploreLocation();
            Debug.setIndicatorLine(Profile.EXPLORER, Cache.MY_LOCATION, target, 255, 128, 0);
//            return Util.tryPathfindingMove(target);
            return Pathfinding.executeResetIfNotAdjacent(target);
        }
    }

    public static MapLocation getExploreLocation() {
        double cos = Math.cos(currentExploreDirection);
        double sin = Math.sin(currentExploreDirection);
        return Cache.MY_LOCATION.translate((int) (cos * 20.0), (int) (sin * 20.0));
    }

    private static final Double[] onTheMapProbeLengths = { 2.0, 2.0, 2.0 };
    private static final Double[] onTheMapProbeAngles = { -Math.PI/6, 0.0, Math.PI/6 };
    public static boolean reachedBorder(double direction) {
        // On the map probing
        for (int i = onTheMapProbeLengths.length; --i >= 0; ) {
            double x = Math.cos(direction + onTheMapProbeAngles[i]) * onTheMapProbeLengths[i];
            double y = Math.sin(direction + onTheMapProbeAngles[i]) * onTheMapProbeLengths[i];
            if (!Util.onTheMap(Cache.MY_LOCATION.translate((int)Math.round(x), (int)Math.round(y)))) return true;
        }
        return false;
    }

    public static double angleBetween(double a, double b) {
        double angle = Math.abs(b - a);
        return Math.min(angle, 2 * Math.PI - angle);
    }
}