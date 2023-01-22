package beforeSpecChanges.pathfinder;

import battlecode.common.*;
import beforeSpecChanges.fast.FastGrid;
import beforeSpecChanges.fast.FastIntGrid;
import beforeSpecChanges.fast.FastMapLocationGridWithDefault;
import beforeSpecChanges.fast.FastMapLocationQueue;
import beforeSpecChanges.util.*;

import static beforeSpecChanges.util.Constants.rc;

public class BFSVisionTemplate {

    private static int[] r = new int[] {255, 255, 255, 0, 0, 0, 128, 255, 255};
    private static int[] g = new int[] {0, 128, 255, 255, 255, 0, 0, 0, 128};
    private static int[] b = new int[] {0, 0, 0, 0, 255, 255, 255, 255, 255};

    private static FastGrid<BFSVisionTemplate> allBFS;
    private static FastMapLocationGridWithDefault currentDestinations;

    // queue for a heuristic for which BFSs to calculate
    private static BFSVisionTemplate[] bfsQueue = new BFSVisionTemplate[20];
    private static int bfsQueueIndex = 0;
    private static int bfsQueueSize = 0;

    private FastMapLocationQueue queue;
    private int[][] moveDirections;
    private int[][] distances;
    private boolean completed = false;
    private MapLocation origin;


