package beforeAdjustments.fast;

import battlecode.common.MapLocation;

public class FastMapLocationQueue {
    private MapLocation[] queue;
    private int maxSize;
    private int index = 0;
    private int size = 0;

    public FastMapLocationQueue(int maxSize) {
        this.queue = new MapLocation[maxSize];
        this.maxSize = maxSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public MapLocation peek() {
        return queue[index % maxSize];
    }

    public MapLocation poll() {
        size--;
        return queue[(index++) % maxSize];
    }

    public void add(MapLocation location) {
        queue[(index + size++) % maxSize] = location;
    }
}
