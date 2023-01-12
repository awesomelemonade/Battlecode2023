package beforeWellTracker2.pathfinder;

import battlecode.common.*;
import beforeWellTracker2.fast.FastIntCounter2D;
import beforeWellTracker2.util.*;

import java.util.Arrays;
import java.util.Comparator;

import static beforeWellTracker2.util.Constants.rc;

public class Pathfinding {
	public static int moveDistance(MapLocation a, MapLocation b) {
		return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
	}

	private static FastIntCounter2D visitedSet = null;
	private static MapLocation visitedSetBuffer = null; // buffer of 1 lol
	private static MapLocation lastTarget;

	public static void loop() {
		if (Cache.TURN_COUNT >= 2 && visitedSet == null) {
			visitedSet = new FastIntCounter2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
			if (visitedSetBuffer != null) {
				visitedSet.add(visitedSetBuffer.x, visitedSetBuffer.y);
			}
		}
	}
	public static int getTurnsSpentSoFar() {
		if (visitedSet == null) {
			return 0;
		}
		return visitedSet.getCounter();
	}
	public static boolean execute(MapLocation target) {
		if (lastTarget == null || !lastTarget.equals(target)) {
			lastTarget = target;
			if (visitedSet == null) {
				visitedSetBuffer = null;
			} else {
				visitedSet.reset();
			}
		}
//		if (Constants.ROBOT_TYPE == RobotType.DELIVERY_DRONE) {
//			visitedSet.updateBaseTrail(5); // Drones only care about the last 5 visited tiles
//		}
		return executeNoReset(target);
	}
	public static boolean executeNoReset(MapLocation target) {
		MapLocation currentLocation = Cache.MY_LOCATION;
		Debug.setIndicatorLine(Profile.PATHFINDING, currentLocation, target, 0, 0, 255);
		if (!rc.isMovementReady()) {
			return false;
		}
		if (currentLocation.equals(target)) {
			// We're already there
			return true;
		}
		if (visitedSet == null) {
			visitedSetBuffer = currentLocation;
		} else {
			visitedSet.add(currentLocation.x, currentLocation.y);
		}
		Direction idealDirection = currentLocation.directionTo(target);
		for (Direction direction : Constants.getAttemptOrder(idealDirection)) {
			MapLocation location = currentLocation.add(direction);
			if (!Util.onTheMap(location)) {
				continue;
			}
			if (visitedSet == null) {
				if (visitedSetBuffer.equals(location)) {
					continue;
				}
			} else {
				if (visitedSet.contains(location.x, location.y)) {
					continue;
				}
			}
			if (trySafeMove(direction)) {
				return true;
			}
		}
		// We stuck bois - let's look for the lowest non-negative
		Direction[] directions = Constants.getAttemptOrder(idealDirection);
		int[] counters = new int[8];
		Integer[] indices = new Integer[8];
		for (int i = counters.length; --i >= 0;) {
			if (visitedSet != null) {
				MapLocation location = currentLocation.add(directions[i]);
				if (Util.onTheMap(location)) {
					counters[i] = visitedSet.get(location.x, location.y);
				} else {
					counters[i] = Integer.MAX_VALUE;
				}
			}
			indices[i] = i;
		}
		Arrays.sort(indices, Comparator.comparingInt(i -> counters[i]));
		for (int i = 0; i < indices.length; i++) {
			if (trySafeMove(directions[indices[i]])) {
				return true;
			}
		}
		for (int i = 0; i < indices.length; i++) {
			if (Util.tryMove(directions[indices[i]])) {
				return true;
			}
		}
		// we're stuck (perhaps surrounded by units?)
		return false;
	}
	public static boolean trySafeMove(Direction direction) {
		// TODO: do not move towards enemy?
		return Util.tryMove(direction);
	}
}