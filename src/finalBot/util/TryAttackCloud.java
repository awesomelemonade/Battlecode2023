package finalBot.util;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static finalBot.util.Constants.rc;

public class TryAttackCloud {
    public static MapLocation getEnemyLocation() {
        MapLocation location = EnemyHqGuesser.getClosestConfirmed();
        if (location == null) {
            location = EnemyHqGuesser.getClosestPrediction(loc -> true);
        }
        return location;
    }
    public static boolean tryAttackCloud(MapLocation enemyLocation) throws GameActionException {
        if (!rc.isActionReady()) {
            return false;
        }
        boolean allowOnTarget = true;
        if (enemyLocation == null) {
            enemyLocation = getEnemyLocation();
            allowOnTarget = false;
        }
        if (enemyLocation == null) {
            // let's just tiebreak by our location
            enemyLocation = Cache.MY_LOCATION;
        }
        if (rc.senseCloud(Cache.MY_LOCATION)) {
            MapLocation location = Cache.MY_LOCATION;
            while (true) {
                Direction direction = location.directionTo(enemyLocation);
                MapLocation next = location.add(direction);
                if (direction != Direction.CENTER &&
                        next.isWithinDistanceSquared(Cache.MY_LOCATION, Constants.ROBOT_TYPE.actionRadiusSquared)) {
                    location = next;
                } else {
                    break;
                }
            }
            rc.attack(location);
            return true;
        } else {
            int bestDistanceSquared = Integer.MAX_VALUE;
            MapLocation bestLocation = null;
            MapLocation[] cloudLocations = rc.senseNearbyCloudLocations(Constants.ROBOT_TYPE.actionRadiusSquared);
            for (int i = cloudLocations.length; --i >= 0 && Clock.getBytecodesLeft() > 200; ) {
                MapLocation location = cloudLocations[i];
                if (!rc.canSenseLocation(location)) {
                    int distanceSquared = location.distanceSquaredTo(enemyLocation);
                    // we check distance > 0 because we don't want to attack enemy HQ locations
                    if ((allowOnTarget || distanceSquared > 0) && distanceSquared < bestDistanceSquared) {
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
}