package sprintBot.util;

import battlecode.common.*;
import sprintBot.pathfinder.BFSCheckpoints;
import sprintBot.pathfinder.Checkpoints;

import static sprintBot.util.Constants.rc;

public class Communication {
    public static final int ALLY_HEADQUARTERS_LOCATIONS_OFFSET = 0; // 4 integers
    public static final int HEADQUARTERS_LOCATIONS_LOCATION_BIT = 0;
    public static final int HEADQUARTERS_LOCATIONS_LOCATION_MASK = 0b111111_111111; // 12 bits, 0 = non-existent, 1-3601 = coordinates of hq
    public static final int HEADQUARTERS_LOCATIONS_ENEMY_DIRECTION_BIT = 12;
    public static final int HEADQUARTERS_LOCATIONS_ENEMY_DIRECTION_MASK = 0b1111; // 1 bit is if there's an enemy, 3 bits representing the 8 ordinal directions
    // TODO: Carriers: do not go to deposit if close to enemy (or from wrong direction?)

    private static int headquartersSharedIndex = -1;
    public static MapLocation[] headquartersLocations;
    public static MapLocation[] enemyLocationsFromHeadquarters;

    // 3 resource types * 4 headquarters * 12 bits per well = 144 bits total = theoretically 9 integers
    public static final int WELL_LOCATIONS_OFFSET = 4; // 144 12 integers
    public static final int WELL_LOCATIONS_SET_BIT = 0;
    public static final int WELL_LOCATIONS_LOCATION_BIT = 1;
    public static final int WELL_LOCATIONS_LOCATION_MASK = 0b111111_111111; // 12 bits, 6 bit per coordinate

    public static final int ENEMY_HQ_INVALIDATIONS_OFFSET = 16;
    public static final int ENEMY_HQ_CONFIRMED_OFFSET = 17;
    // UNUSED: 18, 19, 20

    public static final int CARRIER_TASK_OFFSET = 21; // 16 integers
    public static final int CARRIER_TASK_POSITION_BIT_OFFSET = 0;
    public static final int CARRIER_TASK_POSITION_BIT_MASK = 0b1111111; // 7 bits
    public static final int CARRIER_TASK_HQ_ID_BIT_OFFSET = 7;
    public static final int CARRIER_TASK_HQ_ID_BIT_MASK = 0b11; // Max 4 HQs
    public static final int CARRIER_TASK_ID_BIT_OFFSET = 9;
    public static final int CARRIER_TASK_ID_BIT_MASK = 0b1111; // doesn't really matter for now
    public static final int CARRIER_TASK_NONE_ID = 0;
    public static final int CARRIER_TASK_ANCHOR_PICKUP_ID = 1;
    public static final int CARRIER_TASK_MINE_ADAMANTIUM_ID = 2;
    public static final int CARRIER_TASK_MINE_MANA_ID = 3;
    public static final int CARRIER_TASK_MINE_ELIXIR_ID = 4;

    public static final int MAX_CARRIER_COMMED_TASKS = 16;

    public static final int CHECKPOINTS_OFFSET = 37; // 19 ints
    public static final int CHECKPOINTS_PENDING_OFFSET = 56;
    public static final int CHECKPOINTS_PENDING_LENGTH = 8;

    public enum CarrierTaskType {
        NONE, PICKUP_ANCHOR, MINE_ADAMANTIUM, MINE_MANA, MINE_ELIXIR;
        public static int toId(CarrierTaskType type) {
            switch (type) {
                case NONE:
                    return CARRIER_TASK_NONE_ID;
                case PICKUP_ANCHOR:
                    return CARRIER_TASK_ANCHOR_PICKUP_ID;
                case MINE_ADAMANTIUM:
                    return CARRIER_TASK_MINE_ADAMANTIUM_ID;
                case MINE_MANA:
                    return CARRIER_TASK_MINE_MANA_ID;
                case MINE_ELIXIR:
                    return CARRIER_TASK_MINE_ELIXIR_ID;
                default:
                    Debug.failFast("Unknown Carrier Task: " + type);
                    return -1;
            }
        }
        public static CarrierTaskType fromId(int id) {
            switch (id) {
                case CARRIER_TASK_NONE_ID:
                    return NONE;
                case CARRIER_TASK_ANCHOR_PICKUP_ID:
                    return PICKUP_ANCHOR;
                case CARRIER_TASK_MINE_ADAMANTIUM_ID:
                    return MINE_ADAMANTIUM;
                case CARRIER_TASK_MINE_MANA_ID:
                    return MINE_MANA;
                case CARRIER_TASK_MINE_ELIXIR_ID:
                    return MINE_ELIXIR;
                default:
                    Debug.failFast("Unknown Carrier Task Id");
                    return null;
            }
        }
        public int id() {
            return toId(this);
        }
    }

