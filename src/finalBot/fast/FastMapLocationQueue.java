package finalBot.fast;

import battlecode.common.MapLocation;

public class FastMapLocationQueue {
    public MapLocation[] queue;
    private int maxSize;
    public int index = 0;
    public int size = 0;

    public FastMapLocationQueue(int maxSize) {
        this.queue = new MapLocation[maxSize];
        this.maxSize = maxSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public MapLocation peek() {
        return queue[index];
    }

    public MapLocation poll() {
        size--;
        return queue[index++];
    }

    public void add(MapLocation location) {
        queue[index + size++] = location;
    }

    public void clear() {
        this.size = 0;
    }
}
