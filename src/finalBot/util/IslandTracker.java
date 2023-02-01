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
    public static Team[] NEARBY_ISLAND_TEAMS = new Team[ARRAY_SIZE];

    // visible locations
    public static int islandIndexAtBeginningOfTurn = -1; // DOES NOT UPDATE WITH MOVES
    public static MapLocation nearestUnoccupiedNeutralOrEnemy = null; // To capture island with anchor as carrier - TODO-someday: not used because of difficulty adapting to carrier blacklist and carrier double move
    public static MapLocation nearestUnoccupiedAllyForHealing = null; // To heal at island as launcher
    public static MapLocation nearestUnoccupiedDamagedAllyOrEnemy = null; // To hold an anchor or eliminate an anchor as launcher

    public static void loop() {
        // recalculate visible locations of interest
        nearestUnoccupiedNeutralOrEnemy = null;
        nearestUnoccupiedAllyForHealing = null;
        nearestUnoccupiedDamagedAllyOrEnemy = null;
        int nearestUnoccupiedNeutralOrEnemyDistanceSquared = Integer.MAX_VALUE;
        int nearestUnoccupiedAllyForHealingDistanceSquared = Integer.MAX_VALUE;
        int nearestUnoccupiedDamagedAllyOrEnemyDistanceSquared = Integer.MAX_VALUE;

        // We handle Cache.MY_LOCATION separately to save bytecodes
        // canSenseRobotAtLocation() will return true for Cache.MY_LOCATION
        // this is so one does not have to include the if statement inside the hot loop
        try {
            islandIndexAtBeginningOfTurn = rc.senseIsland(Cache.MY_LOCATION);
            if (islandIndexAtBeginningOfTurn != -1) {
                Team team = rc.senseTeamOccupyingIsland(islandIndexAtBeginningOfTurn);
                // TODO
                if (team == Team.NEUTRAL) {
                    nearestUnoccupiedNeutralOrEnemy = Cache.MY_LOCATION;
                } else if (team == Constants.ALLY_TEAM) {
                    if (rc.senseAnchorPlantedHealth(islandIndexAtBeginningOfTurn) < rc.senseAnchor(islandIndexAtBeginningOfTurn).totalHealth) {
                        nearestUnoccupiedDamagedAllyOrEnemy = Cache.MY_LOCATION;
                    }
                    nearestUnoccupiedAllyForHealing = Cache.MY_LOCATION;
                } else {
                    nearestUnoccupiedNeutralOrEnemy = Cache.MY_LOCATION;
                    nearestUnoccupiedDamagedAllyOrEnemy = Cache.MY_LOCATION;
                }
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }

        // Handle visible islands
        NEARBY_ISLANDS = rc.senseNearbyIslands();
        for (int i = NEARBY_ISLANDS.length; --i >= 0; ) {
            int islandIndex = NEARBY_ISLANDS[i];
            Team islandTeam;
            try {
                islandTeam = rc.senseTeamOccupyingIsland(islandIndex);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
                islandTeam = Team.NEUTRAL;
            }
            NEARBY_ISLAND_TEAMS[i] = islandTeam;
            if (!isInKnownIslands[islandIndex]) {
                knownIslands[knownIslandsSize++] = islandIndex;
                isInKnownIslands[islandIndex] = true;
            }
            if (islandTeam == Constants.ALLY_TEAM) {
                if (!isInOurIslands[islandIndex]) {
                    ourIslands[ourIslandsSize++] = islandIndex;
                    isInOurIslands[islandIndex] = true;
                }
            } else {
                if (isInOurIslands[islandIndex]) {
                    // locate where it is
                    for (int j = 0; j < ourIslandsSize; j++) {
                        if (ourIslands[j] == islandIndex) {
                            // we can do a swap remove because we don't care about order
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
                // We don't want to do the if statement inside the hot loop - bytecodes :(
                // sorry for your eyes .-.
                if (islandTeam == Team.NEUTRAL) {
                    for (int j = islandLocations.length; --j >= 0; ) {
                        MapLocation location = islandLocations[j];
                        int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                        if (distanceSquared < bestDistanceSquared) {
                            bestDistanceSquared = distanceSquared;
                            closestLocation = location;
                        }
                        if (!rc.canSenseRobotAtLocation(location)) {
                            if (distanceSquared < nearestUnoccupiedNeutralOrEnemyDistanceSquared) {
                                nearestUnoccupiedNeutralOrEnemyDistanceSquared = distanceSquared;
                                nearestUnoccupiedNeutralOrEnemy = location;
                            }
                        }
                    }
                } else if (islandTeam == Constants.ALLY_TEAM) {
                    if (rc.senseAnchorPlantedHealth(islandIndex) < rc.senseAnchor(islandIndex).totalHealth) {
                        for (int j = islandLocations.length; --j >= 0; ) {
                            MapLocation location = islandLocations[j];
                            int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                            if (distanceSquared < bestDistanceSquared) {
                                bestDistanceSquared = distanceSquared;
                                closestLocation = location;
                            }
                            if (!rc.canSenseRobotAtLocation(location)) {
                                if (distanceSquared < nearestUnoccupiedDamagedAllyOrEnemyDistanceSquared) {
                                    nearestUnoccupiedDamagedAllyOrEnemyDistanceSquared = distanceSquared;
                                    nearestUnoccupiedDamagedAllyOrEnemy = location;
                                }
                                if (distanceSquared < nearestUnoccupiedAllyForHealingDistanceSquared) {
                                    nearestUnoccupiedAllyForHealingDistanceSquared = distanceSquared;
                                    nearestUnoccupiedAllyForHealing = location;
                                }
                            }
                        }
                    } else {
                        for (int j = islandLocations.length; --j >= 0; ) {
                            MapLocation location = islandLocations[j];
                            int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                            if (distanceSquared < bestDistanceSquared) {
                                bestDistanceSquared = distanceSquared;
                                closestLocation = location;
                            }
                            if (!rc.canSenseRobotAtLocation(location)) {
                                if (distanceSquared < nearestUnoccupiedAllyForHealingDistanceSquared) {
                                    nearestUnoccupiedAllyForHealingDistanceSquared = distanceSquared;
                                    nearestUnoccupiedAllyForHealing = location;
                                }
                            }
                        }
                    }
                } else {
                    // enemy
                    for (int j = islandLocations.length; --j >= 0; ) {
                        MapLocation location = islandLocations[j];
                        int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                        if (distanceSquared < bestDistanceSquared) {
                            bestDistanceSquared = distanceSquared;
                            closestLocation = location;
                        }
                        if (!rc.canSenseRobotAtLocation(location)) {
                            if (distanceSquared < nearestUnoccupiedNeutralOrEnemyDistanceSquared) {
                                nearestUnoccupiedNeutralOrEnemyDistanceSquared = distanceSquared;
                                nearestUnoccupiedNeutralOrEnemy = location;
                            }
                            if (distanceSquared < nearestUnoccupiedDamagedAllyOrEnemyDistanceSquared) {
                                nearestUnoccupiedDamagedAllyOrEnemyDistanceSquared = distanceSquared;
                                nearestUnoccupiedDamagedAllyOrEnemy = location;
                            }
                        }
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
