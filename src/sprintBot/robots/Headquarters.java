package sprintBot.robots;

import battlecode.common.*;
import sprintBot.fast.FastDoubleTracker;
import sprintBot.fast.FastIntMap;
import sprintBot.fast.FastIntTracker;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

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

    @Override
    public void init() throws GameActionException {
        // max robots = num hqs * num turns = 8 * 2000 = 16000
        // 4096 * 16 is well beyond that
        carrierTasks = new FastIntMap(4096 * 16);
        shuffledLocations = rc.getAllLocationsWithinRadiusSquared(Cache.MY_LOCATION, RobotType.HEADQUARTERS.actionRadiusSquared);
    }

    public static int getNewTask() {
        int numAnchors = rc.getNumAnchors(null);
        if (numAnchors > 0) {
            return Communication.CarrierTaskType.PICKUP_ANCHOR.id();
        }
        return 0; // No Task
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

        assignTasks();
        lastAdamantium = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        lastMana = rc.getResourceAmount(ResourceType.MANA);
        lastElixir = rc.getResourceAmount(ResourceType.ELIXIR);
    }

    public static void assignTasks() throws GameActionException {
        RobotInfo[] allies = rc.senseNearbyRobots(RobotType.CARRIER.visionRadiusSquared, Constants.ALLY_TEAM);
        int numHeadquarters = Communication.headquartersLocations == null ? 1 : Communication.headquartersLocations.length;
        int assignedCount = 0;
        for (int i = allies.length; --i >= 0; ) {
            RobotInfo ally = allies[i];
            if (ally.type == RobotType.CARRIER) {
                int weight = Util.getWeight(ally);
                int robotIndex = ally.getID() - Constants.ROBOT_STARTING_ID;
                if (weight == 0) {
                    // check if there is a task set
                    int task = carrierTasks.get(robotIndex);
                    if (task == 0) {
                        // TODO: find new a task
                        int newTask = getNewTask();
                        task = newTask;
                        carrierTasks.set(robotIndex, newTask);
                    }
                    Communication.addTask(ally.location, task);
                    assignedCount++;
                    if (assignedCount >= Communication.MAX_CARRIER_COMMED_TASKS / numHeadquarters + 1) {
                        break;
                    }
                } else {
                    // reset the task
                    carrierTasks.set(robotIndex, 0);
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
                    if (tryBuildRandom(RobotType.CARRIER)) {
                        return;
                    }
                    tryBuildRandom(RobotType.LAUNCHER);
                }
            }
            // save to build anchor
        } else {
            if (Cache.ENEMY_ROBOTS.length > 0) {
                if (tryBuildRandom(RobotType.LAUNCHER)) {
                    return;
                }
                tryBuildRandom(RobotType.CARRIER);
            } else {
                if (tryBuildRandom(RobotType.CARRIER)) {
                    return;
                }
                tryBuildRandom(RobotType.LAUNCHER);
            }
        }
    }

    public static boolean tryBuildAnchor(Anchor anchorType) {
        if (rc.canBuildAnchor(anchorType)) {
            try {
                rc.buildAnchor(anchorType);
                return true;
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
    }

    public static boolean tryBuildRandom(RobotType type) {
        for (int i = shuffledLocations.length; --i >= 0;) {
            if (Util.tryBuild(type, shuffledLocations[i])) {
                return true;
            }
        }
        return false;
    }
}