    public static class CarrierTask {
        public CarrierTaskType type;
        public MapLocation hqLocation;
        public CarrierTask(MapLocation hqLocation, CarrierTaskType type) {
            this.hqLocation = hqLocation;
            this.type = type;
        }
        public static ResourceType getMineResourceType(CarrierTask task) {
            if (task == null) {
                return null;
            }
            switch (task.type) {
                case MINE_ADAMANTIUM:
                    return ResourceType.ADAMANTIUM;
                case MINE_MANA:
                    return ResourceType.MANA;
                case MINE_ELIXIR:
                    return ResourceType.ELIXIR;
            }
            return null;
        }
    }

    // should only be called by carriers
    public static CarrierTask getTaskAsCarrier() {
        CarrierTask bestTask = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        // locate all headquarters nearby
        MapLocation[] visibleHqLocations = new MapLocation[headquartersLocations.length];
        boolean hasVisibleHQLocation = false;
        for (int i = headquartersLocations.length; --i >= 0; ) {
            if (Cache.MY_LOCATION.isWithinDistanceSquared(headquartersLocations[i], RobotType.CARRIER.visionRadiusSquared)) {
                visibleHqLocations[i] = headquartersLocations[i];
                hasVisibleHQLocation = true;
            }
        }
        // loop
        if (!hasVisibleHQLocation) {
            return null;
        }
        for (int i = MAX_CARRIER_COMMED_TASKS; --i >= 0; ) {
            int commIndex = CARRIER_TASK_OFFSET + i;
            try {
                int message = rc.readSharedArray(commIndex);
                int hqIndex = (message >> CARRIER_TASK_HQ_ID_BIT_OFFSET) & CARRIER_TASK_HQ_ID_BIT_MASK;
                MapLocation hqLocation = visibleHqLocations[hqIndex];
                // if visible
                if (hqLocation != null) {
                    // read offset
                    int offset = (message >> CARRIER_TASK_POSITION_BIT_OFFSET) & CARRIER_TASK_POSITION_BIT_MASK;
                    if (offset == 0) {
                        // not possible
                        continue;
                    }
                    int offsetX = (offset / 9) - 4;
                    int offsetY = (offset % 9) - 4;
                    MapLocation referencedLocation = hqLocation.translate(offsetX, offsetY);
                    if (Cache.MY_LOCATION.equals(referencedLocation)) {
                        // get task
                        int task = (message >> CARRIER_TASK_ID_BIT_OFFSET) & CARRIER_TASK_ID_BIT_MASK;
                        int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(hqLocation);
                        if (distanceSquared < bestDistanceSquared) {
                            CarrierTaskType taskType = CarrierTaskType.fromId(task);
                            if (taskType != null) {
                                bestTask = new CarrierTask(hqLocation, taskType);
                                bestDistanceSquared = distanceSquared;
                            }
                        }
                    }
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return bestTask;
    }

    private static int[] taskWrittenIndices = new int[MAX_CARRIER_COMMED_TASKS];
    private static int numTasksWritten = 0;

    public static void clearOurTasks() {
        for (int i = numTasksWritten; --i >= 0; ) {
            int commIndex = CARRIER_TASK_OFFSET + taskWrittenIndices[i];
            tryWriteSharedIndex(commIndex, 0); // TODO-someday: may want to delay to save bytecodes?
        }
        numTasksWritten = 0;
    }

    public static boolean addTask(MapLocation location, int taskId) {
        if (Constants.DEBUG_FAIL_FAST) {
            if (!Cache.MY_LOCATION.isWithinDistanceSquared(location, RobotType.CARRIER.visionRadiusSquared)) {
                Debug.failFast("Should not add task to location this far out: " + location);
            }
            if (Constants.ROBOT_TYPE != RobotType.HEADQUARTERS) {
                Debug.failFast("Must be headquarters");
            }
            if ((taskId & CARRIER_TASK_ID_BIT_MASK) != taskId) {
                Debug.failFast("taskId too large: " + taskId);
            }
        }
        int offsetX = location.x - Cache.MY_LOCATION.x;
        int offsetY = location.y - Cache.MY_LOCATION.y;
        int packed = (offsetX + 4) * 9 + (offsetY + 4);
        int message = (packed << CARRIER_TASK_POSITION_BIT_OFFSET)
                | (headquartersSharedIndex << CARRIER_TASK_HQ_ID_BIT_OFFSET)
                | (taskId << CARRIER_TASK_ID_BIT_OFFSET);
        return writeTask(message);
    }

    public static boolean writeTask(int message) {
        for (int i = MAX_CARRIER_COMMED_TASKS; --i >= 0; ) {
            int commIndex = CARRIER_TASK_OFFSET + i;
            try {
                int existingMessage = rc.readSharedArray(commIndex);
                int taskId = (existingMessage >> CARRIER_TASK_ID_BIT_OFFSET) & CARRIER_TASK_ID_BIT_MASK;
                if (taskId == CARRIER_TASK_NONE_ID) {
                    // we can use this index
                    tryWriteSharedIndex(commIndex, message);
                    taskWrittenIndices[numTasksWritten] = i;
                    numTasksWritten++;
                    return true;
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        Debug.println("Warning: ran out of carrier task bandwidth");
        return false;
    }

    public static boolean tryWriteSharedIndex(int index, int message) {
        if (rc.canWriteSharedArray(index, message)) {
            try {
                rc.writeSharedArray(index, message);
                return true;
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
        return false;
    }

    public static void init() throws GameActionException {
        // Set Headquarters location
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            // Find next available slot
            for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
                int sharedArrayIndex = ALLY_HEADQUARTERS_LOCATIONS_OFFSET + i;
                if (rc.readSharedArray(sharedArrayIndex) == 0) {
                    headquartersSharedIndex = sharedArrayIndex;
                    break;
                }
            }
            if (headquartersSharedIndex == -1) {
                Debug.failFast("-1 headquarters index");
            }
        }
        Checkpoints.init();
    }

    public static void loop() throws GameActionException {
        if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
            if (headquartersSharedIndex != -1) {
                RobotInfo enemy = Util.getClosestEnemyRobot(r -> Util.isAttacker(r.type));
                if (enemy == null) {
                    // Broadcast headquarter location
                    rc.writeSharedArray(headquartersSharedIndex,
                            (((Cache.MY_LOCATION.x * Constants.MAX_MAP_SIZE + Cache.MY_LOCATION.y) + 1) << HEADQUARTERS_LOCATIONS_LOCATION_BIT));
                } else {
                    MapLocation enemyLocation = enemy.location;
                    Direction enemyDirection = Cache.MY_LOCATION.directionTo(enemyLocation);
                    if (enemyDirection == Direction.CENTER) {
                        Debug.failFast("Center enemy direction?");
                    } else {
                        // Broadcast headquarter location AND enemyDirection
                        rc.writeSharedArray(headquartersSharedIndex,
                                (((Cache.MY_LOCATION.x * Constants.MAX_MAP_SIZE + Cache.MY_LOCATION.y) + 1) << HEADQUARTERS_LOCATIONS_LOCATION_BIT)
                                        | ((enemyDirection.ordinal() + 1) << HEADQUARTERS_LOCATIONS_ENEMY_DIRECTION_BIT));
                    }
                }
            }
            clearOurTasks();
        }
        if (Constants.ROBOT_TYPE != RobotType.HEADQUARTERS || Cache.TURN_COUNT > 1) {
            // Initialize Arrays
            if (headquartersLocations == null) {
                boolean initialized = false;
                for (int i = GameConstants.MAX_STARTING_HEADQUARTERS; --i >= 0; ) {
                    int value = rc.readSharedArray(i);
                    if (value != 0) {
                        if (!initialized) {
                            headquartersLocations = new MapLocation[i + 1];
                            enemyLocationsFromHeadquarters = new MapLocation[i + 1];
                            initialized = true;
                        }
                    }
                }
                if (!initialized) {
                    Debug.failFast("Cannot read any headquarters locations");
                }
            }
            if (headquartersLocations != null) {
                // Read headquarters locations and enemy directions
                for (int i = GameConstants.MAX_STARTING_HEADQUARTERS; --i >= 0; ) {
                    int value = rc.readSharedArray(i);
                    int location = ((value >> HEADQUARTERS_LOCATIONS_LOCATION_BIT) & HEADQUARTERS_LOCATIONS_LOCATION_MASK) - 1;
                    if (location >= 0) {
                        // valid hq
                        headquartersLocations[i] = new MapLocation(location / Constants.MAX_MAP_SIZE, location % Constants.MAX_MAP_SIZE);
                        try {
                            int directionIndex = ((value >> HEADQUARTERS_LOCATIONS_ENEMY_DIRECTION_BIT) & HEADQUARTERS_LOCATIONS_ENEMY_DIRECTION_MASK) - 1;
                            if (directionIndex == -1) {
                                enemyLocationsFromHeadquarters[i] = null;
                            } else {
                                Direction direction = Constants.ORDINAL_DIRECTIONS[directionIndex];
                                // we will project it approximately 16 distance squared
                                if (Util.isStraightDirection(direction)) {
                                    enemyLocationsFromHeadquarters[i] = headquartersLocations[i].add(direction).add(direction).add(direction).add(direction);
                                } else {
                                    enemyLocationsFromHeadquarters[i] = headquartersLocations[i].add(direction).add(direction).add(direction);
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            enemyLocationsFromHeadquarters[i] = null;
                            Debug.failFast("Out of bounds");
                        }
                    }
                }

            }
        }
        // Update enemy hqs from comms
        EnemyHqGuesser.update();
        WellTracker.update();
        //Checkpoints.update();
        //BFSCheckpoints.debug_render();
    }

    public static int pack(MapLocation location) {
        return (location.x << 6) | location.y;
    }

    public static MapLocation unpack(int packed) {
        return new MapLocation((packed >> 6) & 0b111111, packed & 0b111111);
    }
}
