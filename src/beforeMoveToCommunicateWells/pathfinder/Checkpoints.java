package beforeMoveToCommunicateWells.pathfinder;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import beforeMoveToCommunicateWells.util.*;

import static beforeMoveToCommunicateWells.util.Constants.rc;

public class Checkpoints {
    // 15 x 15 grid of checkpoints. Each checkpoint requires 8 bits.
    public static final int SIZE = 15;
    public static final int HALF_SIZE = SIZE / 2;
    public static final int SIZE_MINUS_ONE = SIZE - 1;
    private static final int CYCLES_PER_FULL_REFRESH = 6;
    private static final int CHECKPOINTS_PER_CYCLE = (SIZE * SIZE + CYCLES_PER_FULL_REFRESH - 1) / CYCLES_PER_FULL_REFRESH; // number of checkpoints = 38
    private static final int COMM_INTS_PER_CYCLE = (CHECKPOINTS_PER_CYCLE + 1) / 2; // 2 checkpoints per comm int: 19 ints
    private static int[] checkpoints = new int[SIZE * SIZE + CYCLES_PER_FULL_REFRESH]; // + x to avoid out of bounds and bound checking. last elements are meaningless
    // ^ can we somehow unpack the checkpoints only once (get neighbor chunks)
//    private static ChunkCoord[][] neighborCheckpoints = new ChunkCoord[SIZE * SIZE + CYCLES_PER_FULL_REFRESH][];

    private static ChunkCoord[] pending = new ChunkCoord[20];
    private static int pendingSize = 0;

    private static final int MASK_8 = 0b1111_1111; // 8 bits
    private static int HALF_MAP_WIDTH;
    private static int HALF_MAP_HEIGHT;

    public static void init() {
        HALF_MAP_WIDTH = Constants.MAP_WIDTH / 2;
        HALF_MAP_HEIGHT = Constants.MAP_HEIGHT / 2;
    }

    public static void debug_render() {
        if (Profile.CHECKPOINTS.enabled()) {
            if (rc.getRoundNum() <= 1) {
                // For debugging getNearestChunkCoord
                for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                    for (int j = 0; j < Constants.MAP_WIDTH; j++) {
                        MapLocation location = new MapLocation(i, j);
                        ChunkCoord chunkCoord = getNearestChunkCoord(location);
                        MapLocation chunkLocation = chunkToMapLocation(chunkCoord);
                        Debug.setIndicatorLine(location, chunkLocation, 0, 255, 0);
                        Debug.setIndicatorDot(chunkLocation, 0, 255, 0);
                    }
                }
            } else {
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        ChunkCoord chunk = new ChunkCoord(i, j);

                        boolean isPending = false;
                        for (int k = pendingSize; --k >= 0; ) {
                            if (pending[k].equals(chunk)) {
                                isPending = true;
                                break;
                            }
                        }

                        int checkpoint = checkpoints[chunk.chunkX * SIZE + chunk.chunkY];
                        MapLocation location = chunkToMapLocation(chunk);
                        if (checkpoint != 0) {
                            if (isPending) {
                                Debug.setIndicatorDot(Profile.CHECKPOINTS, location, 255, 255, 0); // yellow
                            } else {
                                Debug.setIndicatorDot(Profile.CHECKPOINTS, location, 0, 255, 0); // green
                            }
                        } else {
                            Debug.setIndicatorDot(Profile.CHECKPOINTS, location, 255, 0, 0); // red
                        }
                    }
                }
            }
        }
    }

    public static void update() throws GameActionException {
        debug_render();
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            // read from pending
            boolean isLastHq = Communication.headquartersLocations != null &&
                    Cache.MY_LOCATION.equals(Communication.headquartersLocations[Communication.headquartersLocations.length - 1]);
            for (int i = Communication.CHECKPOINTS_PENDING_LENGTH; --i >= 0; ) {
                int read = rc.readSharedArray(i + Communication.CHECKPOINTS_PENDING_OFFSET);
                if (read != 0) {
                    checkpoints[(read >> 8) - 1] = read & MASK_8;
                    if (isLastHq) {
                        // remove pending if we're the last HQ?
                        rc.writeSharedArray(i + Communication.CHECKPOINTS_PENDING_OFFSET, 0);
                    }
                }
            }
            // write next cycle
            int cycleIndex = rc.getRoundNum() % CYCLES_PER_FULL_REFRESH;
            for (int i = COMM_INTS_PER_CYCLE; --i >= 0; ) {
                int index = CHECKPOINTS_PER_CYCLE * cycleIndex + i * 2;
                rc.writeSharedArray(i + Communication.CHECKPOINTS_OFFSET, (checkpoints[index + 1] << 8) | checkpoints[index]);
            }
        } else {
            // update checkpoints from comms
            int cycleIndex = rc.getRoundNum() % CYCLES_PER_FULL_REFRESH;
            for (int i = COMM_INTS_PER_CYCLE; --i >= 0;) {
                int read = rc.readSharedArray(i + Communication.CHECKPOINTS_OFFSET);
                int index = CHECKPOINTS_PER_CYCLE * cycleIndex + i * 2;
                // TODO: invalidate BFSCheckpoints?
                checkpoints[index] |= read & MASK_8;
                checkpoints[index + 1] |= (read >> 8) & MASK_8;
            }
        }
        // flush pending
        if (rc.canWriteSharedArray(0, 0)) {
            for (int i = Communication.CHECKPOINTS_PENDING_LENGTH; --i >= 0; ) {
                int commIndex = i + Communication.CHECKPOINTS_PENDING_OFFSET;
                if (pendingSize > 0 && rc.readSharedArray(commIndex) == 0) {
                    ChunkCoord chunkCoord = pending[--pendingSize];
                    int chunkIndex = chunkCoord.chunkX * SIZE + chunkCoord.chunkY;
                    rc.writeSharedArray(commIndex, ((1 + chunkIndex) << 8) | (checkpoints[chunkIndex]));
                }
            }
            if (pendingSize > 0) {
                Debug.println("Warning: Out of space to write pending checkpoints");
            }
        }
    }

    public static void onBFSCompleted(BFSVision bfs) {
        debug_onBFSCompleted(bfs);
    }

    public static void debug_onBFSCompleted(BFSVision bfs) {
        ChunkCoord chunk = getNearestChunkCoord(bfs.origin);
        if (bfsCanReachChunk(bfs, chunk) && chunkIsUnexplored(chunk)) {
            int checkpoint = 0;
            // TODO - very costly
            for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
                ChunkCoord neighborChunk = chunk.add(Constants.ORDINAL_DIRECTIONS[i]);
                if (bfsCanReachChunk(bfs, neighborChunk)) { // TODO: maybe bfsCanReachChunk can be cached - we do the same computation in getNearestUnexplored()
                    // assumed: i = direction.ordinal()
                    checkpoint |= (1 << i);
                }
            }
            addPendingCheckpoint(chunk, checkpoint);
            BFSCheckpoints.invalidate();
        }
    }

    static int[] dx = new int[] {-1, 1, 0, 0, -1, 1, -1, 1};
    static int[] dy = new int[] {0, 0, -1, 1, -1, -1, 1, 1};
    // TODO: can be a list of neighbor chunks stored in bfs
    public static ChunkCoord getNearestUnexplored(BFSVision bfs) {
        ChunkCoord currentChunkCoord = getNearestChunkCoord(Cache.MY_LOCATION);
        if (bfsCanReachChunk(bfs, currentChunkCoord) && chunkIsUnexplored(currentChunkCoord)) {
            return currentChunkCoord;
        }
        for (int i = dx.length; --i >= 0; ) {
            ChunkCoord chunk = currentChunkCoord.translate(dx[i], dy[i]);
            if (bfsCanReachChunk(bfs, chunk) && chunkIsUnexplored(chunk)) {
                return chunk;
            }
        }
        return null;
    }

    public static void addPendingCheckpoint(ChunkCoord chunk, int direction) {
        if (pendingSize < pending.length) {
            int chunkIndex = chunk.chunkX * SIZE + chunk.chunkY;
            int before = checkpoints[chunkIndex];
            if ((checkpoints[chunkIndex] |= direction) != before) {
                pending[pendingSize++] = chunk;
            }
        } else {
            Debug.println("Warning: out of pending checkpoints");
        }
    }

    public static ChunkCoord[] neighbors;
    public static int neighborsLength;
    public static void debug_pathableNeighborChunks(ChunkCoord chunk) {
        int checkpoint = checkpoints[chunk.chunkX * SIZE + chunk.chunkY];

        // TODO: assume checkpoints not explored are checkpoints that can get everywhere - i.e. 0b1111_1111?
//        if (checkpoint == 0) {
//            checkpoint = 0b1111_1111;
//        }

        // can be unrolled
//        switch (checkpoint) {
//
//        }
        ChunkCoord[] buffer = new ChunkCoord[Constants.ORDINAL_DIRECTIONS.length];
        int length = 0;
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            if ((checkpoint & (1 << direction.ordinal())) != 0) {
                buffer[length++] = chunk.add(direction);
            }
        }
        neighbors = buffer;
        neighborsLength = length;
