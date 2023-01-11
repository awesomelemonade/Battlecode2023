package beforeTraffic.robots;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import beforeTraffic.util.*;

import static beforeTraffic.util.Constants.rc;

public class Launcher implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        if (Profile.ATTACKING.enabled()) {
            EnemyHqGuesser.forEach(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 0)); // black
        }
        action();
        move();
        action();
    }

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
        MapLocation location = Communication.getClosestAllyHQ();
        if (location != null) {
            Util.tryPathfindingMove(location);
        }
    }

    public void moveWithAction() {
        if (!rc.isMovementReady()) {
            return;
        }
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS);
        if (enemy == null) {
            // camp the headquarters
            RobotInfo hq = Util.getClosestEnemyRobot(robot -> robot.type == RobotType.HEADQUARTERS);
            if (hq == null) {
                MapLocation location = EnemyHqGuesser.getClosest();
                if (location == null) {
                    Util.tryExplore();
                } else {
                    Debug.setIndicatorLine(Profile.ATTACKING, Cache.MY_LOCATION, location, 0, 0, 0); // black
                    Util.tryPathfindingMove(location);
                }
            } else {
                MapLocation hqLocation = hq.location;
                // TODO - try to circle around it?
                Util.tryPathfindingMove(hqLocation);
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
