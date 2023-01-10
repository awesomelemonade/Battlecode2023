package sprintBot.util;

public enum Profile {
    ERROR_STATE(true),
    CHUNK_INFO(false),
    PATHFINDING(true),
    EXPLORER(true),
    MINING(true),
    ATTACKING(false);

    private final boolean enabled;

    private Profile(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return enabled;
    }
}