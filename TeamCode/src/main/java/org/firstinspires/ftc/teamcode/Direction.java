package org.firstinspires.ftc.teamcode;

public enum Direction {
    FORWARD, REVERSE, LEFT, RIGHT;

    public Direction opposite() {
        switch (this) {
            case FORWARD:   return REVERSE;
            case REVERSE:   return FORWARD;
            case LEFT:      return RIGHT;
            case RIGHT:     return LEFT;
            default: return this;
        }
    }

    public boolean isXAxis() {
        return this == LEFT || this == RIGHT;
    }
}
