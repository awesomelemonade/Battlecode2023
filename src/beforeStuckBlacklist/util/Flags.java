package beforeStuckBlacklist.util;

public class Flags {
    private static final StringBuilder flags = new StringBuilder();
    private static final char SEPARATOR = '|';

    public static final String BFS_CHECKPOINTS_INVALIDATE = "?";
    public static final String BFS_CHECKPOINTS_EXECUTE = "!";
    public static final String CARRIER_GAVE_UP_TARGET_RESOURCE = "^";
    public static final String HEADQUARTERS_BUILDING_CARRIER_FOR_ANCHOR = "%";
    public static final String CARRIER_MOVE_TO_COMMUNICATE_PENDING_MANA_WELL = "&";
    public static final String EARLIER_ANCHORS = "A";


    public static void flag(String flag) {
        if (Constants.DEBUG_FLAGS) {
            if (flags.indexOf(flag) == -1) {
                flags.append(flag);
                flags.append(SEPARATOR);
                Debug.println("FLAG{" + flag + "}");
            }
        }
    }
}
