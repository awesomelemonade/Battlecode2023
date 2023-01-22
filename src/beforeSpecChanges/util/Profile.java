package beforeSpecChanges.util;

public enum Profile {
    ERROR_STATE(true),
    PATHFINDING(false),
    EXPLORER(false),
    MINING(false),
    ATTACKING(false),
    REPEAT_MOVE(false),
    CHECKPOINTS(false),
    BFS(false);

    private final boolean enabled;

    private Profile(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return Constants.DEBUG_PROFILES && enabled;
    }
}