package sprintBot.util;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import static sprintBot.util.Constants.rc;

public class Communication {
    public static final int HEADQUARTERS_LOCATIONS_OFFSET = 0;
    private static final int HEADQUARTERS_LOCATIONS_SET_BIT = 0;
    private static final int HEADQUARTERS_LOCATIONS_LOCATION_BIT = 1;
    private static final int HEADQUARTERS_LOCATIONS_LOCATION_MASK = 0b111111_111111; // 12 bits, 6 bit per coordinate
    private static int headquartersSharedIndex = -1;
    public static MapLocation[] headquartersLocations;

    public static void init() throws GameActionException {
        // Set Headquarters location
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            // Find next available slot
            for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
                int sharedArrayIndex = HEADQUARTERS_LOCATIONS_OFFSET + i;
                if (rc.readSharedArray(sharedArrayIndex) == 0) {
                    headquartersSharedIndex = sharedArrayIndex;
                    break;
                }
            }
            if (headquartersSharedIndex == -1) {
                Debug.fastFail("-1 headquarters index");
            }
            // Broadcast headquarter location
            rc.writeSharedArray(headquartersSharedIndex,
                    (pack(Cache.MY_LOCATION) << HEADQUARTERS_LOCATIONS_LOCATION_BIT) |
                            (1 << HEADQUARTERS_LOCATIONS_SET_BIT));
        }
    }

    public static void loop() throws GameActionException {
        if (Constants.ROBOT_TYPE != RobotType.HEADQUARTERS || Cache.TURN_COUNT > 1) {
            // Initialize Arrays
            if (headquartersLocations == null) {
                boolean initialized = false;
                for (int i = GameConstants.MAX_STARTING_HEADQUARTERS; --i >= 0; ) {
                    int value = rc.readSharedArray(i);
                    if (value != 0) {
                        if (!initialized) {
                            headquartersLocations = new MapLocation[i + 1];
                            initialized = true;
                        }
                    }
                }
                if (!initialized) {
                    Debug.fastFail("Cannot read any archon locations");
                }
                // Read archon locations
                for (int i = GameConstants.MAX_STARTING_HEADQUARTERS; --i >= 0; ) {
                    int value = rc.readSharedArray(i);
                    // check if set
                    if (((value >> HEADQUARTERS_LOCATIONS_SET_BIT) & 0b1) == 1) {
                        headquartersLocations[i] = unpack((value >> HEADQUARTERS_LOCATIONS_LOCATION_BIT) & HEADQUARTERS_LOCATIONS_LOCATION_MASK);
                    }
                }
            }
            // TODO: broadcast whether the headquarters is safe (for carriers to deposit resources)
        }
    }

    public static void postLoop() {

    }

    public static MapLocation getClosestAllyHQ() {
        return Util.getClosestMapLocation(headquartersLocations);
    }

    public static MapLocation getClosestSafeAllyHQ() {
        // TODO
//        Util.getClosestMapLocation(locations, predicate)
        return getClosestAllyHQ();
    }

    public static int pack(MapLocation location) {
        return (location.x << 6) | location.y;
    }

    public static MapLocation unpack(int packed) {
        return new MapLocation((packed >> 6) & 0b111111, packed & 0b111111);
    }
}
