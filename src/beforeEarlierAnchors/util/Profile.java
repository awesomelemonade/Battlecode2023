package beforeEarlierAnchors.util;

public enum Profile {
    ERROR_STATE(true),
    PATHFINDING(false),
    EXPLORER(false),
    MINING(false),
    ATTACKING(false),
    BFS(false), // BFS_VISION
    CHECKPOINTS(false),
    BFS_CHECKPOINTS(false);

    private final boolean enabled;

    private Profile(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return Constants.DEBUG_PROFILES && enabled;
    }
}