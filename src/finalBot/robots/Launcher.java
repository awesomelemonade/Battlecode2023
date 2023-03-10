package finalBot.robots;

import battlecode.common.*;
import finalBot.fast.FastIntSet2D;
import finalBot.pathfinder.Pathfinding;
import finalBot.util.*;

import java.util.function.ToDoubleBiFunction;

import static finalBot.util.Constants.rc;

public class Launcher implements RunnableBot {
    private static final int RETREAT_HEALTH_THRESHOLD = RobotType.LAUNCHER.getMaxHealth() / 2;
    private static MapLocation cachedClosestAllyAttackerLocation = null;
    private static MapLocation lastEnemyLocation = null;
    private static int lastEnemyLocationTurn = -1;

    @Override
    public void init() throws GameActionException {
        blacklist = new FastIntSet2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    private static void debug_render() {
        if (Profile.ATTACKING.enabled()) {
            EnemyHqGuesser.forEachNonInvalidatedPrediction(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 0)); // black
            EnemyHqGuesser.forEachConfirmed(location -> Debug.setIndicatorDot(Profile.ATTACKING, location, 0, 0, 255)); // blue
        }
    }

    @Override
    public void loop() throws GameActionException {
        debug_render();
        LauncherMicro.loop();
        RobotInfo enemy = Util.getClosestEnemyRobot(r -> Util.isAttacker(r.type));
        if (enemy != null) {
            lastEnemyLocation = enemy.location;
            lastEnemyLocationTurn = rc.getRoundNum();
        }
    }

    @Override
    public void action() throws GameActionException {
        if (!rc.isActionReady()) {
            return;
        }
        RobotInfo enemy = getBestImmediateAttackTarget();
        if (enemy != null) {
            MapLocation enemyLocation = enemy.location;
            if (enemy.health <= Constants.ROBOT_TYPE.damage) {
                // it's going to die
                LauncherMicro.onEnemyDeath(enemy);
            }
            tryAttack(enemyLocation);
        }
    }
    
    @Override
    public void postLoop() throws GameActionException {
        RobotInfo enemy = Util.getClosestEnemyRobot(r -> Util.isAttacker(r.type) && r.getID() != LauncherMicro.lastEnemyDeathId);
        if (enemy != null) {
            lastEnemyLocation = enemy.location;
            lastEnemyLocationTurn = rc.getRoundNum();
        }
        LauncherMicro.postLoop();
        TryAttackCloud.tryAttackCloud(lastEnemyLocation);
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

    public static boolean tryMoveToHoldIsland() {
        if (LauncherMicro.numberOfUniqueEnemyAttackersInHistory() > 0) {
            return false;
        }
        MapLocation bestIslandLocation = IslandTracker.nearestUnoccupiedDamagedAllyOrEnemy;
        if (bestIslandLocation == null) {
            return false;
        }
        Util.tryPathfindingMove(bestIslandLocation);
        return true;
    }

    @Override
    public void move() {
        Pathfinding.predicate = loc -> {
            MapLocation afterCurrent = CurrentsCache.get(loc);
            if (loc.equals(afterCurrent)) {
                return !EnemyHqGuesser.anyConfirmed(enemyHqLocation -> enemyHqLocation.isWithinDistanceSquared(loc, 9));
            } else {
                return !EnemyHqGuesser.anyConfirmed(enemyHqLocation -> enemyHqLocation.isWithinDistanceSquared(loc, 9)
                        || enemyHqLocation.isWithinDistanceSquared(afterCurrent, 9));
            }
        };
        RobotInfo closestAllyAttacker = Util.getClosestRobot(Cache.ALLY_ROBOTS, r -> Util.isAttacker(r.type));
        cachedClosestAllyAttackerLocation = closestAllyAttacker == null ? null : closestAllyAttacker.location;
        if (tryMoveToHealAtIsland()) {
            return;
        }
        if (executeMicro()) {
            return;
        }
        if (tryMoveToHoldIsland()) {
            return;
        }
        // go to attack random other enemies (non-attackers)
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS);
        if (enemy != null) {
            Direction direction = getBestMoveDirection((beforeCurrent, afterCurrent) -> getScoreWithActionSingleEnemyAttacker(beforeCurrent, afterCurrent, enemy.location));
            if (direction != Direction.CENTER) {
                Util.tryMove(direction);
            }
            return;
        }
        // do not go to squares within 9 distance of hq
        if (EnemyHqGuesser.anyConfirmed(enemyHqLocation -> enemyHqLocation.isWithinDistanceSquared(Cache.MY_LOCATION, 9))) {
            // we are currently next to enemy hq - let's just try to leave
            RobotInfo enemyHq = Util.getClosestEnemyRobot(r -> r.type == RobotType.HEADQUARTERS);
            if (enemyHq != null) {
                Util.tryKiteFrom(enemyHq.location);
                return;
            }
        }
        // camp the headquarters
        MapLocation location = getMacroAttackLocation();
        if (location == null) {
            Util.tryExplore();
        } else {
            Debug.setIndicatorLine(Profile.ATTACKING, Cache.MY_LOCATION, location, 0, 0, 0); // black
            if (shouldEncircleMacroAttackLocation && Cache.MY_LOCATION.isWithinDistanceSquared(location, 16)) {
                // try to circle around it
                tryPathfindingTangent(location);
            } else {
                Util.tryPathfindingMove(location);
            }
        }
    }

