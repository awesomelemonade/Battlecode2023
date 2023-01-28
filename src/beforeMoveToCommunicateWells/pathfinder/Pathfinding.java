package beforeMoveToCommunicateWells.pathfinder;

import battlecode.common.*;
import beforeMoveToCommunicateWells.fast.FastIntCounter2D;
import beforeMoveToCommunicateWells.util.*;

import java.util.function.Predicate;

import static beforeMoveToCommunicateWells.util.Constants.rc;

public class Pathfinding {
	public static Predicate<MapLocation> predicate = location -> true;

	public static int moveDistance(MapLocation a, MapLocation b) {
		return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
	}

	private static FastIntCounter2D visitedSet = null;
	private static MapLocation lastTarget;

	public static void init() {
		visitedSet = new FastIntCounter2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
	}

	public static int getTurnsSpentSoFar() {
		if (visitedSet == null) {
			return 0;
		}
		return visitedSet.getCounter();
	}
	public static void reset() {
		lastTarget = null;
	}
	public static boolean execute(MapLocation target) {
		if (lastTarget == null || !lastTarget.equals(target)) {
			lastTarget = target;
			visitedSet.reset();
		}
//		if (Constants.ROBOT_TYPE == RobotType.DELIVERY_DRONE) {
//			visitedSet.updateBaseTrail(5); // Drones only care about the last 5 visited tiles
//		}
		return executeNoReset(target);
	}
	public static boolean executeResetIfNotAdjacent(MapLocation target) {
		if (lastTarget == null || !lastTarget.isAdjacentTo(target)) {
			visitedSet.reset();
		}
		lastTarget = target;
		return executeNoReset(target);
	}
	private static boolean executeNoReset(MapLocation target) {
		MapLocation currentLocation = Cache.MY_LOCATION;
		Debug.setIndicatorLine(Profile.PATHFINDING, currentLocation, target, 0, 0, 255);
		if (!rc.isMovementReady()) {
			return false;
		}
		if (currentLocation.equals(target)) {
			return true;
		}
		visitedSet.add(currentLocation.x, currentLocation.y);
		Direction fallbackDirection = null;
		int bestFallbackCount = Integer.MAX_VALUE;
		for (Direction direction : Constants.getAttemptOrder(currentLocation.directionTo(target))) {
			if (Util.canMoveAndCheckCurrents(direction)) {
				// has to be on the map
				MapLocation location = currentLocation.add(direction);
				if (!location.equals(target) && !predicate.test(location)) {
					continue;
				}
				int fallbackCount = visitedSet.get(location.x, location.y);
				if (fallbackCount < bestFallbackCount) {
					bestFallbackCount = fallbackCount;
					fallbackDirection = direction;
				}
				if (visitedSet.contains(location.x, location.y)) {
					continue;
				}
				Util.move(direction);
				return true;
			}
		}
		// We stuck bois - let's look for the lowest non-negative
		if (fallbackDirection != null) {
			Util.move(fallbackDirection);
			return true;
		}
		// we're stuck (perhaps surrounded by units?)
		return false;
	}
}