package sprintBot.robots;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import sprintBot.pathfinder.Pathfinding;
import sprintBot.util.RunnableBot;
import sprintBot.util.Util;

import static sprintBot.util.Constants.rc;

public class Launcher implements RunnableBot {
    @Override
    public void init() throws GameActionException {

    }

    @Override
    public void loop() throws GameActionException {
        RobotInfo enemy = Util.getClosestEnemyRobot(robot -> robot.type != RobotType.HEADQUARTERS);
        if (enemy == null) {
            // camp the headquarters
            RobotInfo hq = Util.getClosestEnemyRobot(robot -> robot.type == RobotType.HEADQUARTERS);
            if (hq == null) {
                Util.tryExplore();
            } else {
                MapLocation hqLocation = hq.location;
                // TODO - try to circle around it?
                Util.tryPathfindingMove(hqLocation);
            }
        } else {
            // Attack
            MapLocation enemyLocation = enemy.location;
            tryAttack(enemyLocation);
            Util.tryPathfindingMove(enemyLocation);
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
