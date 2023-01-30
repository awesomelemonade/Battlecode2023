package beforeEarlierAnchors.util;

import battlecode.common.*;
import beforeEarlierAnchors.pathfinder.Pathfinding;

public class Explorer {
    private static Direction[] buffer = new Direction[8];
    private static int bufferLength = 0;
    private static Direction exploreDirection = Util.randomAdjacentDirection();

    public static void init() throws GameActionException {
    }

    public static boolean smartExplore() {
        Debug.setIndicatorDot(Profile.EXPLORER, Cache.MY_LOCATION, 255, 128, 0); // orange
        if (!Util.canMoveAndCheckCurrents(exploreDirection) || !Pathfinding.predicate.test(Cache.MY_LOCATION.add(exploreDirection))) {
            // find new direction
            bufferLength = 0;
            for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
                Direction direction = Constants.ORDINAL_DIRECTIONS[i];
                // new direction cannot be directly opposite of previous direction
                if (direction.equals(exploreDirection) || direction.opposite().equals(exploreDirection)) {
                    continue;
                }
                if (!Util.canMoveAndCheckCurrents(direction) || !Pathfinding.predicate.test(Cache.MY_LOCATION.add(direction))) {
                    continue;
                }
                // possible valid direction
                buffer[bufferLength++] = direction;
            }
            if (bufferLength == 0) {
                // can't explore :(
                exploreDirection = Util.randomAdjacentDirection();
            } else {
                exploreDirection = buffer[(int) (Math.random() * bufferLength)];
            }
        }
        if (Util.canMoveAndCheckCurrents(exploreDirection) && Pathfinding.predicate.test(Cache.MY_LOCATION.add(exploreDirection))) {
            Util.move(exploreDirection);
            return true;
        }
        return false;
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