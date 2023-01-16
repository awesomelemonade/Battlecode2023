package sprintTesting5.util;

import battlecode.common.*;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static sprintTesting5.util.Constants.rc;

public class WellTracker {
    private static final int NUM_WELLS_TRACKED = 12;

    public static MapLocation[] knownWells = new MapLocation[NUM_WELLS_TRACKED];
    public static MapLocation[] pendingWells = new MapLocation[NUM_WELLS_TRACKED];

    private static int lastHqIndex = -1;

    public static MapLocation lastHqLocation() {
        if (lastHqIndex == -1) {
            return null;
        } else {
            return Communication.headquartersLocations[lastHqIndex];
        }
    }

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

    public static MapLocation getClosestKnownWell(Predicate<MapLocation> predicate) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        // look at wells in our vision radius
        WellInfo[] visionWells = rc.senseNearbyWells();
        for (int i = visionWells.length; --i >= 0; ) {
            MapLocation location = visionWells[i].getMapLocation();
            if (predicate.test(location)) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        for (int i = NUM_WELLS_TRACKED; --i >= 0; ) {
            if (lastHqIndex != -1 && lastHqIndex != i / 3) {
                continue;
            }
            MapLocation known = knownWells[i];
            if (known != null && predicate.test(known)) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(known);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = known;
                }
            }
            MapLocation pending = pendingWells[i];
            if (pending != null && predicate.test(pending)) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(pending);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = pending;
                }
            }
        }
        return bestLocation;
    }

    public static MapLocation getClosestKnownWell(ResourceType type, Predicate<MapLocation> predicate) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        // look at wells in our vision radius
        WellInfo[] visionWells = rc.senseNearbyWells(type);
        for (int i = visionWells.length; --i >= 0; ) {
            MapLocation location = visionWells[i].getMapLocation();
            if (predicate.test(location)) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
        }
        if (bestLocation == null) {
            // read from known and read from pending
            int startingIndex = getResourceIndex(type);
            for (int i = startingIndex; i < NUM_WELLS_TRACKED; i += 3) {
                if (lastHqIndex != -1 && lastHqIndex != i / 3) {
                    continue;
                }
                MapLocation known = knownWells[i];
                if (known != null && predicate.test(known)) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(known);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = known;
                    }
                }
                MapLocation pending = pendingWells[i];
                if (pending != null && predicate.test(pending)) {
                    int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(pending);
                    if (distanceSquared < bestDistanceSquared) {
                        bestDistanceSquared = distanceSquared;
                        bestLocation = pending;
                    }
                }
            }
        }
        return bestLocation;
    }

    public static int getResourceIndex(ResourceType type) {
        switch (type) {
            case ADAMANTIUM:
                return 0;
            case MANA:
                return 1;
            case ELIXIR:
                return 2;
            default:
                Debug.failFast("Unknown resource: " + type);
                return -1;
        }
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
        int hqIndex = index / 3;
        if (hqIndex >= Communication.headquartersLocations.length) {
            return null;
        }
        return Communication.headquartersLocations[hqIndex];
    }

    public static void update() throws GameActionException {
        if (Communication.headquartersLocations == null) {
            return;
        }
        {
            // update lastHqIndex
            int bestIndex = -1;
            int bestDistanceSquared = RobotType.HEADQUARTERS.actionRadiusSquared + 1; // add 1 for inclusive
            for (int i = Communication.headquartersLocations.length; --i >= 0; ) {
                MapLocation location = Communication.headquartersLocations[i];
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestIndex = i;
                }
            }
            if (bestIndex != -1) {
                lastHqIndex = bestIndex;
            }
        }

        WellInfo[] adamantiumWells;
        WellInfo[] manaWells;
        WellInfo[] elixirWells;
        if (Cache.TURN_COUNT == 1) {
            // save bytecodes - don't register new wells
            adamantiumWells = new WellInfo[0];
            manaWells = new WellInfo[0];
            elixirWells = new WellInfo[0];
        } else {
            if (Constants.ROBOT_TYPE == RobotType.CARRIER) {
                adamantiumWells = rc.senseNearbyWells(2, ResourceType.ADAMANTIUM);
                manaWells = rc.senseNearbyWells(2, ResourceType.MANA);
                elixirWells = rc.senseNearbyWells(2, ResourceType.ELIXIR);
            } else {
                adamantiumWells = Cache.ADAMANTIUM_WELLS;
                manaWells = Cache.MANA_WELLS;
                elixirWells = Cache.ELIXIR_WELLS;
                // TODO: we want to be careful to not include wells that are not accessible from lastHqIndex
            }
        }

        for (int i = NUM_WELLS_TRACKED; --i >= 0; ) {
            int commIndex = Communication.WELL_LOCATIONS_OFFSET + i;
            int hqIndex = i / 3;
            if (lastHqIndex != -1 && lastHqIndex != hqIndex) {
                continue;
            }
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
            if (hqIndex == lastHqIndex) {
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
                        wells = new WellInfo[0];
                }
            } else {
                wells = new WellInfo[0];
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
