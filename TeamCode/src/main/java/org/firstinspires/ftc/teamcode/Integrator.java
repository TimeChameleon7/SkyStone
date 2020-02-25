package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.meanIntegrate;
import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.plus;

public class Integrator {
    public Position position;
    public Velocity velocity;
    public Acceleration acceleration;

    public Integrator() {
        this.velocity = new Velocity(DistanceUnit.METER, 0, 0, 0, System.currentTimeMillis());
        this.position = new Position(DistanceUnit.METER, 0, 0, 0, System.currentTimeMillis());
    }

    public void update(float[] values) {
        Acceleration accel = new Acceleration(DistanceUnit.METER, values[0], values[1], values[2], System.currentTimeMillis());
        if (acceleration != null) {
            Acceleration accelPrev = acceleration;

            Velocity velocDelta = meanIntegrate(accel, accelPrev);
            Velocity velocPrev = velocity;
            velocity = plus(velocity, velocDelta);

            Position positDelta = meanIntegrate(velocity, velocPrev);
            position = plus(position, positDelta);
        } else {
            acceleration = accel;
        }
    }
}
