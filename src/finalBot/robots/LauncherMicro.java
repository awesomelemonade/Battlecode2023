package finalBot.robots;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import finalBot.util.*;

import static finalBot.util.Constants.rc;

public class LauncherMicro {
    private static final int ENEMY_HISTORY_LENGTH = 5;

    public static void loop() {
        insertTurn();
        if (rc.getRoundNum() >= ENEMY_HISTORY_LENGTH) {
            deleteEverythingBeforeTurn(rc.getRoundNum() - ENEMY_HISTORY_LENGTH);
        }
        updateEnemies();
    }

    public static void postLoop() {
        // record enemies that we are seeing
        updateEnemies();
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

    public static void updateEnemies() {
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            int enemyId = Cache.ENEMY_ROBOTS[i].getID();
            if (enemyId >= Constants.ROBOT_STARTING_ID) {
                insertOrBringToEnd((char) enemyId);
            }
        }
    }

    public static void insertOrBringToEnd(char enemyId) {
        int index = enemyIds.indexOf(Character.toString(enemyId));
        if (index != -1) {
            enemyIds.deleteCharAt(index);
        }
        enemyIds.append(enemyId);
    }

    public static int numberOfEnemies() {
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