//        return buffer;
//        return Util.trimArray(buffer, length);
    }
    public static void debug_backwardsNeighborChunks(ChunkCoord chunk) {
        ChunkCoord[] buffer = new ChunkCoord[Constants.ORDINAL_DIRECTIONS.length];
        int length = 0;
        for (Direction direction : Constants.ORDINAL_DIRECTIONS) {
            ChunkCoord neighbor = chunk.add(direction);
            // TODO: optimize out the bound checking?
            if (neighbor.chunkX >= 0 && neighbor.chunkY >= 0 && neighbor.chunkX < SIZE && neighbor.chunkY < SIZE) {
                int checkpoint = checkpoints[neighbor.chunkX * SIZE + neighbor.chunkY];
                if (checkpoint == 0 || (checkpoint & (1 << direction.opposite().ordinal())) != 0) {
                    buffer[length++] = neighbor;
                }
            }
        }
        neighbors = buffer;
        neighborsLength = length;
    }

    public static boolean chunkIsUnexplored(ChunkCoord chunk) {
        // TODO: dedicated explored array?
        return checkpoints[chunk.chunkX * SIZE + chunk.chunkY] == 0;
    }

    public static boolean bfsCanReachChunk(BFSVision bfs, ChunkCoord chunk) {
        MapLocation location = chunkToMapLocation(chunk);
        return rc.onTheMap(location) && bfs.hasMoveDirection(location);
    }

    public static MapLocation chunkToMapLocation(ChunkCoord chunk) {
        return new MapLocation((chunk.chunkX * Constants.MAP_WIDTH + HALF_SIZE) / SIZE, (chunk.chunkY * Constants.MAP_HEIGHT + HALF_SIZE) / SIZE);
    }

    public static ChunkCoord getNearestChunkCoord(MapLocation location) {
        return new ChunkCoord(Math.min((location.x * SIZE + HALF_MAP_WIDTH) / Constants.MAP_WIDTH, SIZE_MINUS_ONE), Math.min((location.y * SIZE + HALF_MAP_HEIGHT) / Constants.MAP_HEIGHT, SIZE_MINUS_ONE));
    }
}
