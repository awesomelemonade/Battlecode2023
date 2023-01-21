package beforeSpecChanges;

import battlecode.common.*;
import beforeSpecChanges.robots.Carrier;
import beforeSpecChanges.robots.Headquarters;
import beforeSpecChanges.robots.Launcher;
import beforeSpecChanges.util.*;

public class RobotPlayer {
    public static int currentTurn;
    public static void run(RobotController controller) throws GameActionException {
        Constants.rc = controller;

        RobotType robotType = controller.getType();
        RunnableBot bot;
        switch (robotType) { // Can't use switch expressions :(
            case HEADQUARTERS:
                bot = new Headquarters();
                break;
            case CARRIER:
                bot = new Carrier();
                break;
            case LAUNCHER:
                bot = new Launcher();
                break;
            default:
                throw new IllegalStateException("Unknown Robot Type: " + robotType);
        }

        try {
            Util.init(controller);
            bot.init();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean errored = false;
        boolean overBytecodes = false;

        while (true) {
            try {
                while (true) {
                    currentTurn = controller.getRoundNum();
                    Util.loop();
                    bot.loop();

                    tryMultiAction(controller, bot);
                    if (!Util.isBuilding(Constants.ROBOT_TYPE)) {
                        tryMultiMove(controller, bot);
                    }
                    bot.postLoop();
                    Util.postLoop();

                    if (controller.getRoundNum() != currentTurn) {
                        overBytecodes = true;
                        // We ran out of bytecodes!
                        int over = Clock.getBytecodeNum() + (controller.getRoundNum() - currentTurn - 1) * controller.getType().bytecodeLimit;
                        Debug.println(Profile.ERROR_STATE, controller.getLocation() + " out of bytecodes: " + Cache.TURN_COUNT + " (over by " + over + ")");
                    }
                    if (errored) {
                        Debug.setIndicatorDot(Profile.ERROR_STATE, controller.getLocation(), 255, 0, 255); // pink
                    }
                    if (overBytecodes) {
                        Debug.setIndicatorDot(Profile.ERROR_STATE, controller.getLocation(), 128, 0, 255); // purple
//                        markProfiler(20);
                    }
                    Clock.yield();
//                    if (overBytecodes) {
//                        controller.resign();
//                    }
                }
            } catch (Exception ex) {
                Debug.println(Profile.ERROR_STATE, controller.getLocation() + " errored: " + Cache.TURN_COUNT);
                ex.printStackTrace();
                errored = true;
                if (Constants.DEBUG_RESIGN) {
                    controller.resign();
                }
                Clock.yield();
            }
        }
    }
    public static void markProfiler(int x) {
        if (x > 0) {
            markProfiler(x - 1);
            for (int i = 0; i < x; i++) {
                i++;
            }
        }
    }
    public static void tryMultiAction(RobotController controller, RunnableBot bot) throws GameActionException {
        int beforeActionCooldown = controller.getActionCooldownTurns();
        if (controller.isActionReady()) {
            bot.action();
        }
        int afterActionCooldown = controller.getActionCooldownTurns();
        // normally a while loop - but this is defensive code to prevent infinite loops that somehow might happen
        for (int i = 0; i < 5; i++) {
            if (afterActionCooldown > beforeActionCooldown && afterActionCooldown < GameConstants.COOLDOWN_LIMIT) {
                beforeActionCooldown = controller.getActionCooldownTurns();
                bot.action();
                afterActionCooldown = controller.getActionCooldownTurns();
            } else {
                break;
            }
        }
    }

    public static void tryMultiMove(RobotController controller, RunnableBot bot) throws GameActionException {
        int beforeMoveCooldown = controller.getMovementCooldownTurns();
        if (controller.isMovementReady()) {
            bot.move();
        }
        int afterMoveCooldown = controller.getMovementCooldownTurns();
        tryMultiAction(controller, bot);
        // normally a while loop - but this is defensive code to prevent infinite loops that somehow might happen
        for (int i = 0; i < 3; i++) {
            if (afterMoveCooldown > beforeMoveCooldown && afterMoveCooldown < GameConstants.COOLDOWN_LIMIT) {
                beforeMoveCooldown = controller.getActionCooldownTurns();
                bot.move();
                afterMoveCooldown = controller.getActionCooldownTurns();
                tryMultiAction(controller, bot);
            } else {
                break;
            }
        }
    }
}