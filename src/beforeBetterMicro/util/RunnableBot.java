package beforeBetterMicro.util;

import battlecode.common.GameActionException;

public interface RunnableBot {
    public void init() throws GameActionException;
    public void loop() throws GameActionException;
    public void move() throws GameActionException;
    public void action() throws GameActionException;

    public default void postLoop() throws GameActionException {}
}