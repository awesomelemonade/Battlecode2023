package sprintBot.util;

public enum Profile {
    ERROR_STATE(true),
    CHUNK_INFO(false),
    PATHFINDING(false),
    EXPLORER(true),
    MINING(true),
    ATTACKING(false),
    REPEAT_MOVE(true);

    private final boolean enabled;

    private Profile(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return Constants.DEBUG_PROFILES && enabled;
    }
}