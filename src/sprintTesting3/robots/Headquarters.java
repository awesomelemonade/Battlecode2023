package sprintTesting3.robots;

import battlecode.common.*;
import sprintTesting3.fast.FastDoubleTracker;
import sprintTesting3.fast.FastIntMap;
import sprintTesting3.fast.FastIntTracker;
import sprintTesting3.util.*;

import java.util.function.ToIntFunction;

import static sprintTesting3.util.Constants.rc;

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
        hasSpaceForMiners = hasSpaceForMiners();
    }

    public static Communication.CarrierTaskType getNewTask() {
        int numAnchors = rc.getNumAnchors(null);
        if (numAnchors > 0) {
            return Communication.CarrierTaskType.PICKUP_ANCHOR;
        }
        if (Math.random() < 0.3) {
            return Communication.CarrierTaskType.MINE_ADAMANTIUM;
        } else {
            return Communication.CarrierTaskType.MINE_MANA;
        }
//        return Communication.CarrierTaskType.NONE.id(); // No Task
//        if (Cache.ENEMY_ROBOTS.length > 0) {
//            return Communication.CarrierTaskType.MINE_MANA.id();
//        }
//        // if our adamantium income is increasing
//        if (adamantiumDerivativeTracker.average() >= -0.2) {
//            // TODO: could be a linear function
//            if (Math.random() < 0.8) {
//                return Communication.CarrierTaskType.MINE_ADAMANTIUM.id();
//            } else {
//                return Communication.CarrierTaskType.MINE_MANA.id();
//            }
//        } else {
//            if (Math.random() < 0.2) {
//                return Communication.CarrierTaskType.MINE_ADAMANTIUM.id();
//            } else {
//                return Communication.CarrierTaskType.MINE_MANA.id();
//            }
//        }
    }

    private static double lastAdamantiumIncome = 0;

    @Override
    public void loop() throws GameActionException {
        Util.shuffle(shuffledLocations);
        int currentAdamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int currentMana = rc.getResourceAmount(ResourceType.MANA);
        int currentElixir = rc.getResourceAmount(ResourceType.ELIXIR);
        adamantiumIncome.add(currentAdamantium - lastAdamantium);
        manaIncome.add(currentMana - lastMana);
        elixirIncome.add(currentElixir - lastElixir);

        double currentAdamantiumIncome = adamantiumIncome.average();
        adamantiumDerivativeTracker.add(currentAdamantiumIncome - lastAdamantiumIncome);
        lastAdamantiumIncome = currentAdamantiumIncome;

//        Debug.println("adamantiumIncome: " + adamantiumIncome.average() + ", adamantiumDerivative: " + adamantiumDerivativeTracker.average());

    }

    @Override
    public void postLoop() throws GameActionException {
        assignTasks();
        lastAdamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        lastMana = rc.getResourceAmount(ResourceType.MANA);
        lastElixir = rc.getResourceAmount(ResourceType.ELIXIR);
    }

    public static void assignTasks() throws GameActionException {
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
                                break;
                            case MINE_MANA:
                                manaMinerTracker.incrementLast();
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
        if (!hasSpaceForMiners) {
            return false;
        }
        if (Cache.ALLY_ROBOTS.length == 0 && LambdaUtil.arraysAnyMatch(Cache.ENEMY_ROBOTS, r -> Util.isAttacker(r.type))) {
            return false;
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
            ret = EnemyHqGuesser.getFarthest(Cache.MY_LOCATION);
        }
        return ret;
    }

    public static boolean tryBuildLauncher() {
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

    public static boolean hasSpaceForMiners() {
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
