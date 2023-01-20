package sprintBot.pathfinder;

import battlecode.common.GameActionException;
import battlecode.common.RobotType;
import sprintBot.util.Cache;
import sprintBot.util.Communication;
import sprintBot.util.Constants;

import static sprintBot.util.Constants.rc;

public class Checkpoints {
    // 15 x 15 grid of checkpoints. Each checkpoint requires 8 bits.
    // requires 20 ints to comm over cycles of 10 rounds
    private static final int SIZE = 15;
    private static final int NUM_CYCLES = 5;
    private static final int CYCLE_LENGTH = SIZE * SIZE / NUM_CYCLES; // number of checkpoints
    private static final int CYCLE_LENGTH_COMM_INTS = (CYCLE_LENGTH + 1) / 2; // 2 checkpoints per comm int
    private static int[] checkpoints = new int[SIZE * SIZE];
    private static ChunkCoord[] pending = new ChunkCoord[20];
    private static int pendingSize = 0;

    private static final int MASK_8 = 0b1111_1111; // 8 bits

    public static void update() throws GameActionException {
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            // read from pending
            boolean isLastHq = Communication.headquartersLocations != null &&
                    Cache.MY_LOCATION.equals(Communication.headquartersLocations[Communication.headquartersLocations.length - 1]);
            for (int i = PENDING_LENGTH; --i >= 0; ) {
                int read = rc.readSharedArray(i + PENDING_OFFSET);
                if (read != 0) {
                    checkpoints[(read >> 8) - 1] = read & MASK_8;
                    if (isLastHq) {
                        // remove pending if we're the last HQ?
                        rc.writeSharedArray(i + PENDING_OFFSET, 0);
                    }
                }
            }
            // write next cycle
            int cycleIndex = rc.getRoundNum() % CYCLE_LENGTH;
            for (int i = CYCLE_LENGTH_COMM_INTS; --i >= 0; ) {
                int index = CYCLE_LENGTH * cycleIndex + i * 2;
                rc.writeSharedArray(i + OFFSET, (checkpoints[index + 1] << 8) | checkpoints[index]);
            }
        } else {
            // update checkpoints from comms
            int cycleIndex = rc.getRoundNum() % CYCLE_LENGTH;
            for (int i = CYCLE_LENGTH_COMM_INTS; --i >= 0;) {
                int read = rc.readSharedArray(i + OFFSET); // TODO
                int index = CYCLE_LENGTH * cycleIndex + i * 2;
                checkpoints[index] |= read & MASK_8;
                checkpoints[index + 1] |= (read >> 8) & MASK_8;
            }
        }
        // update checkpoint for current chunk
        // TODO
        ChunkCoord currentChunkCoord = getNearestChunkCoord();
        // flush pending
        if (rc.canWriteSharedArray(0, 0)) {
            for (int i = PENDING_LENGTH; --i >= 0; ) {
                int commIndex = i + PENDING_OFFSET; // TODO
                if (pendingSize > 0 && rc.readSharedArray(commIndex) == 0) {
                    ChunkCoord chunkCoord = pending[--pendingSize];
                    int chunkIndex = chunkCoord.chunkX * SIZE + chunkCoord.chunkY;
                    rc.writeSharedArray(commIndex, ((1 + chunkIndex) << 8) | (checkpoints[chunkIndex]));
                }
            }
        }
    }

    public static void addPendingCheckpoint(ChunkCoord chunk, int direction) {
        if (pendingSize < pending.length) {
            checkpoints[chunk.chunkX * SIZE + chunk.chunkY] |= direction;
            pending[pendingSize++] = chunk;
        }
    }

    public static ChunkCoord[] neighborChunks(ChunkCoord chunk) {
        int checkpoint = checkpoints[chunk.chunkX * SIZE + chunk.chunkY];

        // TODO: assume checkpoints not explored are checkpoints that can get everywhere - i.e. 0b1111_1111?
//        if (checkpoint == 0) {
//            checkpoint = 0b1111_1111;
//        }

        // can be unrolled
        switch (checkpoint) {

        }
    }
//    public static boolean isExplored(ChunkCoord chunk) {
//        //
//    }

    static class ChunkCoord {
        int chunkX, chunkY;
        public ChunkCoord(int chunkX, int chunkY) {
            this.chunkX = chunkX;
            this.chunkY = chunkY;
        }
    }
}