    public static boolean tryMoveToHealAtIsland() {
        MapLocation visibleUnoccupiedHealingLocation = IslandTracker.nearestUnoccupiedAllyForHealing;
        MapLocation visibleAnyHealingLocation = IslandTracker.nearestAllyForHealing;
        if (visibleUnoccupiedHealingLocation == null) {
            if (rc.getHealth() >= RETREAT_HEALTH_THRESHOLD) {
                return false;
            }
            // Let's go to an island location that we have seen before
            int islandIndex = IslandTracker.getClosestOurIsland(i -> true);
            if (islandIndex == -1) {
                return false;
            }
            MapLocation islandLocation = IslandTracker.getLocationOfIsland(islandIndex);
            if (islandLocation == null) {
                Debug.failFast("Null island location");
            } else {
                Util.tryPathfindingMove(islandLocation);
                return true;
            }
        } else {
            Debug.setIndicatorLine(Profile.MICRO, Cache.MY_LOCATION, visibleUnoccupiedHealingLocation, 128, 0, 255); // purple
            Debug.setIndicatorLine(Profile.MICRO, Cache.MY_LOCATION, visibleAnyHealingLocation, 0, 255, 255); // cyan
            if (Cache.MY_LOCATION.equals(visibleUnoccupiedHealingLocation)) {
                if (rc.getHealth() >= Constants.ROBOT_TYPE.getMaxHealth()) {
                    return false;
                }
                // we're on an island - feel free to micro anywhere
                // we ignore the micro result - we stay still if there's no micro to be done
                executeMicro();
                return true;
            } else {
                if (rc.getHealth() >= Constants.ROBOT_TYPE.getMaxHealth()) {
                    return false;
                }
                if (rc.getHealth() >= RETREAT_HEALTH_THRESHOLD && !Cache.MY_LOCATION.isAdjacentTo(visibleAnyHealingLocation)) {
                    return false;
                }
                // only micro if the direction yields to a location <= 4 from visibleHealingLocation
                Direction microDirection = getMicroDirection();
                if (microDirection != null && Cache.MY_LOCATION.add(microDirection).isWithinDistanceSquared(visibleAnyHealingLocation, Constants.DISTANCE_SQUARED_FOR_HEALING_FROM_ISLAND)) {
                    Util.tryMove(microDirection);
                } else {
                    Util.tryPathfindingMove(visibleUnoccupiedHealingLocation);
                }
                return true;
            }
        }
        return false;
    }

    public static void tryPathfindingTangent(MapLocation target) {
        double distance = 10;
        double direction = Math.atan2(target.y - Cache.MY_LOCATION.y, target.x - Cache.MY_LOCATION.x) + Math.PI / 2;
        double cos = Math.cos(direction);
        double sin = Math.sin(direction);
        MapLocation tangentTarget = Cache.MY_LOCATION.translate((int) (cos * distance), (int) (sin * distance));
        Util.tryPathfindingMove(tangentTarget);
    }

