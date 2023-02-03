package finalBot.util;

import battlecode.common.*;

import static finalBot.util.Constants.rc;

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

    public static final int CARRIER_TASK_OFFSET = 18; // 16 integers
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

    // Ally Island Locations - last round seen? ehhh might not be worth

    // enemy attacker locations: 0 = no enemy, 1-3601 = location -> 12 bits, 4 bits = turn number % 16 - expires in the last 4 turns? to be removed by HQs
    // we also don't allow locations to be added within 5 distance squared from another

    private static final int ENEMY_LOCATIONS_OFFSET = 34; // 10 ints?
    private static final int ENEMY_LOCATIONS_LOCATION_BIT = 0;
    private static final int ENEMY_LOCATIONS_LOCATION_MASK = 0b111111_111111; // 12 bits
    private static final int ENEMY_LOCATIONS_TURN_BIT = 12;
    private static final int ENEMY_LOCATIONS_TURN_MASK = 0b1111; // 4 bits
    private static final int ENEMY_LOCATIONS_TURN_NUMBER_MOD = 16;

    private static final int MAX_ENEMY_LOCATIONS = 10;
    private static final MapLocation[] commedEnemyLocations = new MapLocation[MAX_ENEMY_LOCATIONS];

    // pending enemy locations and their rounds
    private static MapLocation[] pendingEnemyLocations = new MapLocation[MAX_ENEMY_LOCATIONS];
    private static int[] pendingEnemyLocationsWrite = new int[MAX_ENEMY_LOCATIONS];
    private static int[] pendingEnemyLocationRounds = new int[MAX_ENEMY_LOCATIONS];
    private static int pendingEnemyLocationsIndex = 0;
    private static int pendingEnemyLocationsSize = 0;

    private static int ENEMY_LOCATIONS_EXPIRY = 12; // lasts 12 rounds

    public static void addPendingEnemyLocation(MapLocation location) {
        int turn = rc.getRoundNum() % ENEMY_LOCATIONS_TURN_NUMBER_MOD;
        int write = ((((location.x << 6) | location.y) + 1) << ENEMY_LOCATIONS_LOCATION_BIT) | (turn << ENEMY_LOCATIONS_TURN_BIT);
        if (pendingEnemyLocationsSize >= MAX_ENEMY_LOCATIONS) {
            int index = (pendingEnemyLocationsIndex++) % MAX_ENEMY_LOCATIONS;
            pendingEnemyLocations[index] = location;
            pendingEnemyLocationsWrite[index] = write;
            pendingEnemyLocationRounds[index] = rc.getRoundNum();
        } else {
            int index = (pendingEnemyLocationsIndex + pendingEnemyLocationsSize++) % MAX_ENEMY_LOCATIONS;
            pendingEnemyLocations[index] = location;
            pendingEnemyLocationsWrite[index] = write;
            pendingEnemyLocationRounds[index] = rc.getRoundNum();
        }
    }

    // assumes readCommedEnemyLocations() was called earlier this turn
    public static void writePendingEnemyLocationsToComms() {
        if (rc.canWriteSharedArray(0, 0)) {
            int roundNum = rc.getRoundNum();
            // write from most recent to least recent
            for (int i = pendingEnemyLocationsSize; --i >= 0; ) {
                int index = (pendingEnemyLocationsIndex + i) % MAX_ENEMY_LOCATIONS;
                MapLocation pendingLocation = pendingEnemyLocations[index];
                int pendingWrite = pendingEnemyLocationsWrite[index];
                int pendingRound = pendingEnemyLocationRounds[index];
                if (pendingRound < roundNum - ENEMY_LOCATIONS_EXPIRY) {
                    continue;
                }
                // check if any are already equal
                boolean needsWrite = true;
                int emptyCommIndex = -1;
                for (int j = MAX_ENEMY_LOCATIONS; --j >= 0; ) {
                    int commIndex = ENEMY_LOCATIONS_OFFSET + j;
                    MapLocation commedEnemyLocation = commedEnemyLocations[j];
                    if (commedEnemyLocation == null) {
                        emptyCommIndex = commIndex;
                    } else if (pendingLocation.equals(commedEnemyLocation)) {
                        // we found a duplicate - we can just renew this
                        try {
                            rc.writeSharedArray(commIndex, pendingWrite);
                        } catch (GameActionException ex) {
                            Debug.failFast(ex);
                        }
                        needsWrite = false;
                    }
                }
                // find empty slot to write
                if (needsWrite && emptyCommIndex > 0) {
                    try {
                        rc.writeSharedArray(emptyCommIndex, pendingWrite);
                    } catch (GameActionException ex) {
                        Debug.failFast(ex);
                    }
                }
            }
            // clear
            pendingEnemyLocationsSize = 0;
        }
    }

    public static void readCommedEnemyLocations() {
        int roundNum = rc.getRoundNum();
        for (int j = MAX_ENEMY_LOCATIONS; --j >= 0; ) {
            int commIndex = ENEMY_LOCATIONS_OFFSET + j;
            try {
                int read = rc.readSharedArray(commIndex);
                int locationRead = ((read >> ENEMY_LOCATIONS_LOCATION_BIT) & ENEMY_LOCATIONS_LOCATION_MASK) - 1;
                if (locationRead == -1) {
                    commedEnemyLocations[j] = null;
                } else {
                    commedEnemyLocations[j] = new MapLocation(locationRead >> 6, locationRead & 0b111_111);
                    if (Constants.ROBOT_TYPE == RobotType.HEADQUARTERS) {
                        // Consider clearing stale
                        int enemyLocationRoundNum = (read >> ENEMY_LOCATIONS_TURN_BIT) & ENEMY_LOCATIONS_TURN_MASK;
                        int diff = (roundNum + ENEMY_LOCATIONS_TURN_NUMBER_MOD - enemyLocationRoundNum) % ENEMY_LOCATIONS_TURN_NUMBER_MOD;
                        if (diff > 12) {
                            // clear stale
                            commedEnemyLocations[j] = null;
                            rc.writeSharedArray(commIndex, 0);
                        }
                    }
                }
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
    }

    public static MapLocation getClosestCommedEnemyLocation() {
        int bestDistanceSquared = Integer.MAX_VALUE;
        MapLocation bestLocation = null;
        for (int i = MAX_ENEMY_LOCATIONS; --i >= 0; ){
            MapLocation location = commedEnemyLocations[i];
            if (location != null) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        return bestLocation;
    }

    // island locations: 0 = no island, 1-36 = island id -> 6 bits, location represented with 10 bits
    private static final int ALLY_ISLANDS_OFFSET = 44; // 5

    private static final int ENEMY_ISLANDS_OFFSET = 49; // 5
    
    private static final int NEUTRAL_ISLANDS_OFFSET = 54; // 10

    private static final int NUM_COMMED_ALLY_ISLANDS = 5;
    private static final int NUM_COMMED_ENEMY_ISLANDS = 5;
    private static final int NUM_COMMED_NEUTRAL_ISLANDS = 10;

    private static final int ISLAND_INDEX_BIT = 0;
    private static final int ISLAND_INDEX_MASK = 6;
    private static final int ISLAND_LOCATION_BIT = 6;
    private static final int ISLAND_LOCATION_MASK = 0b11111_11111; // 10 bits
    private static final int ISLAND_LOCATION_SCALE = 2;

    // TODO: assumes that one can write to shared array
    public static void addIslandToComm(int islandIndex, MapLocation location, Team team) {
        int write = (islandIndex << ISLAND_INDEX_BIT) | (((location.x / ISLAND_LOCATION_SCALE) << 5) | (location.y / ISLAND_LOCATION_SCALE) << ISLAND_LOCATION_BIT);
        int offset;
        int max;
        if (team == Team.NEUTRAL) {
            offset = NEUTRAL_ISLANDS_OFFSET;
            max = NUM_COMMED_NEUTRAL_ISLANDS;
        } else if (team == Constants.ALLY_TEAM) {
            offset = ALLY_ISLANDS_OFFSET;
            max = NUM_COMMED_ALLY_ISLANDS;
        } else {
            offset = ENEMY_ISLANDS_OFFSET;
            max = NUM_COMMED_ENEMY_ISLANDS;
        }
        int indexToWrite = -1;
        for (int i = max; --i >= 0; ) {
            // check if islandIndex already exists in the comm array
            try {
                int read = rc.readSharedArray(offset + i);
                int readIslandIndex = (read >> ISLAND_INDEX_BIT) & ISLAND_INDEX_MASK;
                if (readIslandIndex == islandIndex) {
                    indexToWrite = -2;
                } else if (indexToWrite == -1 && readIslandIndex == 0) {
                    // empty
                    indexToWrite = offset + i;
                }
            } catch (GameActionException e) {
                e.printStackTrace();
            }
        }
        if (indexToWrite > 0) {
            try {
                rc.writeSharedArray(indexToWrite, write);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
    }

    public static void readCommedIslands() {
        // read and mark to IslandTracker
        for (int i = NUM_COMMED_ALLY_ISLANDS; --i >= 0; ) {
            try {
                int read = rc.readSharedArray(ALLY_ISLANDS_OFFSET + i);
                int islandIndex = (read >> ISLAND_INDEX_BIT) & ISLAND_INDEX_MASK;
                int readIslandLocation = (read >> ISLAND_LOCATION_BIT) & ISLAND_LOCATION_MASK;
                MapLocation islandLocation = new MapLocation((readIslandLocation >> 5) * ISLAND_LOCATION_SCALE, (readIslandLocation & 0b11111) * ISLAND_LOCATION_SCALE);
                IslandTracker.addIsland(islandIndex, islandLocation, Constants.ALLY_TEAM);
            } catch (GameActionException ex) {
                Debug.failFast(ex);
            }
        }
    }


//    public static final int CHECKPOINTS_OFFSET = 37; // 19 ints
//    public static final int CHECKPOINTS_PENDING_OFFSET = 56;
//    public static final int CHECKPOINTS_PENDING_LENGTH = 8;

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
//        Checkpoints.init();
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
                                    int x = Math.max(0, Math.min(Constants.MAP_WIDTH - 1, headquartersLocations[i].x + direction.dx * 4));
                                    int y = Math.max(0, Math.min(Constants.MAP_HEIGHT - 1, headquartersLocations[i].y + direction.dy * 4));
                                    enemyLocationsFromHeadquarters[i] = new MapLocation(x, y);
                                } else {
                                    int x = Math.max(0, Math.min(Constants.MAP_WIDTH - 1, headquartersLocations[i].x + direction.dx * 3));
                                    int y = Math.max(0, Math.min(Constants.MAP_HEIGHT - 1, headquartersLocations[i].y + direction.dy * 3));
                                    enemyLocationsFromHeadquarters[i] = new MapLocation(x, y);
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
        if (Constants.ROBOT_TYPE != RobotType.LAUNCHER) {
            WellTracker.update();
        }
        //Checkpoints.update();
        //BFSCheckpoints.debug_render();

        readCommedEnemyLocations();
        debug_render();
    }

    public static void debug_render() {
        if (Profile.ATTACKING.enabled()) {
            for (int i = MAX_ENEMY_LOCATIONS; --i >= 0; ){
                MapLocation enemyLocation = commedEnemyLocations[i];
                if (enemyLocation != null) {
                    Debug.setIndicatorDot(Profile.ATTACKING, enemyLocation, 255, 0, 0);
                }
            }
        }
    }

    public static void postLoop() {
        if (Constants.ROBOT_TYPE != RobotType.HEADQUARTERS) {
            // headquarters has its own write system - no need for clogging enemyLocations up
            int numWritesLeftThisTurn = 3; // limit the number of enemies we can transmit
            for (int i = Cache.ENEMY_ROBOTS.length; --i >= 0 && numWritesLeftThisTurn > 0; ) {
                RobotInfo enemy = Cache.ENEMY_ROBOTS[i];
                if (Util.isAttacker(enemy.type)) {
                    addPendingEnemyLocation(enemy.location);
                    numWritesLeftThisTurn--;
                }
            }
        }
        writePendingEnemyLocationsToComms();
    }

    public static int pack(MapLocation location) {
        return (location.x << 6) | location.y;
    }

    public static MapLocation unpack(int packed) {
        return new MapLocation((packed >> 6) & 0b111111, packed & 0b111111);
    }
}
