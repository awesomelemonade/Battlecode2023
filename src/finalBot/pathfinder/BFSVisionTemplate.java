package finalBot.pathfinder;

import battlecode.common.*;
import finalBot.fast.FastBooleanArray2D;
import finalBot.fast.FastGrid;
import finalBot.fast.FastMapLocationQueue;
import finalBot.util.*;

import static finalBot.util.Constants.rc;

public class BFSVisionTemplate {

    private static int[] r = new int[] {255, 255, 255, 0, 0, 0, 128, 255, 255};
    private static int[] g = new int[] {0, 128, 255, 255, 255, 0, 0, 0, 128};
    private static int[] b = new int[] {0, 0, 0, 0, 255, 255, 255, 255, 255};

    private static FastGrid<BFSVisionTemplate> allBFS;

    private static FastQueueWithRemove bfsQueue = new FastQueueWithRemove();

    private static FastBooleanArray2D hasSensedNearbyMapInfos;

    private FastMapLocationQueue queue;
    private int[][] moveDirections;
    private int[][] distances; // TODO: is this necessary?
    private boolean completed = false;
    public MapLocation origin;


    public static void init() {
        allBFS = new FastGrid<>(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        hasSensedNearbyMapInfos = new FastBooleanArray2D(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
    }

    public static void postLoop() throws GameActionException {
        if (Cache.TURN_COUNT > 1) { // save bytecodes on turn 1
            if (!hasSensedNearbyMapInfos.get(Cache.MY_LOCATION)) {
                if (Cache.TURN_COUNT < 20 || Clock.getBytecodesLeft() >= 2300) {
                    // update passability and currents
                    MapInfo[] infos = rc.senseNearbyMapInfos();
                    for (int i = infos.length; --i >= 0; ) {
                        MapInfo info = infos[i];
                        MapLocation location = info.getMapLocation();
                        int index = location.x * Constants.MAX_MAP_SIZE + location.y;
                        if (PassabilityCache.data.charAt(index) == PassabilityCache.UNKNOWN) {
//                        CurrentsCache.set(location.x, location.y, location.add(info.getCurrentDirection())); // INLINED BELOW TO SAVE BYTECODES
                            CurrentsCache.data[location.x][location.y] = location.add(info.getCurrentDirection());
//                        PassabilityCache.setPassable(location, info.isPassable()); // INLINED BELOW TO SAVE BYTECODES
                            PassabilityCache.data.setCharAt(index, info.isPassable() ? PassabilityCache.PASSABLE : PassabilityCache.UNPASSABLE);
                        }
                    }
                    MapLocation[] cloudLocations = rc.senseNearbyCloudLocations();
                    for (int i = cloudLocations.length; --i >= 0; ) {
                        MapLocation location = cloudLocations[i];
                        // cloud locations always do not have a current and are passable
                        PassabilityCache.data.setCharAt(location.x * Constants.MAX_MAP_SIZE + location.y, PassabilityCache.PASSABLE);
                    }
                    hasSensedNearbyMapInfos.setTrue(Cache.MY_LOCATION);
                }
            }
//            BFSVisionTemplate currentBfs = allBFS.get(Cache.MY_LOCATION);
//            if (currentBfs == null && Clock.getBytecodesLeft() > 2300) { // creating new BFSVision() takes up to ~2100 bytecodes on large maps
//                currentBfs = new BFSVisionTemplate(Cache.MY_LOCATION);
//                allBFS.set(Cache.MY_LOCATION, currentBfs);
//            }
//            if (currentBfs != null) {
//                currentBfs.bfs(1100);
//                if (currentBfs.completed) {
//                    // find bfs's to execute in queue - most recent first
//                    bfsQueue.retain(x -> {
//                        BFSVisionTemplate bfs = allBFS.get(x / Constants.MAX_MAP_SIZE, x % Constants.MAX_MAP_SIZE);
//                        bfs.bfs(1100);
//                        return !bfs.completed;
//                    }, 1100);
//                } else {
//                    // add to queue
//                    bfsQueue.addOrBringToFront(Cache.MY_LOCATION.x * Constants.MAX_MAP_SIZE + Cache.MY_LOCATION.y);
//                }
//            }
            debug_render();
        }
    }

    public static BFSVisionTemplate getBFSIfCompleted() {
        if (Cache.TURN_COUNT <= 2) {
            // save bytecodes
            return null;
        }
        BFSVisionTemplate currentBfsVision = allBFS.get(Cache.MY_LOCATION);
        return currentBfsVision != null && currentBfsVision.completed ? currentBfsVision : null;
    }

    public BFSVisionTemplate(MapLocation origin) { // we should optimize this
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
                neighbor = CurrentsCache.get(neighbor);
                // should never be out of bounds because currents should never put a robot out of the map
                queue.add(neighbor); // theoretically we can just unroll this
                moveDirections[neighbor.x][neighbor.y] = 1 << i;
            }
        }
        // TODO: move to bfs?
    }

