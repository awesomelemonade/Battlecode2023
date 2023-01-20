package sprintBot.pathfinder;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import sprintBot.util.*;

import static sprintBot.util.Constants.rc;

public class Checkpoints {
    // 15 x 15 grid of checkpoints. Each checkpoint requires 8 bits.
    private static final int SIZE = 15;
    private static final int NUM_CYCLES = 5;
    private static final int CYCLE_LENGTH = SIZE * SIZE / NUM_CYCLES; // number of checkpoints
    private static final int CYCLE_LENGTH_COMM_INTS = (CYCLE_LENGTH + 1) / 2; // 2 checkpoints per comm int: 23 ints
    private static int[] checkpoints = new int[SIZE * SIZE + 1]; // + 1 to avoid out of bounds and bound checking. last element is meaningless
    private static ChunkCoord[] pending = new ChunkCoord[20];
    private static int pendingSize = 0;

    private static final int MASK_8 = 0b1111_1111; // 8 bits

    public static void debug_render() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ChunkCoord chunk = new ChunkCoord(i, j);
                int checkpoint = checkpoints[chunk.chunkX * SIZE + chunk.chunkY];
                if (checkpoint != 0) {
                    Debug.setIndicatorDot(chunkToMapLocation(chunk), 0, 255, 0);
                } else {
                    Debug.setIndicatorDot(chunkToMapLocation(chunk), 255, 0, 0);
                }
            }
        }
//        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
//            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
//                Debug.setIndicatorDot(chunkToMapLocation(getNearestChunkCoord(new MapLocation(i, j))), 0, 0, 255);
//            }
//        }
    }

    public static void update() throws GameActionException {
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            debug_render();
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
            int cycleIndex = rc.getRoundNum() % NUM_CYCLES;
            for (int i = CYCLE_LENGTH_COMM_INTS; --i >= 0; ) {
                int index = CYCLE_LENGTH * cycleIndex + i * 2;
                rc.writeSharedArray(i + Communication.CHECKPOINTS_OFFSET, (checkpoints[index + 1] << 8) | checkpoints[index]);
            }
        } else {
            // update checkpoints from comms
            int cycleIndex = rc.getRoundNum() % NUM_CYCLES;
            for (int i = CYCLE_LENGTH_COMM_INTS; --i >= 0;) {
                int read = rc.readSharedArray(i + Communication.CHECKPOINTS_OFFSET); // TODO
                int index = CYCLE_LENGTH * cycleIndex + i * 2;
                checkpoints[index] |= read & MASK_8;
                checkpoints[index + 1] |= (read >> 8) & MASK_8;
            }
        }
        // update checkpoint for current chunk
        // TODO
        BFSVision bfs = BFSVision.getBFSIfCompleted();
        if (bfs != null) {
            ChunkCoord unexplored = getNearestUnexplored(bfs);
            if (unexplored != null) {
                int checkpoint = 0;
                for (int i = Constants.ORDINAL_DIRECTIONS.length; --i >= 0; ) {
                    ChunkCoord neighborChunk = unexplored.add(Constants.ORDINAL_DIRECTIONS[i]);
                    if (chunkIsOnTheMap(neighborChunk) && bfsCanReachChunk(bfs, neighborChunk)) {
                        // assumed: i = direction.ordinal()
                        checkpoint |= (1 << i);
                    }
                }
                addPendingCheckpoint(unexplored, checkpoint);
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

    static int[] dx = new int[] {-1, 1, 0, 0, -1, 1, -1, 1};
    static int[] dy = new int[] {0, 0, -1, 1, -1, -1, 1, 1};
    public static ChunkCoord getNearestUnexplored(BFSVision bfs) {
        ChunkCoord currentChunkCoord = getNearestChunkCoord(Cache.MY_LOCATION);
        if (bfsCanReachChunk(bfs, currentChunkCoord) && chunkIsUnexplored(currentChunkCoord)) {
            return currentChunkCoord;
        }
        for (int i = dx.length; --i >= 0; ) {
            ChunkCoord chunk = currentChunkCoord.translate(dx[i], dy[i]);
            if (chunkIsOnTheMap(chunk)) {
                if (bfsCanReachChunk(bfs, chunk) && chunkIsUnexplored(chunk)) {
                    return chunk;
                }
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

    public static ChunkCoord[] pathableNeighborChunks(ChunkCoord chunk) {
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
        return Util.trimArray(buffer, length);
    }

    public static boolean chunkIsUnexplored(ChunkCoord chunk) {
        // TODO: dedicated explored array?
        return checkpoints[chunk.chunkX * SIZE + chunk.chunkY] == 0;
    }

    public static boolean bfsCanReachChunk(BFSVision bfs, ChunkCoord chunk) {
        MapLocation location = chunkToMapLocation(chunk);
        return bfs.hasMoveDirection(location) && PassabilityCache.isPassableOrFalse(location); // TODO: check hq location?
    }

    public static boolean chunkIsOnTheMap(ChunkCoord chunk) {
        return chunk.chunkX >= 0 && chunk.chunkY >= 0 && chunk.chunkX < SIZE && chunk.chunkY < SIZE;
    }

    public static MapLocation chunkToMapLocation(ChunkCoord chunk) {
        return new MapLocation(chunk.chunkX * Constants.MAP_WIDTH / SIZE, chunk.chunkY * Constants.MAP_HEIGHT / SIZE);
    }

    public static ChunkCoord getNearestChunkCoord(MapLocation location) {
        return new ChunkCoord(location.x * SIZE / Constants.MAP_WIDTH, location.y * SIZE / Constants.MAP_HEIGHT);
    }

    // TODO: when we are done, we can replace with MapLocation to save bytecodes
    static class ChunkCoord {
        int chunkX, chunkY;
        public ChunkCoord(int chunkX, int chunkY) {
            this.chunkX = chunkX;
            this.chunkY = chunkY;
        }
        public ChunkCoord add(Direction direction) {
            return new ChunkCoord(chunkX + direction.dx, chunkY + direction.dy);
        }
        public ChunkCoord translate(int dx, int dy) {
            return new ChunkCoord(chunkX + dx, chunkY + dy);
        }
    }
}
