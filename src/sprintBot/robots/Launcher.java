package sprintBot.robots;

import battlecode.common.*;
import sprintBot.fast.FastIntSet2D;
import sprintBot.pathfinder.Pathfinding;
import sprintBot.util.*;

import java.util.function.ToDoubleBiFunction;

import static sprintBot.util.Constants.rc;

public class Launcher implements RunnableBot {
    private static MapLocation cachedClosestAllyAttackerLocation = null;
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
    }
    
    @Override
    public void postLoop() throws GameActionException {
        TryAttackCloud.tryAttackCloud();
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
                score += 1000000;
                break;
        }
        if (robot.health <= Constants.ROBOT_TYPE.damage) {
            score += 100000;
            // attack the one with more health
            score += robot.health * 100; // max health is 400
        } else {
            score -= robot.health * 100; // max health is 400
        }
        // TODO: target carriers with more resources?
        score -= robot.location.distanceSquaredTo(Cache.MY_LOCATION);
        return score;
    }

    @Override
    public void move() {
        RobotInfo closestAllyAttacker = Util.getClosestRobot(Cache.ALLY_ROBOTS, r -> Util.isAttacker(r.type));
        cachedClosestAllyAttackerLocation = closestAllyAttacker == null ? null : closestAllyAttacker.location;
        Pathfinding.predicate = loc -> true;
        if (executeMicro()) {
            return;
        }
        // go to attack random other enemies (non-attackers)
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS);
        if (enemy != null) {
            Direction direction = getBestMoveDirection((beforeCurrent, afterCurrent) -> getScoreWithActionSingleEnemyAttacker(beforeCurrent, afterCurrent, enemy));
            if (direction != Direction.CENTER) {
                Util.tryMove(direction);
            }
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
            // do not go to squares within 9 distance of hq
            if (EnemyHqTracker.anyKnownAndPending(enemyHqLocation -> enemyHqLocation.isWithinDistanceSquared(Cache.MY_LOCATION, 9))) {
                // we are currently next to enemy hq - let's just try to leave
                RobotInfo enemyHq = Util.getClosestEnemyRobot(r -> r.type == RobotType.HEADQUARTERS);
                if (enemyHq != null) {
                    Util.tryKiteFrom(enemyHq.location);
                    return;
                }
            }
            Pathfinding.predicate = loc -> {
                return !EnemyHqTracker.anyKnownAndPending(enemyHqLocation -> enemyHqLocation.isWithinDistanceSquared(loc, 9));
            };
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
        double direction = Math.atan2(target.y - Cache.MY_LOCATION.y, target.x - Cache.MY_LOCATION.x) + Math.PI / 2;
        double cos = Math.cos(direction);
        double sin = Math.sin(direction);
        MapLocation tangentTarget = Cache.MY_LOCATION.translate((int) (cos * distance), (int) (sin * distance));
        return Pathfinding.executeResetIfNotAdjacent(tangentTarget);
    }

    public static boolean executeMicro() {
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> Util.isAttacker(robot.type));
        if (enemy == null) {
            if (Cache.prevClosestEnemyAttacker != null) {
                // stay still and let them run into our vision
                // TODO: unless a friendly is in front? if we have action maybe go towards it?
                return true;
            }
            return false;
        }
        if (Cache.ALLY_ROBOTS.length > 15) {
            Util.tryPathfindingMove(enemy.location);
        } else {
            Direction direction = getMicroDirection(enemy);
            Debug.setIndicatorString(Profile.ATTACKING, "Micro direction: " + direction);
            if (direction != Direction.CENTER) {
                Util.tryMove(direction);
            }
        }
        return true;
    }

    // TODO: remember the enemies of the previous turn?
    public static Direction getMicroDirection(RobotInfo closestEnemyAttacker) {
        // assumption: there is an enemy attacker in vision
        if (rc.isActionReady()) {
            // must be seeing an enemy but not in attack radius
            RobotInfo enemy = getSingleAttackerOrNull();
            if (enemy == null) {
                // we must be seeing multiple enemies
                // see if there are allies w/ distance < or <= our distance
                if (Util.hasAllyAttackersWithin(closestEnemyAttacker.location, Cache.MY_LOCATION.distanceSquaredTo(closestEnemyAttacker.location))) {
                    // if so, just micro towards it
                    return getBestMoveDirection((beforeCurrent, afterCurrent) -> getScoreWithActionSingleEnemyAttacker(beforeCurrent, afterCurrent, closestEnemyAttacker));
                } else {
                    // otherwise, kite
                    return getBestMoveDirection(Launcher::getScoreForKiting);
                }
            } else {
                if (shouldAttackSingleEnemyWithAction(enemy)) {
                    Debug.setIndicatorDot(Profile.ATTACKING, Cache.MY_LOCATION, 0, 255, 0);
                    return getBestMoveDirection((beforeCurrent, afterCurrent) -> getScoreWithActionSingleEnemyAttacker(beforeCurrent, afterCurrent, enemy));
                } else {
                    // we think we will lose the 1 on 1
                    return getBestMoveDirection(Launcher::getScoreForKiting);
                }
            }
        } else {
            // we see an enemy that must be in our attack radius? (or maybe the enemy died)
            // see if there are allies w/ distance <= our distance
            if (Util.hasAllyAttackersWithin(closestEnemyAttacker.location, Cache.MY_LOCATION.distanceSquaredTo(closestEnemyAttacker.location))) {
                // if so, stay still
                return Direction.CENTER;
            } else {
                // otherwise, kite
                return getBestMoveDirection(Launcher::getScoreForKiting);
            }
        }
    }

    public static Direction getBestMoveDirection(ToDoubleBiFunction<MapLocation, MapLocation> scorer) {
        Direction bestDirection = Direction.CENTER;
        double bestScore = -Double.MAX_VALUE;
        for (int i = Constants.ALL_DIRECTIONS.length; --i >= 0; ) {
            Direction direction = Constants.ALL_DIRECTIONS[i];
            MapLocation location = Cache.MY_LOCATION.add(direction);
            if (direction != Direction.CENTER && !rc.canMove(direction)) { // this is for micro - so let's ignore currents
                // occupied
                continue;
            }
            double score = scorer.applyAsDouble(location, CurrentsCache.get(location));
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

    public static double getScoreWithActionSingleEnemyAttacker(MapLocation beforeCurrent, MapLocation afterCurrent, RobotInfo enemy) {
        MapLocation enemyLocation = enemy.location;

        double score = 0;
        // prefer non clouds if we're not in a cloud
        try {
            if (!rc.senseCloud(Cache.MY_LOCATION) && !rc.senseCloud(afterCurrent)) {
                score += 20_000_000;
            }
        } catch (GameActionException ex) {
            Debug.failFast(ex);
        }

        // prefer squares that we can attack the enemy
        if (beforeCurrent.isWithinDistanceSquared(enemyLocation, Constants.ROBOT_TYPE.actionRadiusSquared)) {
            score += 10_000_000;
        }

        // prefer squares where you're not in enemy hq attack range
        if (EnemyHqTracker.anyKnownAndPending(enemyHqLocation -> {
            return beforeCurrent.isWithinDistanceSquared(enemyHqLocation, RobotType.HEADQUARTERS.actionRadiusSquared);
        })) {
            score -= 5_000_000;
        }

        // Prefer not moving - save our movement for next turn
        if (Cache.MY_LOCATION.equals(beforeCurrent)) {
            score += 1_000_000;
        }

        // Prefer closer to closest attacker ally
        if (cachedClosestAllyAttackerLocation != null) {
            score -= afterCurrent.distanceSquaredTo(cachedClosestAllyAttackerLocation) * 20_000.0; // 35 * 20k < 1 mil
        }

        // prefer straight moves
        if (Util.isStraightDirection(Cache.MY_LOCATION.directionTo(afterCurrent))) {
            score += 10_000;
        }

        // prefer squares where you're further away from the enemy
        score += afterCurrent.distanceSquaredTo(enemyLocation);

        return score;
    }

    public static double getScoreForKiting(MapLocation beforeCurrent, MapLocation afterCurrent) {
        double score = 0;

        // compute enemy statistics
        int closestEnemyAttackerDistanceSquared = Integer.MAX_VALUE;
        int numEnemyAttackerRobotsWithin = 0;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type)) {
                int distanceSquared = afterCurrent.distanceSquaredTo(enemy.location);
                closestEnemyAttackerDistanceSquared = Math.min(closestEnemyAttackerDistanceSquared, distanceSquared);
                if (distanceSquared < Constants.ROBOT_TYPE.visionRadiusSquared) {
                    numEnemyAttackerRobotsWithin++;
                }
            }
        }

        // prefer squares where attackers can't see you
        score -= numEnemyAttackerRobotsWithin * 2_000_000.0;

        // prefer squares where you're not in enemy hq attack range
        // you get damaged BEFORE currents are applied
        if (EnemyHqTracker.anyKnownAndPending(enemyHqLocation -> {
            return beforeCurrent.isWithinDistanceSquared(enemyHqLocation, RobotType.HEADQUARTERS.actionRadiusSquared);
        })) {
            score -= 1_000_000;
        }

        // prefer squares where you're further away from the closest enemy
        score += closestEnemyAttackerDistanceSquared * 10_000.0; // 35 * 10 < 1_000_000

        // prefer squares where you're closest to an ally
        if (cachedClosestAllyAttackerLocation != null) {
            score -= afterCurrent.distanceSquaredTo(cachedClosestAllyAttackerLocation) * 100.0;
        }

        // prefer diagonals over straight directions
        if (Util.isDiagonalDirection(Cache.MY_LOCATION.directionTo(afterCurrent))) {
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
                ret = EnemyHqGuesser.getClosestPreferRotationalSymmetry(location -> !blacklist.contains(location.x, location.y));
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
