package sprintBot.pathfinder;

import battlecode.common.*;
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
    private int[][] moveDirections;
    private int[][] distances;
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
        BFSVisionTemplate currentBfsVision = allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y];
        if (currentBfsVision == null) {
            currentBfsVision = new BFSVisionTemplate();
            allBFS[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y] = currentBfsVision;
        }
        currentBfsVision.bfs();
        debug_render(currentBfsVision);
    }

    public BFSVisionTemplate() {
        moveDirections = new int[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        distances = new int[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
        queue = new FastMapLocationQueue(Constants.MAX_VISION_SQUARES); // MAX_VISION_SQUARES
        resetAndReadMap();

        moveDirections[Cache.MY_LOCATION.x][Cache.MY_LOCATION.y] = -1;

        // TODO: unroll
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            MapLocation neighbor = Cache.MY_LOCATION.add(direction);
            if (rc.onTheMap(neighbor)) {
                neighbor = currentDestinations[neighbor.x][neighbor.y];
                // should never be out of bounds because currents should never put a robot out of the map
                queue.add(neighbor); // TODO: theoretically we can just unroll this
                // the following theoretically can be set beforehand during the reset phase
                moveDirections[neighbor.x][neighbor.y] = (1 << direction.ordinal());
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
                    if (moveDirections[i][j] != -1 && (moveDirections[i][j] & (1 << direction.ordinal())) != 0) {
                        int index = direction.ordinal() % r.length;
                        MapLocation location = new MapLocation(i, j);
                        Debug.setIndicatorDot(location, r[index], g[index], b[index]);
                    }
                }
            }
        }
    }

    public void resetAndReadMap() {
        // locate currents
        // should only return locations that can be fully sensable (even with clouds)
        MapInfo[] infos = rc.senseNearbyMapInfos();
        for (MapInfo info : infos) {
            MapLocation location = info.getMapLocation();
            currentDestinations[location.x][location.y] = location.add(info.getCurrentDirection());
        }
    }

    public static boolean sensePassable(MapLocation location) throws GameActionException {
        return rc.sensePassability(location); // TODO: check for ally & enemy HQ. they should also be not passable
    }

    public void bfs() throws GameActionException  {
        // idk why but while loop breaks the profiler
        for (int i = 50; --i >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > 1000;) {
            MapLocation location = queue.poll();
            if (rc.canSenseLocation(location) && sensePassable(location)) {
                int distance_plus_1 = distances[location.x][location.y] + 1;
                int moveDirection = moveDirections[location.x][location.y];
                // get neighbors
                // unroll_ordinal_directions! addNeighbor direction
                /*
                macro! addNeighbor
                ---
                MapLocation neighbor = location.add(direction);
                if (rc.onTheMap(neighbor)) {
                    neighbor = currentDestinations[neighbor.x][neighbor.y];
                    // should never be out of bounds because currents should never put a robot out of the map
                    int neighborDistance = distances[neighbor.x][neighbor.y];
                    if (moveDirections[neighbor.x][neighbor.y] == 0) {
                        // unvisited square
                        queue.add(neighbor);
                        moveDirections[neighbor.x][neighbor.y] = moveDirection;
                        distances[neighbor.x][neighbor.y] = distance_plus_1;
                    } else if (neighborDistance == distance_plus_1) {
                        // visited square with the same distance
                        moveDirections[neighbor.x][neighbor.y] |= moveDirection;
                    }
                }
                ---
                 */
            }
        }
        completed = queue.isEmpty();
    }

    public Direction getImmediateMoveDirection(MapLocation target) {
        int moveDirection = moveDirections[target.x][target.y];
        for (Direction direction : Constants.getAttemptOrder(Cache.MY_LOCATION.directionTo(target))) {
            if (rc.canMove(direction) && (moveDirection & (1 << direction.ordinal())) != 0) {
                return direction;
            }
        }
        return null;
    }
}
