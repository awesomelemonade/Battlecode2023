package finalBot.robots;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import finalBot.util.*;

import static finalBot.util.Constants.rc;

public class LauncherMicro {
    private static final int ENEMY_HISTORY_LENGTH = 6;

    public static void loop() {
        insertTurn();
        if (rc.getRoundNum() >= ENEMY_HISTORY_LENGTH) {
            deleteEverythingBeforeTurn(rc.getRoundNum() - ENEMY_HISTORY_LENGTH);
        }
        // update enemies
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo robot = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(robot.type)) {
                int enemyId = Cache.ENEMY_ROBOTS[i].getID();
                if (enemyId != lastEnemyDeathId) {
                    insertOrBringToEnd((char) enemyId);
                }
            }
        }
    }

    public static void postLoop() {
        // record enemies that we are seeing
        clearEnemyLocationsLastTurn();
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo robot = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(robot.type)) {
                int enemyId = Cache.ENEMY_ROBOTS[i].getID();
                if (enemyId != lastEnemyDeathId) {
                    insertOrBringToEnd((char) enemyId);
                    addEnemyLocationLastTurn(robot.location, enemyId);
                }
            }
        }
    }

    public static int lastEnemyDeathId = -1;
    public static void onEnemyDeath(RobotInfo enemy) {
        if (Util.isAttacker(enemy.type)) {
            deleteEnemyIdIfExists((char) enemy.getID());
            lastEnemyDeathId = enemy.getID();
        }
    }

    private static StringBuilder enemyLocationsLastTurn = new StringBuilder();

    public static void clearEnemyLocationsLastTurn() {
        enemyLocationsLastTurn.setLength(0);
    }
    
    public static void addEnemyLocationLastTurn(MapLocation location, int id) {
        enemyLocationsLastTurn.append((char) (location.x * Constants.MAX_MAP_SIZE + location.y));
        enemyLocationsLastTurn.append((char) id);
    }

    // returns -1 if it doesn't exist
    public static int enemyLocationLastTurnId(MapLocation location) {
        int index = enemyLocationsLastTurn.indexOf(Character.toString((char) (location.x * Constants.MAX_MAP_SIZE + location.y)));
        if (index == -1) {
            return -1;
        } else {
            return enemyLocationsLastTurn.charAt(index + 1);
        }
    }

    private static StringBuilder enemyIds = new StringBuilder();

    public static void insertTurn() {
        enemyIds.append((char) rc.getRoundNum());
    }

    public static void deleteEverythingBeforeTurn(int turn) {
        char turnChar = (char) turn;
        int index = enemyIds.indexOf(Character.toString(turnChar));
        if (index != -1) {
            enemyIds.delete(0, index + 1);
        }
    }

    public static void insertOrBringToEnd(char enemyId) {
        int index = enemyIds.indexOf(Character.toString(enemyId));
        if (index != -1) {
            enemyIds.deleteCharAt(index);
        }
        enemyIds.append(enemyId);
    }

    public static void deleteEnemyIdIfExists(char enemyId) {
        int index = enemyIds.indexOf(Character.toString(enemyId));
        if (index != -1) {
            enemyIds.deleteCharAt(index);
        }
    }

    public static int numberOfUniqueEnemyAttackersInHistory() {
        return Math.max(0, enemyIds.length() - ENEMY_HISTORY_LENGTH);
    }

    public static void debug_printIds() {
        StringBuilder out = new StringBuilder();
        out.append('[');
        for (int i = 0; i < enemyIds.length(); i++) {
            char c = enemyIds.charAt(i);
            if (c < 10000) {
                out.append('T');
                out.append(((int) c));
            } else {
                out.append('R');
                out.append(((int) c));
            }
            out.append(", ");
        }
        out.append(']');
        Debug.println(out);
    }

    public static boolean allowedToStandStill(MapLocation enemyLocation) {
        int actionRadiusSquared = RobotType.LAUNCHER.actionRadiusSquared;
        if (enemyLocation.isWithinDistanceSquared(Cache.MY_LOCATION, actionRadiusSquared)) {
            return false;
        }
        Direction directionToUs = enemyLocation.directionTo(Cache.MY_LOCATION);
        MapLocation a = enemyLocation.add(directionToUs);
        MapLocation b = enemyLocation.add(directionToUs.rotateLeft());
        MapLocation c = enemyLocation.add(directionToUs.rotateRight());
        if (Cache.MY_LOCATION.isWithinDistanceSquared(a, actionRadiusSquared)) {
            // check that there are allies that can attack too
            if (!LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS, r -> r.location.isWithinDistanceSquared(a, actionRadiusSquared))) {
                return false;
            }
        }
        if (Cache.MY_LOCATION.isWithinDistanceSquared(b, actionRadiusSquared)) {
            // check that there are allies that can attack too
            if (!LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS, r -> r.location.isWithinDistanceSquared(b, actionRadiusSquared))) {
                return false;
            }
        }
        if (Cache.MY_LOCATION.isWithinDistanceSquared(c, actionRadiusSquared)) {
            // check that there are allies that can attack too
            if (!LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS, r -> r.location.isWithinDistanceSquared(c, actionRadiusSquared))) {
                return false;
            }
        }
        return true;
    }
}
