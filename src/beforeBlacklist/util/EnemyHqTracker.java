package beforeBlacklist.util;

import battlecode.common.*;

import java.util.function.Consumer;

import static beforeBlacklist.util.Constants.rc;
import static beforeBlacklist.util.Communication.*;

public class EnemyHqTracker {
    public static MapLocation[] enemyHeadquartersLocations; // may include null (for unknown enemy headquarters)
    public static int numKnownEnemyHeadquarterLocations = 0;

    public static MapLocation[] pendingLocations = new MapLocation[4]; // may include null
    public static int numPendingLocations = 0;

    public static void init(int numHqs) {
        enemyHeadquartersLocations = new MapLocation[numHqs];
    }

    public static void update() throws GameActionException {
        // read new enemy HQ locations
        if (enemyHeadquartersLocations != null) {
            for (int i = numKnownEnemyHeadquarterLocations; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
                int value = rc.readSharedArray(ENEMY_HEADQUARTERS_LOCATIONS_OFFSET + i);
                // check if set
                if (((value >> HEADQUARTERS_LOCATIONS_SET_BIT) & 0b1) == 1) {
                    enemyHeadquartersLocations[i] = unpack((value >> HEADQUARTERS_LOCATIONS_LOCATION_BIT) & HEADQUARTERS_LOCATIONS_LOCATION_MASK);
                    numKnownEnemyHeadquarterLocations++;
                } else {
                    break;
                }
            }
        }
        // mark pending locations
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (enemy.type == RobotType.HEADQUARTERS) {
                markKnownEnemyHQ(enemy.location);
            }
        }
        // if we can write, flush pending to communications
        if (rc.canWriteSharedArray(0, 0)) {
            // TODO - remove this when the comm bug gets fixed
            MapLocation closestAllyHq = Communication.getClosestAllyHQ();
            if (closestAllyHq == null || !closestAllyHq.isWithinDistanceSquared(Cache.MY_LOCATION, GameConstants.DISTANCE_SQUARED_FROM_HEADQUARTER)) {
                return;
            }
            // we can write
            for (int i = numPendingLocations; --i >= 0; ) {
                MapLocation location = pendingLocations[i];
                if (isKnownEnemyHQ(location)) {
                    // already found
                    continue;
                }
                // write to comms
                int message = (pack(location) << HEADQUARTERS_LOCATIONS_LOCATION_BIT) | (1 << HEADQUARTERS_LOCATIONS_SET_BIT);
                rc.writeSharedArray(ENEMY_HEADQUARTERS_LOCATIONS_OFFSET + numKnownEnemyHeadquarterLocations, message);
                // set for our own record keeping
                enemyHeadquartersLocations[numKnownEnemyHeadquarterLocations] = location;
                numKnownEnemyHeadquarterLocations++;
            }
            numPendingLocations = 0;
        }
    }

    public static MapLocation getClosest() {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        if (enemyHeadquartersLocations != null) {
            for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
                MapLocation location = enemyHeadquartersLocations[i];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestLocation = location;
                    bestDistanceSquared = distanceSquared;
                }
            }
        }
        // check in pending
        for (int i = numPendingLocations; --i >= 0; ) {
            MapLocation location = pendingLocations[i];
            int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
            if (distanceSquared < bestDistanceSquared) {
                bestLocation = location;
                bestDistanceSquared = distanceSquared;
            }
        }
        return bestLocation;
    }

    public static boolean isKnownEnemyHQ(MapLocation location) {
        for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
            MapLocation hqLocation = enemyHeadquartersLocations[i];
            if (hqLocation.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPendingLocation(MapLocation location) {
        for (int i = numPendingLocations; --i >= 0; ) {
            MapLocation hqLocation = pendingLocations[i];
            if (hqLocation.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public static void markKnownEnemyHQ(MapLocation location) {
        // check if already exists
        if (isKnownEnemyHQ(location)) {
            return;
        }
        if (isPendingLocation(location)) {
            return;
        }
        // queue to comms
        pendingLocations[numPendingLocations] = location;
        numPendingLocations++;
    }

    public static void forEachKnown(Consumer<MapLocation> consumer) {
        for (int i = numKnownEnemyHeadquarterLocations; --i >= 0; ) {
            consumer.accept(enemyHeadquartersLocations[i]);
        }
    }

    public static void forEachPending(Consumer<MapLocation> consumer) {
        for (int i = numPendingLocations; --i >= 0; ) {
            consumer.accept(pendingLocations[i]);
        }
    }
}
