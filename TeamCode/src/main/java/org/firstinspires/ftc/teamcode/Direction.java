package org.firstinspires.ftc.teamcode;

public enum Direction {
    FORWARD, REVERSE, LEFT, RIGHT;

    public Direction oppositeOf(Direction direction) {
        switch (direction) {
            case FORWARD:   return REVERSE;
            case REVERSE:   return FORWARD;
            case LEFT:      return RIGHT;
            case RIGHT:     return LEFT;
            default: return direction;
        }
    }
}
