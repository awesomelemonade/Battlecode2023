package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static sprintBot.util.Constants.rc;

public class EnemyHqGuesser {
    private static boolean initialized = false;
    private static MapLocation[] predictions;
    private static int invalidations; // commed invalidations - bit field
    private static int invalidationsPending; // bit field

    public static void generateHQGuessList() {
        MapLocation[] hqLocations = Communication.headquartersLocations;
        int numHqLocations = hqLocations.length;
        predictions = new MapLocation[3 * numHqLocations];

        for (int i = hqLocations.length; --i >= 0; ) {
            guessEnemyArchonLocations(hqLocations[i], i);
        }
        initialized = true;
    }

    public static void guessEnemyArchonLocations(MapLocation location, int hqIndex) {
        int x = location.x;
        int y = location.y;
        int symX = Constants.MAP_WIDTH - x - 1;
        int symY = Constants.MAP_HEIGHT - y - 1;
        predictions[hqIndex * 3] = new MapLocation(x, symY);
        predictions[hqIndex * 3 + 1] = new MapLocation(symX, y);
        predictions[hqIndex * 3 + 2] = new MapLocation(symX, symY);
    }

    public static void invalidatePending(int index) {
        invalidationsPending = invalidationsPending | (1 << index);
    }

    public static boolean invalidated(int index) {
        return ((invalidations | invalidationsPending) & (1 << index)) != 0;
    }

    public static void update() {
        if (!initialized) {
            return;
        }
        // receive from communication
        try {
            invalidations = rc.readSharedArray(Communication.ENEMY_HQ_GUESSER_OFFSET);
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }

        // traverse and remove any that are visible and not there
        for (int i = predictions.length; --i >= 0; ) {
            if (invalidated(i)) {
                continue;
            }
            MapLocation prediction = predictions[i];
            if (rc.canSenseLocation(prediction)) {
                try {
                    RobotInfo robot = rc.senseRobotAtLocation(prediction);
                    boolean hasEnemyHeadquarters = robot != null && robot.type == RobotType.HEADQUARTERS && robot.team == Constants.ENEMY_TEAM;
                    if (!hasEnemyHeadquarters) {
                        invalidatePending(i);
                    }
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
        }

        // write to communication
        if (rc.canWriteSharedArray(0, 0)) {
            try {
                rc.writeSharedArray(Communication.ENEMY_HQ_GUESSER_OFFSET, invalidations | invalidationsPending);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
    }

    public static MapLocation getClosest() {
        return getClosest(location -> true);
    }

    public static MapLocation getClosest(Predicate<MapLocation> predicate) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!invalidated(i)) {
                MapLocation location = predictions[i];
                if (predicate.test(location)) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = location;
                    }
                }
            }
        }
        return bestLocation;
    }

    public static void forEach(Consumer<MapLocation> consumer) {
        for (int i = predictions.length; --i >= 0; ) {
            if ((invalidations & (1 << i)) == 0 && (invalidationsPending & (1 << i)) == 0) {
                consumer.accept(predictions[i]);
            }
        }
    }

    public static void forEachPendingInvalidations(Consumer<MapLocation> consumer) {
        for (int i = predictions.length; --i >= 0; ) {
            if ((invalidations & (1 << i)) == 0 && (invalidationsPending & (1 << i)) != 0) {
                consumer.accept(predictions[i]);
            }
        }
    }
}
