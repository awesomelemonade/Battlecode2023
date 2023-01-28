package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static sprintBot.util.Constants.rc;

public class TryAttackCloud {
    public static MapLocation getEnemyLocation() {
        MapLocation location = EnemyHqGuesser.getClosestConfirmed();
        if (location == null) {
            location = EnemyHqGuesser.getClosestPrediction(loc -> true);
        }
        return location;
    }
    public static boolean tryAttackCloud() throws GameActionException {
        if (!rc.isActionReady()) {
            return false;
        }
        MapLocation enemyLocation = getEnemyLocation();
        if (enemyLocation == null) {
            // let's just tiebreak by our location
            enemyLocation = Cache.MY_LOCATION;
        }
        MapLocation[] cloudLocations = rc.senseNearbyCloudLocations();
        int bestDistanceSquared = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        for (int i = cloudLocations.length; --i >= 0; ) {
            MapLocation location = cloudLocations[i];
            if (!rc.canSenseLocation(location) && rc.canAttack(location)) {
                int distanceSquared = location.distanceSquaredTo(enemyLocation);
                if (distanceSquared > 0 && distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        if (bestLocation == null) {
            return false;
        } else {
            rc.attack(bestLocation);
            return true;
        }
    }
}