    public static boolean executeMicro() {
        if (Cache.ALLY_ROBOTS.length > 15) {
            RobotInfo enemy = Util.getClosestEnemyRobot(robot -> Util.isAttacker(robot.type));
            if (enemy == null) {
                return false;
            } else {
                Util.tryPathfindingMove(enemy.location);
                return true;
            }
        } else {
            Direction direction = getMicroDirection();
            if (direction == null) {
                return false;
            }
            if (direction != Direction.CENTER) {
                Util.tryMove(direction);
            }
            return true;
        }
    }

    public static void debug_renderMicro() {
        if (Profile.MICRO.enabled()) {
            Debug.setIndicatorDot(Profile.MICRO, Cache.MY_LOCATION, 255, 255, 0); // yellow
            for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
                RobotInfo robot = Cache.ENEMY_ROBOTS[i];
                if (robot.getID() == LauncherMicro.lastEnemyDeathId) {
                    Debug.setIndicatorDot(Profile.MICRO, robot.location, 255, 0, 255); // pink
                } else if (Util.isAttacker(robot.type)) {
                    Debug.setIndicatorDot(Profile.MICRO, robot.location, 255, 0, 0); // red
                } else {
                    Debug.setIndicatorDot(Profile.MICRO, robot.location, 255, 128, 0); // orange
                }
            }
            for (int i = Cache.ALLY_ROBOTS.length; --i >= 0; ) {
                RobotInfo robot = Cache.ALLY_ROBOTS[i];
                if (Util.isAttacker(robot.type)) {
                    Debug.setIndicatorDot(Profile.MICRO, robot.location, 0, 255, 0); // green
                }
            }
        }
    }

    public static Direction getMicroDirection() {
        RobotInfo closestEnemyAttacker = Util.getClosestEnemyRobot(robot -> Util.isAttacker(robot.type) && robot.getID() != LauncherMicro.lastEnemyDeathId);
        debug_renderMicro();
        if (closestEnemyAttacker == null) {
            if (LauncherMicro.numberOfUniqueEnemyAttackersInHistory() > 0) {
                MapLocation enemyLocation = lastEnemyLocation;
                if (lastEnemyLocation == null || lastEnemyLocationTurn < rc.getRoundNum() - 10) {
                    enemyLocation = Cache.MY_LOCATION;
                    // likely just ran out of bytecodes
//                    Debug.failFast("???"); // should never happen
                }
                RobotInfo allyAttacker = Util.getClosestRobot(Cache.ALLY_ROBOTS, enemyLocation, r -> Util.isAttacker(r.type));
                if (allyAttacker == null) {
                    Debug.setIndicatorString(Profile.MICRO, "No Ally");
                    return Direction.CENTER;
                } else {
                    // go towards ally attacker
                    MapLocation allyLocation = allyAttacker.location;
                    Direction bestDir = getBestMoveDirection((beforeCurrent, afterCurrent) -> {
                        if (Cache.MY_LOCATION.equals(beforeCurrent)) {
                            // tiebreaker goes in favor of standing still
                            return -afterCurrent.distanceSquaredTo(allyLocation) * 1_000_000 + 10_000;
                        } else {
                            return -afterCurrent.distanceSquaredTo(allyLocation) * 1_000_000;
                        }
                    });
                    Debug.setIndicatorLine(Profile.MICRO, Cache.MY_LOCATION, allyLocation, 255, 255, 0); // yellow
                    Debug.setIndicatorString(Profile.MICRO, "No Enemy: " + bestDir);
                    // execute bestDir only if it gets us closer to the enemy
                    if (Cache.MY_LOCATION.add(bestDir).distanceSquaredTo(enemyLocation) < Cache.MY_LOCATION.distanceSquaredTo(enemyLocation)) {
                        return bestDir;
                    } else {
                        return Direction.CENTER;
                    }
                }
            } else {
                Debug.setIndicatorString(Profile.MICRO, "No Enemy History");
                return null; // don't micro
            }
        } else {
            int enemyId = closestEnemyAttacker.getID();
            MapLocation enemyLocation = closestEnemyAttacker.location;
            Debug.setIndicatorLine(Profile.MICRO, Cache.MY_LOCATION, enemyLocation, 0, 255, 255); // cyan
            // assumption: there is an enemy attacker in vision
            if (rc.isActionReady()) {
                int numAlliesWithinVisionRange = 1; // include ourselves
                int visionRange = RobotType.LAUNCHER.visionRadiusSquared;
                for (int i = Cache.ALLY_ROBOTS.length; --i >= 0; ) {
                    RobotInfo robot = Cache.ALLY_ROBOTS[i];
                    if (Util.isAttacker(robot.type) && robot.location.isWithinDistanceSquared(enemyLocation, visionRange)) {
                        numAlliesWithinVisionRange++;
                    }
                }

                // check if any enemy attackers are 1 shot
                int discount = 0;
                if (LambdaUtil.arraysAnyMatch(Cache.ENEMY_ROBOTS, r -> Util.isAttacker(r.type) && r.health <= Constants.ROBOT_TYPE.damage && r.getID() != LauncherMicro.lastEnemyDeathId)) {
                    discount = 1;
                }
                Debug.setIndicatorString(Profile.MICRO, "Action Ready: " + numAlliesWithinVisionRange + " - " + LauncherMicro.numberOfUniqueEnemyAttackersInHistory() + " - " + discount);
                if (numAlliesWithinVisionRange >= LauncherMicro.numberOfUniqueEnemyAttackersInHistory() - discount) {
                    // if so, just micro towards it


                    // allowed to go to squares that are [20 -> 17] or [17 -> 16] or [18 -> 13] distance squared?

                    // if we've been sitting here for a certain number of turns, then we can attack
                    return getBestMoveDirection((beforeCurrent, afterCurrent) -> getScoreWithActionSingleEnemyAttacker(beforeCurrent, afterCurrent, enemyLocation));
                } else {
                    if (LauncherMicro.allowedToStandStill(enemyLocation)) {
                        return Direction.CENTER;
                    }
                    // otherwise, kite
                    return getBestMoveDirection(Launcher::getScoreForKiting);
                }
            } else {
//                don't kite the enemy that JUST showed up? (cuz he will be there next turn)
//                BUT only if we have allies that are in vision range of the enemy

                // if enemyLocation was not the same launcher the turn before AND allies exist in vision range of enemyLocation
                //       return Direction.CENTER
                if (rc.getHealth() > 40 &&
                        // whether the enemy just got here (he will be here the next turn)
                        enemyId != LauncherMicro.enemyLocationLastTurnId(enemyLocation) &&
                        // whether our allies can see them
                        LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS,
                                r -> Util.isAttacker(r.type) &&
                                        r.location.isWithinDistanceSquared(enemyLocation, RobotType.LAUNCHER.visionRadiusSquared))) {
                    Debug.setIndicatorString(Profile.MICRO, "Action Not Ready - CENTER A");
                    // we can attack him next turn too and still be able to kite
                    return Direction.CENTER;
                } else if (LauncherMicro.allowedToStandStill(enemyLocation)) {
                    Debug.setIndicatorString(Profile.MICRO, "Action Not Ready - CENTER B");
                    // if so, stay still
                    return Direction.CENTER;
                } else {
                    // otherwise, kite
                    Debug.setIndicatorString(Profile.MICRO, "Action Not Ready - KITE");
                    // if so, stay still
                    return getBestMoveDirection(Launcher::getScoreForKiting);
                }
            }
        }
    }

    // want to maximize score
    public static Direction getBestMoveDirection(ToDoubleBiFunction<MapLocation, MapLocation> scorer) {
        Direction bestDirection = Direction.CENTER;
        double bestScore = -Double.MAX_VALUE;
        for (int i = Constants.ALL_DIRECTIONS.length; --i >= 0; ) {
            Direction direction = Constants.ALL_DIRECTIONS[i];
            if (direction != Direction.CENTER && !rc.canMove(direction)) { // this is for micro - so let's ignore currents
                // occupied
                continue;
            }
            MapLocation location = Cache.MY_LOCATION.add(direction);
            double score = scorer.applyAsDouble(location, CurrentsCache.get(location));
            if (score > bestScore) {
                bestScore = score;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    public static double getScoreWithActionSingleEnemyAttacker(MapLocation beforeCurrent, MapLocation afterCurrent, MapLocation enemyLocation) {
        double score = 0;
        // prefer non clouds if we're not in a cloud
        try {
            if (!rc.senseCloud(Cache.MY_LOCATION) && (rc.canSenseLocation(afterCurrent) && !rc.senseCloud(afterCurrent))) {
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
        if (EnemyHqGuesser.anyConfirmed(enemyHqLocation -> {
            return beforeCurrent.isWithinDistanceSquared(enemyHqLocation, RobotType.HEADQUARTERS.actionRadiusSquared);
        })) {
            score -= 5_000_000;
        }

        // Prefer not moving - save our movement for next turn
        if (Cache.MY_LOCATION.equals(beforeCurrent)) {
            score += 2_000_000;
        }

        // prefer squares where you're further away from the enemy
        score += afterCurrent.distanceSquaredTo(enemyLocation) * 30_000;

        // prefer straight moves
        if (Util.isStraightDirection(Cache.MY_LOCATION.directionTo(afterCurrent))) {
            score += 10_000;
        }

        // Prefer closer to closest attacker ally
        if (cachedClosestAllyAttackerLocation != null) {
            score -= afterCurrent.distanceSquaredTo(cachedClosestAllyAttackerLocation);
        }

        return score;
    }

    public static double getScoreForKiting(MapLocation beforeCurrent, MapLocation afterCurrent) {
        double score = 0;

        // compute enemy statistics
        int closestEnemyAttackerDistanceSquared = Integer.MAX_VALUE;
        int numEnemyAttackerRobotsWithin = 0;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type) && enemy.getID() != LauncherMicro.lastEnemyDeathId) {
                int distanceSquared = afterCurrent.distanceSquaredTo(enemy.location);
                closestEnemyAttackerDistanceSquared = Math.min(closestEnemyAttackerDistanceSquared, distanceSquared);
                if (distanceSquared <= Constants.ROBOT_TYPE.visionRadiusSquared) {
                    numEnemyAttackerRobotsWithin++;
                }
            }
        }

        // prefer squares where attackers can't see you
        score -= numEnemyAttackerRobotsWithin * 2_000_000.0;

        // prefer squares where you're not in enemy hq attack range
        // you get damaged BEFORE currents are applied
        if (EnemyHqGuesser.anyConfirmed(enemyHqLocation -> {
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


    private static MapLocation lastMacroLocation = null;
    private static StringBuilder macroLocationDistances = new StringBuilder();
    private static int MACRO_LOCATIONS_HISTORY_LENGTH = 35;

    // returns whether it has gotten closer in the past 50 turns
    public static boolean hasGottenCloserToMacroLocation(char distance) {
        // oh wait we can't do a binary insert in a StringBuilder.. so we're just going to check x + 2, x + 3, x + 4
        macroLocationDistances.append(distance);
        if (macroLocationDistances.length() > MACRO_LOCATIONS_HISTORY_LENGTH) {
            char before = macroLocationDistances.charAt(0);
            // remove 0th char
            macroLocationDistances.deleteCharAt(0);
            Debug.setIndicatorDot(Profile.ATTACKING, Cache.MY_LOCATION, 255, 255, 0); // yellow
            return before + 2 > distance;
        } else {
            // we haven't tried enough
            return true;
        }
    }

    public static void clearMacroLocationDistances() {
        macroLocationDistances.setLength(0);
    }

    private static FastIntSet2D blacklist;

    private static boolean shouldEncircleMacroAttackLocation = false; // lol code is totally clean here
    public static MapLocation getMacroAttackLocation() {
        // NOTE: if we change this, don't forget to change Carrier.getMacroAttackLocation()
        MapLocation macroLocation = getMacroEnemyHQLocation();
        if (macroLocation == null) {
            blacklist.reset();
        } else {
            // consider blacklisting if we're not getting any closer
            if (lastMacroLocation != null && lastMacroLocation.equals(macroLocation)) {
                if (Cache.MY_LOCATION.isWithinDistanceSquared(macroLocation, 16)) {
                    // we're basically there, no need to blacklist via distances
                    clearMacroLocationDistances();
                } else {
                    // wow this is this first time i've casted a double to a char - double truncates to an integer then converts to char
                    char distance = (char) Math.sqrt(Cache.MY_LOCATION.distanceSquaredTo(macroLocation));
                    if (!hasGottenCloserToMacroLocation(distance)) {
                        if (Cache.ALLY_ROBOTS.length >= 5) {
                            Debug.setIndicatorString(Profile.ATTACKING, "STUCK - BLACKLISTING");
                            blacklist.add(macroLocation.x, macroLocation.y);
                        }
                    }
                }
            } else {
                // we changed macro locations
                clearMacroLocationDistances();
                lastMacroLocation = macroLocation;
            }
            // don't consider blacklisting via vision if we're not even close to the location (saves bytecodes)
            if (Cache.MY_LOCATION.isWithinDistanceSquared(macroLocation, 50)) {
                // check if hq already has tons of ally units nearby the macro location
                if (Util.hasAtLeastXAllyAttackersWithin(macroLocation, 20, 5)) {
                    if (!Cache.MY_LOCATION.isAdjacentTo(macroLocation)) {
                        blacklist.add(macroLocation.x, macroLocation.y);
                    }
                }
            }
        }

        // Consider defending separately from enemy hq locations - we don't have blacklisting defend locations
        MapLocation defendLocation = getLocationToDefend();
        if (defendLocation != null) {
            if (macroLocation == null || !Cache.MY_LOCATION.isWithinDistanceSquared(macroLocation, Cache.MY_LOCATION.distanceSquaredTo(defendLocation))) {
                macroLocation = defendLocation;
                shouldEncircleMacroAttackLocation = false;
            }
        }
        return macroLocation;
    }

    public static MapLocation getMacroEnemyHQLocation() {
        RobotInfo closestVisibleEnemyHQ = Util.getClosestEnemyRobot(robot -> robot.type == RobotType.HEADQUARTERS &&
                !blacklist.contains(robot.location.x, robot.location.y));
        MapLocation ret = closestVisibleEnemyHQ == null ? null : closestVisibleEnemyHQ.location;
        if (ret == null) {
            ret = EnemyHqGuesser.getClosestConfirmed(location -> !blacklist.contains(location.x, location.y));
            shouldEncircleMacroAttackLocation = true;
        }
        if (ret == null) {
            ret = EnemyHqGuesser.getClosestPredictionPreferRotationalSymmetry(location -> !blacklist.contains(location.x, location.y));
            shouldEncircleMacroAttackLocation = false;
        }
        if (ret == null) {
            ret = EnemyHqGuesser.getClosestPrediction(location -> !blacklist.contains(location.x, location.y));
            shouldEncircleMacroAttackLocation = false;
        }
        return ret;
    }

    private static MapLocation lastDefendLocation;
    public static MapLocation getLocationToDefend() {
        // Try to attack enemies near our headquarters
        if (Communication.enemyLocationsFromHeadquarters != null) {
            int bestDistanceSquared = 257; // max distance + 1 that we will respond to
            MapLocation bestLocation = null;
            for (int i = Communication.enemyLocationsFromHeadquarters.length; --i >= 0; ) {
                MapLocation enemyLocation = Communication.enemyLocationsFromHeadquarters[i];
                if (enemyLocation != null) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(enemyLocation);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = enemyLocation;
                    }
                }
            }
            // consider commed enemies
            MapLocation commedEnemyLocation = Communication.getClosestCommedEnemyLocation();
            if (commedEnemyLocation != null) {
                if (Cache.MY_LOCATION.isWithinDistanceSquared(commedEnemyLocation, bestDistanceSquared)) {
                    bestLocation = commedEnemyLocation;
                    // no need to set bestDistanceSquared because this is the last one
                }
            }
            if (bestLocation == null) {
                // invalidate lastDefendLocation if necessary
                if (lastDefendLocation == null) {
                    return null;
                } else {
                    if (Cache.ENEMY_ROBOTS.length == 0 && Cache.MY_LOCATION.isAdjacentTo(lastDefendLocation)) {
                        lastDefendLocation = null;
                        return null;
                    } else {
                        return lastDefendLocation;
                    }
                }
            } else {
                lastDefendLocation = bestLocation;
                return bestLocation;
            }
        }
        return null;
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
