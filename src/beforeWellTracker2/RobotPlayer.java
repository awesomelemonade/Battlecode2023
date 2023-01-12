package beforeWellTracker2;

import battlecode.common.*;
import beforeWellTracker2.robots.Carrier;
import beforeWellTracker2.robots.Headquarters;
import beforeWellTracker2.robots.Launcher;
import beforeWellTracker2.util.*;

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

                    int beforeActionCooldown = controller.getActionCooldownTurns();
                    int beforeMovementCooldown = controller.getMovementCooldownTurns();
                    bot.loop();
                    int afterActionCooldown = controller.getActionCooldownTurns();
                    int afterMovementCooldown = controller.getMovementCooldownTurns();

                    // normally a while loop - but this is defensive code to prevent infinite loops that somehow might happen
                    for (int i = 0; i < 3; i++) {
                        boolean shouldRepeat =
                                afterActionCooldown > beforeActionCooldown && afterActionCooldown < GameConstants.COOLDOWN_LIMIT ||
                                afterMovementCooldown > beforeMovementCooldown && afterMovementCooldown < GameConstants.COOLDOWN_LIMIT;
                        if (shouldRepeat) {
                            beforeActionCooldown = controller.getActionCooldownTurns();
                            beforeMovementCooldown = controller.getMovementCooldownTurns();
                            bot.loop();
                            afterActionCooldown = controller.getActionCooldownTurns();
                            afterMovementCooldown = controller.getMovementCooldownTurns();
                            Debug.setIndicatorDot(Profile.REPEAT_MOVE, controller.getLocation(), 128, 128, 128);
                        } else {
                            break;
                        }
                    }

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
                    }
                    Clock.yield();
                }
            } catch (Exception ex) {
                Debug.println(Profile.ERROR_STATE, controller.getLocation() + " errored: " + Cache.TURN_COUNT);
                ex.printStackTrace();
                errored = true;
                Clock.yield();
            }
        }
    }
}