package beforeBetterMicro.robots;

import battlecode.common.*;
import beforeBetterMicro.fast.FastDoubleTracker;
import beforeBetterMicro.fast.FastIntMap;
import beforeBetterMicro.fast.FastIntTracker;
import beforeBetterMicro.util.*;

import java.util.function.ToIntFunction;

import static beforeBetterMicro.util.Constants.rc;

public class Headquarters implements RunnableBot {
    private static FastIntMap carrierTasks;

    private static final int TRACKER_SIZE = 50;

    private static FastIntTracker adamantiumIncome = new FastIntTracker(TRACKER_SIZE);
    private static FastIntTracker manaIncome = new FastIntTracker(TRACKER_SIZE);
    private static FastIntTracker elixirIncome = new FastIntTracker(TRACKER_SIZE);

    private static FastIntTracker adamantiumMinerTracker = new FastIntTracker(TRACKER_SIZE);
    private static FastIntTracker manaMinerTracker = new FastIntTracker(TRACKER_SIZE);
    private static FastIntTracker elixirMinerTracker = new FastIntTracker(TRACKER_SIZE);

    private static FastDoubleTracker adamantiumDerivativeTracker = new FastDoubleTracker(10);

    private static int lastAdamantium = GameConstants.INITIAL_AD_AMOUNT;
    private static int lastMana = GameConstants.INITIAL_MN_AMOUNT;
    private static int lastElixir = 0;

    private static MapLocation[] shuffledLocations;

    private static boolean hasSpaceForMiners = true;

    @Override
    public void init() throws GameActionException {
        // max robots = num hqs * num turns = 8 * 2000 = 16000
        // 4096 * 16 is well beyond that
        carrierTasks = new FastIntMap(4096 * 16);
        shuffledLocations = rc.getAllLocationsWithinRadiusSquared(Cache.MY_LOCATION, RobotType.HEADQUARTERS.actionRadiusSquared);
        hasSpaceForMiners = hasSpaceForMinersToDeposit();
    }

    public static MapLocation getNearestEnemyHQLocation() {
        // use known, then fall back to predicted
        MapLocation ret = EnemyHqTracker.getClosest();
        if (ret == null) {
            ret = EnemyHqGuesser.getClosest(l -> true);
        }
        return ret;
    }

    public static Communication.CarrierTaskType getNewTask() {
        int numAnchors = rc.getNumAnchors(null);
        if (numAnchors > 0) {
            return Communication.CarrierTaskType.PICKUP_ANCHOR;
        }
        if (LambdaUtil.arraysAnyMatch(Cache.ENEMY_ROBOTS, r -> r.type != RobotType.HEADQUARTERS)) {
            return Communication.CarrierTaskType.MINE_MANA;
        }
//        if (closestEnemyHq == null) {
//
//        } else {
//            double distance = Math.sqrt(Cache.MY_LOCATION.distanceSquaredTo(closestEnemyHq));
//        }
        double targetRatio = 0.9; // 0 = full adamantium, 1 = full mana
        if (currentMana * (1.0 - targetRatio) <= currentAdamantium * targetRatio) {
            return Communication.CarrierTaskType.MINE_MANA;
        } else {
            return Communication.CarrierTaskType.MINE_ADAMANTIUM;
        }
//        if (currentMana <= currentAdamantium) {
//            return Communication.CarrierTaskType.MINE_MANA;
//        } else {
//            return Communication.CarrierTaskType.MINE_ADAMANTIUM;
//        }
    }

    private static double lastAdamantiumIncome = 0;

    public static void debug_render() {
//        if (rc.getRoundNum() >= 50) {
//            rc.resign();
//        }
//        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
//            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
//                if (i % 3 == 1 && j % 3 == 1) {
//                    MapLocation location = new MapLocation(i, j);
//                    Debug.setIndicatorDot(location, 0, 255, 0);
//                }
//            }
//        }
    }

    public void debug_printInfo() {
//        Debug.println(String.format("[data] round: %d, adamantiumIncome: %f, manaIncome: %f, adamantiumMiners: %f, manaMiners: %f",
//                rc.getRoundNum(), adamantiumIncome.average(), manaIncome.average(), adamantiumMinerTracker.average(), manaMinerTracker.average()));
    }

    @Override
    public void loop() throws GameActionException {
        debug_render();

        int currentAdamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int currentMana = rc.getResourceAmount(ResourceType.MANA);
        int currentElixir = rc.getResourceAmount(ResourceType.ELIXIR);
        adamantiumIncome.add(currentAdamantium - lastAdamantium);
        manaIncome.add(currentMana - lastMana);
        elixirIncome.add(currentElixir - lastElixir);

        double currentAdamantiumIncome = adamantiumIncome.average();
        adamantiumDerivativeTracker.add(currentAdamantiumIncome - lastAdamantiumIncome);
        lastAdamantiumIncome = currentAdamantiumIncome;

        debug_printInfo();
    }

