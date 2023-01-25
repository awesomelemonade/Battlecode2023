package sprintBot.pathfinder;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import sprintBot.util.Cache;
import sprintBot.util.Debug;
import sprintBot.util.EnemyHqGuesser;

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
                if (visited[i][j] && false) {
                    Debug.setIndicatorDot(location, 0, 0, 255); // blue
                } else if (Checkpoints.chunkIsUnexplored(chunkCoord)) {
                    Debug.setIndicatorDot(location, 255, 0, 0); // red
                } else {
                    Debug.setIndicatorDot(location, 0, 255, 255); // cyan
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

    public static void debug_bfs() {
        debug_reset();
        Debug.println("A: " + Clock.getBytecodeNum());
        // bfs from current location
        // TODO: work backwards from the destinations?
        MapLocation location = EnemyHqGuesser.getClosestPreferRotationalSymmetry(loc -> true);
        if (location == null) {
            return;
        }
        Debug.setIndicatorLine(Cache.MY_LOCATION, location, 255, 255, 0); // yellow
        ChunkCoord currentChunk = Checkpoints.getNearestChunkCoord(location);
        queue.add(currentChunk);
        visited[currentChunk.chunkX][currentChunk.chunkY] = true;
        int count = 0;
        while (!queue.isEmpty()) {
            count++;
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
        }
        Debug.println("B: " + Clock.getBytecodeNum() + " - " + count);
    }
}
