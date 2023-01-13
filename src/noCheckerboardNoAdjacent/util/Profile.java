package noCheckerboardNoAdjacent.util;

public enum Profile {
    ERROR_STATE(true),
    PATHFINDING(false),
    EXPLORER(false),
    MINING(true),
    ATTACKING(false),
    REPEAT_MOVE(false);

    private final boolean enabled;

    private Profile(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enabled() {
        return Constants.DEBUG_PROFILES && enabled;
    }
}