    @Override
    public void postLoop() throws GameActionException {
        assignTasks();
        lastAdamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        lastMana = rc.getResourceAmount(ResourceType.MANA);
        lastElixir = rc.getResourceAmount(ResourceType.ELIXIR);
    }

    private static MapLocation closestEnemyHq = null;
    private static double currentAdamantium = 0.0;
    private static double currentMana = 0.0;
    private static double adamantiumPerMiner = 0.0;
    private static double manaPerMiner = 0.0;
    public static void assignTasks() throws GameActionException {
        closestEnemyHq = getNearestEnemyHQLocation();
        currentAdamantium = adamantiumIncome.average();
        currentMana = manaIncome.average();
        if (adamantiumMinerTracker.sum() == 0) {
            adamantiumPerMiner = 1; // assume 1
        } else {
            adamantiumPerMiner = currentAdamantium / adamantiumMinerTracker.average();
        }
        if (manaMinerTracker.sum() == 0) {
            manaPerMiner = 1; // assume 1
        } else {
            manaPerMiner = currentMana / manaMinerTracker.average();
        }
        RobotInfo[] allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, Constants.ALLY_TEAM);
        if (allies.length > 28) {
            // pigeonhole principle
            allies = rc.senseNearbyRobots(RobotType.HEADQUARTERS.actionRadiusSquared, Constants.ALLY_TEAM);
        }
        int numHeadquarters = Communication.headquartersLocations == null ? 1 : Communication.headquartersLocations.length;
        int assignedCount = 0;
        // new turn for the trackers
        adamantiumMinerTracker.add(0);
        manaMinerTracker.add(0);
        elixirMinerTracker.add(0);
        for (int i = allies.length; --i >= 0; ) {
            RobotInfo ally = allies[i];
            if (ally.type == RobotType.CARRIER) {
                int weight = Util.getWeight(ally);
                int robotIndex = ally.getID() - Constants.ROBOT_STARTING_ID;
                if (weight == 0) {
                    // check if there is a task set
                    int task = carrierTasks.get(robotIndex);
                    if (task == Communication.CARRIER_TASK_NONE_ID || task == Communication.CARRIER_TASK_ANCHOR_PICKUP_ID) {
                        // TODO: find new a task
                        Communication.CarrierTaskType newTask = getNewTask();
                        switch (newTask) {
                            case MINE_ADAMANTIUM:
                                adamantiumMinerTracker.incrementLast();
                                currentAdamantium += adamantiumPerMiner;
                                break;
                            case MINE_MANA:
                                manaMinerTracker.incrementLast();
                                currentMana += manaPerMiner;
                                break;
                            case MINE_ELIXIR:
                                elixirMinerTracker.incrementLast();
                                break;
                        }
                        task = newTask.id();
                        carrierTasks.set(robotIndex, task);
                    }
//                    int[] r = new int[] {255, 255, 255, 0, 0, 0};
//                    int[] g = new int[] {0, 128, 255, 255, 255, 0};
//                    int[] b = new int[] {0, 0, 0, 0, 255, 255};
//                    Debug.setIndicatorDot(ally.location, r[task], g[task], b[task]);
                    Communication.addTask(ally.location, task);
                    assignedCount++;
                    if (assignedCount >= Communication.MAX_CARRIER_COMMED_TASKS / numHeadquarters + 1) {
                        break;
                    }
                } else {
                    // reset the task
                    carrierTasks.set(robotIndex, Communication.CARRIER_TASK_NONE_ID);
                }
            }
        }
    }

    @Override
    public void move() {
        // HQs don't move
    }

    @Override
    public void action() {
        int robotCount = rc.getRobotCount();
        int roundNum = rc.getRoundNum();
        if (robotCount > 0.25 * Constants.MAP_WIDTH * Constants.MAP_HEIGHT
                || robotCount > 100 && roundNum > 1800
                || robotCount > 50 && roundNum > 1900
                || roundNum > 1950) {
            int adamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
            int mana = rc.getResourceAmount(ResourceType.MANA);
            if (adamantium >= Anchor.STANDARD.adamantiumCost
                    && mana >= Anchor.STANDARD.manaCost) { // simple random heuristic to build anchors
                if (Math.random() < 0.8) {
                    if (tryBuildAnchor(Anchor.STANDARD)) {
                        return;
                    }
                } else {
                    if (tryBuildCarrier()) {
                        return;
                    }
                    if (tryBuildLauncher()) {
                        return;
                    }
                    // what if there is nowhere to spawn units? let's just build anchors
                    if (tryBuildAnchor(Anchor.STANDARD)) {
                        return;
                    }
                }
            }
            // save to build anchor
        } else {
            if (Cache.ENEMY_ROBOTS.length > 0) {
                if (tryBuildLauncher()) {
                    return;
                }
                tryBuildCarrier();
            } else {
                if (tryBuildCarrier()) {
                    return;
                }
                tryBuildLauncher();
            }
        }
    }

    public static boolean tryBuildAnchor(Anchor anchorType) {
        // don't build more than 2 anchors
        if (rc.getNumAnchors(null) >= 2) {
            return false;
        }
        // do not build anchors if we don't have an ally carrier nearby
        // we can save mana for the tiebreaker
        if (LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS, r -> r.type == RobotType.CARRIER)) {
            if (rc.canBuildAnchor(anchorType)) {
                try {
                    rc.buildAnchor(anchorType);
                    return true;
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
        }
        return false;
    }

    public static boolean tryBuildCarrier() {
        if (rc.getResourceAmount(ResourceType.ADAMANTIUM) < RobotType.CARRIER.buildCostAdamantium) {
            return false;
        }
        if (!hasSpaceForMiners) {
            return false;
        }
        if (LambdaUtil.arraysAllMatch(Cache.ALLY_ROBOTS, r -> r.type == RobotType.HEADQUARTERS)) {
            if (LambdaUtil.arraysAnyMatch(Cache.ENEMY_ROBOTS, r -> Util.isAttacker(r.type))) {
                return false;
            }
        }
        MapLocation wellLocation = WellTracker.getClosestKnownWell(location -> true);
        return tryBuildByScore(RobotType.CARRIER, location -> {
            // we want to be as close to a well as possible
            // heuristic: just use the closest well
            // in the future, we can consider separating adamantium vs mana carriers
            if (wellLocation == null) {
                return 0;
            } else {
                return location.distanceSquaredTo(wellLocation);
            }
        });
    }

    public static MapLocation getMacroAttackLocation() {
        MapLocation ret = EnemyHqTracker.getClosest();
        if (ret == null) {
            // we should use furthest to be more stable?
            ret = EnemyHqGuesser.getClosestPreferRotationalSymmetry(l -> true);
        }
        return ret;
    }

    public static boolean tryBuildLauncher() {
        if (rc.getResourceAmount(ResourceType.MANA) < RobotType.LAUNCHER.buildCostMana) {
            return false;
        }
        if (LambdaUtil.arraysAllMatch(Cache.ALLY_ROBOTS, r -> r.type == RobotType.HEADQUARTERS)) {
            // if 2 or more enemy attackers within radius 16 OR 5 or more enemy attackers within vision radius
            if (Util.numEnemyAttackersWithin(Cache.MY_LOCATION, 16) >= 2
                    || Util.numEnemyAttackersWithin(Cache.MY_LOCATION, Constants.ROBOT_TYPE.visionRadiusSquared) >= 5) {
                return false;
            }
        }
        MapLocation macroLocation = getMacroAttackLocation();
        Debug.setIndicatorLine(Profile.ATTACKING, Cache.MY_LOCATION, macroLocation, 255, 128, 0);
        return tryBuildByScore(RobotType.LAUNCHER, location -> {
            if (macroLocation == null) {
                return 0;
            } else {
                return location.distanceSquaredTo(macroLocation);
            }
        });
    }

    // minimize score
    public static boolean tryBuildByScore(RobotType type, ToIntFunction<MapLocation> scorer) {
        int bestScore = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        for (int i = shuffledLocations.length; --i >= 0;) {
            MapLocation location = shuffledLocations[i];
            if (rc.canBuildRobot(type, location)) {
                int score = scorer.applyAsInt(location);
                if (score < bestScore) {
                    bestLocation = location;
                    bestScore = score;
                }
            }
        }
        if (bestLocation == null) {
            return false;
        } else {
            try {
                rc.buildRobot(type, bestLocation);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
            return true;
        }
    }

    public static boolean hasSpaceForMinersToDeposit() {
        boolean passable = false;
        for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
            Direction direction = Constants.ORDINAL_DIRECTIONS[i];
            MapLocation location = Cache.MY_LOCATION.add(direction);
            // check for passable
            try {
                if (rc.onTheMap(location) && rc.sensePassability(location)) {
                    RobotInfo robot = rc.senseRobotAtLocation(location);
                    if (robot == null || robot.type != RobotType.HEADQUARTERS) {
                        passable = true;
                        break;
                    }
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return passable;
    }
}
