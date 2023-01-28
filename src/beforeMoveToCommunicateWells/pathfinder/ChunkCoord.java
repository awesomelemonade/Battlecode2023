package beforeMoveToCommunicateWells.pathfinder;

import battlecode.common.Direction;

// TODO: when we are done, we can replace with MapLocation to save bytecodes
public class ChunkCoord {
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
    @Override
    public boolean equals(Object o) {
        if (o instanceof ChunkCoord) {
            ChunkCoord other = (ChunkCoord) o;
            return chunkX == other.chunkX && chunkY == other.chunkY;
        }
        return false;
    }
    @Override
    public String toString() {
        return String.format("ChunkCoord[x=%d, y=%d]", chunkX, chunkY);
    }
}