    public static void debug_render() {
        debug_renderAllBfs();
//        PassabilityCache.debug_render();
//        BFSVisionTemplate bfs = allBFS.get(Cache.MY_LOCATION);
//        if (bfs != null) {
//            bfs.render();
//        }
    }

    public static void debug_renderAllBfs() {
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

    public void bfs(int bytecodeThreshold) {
        boolean madeProgress = false;
        // this monstrosity is just to save bytecodes :(
        // idk why but while loop breaks the profiler
        loop: for (int i = 2050; --i >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > bytecodeThreshold; ) {
            MapLocation location = queue.peek();
            switch (PassabilityCache.isPassable(location)) {
                case PassabilityCache.UNPASSABLE:
                    madeProgress = true;
                    queue.poll();
                    moveDirections[location.x][location.y] = 0; // no moves can reach here
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
                    if (rc.onTheMap(neighbor) && !neighbor.isWithinDistanceSquared(origin, location.distanceSquaredTo(origin))) {
                        neighbor = CurrentsCache.get(neighbor);
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
        if (!completed && queue.isEmpty()) {
            completed = queue.isEmpty();
            // so much for encapsulation...
//            Checkpoints.onBFSCompleted(this); // we need a macro to do this
            /*
            macro! onBFSCompleted
            ---
            //Checkpoints.onBFSCompleted(this);
            ---
             */
            // onBFSCompleted!
        }
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

    public Direction getImmediateMoveDirectionNearbyTarget(MapLocation target) {
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

//    public Direction getImmediateMoveDirectionFarTarget(MapLocation target) {
//        Debug.setIndicatorDot(Cache.MY_LOCATION, 255, 255, 0);
//        double bestDistance = Double.MAX_VALUE;
//        MapLocation bestTarget = null;
//        for (int i = this.queue.index; --i >= 0; ) { // TODO: it cannot use all - it can only be ones that yield no neighbors?
//            MapLocation potentialLocation = this.queue.queue[i];
//            if (PassabilityCache.isPassableOrTrue(potentialLocation)) {
//                Debug.setIndicatorDot(potentialLocation, 0, 255, 255);
//                double distance = distances[potentialLocation.x][potentialLocation.y] + 8 * Math.sqrt(potentialLocation.distanceSquaredTo(target));
//                if (distance < bestDistance) {
//                    bestDistance = distance;
//                    bestTarget = potentialLocation;
//                }
//            }
//        }
//        if (bestTarget == null) {
//            return null;
//        }
//        Debug.setIndicatorDot(bestTarget, 0, 0, 255);
//        int moveDirection = moveDirections[bestTarget.x][bestTarget.y];
//        for (Direction direction : Constants.getAttemptOrder(Cache.MY_LOCATION.directionTo(target))) {
//            if (rc.canMove(direction) && (moveDirection & (1 << direction.ordinal())) != 0) {
//                return direction;
//            }
//        }
//        return null;
//    }

    public boolean hasMoveDirection(MapLocation target) {
        int[] array = moveDirections[target.x];
        return array != null && array[target.y] != 0;
    }
}
