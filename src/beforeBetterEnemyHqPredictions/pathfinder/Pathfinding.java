package beforeBetterEnemyHqPredictions.pathfinder;

import battlecode.common.*;
import beforeBetterEnemyHqPredictions.fast.FastIntCounter2D;
import beforeBetterEnemyHqPredictions.util.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

import beforeBetterEnemyHqPredictions.fast.FastSort;

import static beforeBetterEnemyHqPredictions.util.Constants.rc;

public class Pathfinding {
	public static Predicate<MapLocation> predicate = location -> true;
	private static int[] counters = new int[8];

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
	public static boolean executeNoReset(MapLocation target) {
		MapLocation currentLocation = Cache.MY_LOCATION;
		Debug.setIndicatorLine(Profile.PATHFINDING, currentLocation, target, 0, 0, 255);
		if (!rc.isMovementReady()) {
			return false;
		}
		if (currentLocation.equals(target)) {
			return true;
		}
		visitedSet.add(currentLocation.x, currentLocation.y);
		Direction idealDirection = currentLocation.directionTo(target);
		Direction[] directions = Constants.getAttemptOrder(idealDirection);
		for (Direction direction : directions) {
			MapLocation location = currentLocation.add(direction);
			if (!Util.onTheMap(location)) {
				continue;
			}
			if (visitedSet.contains(location.x, location.y)) {
				continue;
			}
			if (!location.equals(target) && !predicate.test(location)) {
				continue;
			}
			if (Util.tryMove(direction)) {
				return true;
			}
		}
		// We stuck bois - let's look for the lowest non-negative
		for (int i = counters.length; --i >= 0;) {
			MapLocation location = currentLocation.add(directions[i]);
			counters[i] = Util.onTheMap(location) ? visitedSet.get(location.x, location.y) : Integer.MAX_VALUE;
		}
		int[] indices = FastSort.sort(counters);
		for (int i = 0; i < indices.length; i++) {
			Direction direction = directions[indices[i]];
			MapLocation location = currentLocation.add(direction);
			if (!Util.onTheMap(location)) {
				continue;
			}
			if (!location.equals(target) && !predicate.test(location)) {
				continue;
			}
			if (Util.tryMove(direction)) {
				return true;
			}
		}
		// we're stuck (perhaps surrounded by units?)
		return false;
	}
}