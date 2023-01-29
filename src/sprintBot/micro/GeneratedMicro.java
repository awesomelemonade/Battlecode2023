package sprintBot.micro;

import battlecode.common.Direction;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import sprintBot.util.Cache;
import battlecode.common.MapLocation;
import sprintBot.util.LambdaUtil;
import sprintBot.util.Util;

import static sprintBot.util.Constants.rc;

public class GeneratedMicro {
    public static boolean PARAM_0;
    public static boolean PARAM_1;
    public static boolean PARAM_2;
    public static boolean PARAM_3;
    public static boolean PARAM_4;
    public static long SUBPARAM_0;
    public static long SUBPARAM_1;
    public static long SUBPARAM_2;
    public static long SUBPARAM_3;
    public static long SUBPARAM_4;
    public static long SUBPARAM_5;
    public static long SUBPARAM_6;

    public static Direction getBestDirection_0000000000000000() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_4);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000001() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_6 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000010() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_4 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_0 * 100L) + (-SUBPARAM_6);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000011() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (-SUBPARAM_2 * 1000000L) + (-SUBPARAM_5 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000100() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_5 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_4);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000101() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_6 * 10000L) + (-SUBPARAM_3 * 100L) + (SUBPARAM_2);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000110() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000000111() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_3 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (-SUBPARAM_5 * 10000L) + (-SUBPARAM_1 * 100L) + (SUBPARAM_6);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001000() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_5 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_2 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001001() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001010() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_2 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001011() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (SUBPARAM_5 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (SUBPARAM_2 * 100L) + (SUBPARAM_6);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001100() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001101() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_5 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (SUBPARAM_4 * 1000000L) + (SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_0);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001110() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (SUBPARAM_2);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000001111() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_4 * 100000000L) + (-SUBPARAM_6 * 1000000L) + (SUBPARAM_1 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010000() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_3 * 10000000000L) + (-SUBPARAM_6 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_5 * 100L) + (-SUBPARAM_1);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010001() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (SUBPARAM_2 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_1);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010010() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_0 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_2 * 1000000L) + (SUBPARAM_6 * 10000L) + (SUBPARAM_4 * 100L) + (-SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010011() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_0 * 1000000L) + (-SUBPARAM_6 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010100() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_2 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (SUBPARAM_1 * 1000000L) + (-SUBPARAM_3 * 10000L) + (-SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010101() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_2 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_0 * 100000000L) + (-SUBPARAM_5 * 1000000L) + (-SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010110() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_0 * 10000000000L) + (-SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000010111() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_3 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_0 * 10000L) + (SUBPARAM_1 * 100L) + (SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011000() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (SUBPARAM_0 * 1000000L) + (SUBPARAM_2 * 10000L) + (SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011001() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_3 * 1000000000000L) + (-SUBPARAM_6 * 10000000000L) + (SUBPARAM_1 * 100000000L) + (-SUBPARAM_4 * 1000000L) + (SUBPARAM_5 * 10000L) + (-SUBPARAM_0 * 100L) + (SUBPARAM_2);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011010() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_6 * 1000000000000L) + (SUBPARAM_2 * 10000000000L) + (-SUBPARAM_5 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_4 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011011() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_4 * 1000000000000L) + (SUBPARAM_1 * 10000000000L) + (SUBPARAM_6 * 100000000L) + (-SUBPARAM_3 * 1000000L) + (-SUBPARAM_2 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_0);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011100() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_0 * 1000000000000L) + (SUBPARAM_4 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_1 * 1000000L) + (SUBPARAM_3 * 10000L) + (SUBPARAM_6 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011101() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_1 * 1000000000000L) + (SUBPARAM_5 * 10000000000L) + (-SUBPARAM_2 * 100000000L) + (-SUBPARAM_0 * 1000000L) + (SUBPARAM_4 * 10000L) + (-SUBPARAM_6 * 100L) + (SUBPARAM_3);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011110() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (SUBPARAM_6 * 1000000000000L) + (-SUBPARAM_1 * 10000000000L) + (-SUBPARAM_0 * 100000000L) + (SUBPARAM_2 * 1000000L) + (-SUBPARAM_4 * 10000L) + (SUBPARAM_3 * 100L) + (-SUBPARAM_5);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection_0000000000011111() {
        long bestScore = Long.MAX_VALUE;
        Direction bestDirection = null;
        compute(Cache.MY_LOCATION.add(Direction.NORTH));
        long score_NORTH = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_NORTH < bestScore) {
            bestScore = score_NORTH;
            bestDirection = Direction.NORTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTH));
        long score_SOUTH = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_SOUTH < bestScore) {
            bestScore = score_SOUTH;
            bestDirection = Direction.SOUTH;
        }
        compute(Cache.MY_LOCATION.add(Direction.EAST));
        long score_EAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_EAST < bestScore) {
            bestScore = score_EAST;
            bestDirection = Direction.EAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.WEST));
        long score_WEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_WEST < bestScore) {
            bestScore = score_WEST;
            bestDirection = Direction.WEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHWEST));
        long score_NORTHWEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_NORTHWEST < bestScore) {
            bestScore = score_NORTHWEST;
            bestDirection = Direction.NORTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.NORTHEAST));
        long score_NORTHEAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_NORTHEAST < bestScore) {
            bestScore = score_NORTHEAST;
            bestDirection = Direction.NORTHEAST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHWEST));
        long score_SOUTHWEST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_SOUTHWEST < bestScore) {
            bestScore = score_SOUTHWEST;
            bestDirection = Direction.SOUTHWEST;
        }
        compute(Cache.MY_LOCATION.add(Direction.SOUTHEAST));
        long score_SOUTHEAST = (-SUBPARAM_0 * 1000000000000L) + (-SUBPARAM_4 * 10000000000L) + (SUBPARAM_2 * 100000000L) + (SUBPARAM_3 * 1000000L) + (SUBPARAM_1 * 10000L) + (-SUBPARAM_5 * 100L) + (SUBPARAM_6);
        if (score_SOUTHEAST < bestScore) {
            bestScore = score_SOUTHEAST;
            bestDirection = Direction.SOUTHEAST;
        }
        return bestDirection;
    }

    public static Direction getBestDirection() {
        if (PARAM_0) {
            if (PARAM_1) {
                if (PARAM_2) {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000011111();
                        } else {
                            return getBestDirection_0000000000011110();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000011101();
                        } else {
                            return getBestDirection_0000000000011100();
                        }
                    }
                } else {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000011011();
                        } else {
                            return getBestDirection_0000000000011010();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000011001();
                        } else {
                            return getBestDirection_0000000000011000();
                        }
                    }
                }
            } else {
                if (PARAM_2) {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000010111();
                        } else {
                            return getBestDirection_0000000000010110();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000010101();
                        } else {
                            return getBestDirection_0000000000010100();
                        }
                    }
                } else {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000010011();
                        } else {
                            return getBestDirection_0000000000010010();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000010001();
                        } else {
                            return getBestDirection_0000000000010000();
                        }
                    }
                }
            }
        } else {
            if (PARAM_1) {
                if (PARAM_2) {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000001111();
                        } else {
                            return getBestDirection_0000000000001110();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000001101();
                        } else {
                            return getBestDirection_0000000000001100();
                        }
                    }
                } else {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000001011();
                        } else {
                            return getBestDirection_0000000000001010();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000001001();
                        } else {
                            return getBestDirection_0000000000001000();
                        }
                    }
                }
            } else {
                if (PARAM_2) {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000000111();
                        } else {
                            return getBestDirection_0000000000000110();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000000101();
                        } else {
                            return getBestDirection_0000000000000100();
                        }
                    }
                } else {
                    if (PARAM_3) {
                        if (PARAM_4) {
                            return getBestDirection_0000000000000011();
                        } else {
                            return getBestDirection_0000000000000010();
                        }
                    } else {
                        if (PARAM_4) {
                            return getBestDirection_0000000000000001();
                        } else {
                            return getBestDirection_0000000000000000();
                        }
                    }
                }
            }
        }
    }

    private static MapLocation closestEnemyLocation = null;
    public static void executeMicro(MapLocation closestEnemyLocation) {
        GeneratedMicro.closestEnemyLocation = closestEnemyLocation;
        PARAM_0 = rc.getRoundNum() % 2 == 0;
        PARAM_1 = rc.isActionReady();
        PARAM_2 = rc.getHealth() <= RobotType.LAUNCHER.damage;
        PARAM_3 = Cache.ENEMY_ROBOTS.length == 0;

        if (closestEnemyLocation == null) {
            PARAM_4 = false;
        } else {
            int ourDistanceToEnemy = Cache.MY_LOCATION.distanceSquaredTo(closestEnemyLocation) - 1;
            // hasCloserAlly
            PARAM_4 = LambdaUtil.arraysAnyMatch(Cache.ALLY_ROBOTS,
                    r -> Util.isAttacker(r.type) && r.location.isWithinDistanceSquared(closestEnemyLocation, ourDistanceToEnemy));
        }
        Direction direction = getBestDirection();
        if (direction != null && direction != Direction.CENTER) {
            Util.tryMove(direction);
        }
    }

    public static void compute(MapLocation location) {
        int numVisionEnemies = 0;
        int numActionEnemies = 0;
        for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0; ) {
            RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
            if (Util.isAttacker(enemy.type)) {
                int distanceSquared = enemy.location.distanceSquaredTo(location);
                if (distanceSquared <= 20) { // vision radius squared
                    numVisionEnemies++;
                    if (distanceSquared <= 16) { // action radius squared
                        numActionEnemies++;
                    }
                }
            }
        }
        SUBPARAM_0 = numActionEnemies;
        SUBPARAM_1 = numActionEnemies == 0 ? 0 : 1;
        SUBPARAM_2 = numVisionEnemies;
        SUBPARAM_3 = numVisionEnemies == 0 ? 0 : 1;
        SUBPARAM_4 = Cache.MY_LOCATION.equals(location) ? 1 : 0;
        SUBPARAM_5 = Cache.MY_LOCATION.distanceSquaredTo(location) == 1 ? 1 : 0;
        SUBPARAM_5 = Cache.MY_LOCATION.distanceSquaredTo(location) == 1 ? 1 : 0;
        SUBPARAM_6 = closestEnemyLocation == null ? 0 : closestEnemyLocation.distanceSquaredTo(location);
    }
}