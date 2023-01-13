package noCheckerboardNoAdjacent.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static noCheckerboardNoAdjacent.util.Constants.rc;

public class EnemyHqGuesser {
    private static boolean initialized = false;
    private static Node head = null;
    private static Node tail = null;

    public static void generateHQGuessList() {
        MapLocation[] hqLocations = Communication.headquartersLocations;
        for (int i = hqLocations.length; --i >= 0; ) {
            guessEnemyArchonLocations(hqLocations[i]);
        }
        // traverse and remove any that are the same location as our hqs
        Node node = head;
        while (node != null) {
            MapLocation location = node.location;
            for (int i = hqLocations.length; --i >= 0; ) {
                if (location.equals(hqLocations[i])) {
                    removeEnemyHeadquartersLocationGuess(node);
                    break;
                }
            }
            node = node.next;
        }
        initialized = true;
    }

    public static void guessEnemyArchonLocations(MapLocation location) {
        int x = location.x;
        int y = location.y;
        int symX = Constants.MAP_WIDTH - x - 1;
        int symY = Constants.MAP_HEIGHT - y - 1;
        addEnemyHeadquartersLocationGuess(new MapLocation(x, symY));
        addEnemyHeadquartersLocationGuess(new MapLocation(symX, y));
        addEnemyHeadquartersLocationGuess(new MapLocation(symX, symY));
    }

    public static void addEnemyHeadquartersLocationGuess(MapLocation location) {
        // add to linked list
        Node node = new Node(location);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public static void removeEnemyHeadquartersLocationGuess(Node node) {
        // this code makes me very sad
        if (head == node) {
            head = head.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
        } else {
            node.prev.next = node.next;
            if (tail == node) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        }
    }

    public static void update() {
        if (!initialized) {
            return;
        }
        // traverse and remove any that are visible and not there
        Node node = head;
        while (node != null) {
            // check if it's visible
            MapLocation location = node.location;
            if (rc.canSenseLocation(location)) {
                try {
                    RobotInfo robot = rc.senseRobotAtLocation(location);
                    boolean hasEnemyHeadquarters = robot != null && robot.type == RobotType.HEADQUARTERS && robot.team == Constants.ENEMY_TEAM;
                    if (!hasEnemyHeadquarters) {
                        removeEnemyHeadquartersLocationGuess(node);
                    }
                } catch (GameActionException ex) {
                    Debug.failFast(ex);
                }
            }
            node = node.next;
        }
    }

    public static MapLocation getClosest() {
        return getClosest(location -> true);
    }
    public static MapLocation getClosest(Predicate<MapLocation> predicate) {
        MapLocation bestLocation = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        Node node = head;
        while (node != null) {
            MapLocation location = node.location;
            if (predicate.test(location)) {
                int distanceSquared = Cache.MY_LOCATION.distanceSquaredTo(location);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    bestLocation = location;
                }
            }
            node = node.next;
        }
        return bestLocation;
    }

    public static void forEach(Consumer<MapLocation> consumer) {
        Node node = head;
        while (node != null) {
            MapLocation location = node.location;
            consumer.accept(location);
            node = node.next;
        }
    }

    static class Node {
        Node prev;
        Node next;
        MapLocation location;

        public Node(MapLocation location) {
            this.next = null;
            this.location = location;
        }
    }
}
