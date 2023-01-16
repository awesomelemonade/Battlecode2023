package sprintTesting8.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.function.Consumer;

import static sprintTesting8.util.Constants.rc;

public class EnemyHqGuesser {
    private static final int NUM_POSSIBLE_SYMMETRIES = 3;
    private static boolean initialized = false;
    private static MapLocation[] predictions;
    private static int invalidations; // commed invalidations - bit field
    private static int invalidationsPending; // bit field

    public static void generateHQGuessList() {
        MapLocation[] hqLocations = Communication.headquartersLocations;
        int numHqLocations = hqLocations.length;
        predictions = new MapLocation[NUM_POSSIBLE_SYMMETRIES * numHqLocations];

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
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES] = new MapLocation(x, symY);
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES + 1] = new MapLocation(symX, y);
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES + 2] = new MapLocation(symX, symY);
    }

    public static void invalidatePending(int index) {
        invalidationsPending = invalidationsPending | (1 << index);
    }

    public static boolean invalidated(int index) {
        return ((invalidations | invalidationsPending) & (1 << index)) != 0;
    }

    // symmetry = 0, 1, 2; representing index % 3
    public static boolean isSymmetryPossible(int symmetry) {
        for (int i = symmetry; i < predictions.length; i += NUM_POSSIBLE_SYMMETRIES) {
            if (invalidated(i)) {
                return false;
            }
        }
        // look at all known enemy hq, see if all the known and pending enemy HQ locations exists in symmetry
        return EnemyHqTracker.allKnownAndPending(location -> existsInSymmetry(location, symmetry));
    }

    public static boolean existsInSymmetry(MapLocation location, int symmetry) {
        for (int i = symmetry; i < predictions.length; i += NUM_POSSIBLE_SYMMETRIES) {
            MapLocation prediction = predictions[i];
            if (location.equals(prediction)) {
                return true;
            }
        }
        return true;
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

        // eliminate symmetries - but don't do this in carriers to save bytecodes
        if (Constants.ROBOT_TYPE != RobotType.CARRIER) {
            int possibleSymmetry = -1;
            int numPossibleSymmetries = 0;
            for (int i = NUM_POSSIBLE_SYMMETRIES; --i >= 0; ) {
                if (isSymmetryPossible(i)) {
                    possibleSymmetry = i;
                    numPossibleSymmetries++;
                } else {
                    for (int j = i; j < predictions.length; j += NUM_POSSIBLE_SYMMETRIES) {
                        invalidatePending(j);
                    }
                }
            }
            // check if only one symmetry is possible
            if (numPossibleSymmetries == 1) {
                for (int j = possibleSymmetry; j < predictions.length; j += NUM_POSSIBLE_SYMMETRIES) {
                    EnemyHqTracker.markKnownEnemyHQ(predictions[j]);
                }
            }
        }

        // write to communication
        if (rc.canWriteSharedArray(0, 0)) {
            int write = invalidations | invalidationsPending;
            if (invalidations != write) {
                try {
                    rc.writeSharedArray(Communication.ENEMY_HQ_GUESSER_OFFSET, invalidations | invalidationsPending);
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
        }
    }

    public static MapLocation getClosest() {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!invalidated(i)) {
                MapLocation location = predictions[i];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        return bestLocation;
    }

    public static MapLocation getFarthest(MapLocation from) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MIN_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!invalidated(i)) {
                MapLocation location = predictions[i];
                int distanceSquared = from.distanceSquaredTo(location);
                if (distanceSquared > bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
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
