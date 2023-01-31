package finalBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static finalBot.util.Constants.rc;

public class EnemyHqGuesser {
    private static final int NUM_POSSIBLE_SYMMETRIES = 3;
    private static boolean initialized = false;
    private static MapLocation[] predictions;
    public static int invalidations; // commed invalidations - bit field
    private static int lastInvalidationsRead;

    public static MapLocation[] enemyHeadquartersLocations; // may include null (for unknown enemy headquarters)
    public static int numKnownEnemyHeadquarterLocations = 0;

    private static int confirmed; // commed confirmed - bit field
    private static int lastConfirmedRead;

    private static int knownSymmetry = -1;

    public static void generateHQGuessList() {
        if (initialized) {
            return;
        }
        MapLocation[] hqLocations = Communication.headquartersLocations;
        if (hqLocations == null) {
            return;
        }
        int numHqLocations = hqLocations.length;
        enemyHeadquartersLocations = new MapLocation[numHqLocations];
        predictions = new MapLocation[NUM_POSSIBLE_SYMMETRIES * numHqLocations];

        for (int i = hqLocations.length; --i >= 0; ) {
            guessEnemyHeadquartersLocations(hqLocations[i], i);
        }
        initialized = true;
    }

    public static void guessEnemyHeadquartersLocations(MapLocation location, int hqIndex) {
        int x = location.x;
        int y = location.y;
        int symX = Constants.MAP_WIDTH - x - 1;
        int symY = Constants.MAP_HEIGHT - y - 1;
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES] = new MapLocation(x, symY);
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES + 1] = new MapLocation(symX, y);
        predictions[hqIndex * NUM_POSSIBLE_SYMMETRIES + 2] = new MapLocation(symX, symY);
    }

    public static void addToEnemyHeadquartersLocations(MapLocation location) {
        // Must check because two symmetries can yield the same headquarters locations
        for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
            if (enemyHeadquartersLocations[i].equals(location)) {
                return; // already have it in our list
            }
        }
        enemyHeadquartersLocations[numKnownEnemyHeadquarterLocations++] = location;
    }

    public static void markKnownEnemyHQ(int index) {
        if ((confirmed & (1 << index)) == 0) {
            addToEnemyHeadquartersLocations(predictions[index]);
        }
        confirmed |= 1 << index;
    }

    public static boolean isConfirmedPrediction(int index) {
        return (confirmed & (1 << index)) != 0;
    }

    public static void invalidatePending(int index) {
        invalidations |= 1 << index;
    }

    public static boolean isInvalidatedPrediction(int index) {
        return (invalidations & (1 << index)) != 0;
    }

    // symmetry = 0, 1, 2; representing index % 3
    public static boolean isSymmetryPossible(int symmetry) {
        for (int i = predictions.length; --i >= 0; ) {
            if (i % NUM_POSSIBLE_SYMMETRIES == symmetry) {
                // must not be invalidated
                if (isInvalidatedPrediction(i)) {
                    return false;
                }
            } else {
                if (isConfirmedPrediction(i)) {
                    // if it is confirmed, it has to be within "symmetry" too
                    boolean withinSymmetry = false;
                    for (int j = symmetry; j < predictions.length; j += NUM_POSSIBLE_SYMMETRIES) {
                        if (predictions[i].equals(predictions[j])) {
                            withinSymmetry = true;
                            break;
                        }
                    }
                    if (!withinSymmetry) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void update() {
        if (knownSymmetry != -1) {
            Debug.setIndicatorDot(Profile.ATTACKING, Cache.MY_LOCATION, 0, 255, 0);
        }
        if (Constants.ROBOT_TYPE == RobotType.CARRIER && Cache.TURN_COUNT == 1) {
            // save bytecodes
            return;
        }
        generateHQGuessList();
        if (!initialized) {
            return;
        }
        // receive from communication
        try {
            lastInvalidationsRead = rc.readSharedArray(Communication.ENEMY_HQ_INVALIDATIONS_OFFSET);
            invalidations |= lastInvalidationsRead;
            lastConfirmedRead = rc.readSharedArray(Communication.ENEMY_HQ_CONFIRMED_OFFSET);
            int confirmedDifferences = confirmed;
            confirmed |= lastConfirmedRead;
            confirmedDifferences ^= confirmed; // XOR to find differences
            if (confirmedDifferences != 0) {
                // we have new HQs
                for (int i = predictions.length; --i >= 0; ) {
                    if ((confirmedDifferences & (1 << i)) != 0) {
                        // predictions[i] is now confirmed
                        addToEnemyHeadquartersLocations(predictions[i]);
                    }
                }
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }

        // traverse and remove any that are visible and not there
        for (int i = predictions.length; --i >= 0; ) {
            if (isInvalidatedPrediction(i)) {
                continue;
            }
            MapLocation prediction = predictions[i];
            if (rc.canSenseLocation(prediction)) {
                try {
                    RobotInfo robot = rc.senseRobotAtLocation(prediction);
                    // can we sense an enemy headquarter?
                    if (robot != null && robot.type == RobotType.HEADQUARTERS && robot.team == Constants.ENEMY_TEAM) {
                        markKnownEnemyHQ(i);
                    } else {
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
                    markKnownEnemyHQ(j);
                }
                knownSymmetry = possibleSymmetry;
            }
        }

        // write to communication
        if (rc.canWriteSharedArray(0, 0)) {
            if (invalidations != lastInvalidationsRead) {
                try {
                    rc.writeSharedArray(Communication.ENEMY_HQ_INVALIDATIONS_OFFSET, invalidations);
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
            if (confirmed != lastConfirmedRead) {
                try {
                    rc.writeSharedArray(Communication.ENEMY_HQ_CONFIRMED_OFFSET, confirmed);
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
        }
    }

    public static MapLocation getClosestPrediction(Predicate<MapLocation> predicate) {
        if (!initialized) {
            return null;
        }
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!isInvalidatedPrediction(i)) {
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

    public static MapLocation getClosestPredictionPreferRotationalSymmetry(Predicate<MapLocation> predicate) {
        if (!initialized) {
            return null;
        }
        // rotational symmetry preferred
        MapLocation bestLocation = null;
        double bestScore = Double.MAX_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!isInvalidatedPrediction(i)) {
                MapLocation location = predictions[i];
                if (predicate.test(location)) {
                    double score = Math.sqrt(Cache.MY_LOCATION.distanceSquaredTo(location)); // regular distance
//                    if (i % 3 == 2) {
//                        // rotational symmetry
//                        score /= 2.0; // lower is better
//                    }
                    if (score < bestScore) {
                        bestScore = score;
                        bestLocation = location;
                    }
                }
            }
        }
        return bestLocation;
//        return getClosest(predicate);
    }


    public static int getMaximumPossibleEnemyHqDistanceSquaredAsHeadquarters() {
        if (predictions == null) {
            // just use our location
            int x = Cache.MY_LOCATION.x;
            int y = Cache.MY_LOCATION.y;
            int symX = Constants.MAP_WIDTH - x - 1;
            int symY = Constants.MAP_HEIGHT - y - 1;
            int distanceSquaredA = Cache.MY_LOCATION.distanceSquaredTo(new MapLocation(x, symY));
            int distanceSquaredB = Cache.MY_LOCATION.distanceSquaredTo(new MapLocation(symX, y));
            int distanceSquaredC = Cache.MY_LOCATION.distanceSquaredTo(new MapLocation(symX, symY));
            return Math.max(distanceSquaredA, Math.max(distanceSquaredB, distanceSquaredC));
        }
        // for each symmetry, get the closest distance squared
        // then return the maximum of the three distances
        int distanceSquaredA = Integer.MAX_VALUE;
        int distanceSquaredB = Integer.MAX_VALUE;
        int distanceSquaredC = Integer.MAX_VALUE;
        for (int i = predictions.length; --i >= 0; ) {
            if (!isInvalidatedPrediction(i)) {
                int distanceSquared = predictions[i].distanceSquaredTo(Cache.MY_LOCATION);
                switch (i % 3) {
                    case 0:
                        distanceSquaredA = Math.min(distanceSquaredA, distanceSquared);
                        break;
                    case 1:
                        distanceSquaredB = Math.min(distanceSquaredB, distanceSquared);
                        break;
                    case 2:
                        distanceSquaredC = Math.min(distanceSquaredC, distanceSquared);
                        break;
                }
            }
        }
        if (distanceSquaredA == Integer.MAX_VALUE) {
            distanceSquaredA = 0;
        }
        if (distanceSquaredB == Integer.MAX_VALUE) {
            distanceSquaredB = 0;
        }
        if (distanceSquaredC == Integer.MAX_VALUE) {
            distanceSquaredC = 0;
        }
        return Math.max(distanceSquaredA, Math.max(distanceSquaredB, distanceSquaredC));
    }

    public static void forEachNonInvalidatedPrediction(Consumer<MapLocation> consumer) {
        if (initialized) {
            for (int i = predictions.length; --i >= 0; ) {
                if (!isInvalidatedPrediction(i)) {
                    consumer.accept(predictions[i]);
                }
            }
        }
    }

    public static MapLocation getClosestConfirmed() {
        return getClosestConfirmed(location -> true);
    }

    public static MapLocation getClosestConfirmed(Predicate<MapLocation> predicate) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        if (initialized) {
            for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
                MapLocation location = enemyHeadquartersLocations[i];
                if (predicate.test(location)) {
                    int distanceSquared = location.distanceSquaredTo(Cache.MY_LOCATION);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = location;
                    }
                }
            }
        }
        return bestLocation;
    }

    public static void forEachConfirmed(Consumer<MapLocation> consumer) {
        if (initialized) {
            for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
                consumer.accept(enemyHeadquartersLocations[i]);
            }
        }
    }

    public static boolean anyConfirmed(Predicate<MapLocation> predicate) {
        if (initialized) {
            for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
                if (predicate.test(enemyHeadquartersLocations[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
