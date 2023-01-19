package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static sprintBot.util.Constants.rc;

public class TryAttackCloudTemplate {
    public static void tryAttackCloud() throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }
        int x = Cache.MY_LOCATION.x;
        int y = Cache.MY_LOCATION.y;
        /*
        macro! attack_cloud
        ---
        if (!rc.canSenseLocation(<location>) && rc.senseCloud(<location>)) {rc.attack(<location>); break outer;}
        ---
         */
        outer: {
            // unroll_vision! 20 attack_cloud <location>
        }
        do {
            if (rc.canAttack(new MapLocation(x + 4, y + 0))) {rc.attack(new MapLocation(x + 4, y + 0));break;}
            if (rc.canAttack(new MapLocation(x + 0, y + 4))) {rc.attack(new MapLocation(x + 0, y + 4));break;}
            if (rc.canAttack(new MapLocation(x + 0, y + -4))) {rc.attack(new MapLocation(x + 0, y + -4));break;}
            if (rc.canAttack(new MapLocation(x + -4, y + 0))) {rc.attack(new MapLocation(x + -4, y + 0));break;}
            if (rc.canAttack(new MapLocation(x + 3, y + 2))) {rc.attack(new MapLocation(x + 3, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + 3, y + -2))) {rc.attack(new MapLocation(x + 3, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + 3))) {rc.attack(new MapLocation(x + 2, y + 3));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + -3))) {rc.attack(new MapLocation(x + 2, y + -3));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + 3))) {rc.attack(new MapLocation(x + -2, y + 3));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + -3))) {rc.attack(new MapLocation(x + -2, y + -3));break;}
            if (rc.canAttack(new MapLocation(x + -3, y + 2))) {rc.attack(new MapLocation(x + -3, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + -3, y + -2))) {rc.attack(new MapLocation(x + -3, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + 3, y + 1))) {rc.attack(new MapLocation(x + 3, y + 1));break;}
            if (rc.canAttack(new MapLocation(x + 3, y + -1))) {rc.attack(new MapLocation(x + 3, y + -1));break;}
            if (rc.canAttack(new MapLocation(x + 1, y + 3))) {rc.attack(new MapLocation(x + 1, y + 3));break;}
            if (rc.canAttack(new MapLocation(x + 1, y + -3))) {rc.attack(new MapLocation(x + 1, y + -3));break;}
            if (rc.canAttack(new MapLocation(x + -1, y + 3))) {rc.attack(new MapLocation(x + -1, y + 3));break;}
            if (rc.canAttack(new MapLocation(x + -1, y + -3))) {rc.attack(new MapLocation(x + -1, y + -3));break;}
            if (rc.canAttack(new MapLocation(x + -3, y + 1))) {rc.attack(new MapLocation(x + -3, y + 1));break;}
            if (rc.canAttack(new MapLocation(x + -3, y + -1))) {rc.attack(new MapLocation(x + -3, y + -1));break;}
            if (rc.canAttack(new MapLocation(x + 3, y + 0))) {rc.attack(new MapLocation(x + 3, y + 0));break;}
            if (rc.canAttack(new MapLocation(x + 0, y + 3))) {rc.attack(new MapLocation(x + 0, y + 3));break;}
            if (rc.canAttack(new MapLocation(x + 0, y + -3))) {rc.attack(new MapLocation(x + 0, y + -3));break;}
            if (rc.canAttack(new MapLocation(x + -3, y + 0))) {rc.attack(new MapLocation(x + -3, y + 0));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + 2))) {rc.attack(new MapLocation(x + 2, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + -2))) {rc.attack(new MapLocation(x + 2, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + 2))) {rc.attack(new MapLocation(x + -2, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + -2))) {rc.attack(new MapLocation(x + -2, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + 1))) {rc.attack(new MapLocation(x + 2, y + 1));break;}
            if (rc.canAttack(new MapLocation(x + 2, y + -1))) {rc.attack(new MapLocation(x + 2, y + -1));break;}
            if (rc.canAttack(new MapLocation(x + 1, y + 2))) {rc.attack(new MapLocation(x + 1, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + 1, y + -2))) {rc.attack(new MapLocation(x + 1, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + -1, y + 2))) {rc.attack(new MapLocation(x + -1, y + 2));break;}
            if (rc.canAttack(new MapLocation(x + -1, y + -2))) {rc.attack(new MapLocation(x + -1, y + -2));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + 1))) {rc.attack(new MapLocation(x + -2, y + 1));break;}
            if (rc.canAttack(new MapLocation(x + -2, y + -1))) {rc.attack(new MapLocation(x + -2, y + -1));break;}
        } while (false);
    }
}
