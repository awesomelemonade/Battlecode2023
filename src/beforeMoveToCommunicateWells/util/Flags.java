package beforeMoveToCommunicateWells.util;

public class Flags {
    private static final StringBuilder flags = new StringBuilder();
    private static final char SEPARATOR = '|';
    public static void flag(String flag) {
        if (Constants.DEBUG_PROFILES && false) {
            if (flags.indexOf(flag) == -1) {
                flags.append(flag);
                flags.append(SEPARATOR);
                Debug.println("FLAG{" + flag + "}");
            }
        }
    }
}
