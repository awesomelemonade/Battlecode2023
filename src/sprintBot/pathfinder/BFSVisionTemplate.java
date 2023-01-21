package sprintBot.pathfinder;

import battlecode.common.*;
import sprintBot.fast.FastGrid;
import sprintBot.fast.FastMapLocationGridWithDefault;
import sprintBot.fast.FastMapLocationQueue;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

public class BFSVisionTemplate {

    private static int[] r = new int[] {255, 255, 255, 0, 0, 0, 128, 255, 255};
    private static int[] g = new int[] {0, 128, 255, 255, 255, 0, 0, 0, 128};
    private static int[] b = new int[] {0, 0, 0, 0, 255, 255, 255, 255, 255};

    private static FastGrid<BFSVisionTemplate> allBFS;
    private static FastMapLocationGridWithDefault currentDestinations;

    private static FastQueueWithRemove bfsQueue = new FastQueueWithRemove();

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
                // find bfs's to execute in queue - most recent first
                bfsQueue.retain(x -> {
                    BFSVisionTemplate bfs = allBFS.get(x / Constants.MAX_MAP_SIZE, x % Constants.MAX_MAP_SIZE);
                    bfs.bfs();
                    return !bfs.completed;
                }, 1100);
            } else {
                // add to queue
                bfsQueue.addOrBringToFront(Cache.MY_LOCATION.x * Constants.MAX_MAP_SIZE + Cache.MY_LOCATION.y);
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
//        PassabilityCache.debug_render();
//        bfs.render();
    }

    public static void renderAllBfs() {
        if (Constants.ROBOT_TYPE != RobotType.HEADQUARTERS && Profile.BFS.enabled()) {
            Debug.setIndicatorString(Profile.BFS, "sz: " + bfsQueue.size());
            for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                    BFSVisionTemplate bfs = allBFS.get(i, j);
                    if (bfs == null) {
                        Debug.setIndicatorDot(Profile.BFS, new MapLocation(i, j), 255, 0, 0); // red
                    } else {
                        if (bfs.completed) {
                            Debug.setIndicatorDot(Profile.BFS, new MapLocation(i, j), 0, 255, 0); // green
                        } else {
                            Debug.setIndicatorDot(Profile.BFS, new MapLocation(i, j), 255, 255, 0); // yellow
                        }
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

    public void bfs() {
        boolean madeProgress = false;
        // this monstrosity is just to save bytecodes :(
        // idk why but while loop breaks the profiler
        loop: for (int i = 50; --i >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > 1100; ) {
            MapLocation location = queue.peek();
            switch (PassabilityCache.isPassable(location)) {
                case PassabilityCache.UNPASSABLE:
                    madeProgress = true;
                    queue.poll();
                    break;
                case PassabilityCache.PASSABLE:
                    madeProgress = true;
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
        if (Profile.BFS.enabled()) {
            if (completed) {
                Debug.setIndicatorDot(Profile.BFS, origin, 0, 255, 0); // green
            } else if (madeProgress) {
                Debug.setIndicatorDot(Profile.BFS, origin, 0, 255, 255); // cyan
            } else {
                Debug.setIndicatorDot(Profile.BFS, origin, 255, 0, 0); // red
            }
        }
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
