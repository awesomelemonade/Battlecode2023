package sprintBot.pathfinder;

import battlecode.common.*;
import sprintBot.fast.FastGrid;
import sprintBot.fast.FastIntGrid;
import sprintBot.fast.FastMapLocationGridWithDefault;
import sprintBot.fast.FastMapLocationQueue;
import sprintBot.util.Cache;
import sprintBot.util.Debug;
import sprintBot.util.Constants;

import static sprintBot.util.Constants.rc;

public class BFSVisionTemplate {

    private static int[] r = new int[] {255, 255, 255, 0, 0, 0, 128, 255, 255};
    private static int[] g = new int[] {0, 128, 255, 255, 255, 0, 0, 0, 128};
    private static int[] b = new int[] {0, 0, 0, 0, 255, 255, 255, 255, 255};

    private static FastGrid<BFSVisionTemplate> allBFS;
    private static FastMapLocationGridWithDefault currentDestinations;

    private FastMapLocationQueue queue;
    private FastIntGrid moveDirections;
    private FastIntGrid distances;
    private boolean completed = false;
    private MapLocation origin;


    public static void init() {
        allBFS = new FastGrid<BFSVisionTemplate>(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        currentDestinations = new FastMapLocationGridWithDefault(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    public static void postLoop() throws GameActionException {
        if (Cache.TURN_COUNT > 1) { // save bytecodes on turn 1
            MapInfo[] infos = rc.senseNearbyMapInfos();
            for (int i = infos.length; --i >= 0; ) {
                MapInfo info = infos[i];
                if (info.getCurrentDirection() != Direction.CENTER) {
                    MapLocation location = info.getMapLocation();
                    currentDestinations.set(location, location.add(info.getCurrentDirection()));
                }
            }
            BFSVisionTemplate currentBfsVision = allBFS.get(Cache.MY_LOCATION);
            if (currentBfsVision == null) {
                currentBfsVision = new BFSVisionTemplate(Cache.MY_LOCATION);
                allBFS.set(Cache.MY_LOCATION, currentBfsVision);
            }
            currentBfsVision.bfs();
            debug_render(currentBfsVision);
        }
    }

    public static BFSVisionTemplate getBFSIfCompleted() {
        if (Cache.TURN_COUNT <= 2) {
            // save bytecodes
            return null;
        }
        BFSVisionTemplate currentBfsVision = allBFS.get(Cache.MY_LOCATION);
        if (currentBfsVision == null) {
            currentBfsVision = new BFSVisionTemplate(Cache.MY_LOCATION);
            allBFS.set(Cache.MY_LOCATION, currentBfsVision);
        }
        return currentBfsVision.completed ? currentBfsVision : null;
    }

    public BFSVisionTemplate(MapLocation origin) {
        this.origin = origin;
        moveDirections = new FastIntGrid(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        distances = new FastIntGrid(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        queue = new FastMapLocationQueue(Constants.MAX_VISION_SQUARES); // MAX_VISION_SQUARES
        // locate currents
        // should only return locations that can be fully sensable (even with clouds)

        moveDirections.set(origin, -1);

        // TODO: unroll
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            MapLocation neighbor = origin.add(direction);
            if (rc.onTheMap(neighbor)) {
                neighbor = currentDestinations.get(neighbor);
                // should never be out of bounds because currents should never put a robot out of the map
                queue.add(neighbor); // TODO: theoretically we can just unroll this
                // the following theoretically can be set beforehand during the reset phase
                moveDirections.set(neighbor, 1 << direction.ordinal());
            }
        }
    }

    public static void debug_render(BFSVisionTemplate bfs) {
//        renderAllBfs();
//        bfs.render();
    }

    public static void renderAllBfs() {
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                BFSVisionTemplate bfs = allBFS.get(i, j);
                if (bfs != null) {
                    if (bfs.completed) {
                        Debug.setIndicatorDot(new MapLocation(i, j), 0, 255, 0);
                    } else {
                        Debug.setIndicatorDot(new MapLocation(i, j), 255, 255, 0);
                    }
                }
            }
        }
    }

    private static int renderCount = 0;
    public void render() {
        // render cardinal then diagonal directions
        Direction[] directions = (renderCount++) % 2 == 0 ? Constants.CARDINAL_DIRECTIONS : Constants.DIAGONAL_DIRECTIONS;
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                for (Direction direction : directions) {
                    if (moveDirections.get(i, j) != -1 && (moveDirections.get(i, j) & (1 << direction.ordinal())) != 0) {
                        int index = direction.ordinal() % r.length;
                        MapLocation location = new MapLocation(i, j);
                        Debug.setIndicatorDot(location, r[index], g[index], b[index]);
                    }
                }
            }
        }
    }

    public static boolean sensePassable(MapLocation location) throws GameActionException {
        return rc.sensePassability(location); // TODO: check for ally & enemy HQ. they should also be not passable
    }

    public void bfs() throws GameActionException  {
        // idk why but while loop breaks the profiler
        for (int i = 50; --i >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > 1200; ) {
            MapLocation location = queue.poll();
            // TODO: handle clouds
//            if (origin.isWithinDistanceSquared(location, Constants.ROBOT_TYPE.visionRadiusSquared) && sensePassable(location)) {
            if (rc.canSenseLocation(location) && sensePassable(location)) {
                int distance_plus_1 = distances.get(location) + 1;
                int moveDirection = moveDirections.get(location);
                // get neighbors
                // unroll_ordinal_directions! addNeighbor direction
                /*
                macro! addNeighbor
                ---
                MapLocation neighbor = location.add(direction);
                if (rc.onTheMap(neighbor)) {
                    neighbor = currentDestinations.get(neighbor);
                    // should never be out of bounds because currents should never put a robot out of the map
                    int neighborDistance = distances.get(neighbor);
                    if (moveDirections.get(neighbor) == 0) {
                        // unvisited square
                        queue.add(neighbor);
                        moveDirections.bitwiseOr(neighbor, moveDirection);
                        distances.set(neighbor, distance_plus_1);
                    } else if (neighborDistance == distance_plus_1) {
                        // visited square with the same distance
                        moveDirections.bitwiseOr(neighbor, moveDirection);
                    }
                }
                ---
                 */
            }
        }
        completed = queue.isEmpty();
    }

    public Direction getImmediateMoveDirection(MapLocation target) {
        if (Cache.MY_LOCATION.equals(target)) {
            return Direction.CENTER;
        }
        int moveDirection = moveDirections.get(target);
        for (Direction direction : Constants.getAttemptOrder(Cache.MY_LOCATION.directionTo(target))) {
            if (rc.canMove(direction) && (moveDirection & (1 << direction.ordinal())) != 0) {
                return direction;
            }
        }
        return null;
    }

    public boolean hasMoveDirection(MapLocation target) {
        return moveDirections.get(target) != 0;
    }
}
