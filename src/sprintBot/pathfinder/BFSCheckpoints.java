package sprintBot.pathfinder;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import sprintBot.util.Cache;
import sprintBot.util.Debug;
import sprintBot.util.EnemyHqGuesser;
import sprintBot.util.EnemyHqTracker;

public class BFSCheckpoints {
    private static final int SIZE = Checkpoints.SIZE;
//    private static int[][] distances = new int[SIZE][SIZE];
    private static boolean[][] visited = new boolean[SIZE][SIZE];
    private static FastChunkCoordQueue queue = new FastChunkCoordQueue(200);
//    private Direction[][] prev = new Direction[SIZE][SIZE];

    public static void debug_render() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ChunkCoord chunkCoord = new ChunkCoord(i, j);
                MapLocation location = Checkpoints.chunkToMapLocation(chunkCoord);
                if (Checkpoints.chunkIsUnexplored(chunkCoord)) {
                    if (visited[i][j]) {
                        Debug.setIndicatorDot(location, 255, 128, 0); // orange
                    } else {
                        Debug.setIndicatorDot(location, 255, 0, 0); // red
                    }
                } else {
                    if (visited[i][j]) {
                        Debug.setIndicatorDot(location, 0, 0, 255); // blue
                    } else {
                        Debug.setIndicatorDot(location, 0, 255, 255); // cyan
                    }
                }
            }
        }
    }

    public static void debug_reset() {
        // reset
        queue.clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                visited[i][j] = false;
            }
        }
    }

    private static int lastInvalidateTurn = -1;
    public static void invalidate() {
        if (lastInvalidateTurn == Cache.TURN_COUNT) {
            return;
        }
        lastInvalidateTurn = Cache.TURN_COUNT;
        debug_reset();
        EnemyHqTracker.forEachKnownAndPending(location -> {
            ChunkCoord chunk = Checkpoints.getNearestChunkCoord(location);
            if (!visited[chunk.chunkX][chunk.chunkY]) {
                queue.add(chunk);
                visited[chunk.chunkX][chunk.chunkY] = true;
            }
        });
        if (queue.isEmpty()) {
            // No known Enemy HQs, let's use guesser
            EnemyHqGuesser.forEach(location -> {
                ChunkCoord chunk = Checkpoints.getNearestChunkCoord(location);
                if (!visited[chunk.chunkX][chunk.chunkY]) {
                    queue.add(chunk);
                    visited[chunk.chunkX][chunk.chunkY] = true;
                }
            });
        }
    }

    public static void postLoop() {
        if (Cache.TURN_COUNT == -1) { // TODO: when enemy hqs changed OR when we run into an impassable wall
            invalidate();
        }
        bfs();
    }

    public static void bfs() {
        int max = 0;
        while (!queue.isEmpty() && Clock.getBytecodesLeft() > 900) {
            int a = Clock.getBytecodeNum();
            ChunkCoord chunk = queue.poll();
            Checkpoints.debug_backwardsNeighborChunks(chunk);
            for (int i = Checkpoints.neighborsLength; --i >= 0; ) {
                ChunkCoord neighbor = Checkpoints.neighbors[i];
                if (!visited[neighbor.chunkX][neighbor.chunkY]) {
                    Debug.setIndicatorLine(Checkpoints.chunkToMapLocation(chunk), Checkpoints.chunkToMapLocation(neighbor), 0, 255, 0);
                    queue.add(neighbor);
                    visited[neighbor.chunkX][neighbor.chunkY] = true;
                }
            }
            max = Math.max(max, Clock.getBytecodeNum() - a);
        }
        Debug.println("MAX: " + max);
    }
}
