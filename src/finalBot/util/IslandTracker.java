package finalBot.util;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Team;

import java.util.function.IntPredicate;

import static finalBot.util.Constants.rc;

public class IslandTracker {
    private static final int ARRAY_SIZE = GameConstants.MAX_NUMBER_ISLANDS + 1; // islands are 1-indexed, so 0 slot isn't used
    private static MapLocation[] islandLocations = new MapLocation[ARRAY_SIZE];
    private static MapLocation[][] lastSensedIslandLocations = new MapLocation[ARRAY_SIZE][];

    private static boolean[] isInKnownIslands = new boolean[ARRAY_SIZE];
    private static int[] knownIslands = new int[ARRAY_SIZE];
    private static int knownIslandsSize = 0;

    private static boolean[] isInOurIslands = new boolean[ARRAY_SIZE];
    private static int[] ourIslands = new int[ARRAY_SIZE];
    private static int ourIslandsSize = 0;

    public static int[] NEARBY_ISLANDS = new int[0];

    public static void loop() {
        NEARBY_ISLANDS = rc.senseNearbyIslands();
        for (int i = NEARBY_ISLANDS.length; --i >= 0; ) {
            int islandIndex = NEARBY_ISLANDS[i];
            Team team;
            try {
                team = rc.senseTeamOccupyingIsland(islandIndex);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
                team = Team.NEUTRAL;
            }
            if (!isInKnownIslands[islandIndex]) {
                knownIslands[knownIslandsSize++] = islandIndex;
                isInKnownIslands[islandIndex] = true;
            }
            if (team == Constants.ALLY_TEAM) {
                if (!isInOurIslands[islandIndex]) {
                    ourIslands[ourIslandsSize++] = islandIndex;
                    isInOurIslands[islandIndex] = true;
                }
            } else {
                if (isInOurIslands[islandIndex]) {
                    // locate where it is
                    for (int j = 0; j < ourIslandsSize; j++) {
                        if (ourIslands[j] == islandIndex) {
                            // we can do a swap remove
                            ourIslands[j] = ourIslands[--ourIslandsSize];
                            break;
                        }
                    }
                    isInOurIslands[islandIndex] = false;
                }
            }
            int bestDistanceSquared = Integer.MAX_VALUE;
            MapLocation closestLocation = null;
            try {
                MapLocation[] islandLocations = rc.senseNearbyIslandLocations(islandIndex);
                lastSensedIslandLocations[islandIndex] = islandLocations;
                // this loop is capped by GameConstants.MAX_ISLAND_AREA = 20 so we shouldn't run into bytecode issues
                for (int j = islandLocations.length; --j >= 0; ) {
                    MapLocation location = islandLocations[j];
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        closestLocation = location;
                    }
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
            if (closestLocation != null) {
                // compare with the one stored
                MapLocation storedLocation = islandLocations[islandIndex];
                if (storedLocation == null || !storedLocation.isWithinDistanceSquared(Cache.MY_LOCATION, bestDistanceSquared)) {
                    islandLocations[islandIndex] = closestLocation;
                }
            }
        }
    }

    // returns -1 if no islands
    public static int getClosestOurIsland(IntPredicate predicate) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        int bestIsland = -1;
        for (int i = ourIslandsSize; --i >= 0; ) {
            int islandIndex = ourIslands[i];
            if (predicate.test(islandIndex)) {
                MapLocation islandLocation = islandLocations[islandIndex];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(islandLocation);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestIsland = islandIndex;
                }
            }
        }
        return bestIsland;
    }

    // returns -1 if no islands
    public static int getClosestIsland(IntPredicate predicate) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        int bestIsland = -1;
        for (int i = knownIslandsSize; --i >= 0; ) {
            int islandIndex = knownIslands[i];
            if (predicate.test(islandIndex)) {
                MapLocation islandLocation = islandLocations[islandIndex];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(islandLocation);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestIsland = islandIndex;
                }
            }
        }
        return bestIsland;
    }

    public static MapLocation getLocationOfIsland(int islandIndex) {
        return islandLocations[islandIndex];
    }
}
