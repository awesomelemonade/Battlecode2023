package sprintBot.robots;

import battlecode.common.*;
import sprintBot.fast.FastIntSet2D;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

public class Launcher implements RunnableBot {
    @Override
    public void init() throws GameActionException {
        blacklist = new FastIntSet2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    private static void debug_render() {
        if (Profile.ATTACKING.enabled()) {
            EnemyHqGuesser.forEach(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 0)); // black
            EnemyHqTracker.forEachPending(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 255, 255)); // cyan
            EnemyHqTracker.forEachKnown(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 255)); // blue
        }
    }

    @Override
    public void loop() throws GameActionException {
        debug_render();
    }

    @Override
    public void action() {
        if (!rc.isActionReady()) {
            return;
        }
        RobotInfo enemy = getBestImmediateAttackTarget();
        if (enemy != null) {
            MapLocation enemyLocation = enemy.location;
            tryAttack(enemyLocation);
        }
    }

    public RobotInfo getBestImmediateAttackTarget() {
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

    public double getImmediateAttackScore(RobotInfo robot) {
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
        if (rc.isActionReady()) {
            moveWithAction();
        } else {
            moveWithoutAction();
        }
    }

    public void moveWithoutAction() {
        if (!rc.isMovementReady()) {
            return;
        }
        // TODO: consider moving to a better attacking square
        // TODO: consider kiting
        // Move towards our hq?
        MapLocation location = Util.getClosestAllyHeadquartersLocation();
        if (location != null) {
            Util.tryPathfindingMove(location);
        }
    }

    public void moveWithAction() {
        if (!rc.isMovementReady()) {
            return;
        }
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS); // check if within blacklist
        if (enemy == null) {
            // camp the headquarters
            MapLocation location = getMacroAttackLocation();
            if (location == null) {
                Util.tryExplore();
            } else {
                // check if hq already has tons of ally units nearby
                if (Util.numAllyRobotsWithin(location, 20) >= 10) {
                    blacklist.add(location.x, location.y);
                }
                // TODO - try to circle around it?
                Debug.setIndicatorLine(Profile.ATTACKING, Cache.MY_LOCATION, location, 0, 0, 0); // black
                Util.tryPathfindingMove(location);
            }
        } else {
            // Attack
            MapLocation enemyLocation = enemy.location;
            // TODO: only move towards when there is an action
            if (enemyLocation.isWithinDistanceSquared(Cache.MY_LOCATION, Constants.ROBOT_TYPE.actionRadiusSquared)) {
                // TODO: consider kiting?
            } else {
                Util.tryPathfindingMove(enemyLocation);
            }
        }
    }

    private static FastIntSet2D blacklist;

    public static MapLocation getMacroAttackLocation() {
        RobotInfo closestVisibleEnemyHQ = Util.getClosestEnemyRobot(robot -> robot.type == RobotType.HEADQUARTERS &&
                !blacklist.contains(robot.location.x, robot.location.y));
        MapLocation ret = closestVisibleEnemyHQ == null ? null : closestVisibleEnemyHQ.location;
        if (ret == null) {
            ret = EnemyHqTracker.getClosest(location -> !blacklist.contains(location.x, location.y)); // check if within blacklist?
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
