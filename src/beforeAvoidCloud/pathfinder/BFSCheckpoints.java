package beforeAvoidCloud.pathfinder;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import beforeAvoidCloud.util.*;

public class BFSCheckpoints {
//    private static final int SIZE = Checkpoints.SIZE;
////    private static int[][] distances = new int[SIZE][SIZE];
//    private static boolean[][] visited = new boolean[SIZE][SIZE];
//    private static ChunkCoord[][] next = new ChunkCoord[SIZE][SIZE];
//    private static FastChunkCoordQueue queue = new FastChunkCoordQueue(200);
////    private Direction[][] prev = new Direction[SIZE][SIZE];
//
//    public static void debug_render() {
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                ChunkCoord chunkCoord = new ChunkCoord(i, j);
//                MapLocation location = Checkpoints.chunkToMapLocation(chunkCoord);
//                if (Checkpoints.chunkIsUnexplored(chunkCoord)) {
//                    if (visited[i][j]) {
//                        Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, location, 255, 128, 0); // orange
//                        ChunkCoord nextChunkCoord = next[chunkCoord.chunkX][chunkCoord.chunkY];
//                        if (nextChunkCoord != null) {
//                            MapLocation nextLocation = Checkpoints.chunkToMapLocation(nextChunkCoord);
//                            Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, location, nextLocation, 255, 128, 0); // orange
//                        }
//                    } else {
//                        Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, location, 255, 0, 0); // red
//                    }
//                } else {
//                    if (visited[i][j]) {
//                        Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, location, 0, 255, 255); // cyan
//                        ChunkCoord nextChunkCoord = next[chunkCoord.chunkX][chunkCoord.chunkY];
//                        if (nextChunkCoord != null) {
//                            MapLocation nextLocation = Checkpoints.chunkToMapLocation(nextChunkCoord);
//                            Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, location, nextLocation, 255, 128, 0); // cyan
//                        }
//                    } else {
//                        Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, location, 0, 0, 255); // blue
//                    }
//                }
//            }
//        }
//    }
//
//    public static void debug_reset() {
//        // reset
//        queue.clear();
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                visited[i][j] = false;
//            }
//        }
//    }
//
//    private static int lastInvalidateTurn = -1;
//    public static void invalidate() {
//        if (lastInvalidateTurn == Cache.TURN_COUNT) {
//            return;
//        }
//        lastInvalidateTurn = Cache.TURN_COUNT;
//        debug_reset();
//        EnemyHqGuesser.forEachConfirmed(location -> {
//            ChunkCoord chunk = Checkpoints.getNearestChunkCoord(location);
//            if (!visited[chunk.chunkX][chunk.chunkY]) {
//                queue.add(chunk);
//                visited[chunk.chunkX][chunk.chunkY] = true;
//            }
//        });
//        if (queue.isEmpty()) {
//            // No known Enemy HQs, let's use guesser
//            EnemyHqGuesser.forEachNonInvalidatedPrediction(location -> {
//                ChunkCoord chunk = Checkpoints.getNearestChunkCoord(location);
//                if (!visited[chunk.chunkX][chunk.chunkY]) {
//                    queue.add(chunk);
//                    visited[chunk.chunkX][chunk.chunkY] = true;
//                }
//            });
//        }
//    }
//
//    private static int prevKnownEnemyHeadquarters;
//    private static int prevInvalidations;
//
//    public static void postLoop() {
//        // see if numKnownEnemyHeadquarters or invalidations have changed
//        if (prevKnownEnemyHeadquarters == 0) {
//            // check if invalidations or known enemy hqs have changed
//            if (prevKnownEnemyHeadquarters != EnemyHqGuesser.numKnownEnemyHeadquarterLocations) {
//                invalidate();
//                prevKnownEnemyHeadquarters = EnemyHqGuesser.numKnownEnemyHeadquarterLocations;
//            } else if (prevInvalidations != EnemyHqGuesser.invalidations) {
//                invalidate();
//                prevInvalidations = EnemyHqGuesser.invalidations;
//            }
//        } else {
//            // invalidations are now irrelevant
//            if (prevKnownEnemyHeadquarters != EnemyHqGuesser.numKnownEnemyHeadquarterLocations) {
//                invalidate();
//                prevKnownEnemyHeadquarters = EnemyHqGuesser.numKnownEnemyHeadquarterLocations;
//            }
//        }
//        bfs();
//    }
//
//    public static void bfs() {
////        int max = 0;
//        for (int t = 50; --t >= 0 && !queue.isEmpty() && Clock.getBytecodesLeft() > 1050; ) {
////            int a = Clock.getBytecodeNum();
//            ChunkCoord chunk = queue.poll();
//            Checkpoints.debug_backwardsNeighborChunks(chunk);
//            for (int i = Checkpoints.neighborsLength; --i >= 0; ) {
//                ChunkCoord neighbor = Checkpoints.neighbors[i];
//                if (!visited[neighbor.chunkX][neighbor.chunkY]) {
//                    Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, Checkpoints.chunkToMapLocation(chunk), Checkpoints.chunkToMapLocation(neighbor), 0, 255, 0);
//                    queue.add(neighbor);
//                    visited[neighbor.chunkX][neighbor.chunkY] = true;
//                    next[neighbor.chunkX][neighbor.chunkY] = chunk;
//                }
//            }
////            max = Math.max(max, Clock.getBytecodeNum() - a);
//        }
////        Debug.println("MAX: " + max);
//    }
//
//    public static boolean execute() {
//        // TODO: invalidate if many checkpoints changed?
//        // see if we can navigate to next
//        ChunkCoord currentChunk = Checkpoints.getNearestChunkCoord(Cache.MY_LOCATION);
//        ChunkCoord nextChunk = next[currentChunk.chunkX][currentChunk.chunkY];
//        if (!visited[currentChunk.chunkX][currentChunk.chunkY] || nextChunk == null || !visited[nextChunk.chunkX][nextChunk.chunkY]) {
//            return false;
//        }
//
//        BFSVision bfs = BFSVision.getBFSIfCompleted(); // TODO: we don't have to use BFSVision to get to there
//        if (bfs == null) {
//            // can we just use pathfinding?
//            MapLocation nextChunkLocation = Checkpoints.chunkToMapLocation(nextChunk);
//            Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, Cache.MY_LOCATION, 255, 255, 0); // yellow
//            Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, Cache.MY_LOCATION, nextChunkLocation, 255, 255, 0); // yellow
//            Util.tryPathfindingMove(nextChunkLocation);
//            return true;
//        } else {
//            // let's use bfs because it's completed
//            // see if we can navigate to nextChunk
//            MapLocation currentChunkLocation = Checkpoints.chunkToMapLocation(currentChunk);
//            if (!bfs.hasMoveDirection(currentChunkLocation)) {
//                return false; // we need to find a chunk that we can access
//            }
//            MapLocation nextChunkLocation = Checkpoints.chunkToMapLocation(nextChunk);
//            if (/*bfs.hasMoveDirection(currentChunkLocation) && */!bfs.hasMoveDirection(nextChunkLocation)) {
//                // invalidate
//                Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, currentChunkLocation, 255, 0, 255);
//                Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, currentChunkLocation, nextChunkLocation, 255, 0, 255);
//                BFSCheckpoints.invalidate();
//                Flags.flag(Flags.BFS_CHECKPOINTS_INVALIDATE);
//                return false;
//            }
//            Direction direction = bfs.getImmediateMoveDirectionNearbyTarget(nextChunkLocation);
//            if (direction == null) {
//                // traffic - let's just wait
//                return true;
//            }
//            Flags.flag(Flags.BFS_CHECKPOINTS_EXECUTE);
//            Debug.setIndicatorDot(Profile.BFS_CHECKPOINTS, currentChunkLocation, 128, 255, 0);
//            Debug.setIndicatorLine(Profile.BFS_CHECKPOINTS, currentChunkLocation, nextChunkLocation, 128, 255, 0);
//            if (direction != Direction.CENTER) {
//                Util.move(direction);
//            }
//            return true;
//        }
//    }
}
