package finalBot.pathfinder;

import battlecode.common.MapLocation;

public class FastChunkCoordQueue {
    private ChunkCoord[] queue;
    private int maxSize;
    private int index = 0;
    private int size = 0;

    public FastChunkCoordQueue(int maxSize) {
        this.queue = new ChunkCoord[maxSize];
        this.maxSize = maxSize;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ChunkCoord peek() {
        return queue[index % maxSize];
    }

    public ChunkCoord poll() {
        size--;
        return queue[(index++) % maxSize];
    }

    public void add(ChunkCoord location) {
        queue[(index + size++) % maxSize] = location;
    }

    public void clear() {
        this.size = 0;
    }
}