    public static void init() {
        allBFS = new FastGrid<>(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
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
            BFSVisionTemplate currentBfs = allBFS.get(Cache.MY_LOCATION);
            if (currentBfs == null) {
                currentBfs = new BFSVisionTemplate(Cache.MY_LOCATION);
                allBFS.set(Cache.MY_LOCATION, currentBfs);
            }
            currentBfs.bfs();

            if (currentBfs.completed) {
                // find bfs's to execute in queue
                // in reverse order (most recent first)
                for (int i = bfsQueueSize; --i >= 0 && Clock.getBytecodesLeft() > 1100; ) {
                    bfsQueue[(bfsQueueIndex + i) % bfsQueue.length].bfs();
                }
            } else {
                if (bfsQueueSize > 0) {
                    BFSVisionTemplate lastBfs = bfsQueue[(bfsQueueIndex + bfsQueueSize + bfsQueue.length - 1) % bfsQueue.length];
                    // check if it is the same as the last one so far
                    if (currentBfs != lastBfs) {
                        if (bfsQueueSize == bfsQueue.length) {
                            // overwrite oldest
                            bfsQueue[(bfsQueueIndex++) % bfsQueue.length] = currentBfs;
                        } else {
                            // add to queue
                            bfsQueue[(bfsQueueIndex + bfsQueueSize++) % bfsQueue.length] = currentBfs;
                        }
                    }
                } else {
                    // add to queue
                    bfsQueue[(bfsQueueIndex + bfsQueueSize++) % bfsQueue.length] = currentBfs;
                }
            }
            debug_render(currentBfs);
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
        moveDirections = new int[Constants.MAP_WIDTH][];
        distances = new int[Constants.MAP_WIDTH][];
        if (Constants.ROBOT_TYPE.visionRadiusSquared >= 34) {
            // 11 rows
            int start = Math.max(0, origin.x - 5);
            int end = Math.min(Constants.MAP_WIDTH - 1, origin.x + 5);
            for (int i = start; i <= end; i++) {
                moveDirections[i] = new int[Constants.MAP_HEIGHT];
                distances[i] = new int[Constants.MAP_HEIGHT];
            }
        } else {
            // 9 rows
            int start = Math.max(0, origin.x - 4);
            int end = Math.min(Constants.MAP_WIDTH - 1, origin.x + 4);
            for (int i = start; i <= end; i++) {
                moveDirections[i] = new int[Constants.MAP_HEIGHT];
                distances[i] = new int[Constants.MAP_HEIGHT];
            }
        }
        // only create the relevant
        queue = new FastMapLocationQueue(Constants.MAX_VISION_SQUARES); // MAX_VISION_SQUARES
        // locate currents
        // should only return locations that can be fully sensable (even with clouds)

        moveDirections[origin.x][origin.y] = -1;

        for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
            MapLocation neighbor = origin.add(Constants.ORDINAL_DIRECTIONS[i]);
            if (rc.onTheMap(neighbor)) {
                neighbor = currentDestinations.get(neighbor);
                // should never be out of bounds because currents should never put a robot out of the map
                queue.add(neighbor); // theoretically we can just unroll this
                moveDirections[neighbor.x][neighbor.y] = 1 << i;
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
                        Debug.setIndicatorDot(Profile.BFS, new MapLocation(i, j), 0, 255, 0); // green
                    } else {
                        Debug.setIndicatorDot(Profile.BFS, new MapLocation(i, j), 255, 255, 0); // yellow
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
                    if (moveDirections[i][j] != -1 && (moveDirections[i][j] & (1 << direction.ordinal())) != 0) {
                        int index = direction.ordinal() % r.length;
                        MapLocation location = new MapLocation(i, j);
                        Debug.setIndicatorDot(Profile.BFS, location, r[index], g[index], b[index]);
                    }
                }
            }
        }
    }

    public static boolean sensePassable(MapLocation location) throws GameActionException {
        return rc.sensePassability(location); // TODO: check for ally & enemy HQ. they should also be not passable
    }

    public void bfs() throws GameActionException  {
        // this monstrosity is just to save bytecodes :(
        // idk why but while loop breaks the profiler
        loop: for (int i = 50; --i >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > 1100; ) {
            MapLocation location = queue.peek();
            switch (PassabilityCache.isPassable(location)) {
                case PassabilityCache.UNPASSABLE:
                    queue.poll();
                    break;
                case PassabilityCache.PASSABLE:
                    queue.poll();
                    int distance_plus_1 = distances[location.x][location.y] + 1;
                    int moveDirection = moveDirections[location.x][location.y];
                    // get neighbors
                    // unroll_ordinal_directions! addNeighbor direction
                    /*
                    macro! addNeighbor
                    ---
                    MapLocation neighbor = location.add(direction);
                    if (rc.onTheMap(neighbor)) {
                        neighbor = currentDestinations.get(neighbor);
                        // should never be out of bounds because currents should never put a robot out of the map
                        if (origin.isWithinDistanceSquared(neighbor, Constants.ROBOT_TYPE.visionRadiusSquared)) {
                            if (moveDirections[neighbor.x][neighbor.y] == 0) {
                                // unvisited square
                                queue.add(neighbor);
                                moveDirections[neighbor.x][neighbor.y] |= moveDirection;
                                distances[neighbor.x][neighbor.y] = distance_plus_1;
                            } else if (distances[neighbor.x][neighbor.y] == distance_plus_1) {
                                // visited square with the same distance
                                moveDirections[neighbor.x][neighbor.y] |= moveDirection;
                            }
                        }
                    }
                    ---
                     */
                    break;
                case PassabilityCache.UNKNOWN:
                    break loop;
                default:
                    Debug.failFast("Unknown result from isPassable");
            }
        }
        completed = queue.isEmpty();
    }

    public Direction getImmediateMoveDirection(MapLocation target) {
        if (Cache.MY_LOCATION.equals(target)) {
            return Direction.CENTER;
        }
        int[] array = moveDirections[target.x];
        if (array == null) {
            return null;
        }
        int moveDirection = moveDirections[target.x][target.y];
        for (Direction direction : Constants.getAttemptOrder(Cache.MY_LOCATION.directionTo(target))) {
            if (rc.canMove(direction) && (moveDirection & (1 << direction.ordinal())) != 0) {
                return direction;
            }
        }
        return null;
    }

    public boolean hasMoveDirection(MapLocation target) {
        int[] array = moveDirections[target.x];
        return array != null && array[target.y] != 0;
    }
}