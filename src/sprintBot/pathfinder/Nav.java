package sprintBot.pathfinder;

import battlecode.common.*;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;


public class Nav {
    public static boolean tryMove(Direction direction) {
        if (canMove(direction)) {
            Util.move(direction);
            whereWeLeftOff = Cache.MY_LOCATION;
            return true;
        }
        return false;
    }

    // TODO: to be cached

    private static boolean[] canMove = new boolean[8];

    public static void invalidate() {
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            canMove[direction.ordinal()] = Constants.ROBOT_TYPE == RobotType.LAUNCHER ? Util.canMoveAndCheckCurrentsAndNotNearEnemyHQ(direction) : Util.canMoveAndCheckCurrents(direction);
        }
    }

    public static boolean canMove(Direction direction) {
        return canMove[direction.ordinal()];
    }


    public static Direction tryMoveInDirection(Direction dir) {
        if (tryMove(dir)) {
            return dir;
        }
        Direction left = dir.rotateLeft();
        if (tryMove(left)) {
            return left;
        }
        Direction right = dir.rotateRight();
        if (tryMove(right)) {
            return right;
        }
        return null;
    }

	/*
	Uses the bug pathfinding algorithm to navigate around obstacles towards a target MapLocation
	Taken/adapted from TheDuck314 Battlecode 2016 and Kryptonite Battlecode 2020
	*/

    public static MapLocation whereWeLeftOff = null;
    public static MapLocation bugTarget = null;

    public static boolean bugTracing = false;
    public static MapLocation bugLastWall = null;
    public static int bugClosestDistanceToTarget = Integer.MAX_VALUE;
    public static int bugTurnsWithoutWall = 0;
    public static boolean bugRotateLeft = true; // whether we are rotating left or right

    private static StringBuilder bugVisitedLocations = new StringBuilder();

    public static void addToBugVisitedLocations(MapLocation location) {
        bugVisitedLocations.append((char) (location.x * Constants.MAX_MAP_SIZE + location.y));
    }

    public static boolean isInBugVisitedLocations(MapLocation location) {
        boolean ret = bugVisitedLocations.indexOf(Character.toString((char) (location.x * Constants.MAX_MAP_SIZE + location.y))) != -1;
        return ret;
    }

    public static void clearBugVisitedLocations() {
        bugVisitedLocations.setLength(0);
    }

    public static void debug_render() {
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            if (canMove(direction)) {
                Debug.setIndicatorDot(Profile.PATHFINDING, Cache.MY_LOCATION.add(direction), 0, 255, 0);
            } else {
                Debug.setIndicatorDot(Profile.PATHFINDING, Cache.MY_LOCATION.add(direction), 255, 0, 0);
            }
        }
    }

    public static Direction bugNavigate(MapLocation target) throws GameActionException {
        invalidate();
        debug_render();
        Debug.setIndicatorLine(Profile.PATHFINDING, Cache.MY_LOCATION, target, 0, 0, 255);
        if (!target.equals(bugTarget) || !Cache.MY_LOCATION.equals(whereWeLeftOff)) {
            bugTarget = target;
            bugTracing = false;
            //Debug.println("New Target");
        }

        if (Cache.MY_LOCATION.equals(bugTarget)) {
            return null;
        }

        Direction destDir = Cache.MY_LOCATION.directionTo(bugTarget);

        if (!bugTracing) { // try to go directly towards the target
            //Debug.println("Not bug tracing");
            Direction tryMoveResult = tryMoveInDirection(destDir);
            if (tryMoveResult != null) {
                //Debug.println("move in dir");
                return tryMoveResult;
            } else {
                //Debug.println("start trace");
                bugStartTracing();
            }
        } else { // we are on obstacle, trying to get off of it
            if (Cache.MY_LOCATION.distanceSquaredTo(bugTarget) < bugClosestDistanceToTarget) {
                //Debug.println("attempt to get off");
                Direction tryMoveResult = tryMoveInDirection(destDir);
                if (tryMoveResult != null) { // we got off of the obstacle
                    //Debug.println("got off");
                    bugTracing = false;
                    return tryMoveResult;
                }
            }
        }

        Direction moveDir = bugTraceMove(false);
        //Debug.println("bug trace move");

        if (bugTurnsWithoutWall >= 2) {
            //Debug.println("no wall");
            bugTracing = false;
        }
        //Debug.println("move dir: " + moveDir);

        return moveDir;
    }

    /*
    Runs if we just encountered an obstacle
    */
    public static void bugStartTracing() throws GameActionException {
        bugTracing = true;

        clearBugVisitedLocations();

        bugTurnsWithoutWall = 0;
        bugClosestDistanceToTarget = Cache.MY_LOCATION.distanceSquaredTo(bugTarget);

        Direction destDir = Cache.MY_LOCATION.directionTo(bugTarget);

        Direction leftDir = destDir;
        MapLocation leftDest;
        int leftDist = Integer.MAX_VALUE;
        for (int i = 0; i < 8; ++i) {
            leftDir = leftDir.rotateLeft();
            leftDest = rc.adjacentLocation(leftDir);
            if (canMove(leftDir)) {
                leftDist = leftDest.distanceSquaredTo(bugTarget);
                break;
            }
        }

        Direction rightDir = destDir;
        MapLocation rightDest;
        int rightDist = Integer.MAX_VALUE;
        for (int i = 0; i < 8; ++i) {
            rightDir = rightDir.rotateRight();
            rightDest = rc.adjacentLocation(rightDir);
            if (canMove(rightDir)) {
                rightDist = rightDest.distanceSquaredTo(bugTarget);
                break;
            }
        }

        if (leftDist < rightDist) { // prefer rotate right if equal
            bugRotateLeft = true;
            bugLastWall = rc.adjacentLocation(leftDir.rotateRight());
        } else {
            bugRotateLeft = false;
            bugLastWall = rc.adjacentLocation(rightDir.rotateLeft());
        }
    }

    public static Direction bugTraceMove(boolean recursed) throws GameActionException {
        Direction curDir = Cache.MY_LOCATION.directionTo(bugLastWall);

        addToBugVisitedLocations(Cache.MY_LOCATION);

        if (canMove(curDir)) {
            bugTurnsWithoutWall += 1;
        } else {
            bugTurnsWithoutWall = 0;
        }

        for (int i = 0; i < 8; ++i) {
            if (bugRotateLeft) {
                curDir = curDir.rotateLeft();
            } else {
                curDir = curDir.rotateRight();
            }
            MapLocation curDest = rc.adjacentLocation(curDir);
            if (!rc.onTheMap(curDest) && !recursed) {
                // if we hit the edge of the map, reverse direction and recurse
                bugRotateLeft = !bugRotateLeft;
                return bugTraceMove(true);
            }
            if (tryMove(curDir)) {
                if (isInBugVisitedLocations(Cache.MY_LOCATION)) {
                    //Debug.println("already visited");
                    bugTracing = false;
                }
                return curDir;
            } else {
                bugLastWall = rc.adjacentLocation(curDir);
            }
        }

        return null;
    }
}
