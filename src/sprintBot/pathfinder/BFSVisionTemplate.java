package sprintBot.pathfinder;

import battlecode.common.*;
import sprintBot.fast.FastIntGrid;
import sprintBot.fast.FastMapLocationQueue;
import sprintBot.util.Cache;
import sprintBot.util.Debug;
import sprintBot.util.Constants;

import static sprintBot.util.Constants.rc;

public class BFSVisionTemplate {
    private static MapLocation[][] currentDestinations;

    private static int[] r = new int[] {255, 255, 255, 0, 0, 0, 128, 255, 255};
    private static int[] g = new int[] {0, 128, 255, 255, 255, 0, 0, 0, 128};
    private static int[] b = new int[] {0, 0, 0, 0, 255, 255, 255, 255, 255};

    private FastMapLocationQueue queue;
    private FastIntGrid moveDirections;
    private FastIntGrid distances;
    private boolean completed = false;

    private static BFSVisionTemplate[][] allBFS;


    public static void debug_init() {
        allBFS = new BFSVisionTemplate[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        currentDestinations = new MapLocation[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                currentDestinations[i][j] = new MapLocation(i, j);
            }
        }
    }

    public static void postLoop() throws GameActionException {
        if (Cache.TURN_COUNT > 1) { // save bytecodes on turn 1
            MapInfo[] infos = rc.senseNearbyMapInfos();
            for (int i = infos.length; --i >= 0; ) {
                MapInfo info = infos[i];
                MapLocation location = info.getMapLocation();
                currentDestinations[location.x][location.y] = location.add(info.getCurrentDirection());
            }
            BFSVisionTemplate currentBfsVision = allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y];
            if (currentBfsVision == null) {
                currentBfsVision = new BFSVisionTemplate();
                allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y] = currentBfsVision;
            }
            currentBfsVision.bfs();
            debug_render(currentBfsVision);
        }
    }

    public static BFSVisionTemplate getBFSIfCompleted() {
        if (Cache.TURN_COUNT <= 1) {
            // save bytecodes
            return null;
        }
        BFSVisionTemplate currentBfsVision = allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y];
        if (currentBfsVision == null) {
            currentBfsVision = new BFSVisionTemplate();
            allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y] = currentBfsVision;
        }
        return currentBfsVision.completed ? currentBfsVision : null;
    }

    public BFSVisionTemplate() {
        moveDirections = new FastIntGrid(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        distances = new FastIntGrid(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        queue = new FastMapLocationQueue(Constants.MAX_VISION_SQUARES); // MAX_VISION_SQUARES
        // locate currents
        // should only return locations that can be fully sensable (even with clouds)

        moveDirections.set(Cache.MY_LOCATION, -1);

        // TODO: unroll
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            MapLocation neighbor = Cache.MY_LOCATION.add(direction);
            if (rc.onTheMap(neighbor)) {
                neighbor = currentDestinations[neighbor.x][neighbor.y];
                // should never be out of bounds because currents should never put a robot out of the map
                queue.add(neighbor); // TODO: theoretically we can just unroll this
                // the following theoretically can be set beforehand during the reset phase
                moveDirections.set(neighbor, 1 << direction.ordinal());
            }
        }
    }

    public static void debug_render(BFSVisionTemplate bfs) {
        bfs.render();
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
                    neighbor = currentDestinations[neighbor.x][neighbor.y];
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
}
