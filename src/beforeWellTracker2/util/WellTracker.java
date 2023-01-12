package beforeWellTracker2.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.ResourceType;
import battlecode.common.WellInfo;

import java.util.function.Consumer;

import static beforeWellTracker2.util.Constants.rc;

public class WellTracker {
    private static final int NUM_WELLS_TRACKED = 12;

    public static MapLocation[] knownWells = new MapLocation[NUM_WELLS_TRACKED];
    public static MapLocation[] pendingWells = new MapLocation[NUM_WELLS_TRACKED];

    public static void forEachKnown(Consumer<MapLocation> consumer) {
        for (int i = NUM_WELLS_TRACKED; --i >= 0; ) {
            MapLocation location = knownWells[i];
            if (location != null) {
                consumer.accept(location);
            }
        }
    }

    public static void forEachPending(Consumer<MapLocation> consumer) {
        for (int i = NUM_WELLS_TRACKED; --i >= 0; ) {
            MapLocation location = pendingWells[i];
            if (location != null) {
                consumer.accept(location);
            }
        }
    }

    public static MapLocation getClosestKnownWell(ResourceType type) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        // look at wells in our vision radius
        WellInfo[] visionWells = rc.senseNearbyWells(type);
        for (int i = visionWells.length; --i >= 0; ) {
            MapLocation location = visionWells[i].getMapLocation();
            int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestLocation = location;
            }
        }
        if (bestLocation == null) {
            // read from known and read from pending
            int startingIndex = 0;
            switch (type) {
                case ADAMANTIUM:
                    // 0, 3, 6, 9
                    startingIndex = 0;
                    break;
                case MANA:
                    // 1, 4, 7, 10
                    startingIndex = 1;
                    break;
                case ELIXIR:
                    // 2, 5, 8, 11
                    startingIndex = 2;
                    break;
            }
            for (int i = startingIndex; i < NUM_WELLS_TRACKED; i += 3) {
                MapLocation known = knownWells[i];
                if (known != null) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(known);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = known;
                    }
                }
                MapLocation pending = pendingWells[i];
                if (pending != null) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(pending);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = known;
                    }
                }
            }
        }
        return bestLocation;
    }

    public static ResourceType getExpectedType(int index) {
        switch (index % 3) {
            case 0:
                return ResourceType.ADAMANTIUM;
            case 1:
                return ResourceType.MANA;
            case 2:
                return ResourceType.ELIXIR;
        }
        Debug.failFast("Unknown index: " + index);
        return ResourceType.NO_RESOURCE;
    }

    public static MapLocation getHQLocation(int index) {
        int hqIndex = index / 4;
        if (hqIndex >= Communication.headquartersLocations.length) {
            return null;
        }
        return Communication.headquartersLocations[index / 4];
    }

    public static void update() throws GameActionException {
        if (Communication.headquartersLocations == null) {
            return;
        }
        WellInfo[] adamantiumWells = rc.senseNearbyWells(ResourceType.ADAMANTIUM);
        WellInfo[] manaWells = rc.senseNearbyWells(ResourceType.MANA);
        WellInfo[] elixirWells = rc.senseNearbyWells(ResourceType.ELIXIR);

        for (int i = NUM_WELLS_TRACKED; --i >= 0; ) {
            int commIndex = Communication.WELL_LOCATIONS_OFFSET + i;
            MapLocation hqLocation = getHQLocation(i);
            if (hqLocation == null) {
                continue;
            }
            ResourceType expectedType = getExpectedType(i);
            // read from comms
            int message = rc.readSharedArray(commIndex);
            // read known wells
            MapLocation knownLocation = (((message >> Communication.WELL_LOCATIONS_SET_BIT) & 0b1) == 0) ? null :
                    Communication.unpack((message >> Communication.WELL_LOCATIONS_LOCATION_BIT) & Communication.WELL_LOCATIONS_LOCATION_MASK);
            MapLocation oldLocation = knownLocation;
            // remove invalid known wells
            if (knownLocation != null && rc.canSenseLocation(knownLocation)) {
                WellInfo well = rc.senseWell(knownLocation);
                if (well == null || well.getResourceType() != expectedType) {
                    // remove this well
                    knownLocation = null;
                }
            }
            knownWells[i] = knownLocation;
            // remove invalid from pending
            MapLocation pendingLocation = pendingWells[i];
            if (pendingLocation != null && rc.canSenseLocation(pendingLocation)) {
                WellInfo well = rc.senseWell(pendingLocation);
                if (well == null || well.getResourceType() != expectedType) {
                    // remove this well
                    pendingLocation = null;
                    pendingWells[i] = null;
                }
            }
            // check if pending is better than existing
            int knownDistanceSquared = knownLocation == null ? Integer.MAX_VALUE : hqLocation.distanceSquaredTo(knownLocation);
            int pendingDistanceSquared = pendingLocation == null ? Integer.MAX_VALUE : hqLocation.distanceSquaredTo(pendingLocation);
            if (pendingDistanceSquared > knownDistanceSquared) {
                pendingLocation = null;
            }
            // add to pending
            WellInfo[] wells; // fetch relevant wells
            switch (expectedType) {
                case ADAMANTIUM:
                    wells = adamantiumWells;
                    break;
                case MANA:
                    wells = manaWells;
                    break;
                case ELIXIR:
                    wells = elixirWells;
                    break;
                default:
                    Debug.failFast("Unknown expected type: " + expectedType);
                    wells = adamantiumWells;
            }
            int bestDistanceSquared = Math.min(knownDistanceSquared, pendingDistanceSquared);
            for (int j = wells.length; --j >= 0; ) {
                WellInfo well = wells[j];
                MapLocation location = well.getMapLocation();
                int distanceSquared = hqLocation.distanceSquaredTo(location);
                // check if better than known and pending
                if (distanceSquared < bestDistanceSquared) {
                    pendingLocation = location;
                    bestDistanceSquared = distanceSquared;
                }
            }
            // write pending to comms
            if (pendingLocation != null) {
                if (rc.canWriteSharedArray(0, 0)) {
                    if (!pendingLocation.equals(oldLocation)) {
                        rc.writeSharedArray(commIndex, (Communication.pack(pendingLocation) << Communication.WELL_LOCATIONS_LOCATION_BIT) | (1 << Communication.WELL_LOCATIONS_SET_BIT));
                        knownWells[i] = pendingLocation;
                        pendingLocation = null;
                    }
                }
            }
            pendingWells[i] = pendingLocation;
        }
    }

}
