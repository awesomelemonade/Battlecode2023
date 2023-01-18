package sprintBot.robots;

import battlecode.common.*;
import sprintBot.fast.FastIntSet2D;
import sprintBot.pathfinder.Pathfinding;
import sprintBot.util.*;

import java.util.function.ToDoubleFunction;

import static sprintBot.util.Constants.rc;

public class Launcher implements RunnableBot {
    @Override
    public void init() throws GameActionException {
        blacklist = new FastIntSet2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    private static void debug_render() {
        if (Profile.ATTACKING.enabled()) {
            EnemyHqGuesser.forEach(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 0)); // black
            EnemyHqGuesser.forEachPendingInvalidations(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 128, 128, 128)); // gray
            EnemyHqTracker.forEachPending(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 255, 255)); // cyan
            EnemyHqTracker.forEachKnown(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 255)); // blue
        }
    }

    @Override
    public void loop() throws GameActionException {
        debug_render();
    }

    @Override
    public void action() throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }
        RobotInfo enemy = getBestImmediateAttackTarget();
        if (enemy != null) {
            MapLocation enemyLocation = enemy.location;
            tryAttack(enemyLocation);
        }
        tryAttackCloud();
    }

    public static void tryAttackCloud() throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }
        int x = Cache.MY_LOCATION.x;
        int y = Cache.MY_LOCATION.y;
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

    public static RobotInfo getBestImmediateAttackTarget() {
        RobotInfo bestEnemy = null;
        double bestScore = -Double.MAX_VALUE;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            MapLocation enemyLocation = enemy.location;
            if (enemy.type != RobotType.HEADQUARTERS &&
                    Cache.MY_LOCATION.isWithinDistanceSquared(enemyLocation, Constants.ROBOT_TYPE.actionRadiusSquared)) {
                double score = getImmediateAttackScore(enemy);
                if (score > bestScore) {
                    bestScore = score;
                    bestEnemy = enemy;
                }
            }
        }
        return bestEnemy;
    }

    public static double getImmediateAttackScore(RobotInfo robot) {
        // TODO: replace current behavior with this
        // out of robots we can kill:
        // score these and shoot the best
        // if we cannot kill anything:
        // score those and shoot the best

        // function of robotType, health, canKill, movementCooldown, actionCooldown, distance?
        double score = 0;
        switch (robot.type) {
            case LAUNCHER:
                score += 100000;
                break;
        }
        if (robot.health <= Constants.ROBOT_TYPE.damage) {
            score += 10000;
        }
        score -= robot.health * 100; // max health is 40
        score -= robot.location.distanceSquaredTo(Cache.MY_LOCATION);
        return score;
    }

    @Override
    public void move() {
        if (executeMicro()) {
            return;
        }
        // go to attack random other enemies (non-attackers)
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS);
        if (enemy != null) {
            Util.tryPathfindingMove(enemy.location);
            return;
        }
        // camp the headquarters
        MapLocation location = getMacroAttackLocation();
        if (location == null) {
            Util.tryExplore();
        } else {
//            if (notGettingCloser && Cache.ALLY_ROBOTS.length >= 5) {
//                blacklist.add(location.x, location.y);
//            }
            // check if hq already has tons of ally units nearby
            int numAllyAttackers = Util.numAllyAttackersWithin(location, 20);
            if (numAllyAttackers >= 5) {
                if (!Cache.MY_LOCATION.isAdjacentTo(location)) {
                    blacklist.add(location.x, location.y);
                }
            }
            if (Profile.ATTACKING.enabled()) {
                Debug.setIndicatorLine(Profile.ATTACKING, Cache.MY_LOCATION, location, 0, 0, 0); // black
                Debug.setIndicatorString(Profile.ATTACKING, "Num: " + numAllyAttackers);
            }
            if (Cache.MY_LOCATION.isWithinDistanceSquared(location, 16)) {
                // try to circle around it
                tryPathfindingTangent(location);
            } else {
                Util.tryPathfindingMove(location);
            }
        }
    }

    public static boolean tryPathfindingTangent(MapLocation target) {
        double distance = 10;
        double direction = Math.atan2(target.y - Cache.MY_LOCATION.y, target.x - Cache.MY_LOCATION.x);
        double cos = Math.cos(direction);
        double sin = Math.sin(direction);
        MapLocation tangentTarget = Cache.MY_LOCATION.translate((int) (cos * distance), (int) (sin * distance));
        return Pathfinding.executeResetIfNotAdjacent(tangentTarget);
    }

    public static boolean executeMicro() {
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> Util.isAttacker(robot.type));
        if (enemy == null) {
            return false;
        }
        if (Cache.ALLY_ROBOTS.length > 15) {
            Util.tryPathfindingMove(enemy.location);
        } else {
            Direction direction = getMicroDirection();
            Debug.setIndicatorString(Profile.ATTACKING, "Micro direction: " + direction);
            if (direction != Direction.CENTER) {
                Util.tryMove(direction);
            }
        }
        return true;
    }

    // TODO: remember the enemies of the previous turn?
    public static Direction getMicroDirection() {
        if (rc.isActionReady()) {
            // must be seeing an enemy but not in attack radius
            RobotInfo enemy = getSingleAttackerOrNull();
            if (enemy != null) {
                boolean shouldAttack = shouldAttackSingleEnemyWithAction(enemy);
                if (shouldAttack) {
                    Debug.setIndicatorDot(Profile.ATTACKING, Cache.MY_LOCATION, 0, 255, 0);
                    return getBestMoveDirection(location -> getScoreWithActionSingleEnemyAttacker(location, enemy));
                }
            }
        }
        return getBestMoveDirection(Launcher::getScoreForKiting);
    }

    public static Direction getBestMoveDirection(ToDoubleFunction<MapLocation> scorer) {
        Direction bestDirection = Direction.CENTER;
        double bestScore = -Double.MAX_VALUE;
        for (int i = Constants.ALL_DIRECTIONS.length; --i >= 0; ) {
            Direction direction = Constants.ALL_DIRECTIONS[i];
            MapLocation location = Cache.MY_LOCATION.add(direction);
            if (direction != Direction.CENTER && !rc.canMove(direction)) {
                // occupied
                continue;
            }
            double score = scorer.applyAsDouble(location);
            if (score > bestScore) {
                bestScore = score;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    public static boolean shouldAttackSingleEnemyWithAction(RobotInfo enemy) {
        if (enemy.type == RobotType.LAUNCHER) {
            int damage = RobotType.LAUNCHER.damage;
            int numAttacksToEnemy = (enemy.health + damage - 1) / damage; // round up
            int numAttacksToUs = (rc.getHealth() + damage - 1) / damage; // round up
            // see if we can win the fight
            return numAttacksToUs >= numAttacksToEnemy;
        } else {
            return true;
        }
    }

    public static double getScoreWithActionSingleEnemyAttacker(MapLocation location, RobotInfo enemy) {
        MapLocation enemyLocation = enemy.location;

        double score = 0;
        // prefer non clouds if we're not in a cloud
        try {
            if (!rc.senseMapInfo(Cache.MY_LOCATION).hasCloud()) {
                if (!rc.senseMapInfo(location).hasCloud()) {
                    score += 2000000;
                }
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }

        // prefer squares that we can attack the enemy
        if (location.isWithinDistanceSquared(enemyLocation, Constants.ROBOT_TYPE.actionRadiusSquared)) {
            score += 1000000;
        }

        // prefer straight moves
        if (Util.isStraightDirection(Cache.MY_LOCATION.directionTo(location))) {
            score += 1000;
        }

        // prefer squares where you're further away from the enemy
        score += location.distanceSquaredTo(enemyLocation);

        return score;
    }

    public static double getScoreForKiting(MapLocation location) {
        double score = 0;
        // prefer squares where attackers can't see you
        score -= numAttackerRobotsWithin(location, Constants.ROBOT_TYPE.visionRadiusSquared) * 100000.0;

        // prefer squares where you're further away from the closest enemy
        score += getClosestEnemyAttackerDistanceSquared(location) * 1000.0;

        // prefer diagonals over straight directions
        if (Util.isDiagonalDirection(Cache.MY_LOCATION.directionTo(location))) {
            score += 50;
        }

        return score;
    }

    public static RobotInfo getSingleAttackerOrNull() {
        RobotInfo robot = null;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type)) {
                if (robot != null) {
                    return null;
                }
                robot = enemy;
            }
        }
        return robot;
    }

    public static int numAttackerRobotsWithin(MapLocation location, int distanceSquared) {
        int count = 0;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type) && location.isWithinDistanceSquared(enemy.getLocation(), distanceSquared)) {
                count++;
            }
        }
        return count;
    }

    public static int getClosestEnemyAttackerDistanceSquared(MapLocation location) {
        int bestDistanceSquared = Integer.MAX_VALUE;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type)) {
                bestDistanceSquared = Math.min(bestDistanceSquared, location.distanceSquaredTo(enemy.location));
            }
        }
        return bestDistanceSquared;
    }

    private static FastIntSet2D blacklist;

    public static MapLocation getMacroAttackLocation() {
        RobotInfo closestVisibleEnemyHQ = Util.getClosestEnemyRobot(robot -> robot.type == RobotType.HEADQUARTERS &&
                !blacklist.contains(robot.location.x, robot.location.y));
        MapLocation ret = closestVisibleEnemyHQ == null ? null : closestVisibleEnemyHQ.location;
        if (ret == null) {
            ret = EnemyHqTracker.getClosest(location -> !blacklist.contains(location.x, location.y));
        }
        if (ret == null) {
            MapLocation lastHqLocation = WellTracker.lastHqLocation();
            if (lastHqLocation != null) {
                ret = EnemyHqGuesser.getFarthest(lastHqLocation, location -> !blacklist.contains(location.x, location.y));
            }
        }
        if (ret == null) {
            ret = EnemyHqGuesser.getClosest(location -> !blacklist.contains(location.x, location.y));
        }
        if (ret == null) {
            blacklist.reset();
        }
        return ret;
    }

    public static boolean tryAttack(MapLocation location) {
        if (rc.canAttack(location)) {
            try {
                rc.attack(location);
                return true;
            } catch (GameActionException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return false;
    }
}
