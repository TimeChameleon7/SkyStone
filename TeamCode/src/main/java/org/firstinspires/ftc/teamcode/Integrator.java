package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.ArrayList;

import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.meanIntegrate;
import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.minus;
import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.plus;

public class Integrator {
    public Position position;
    public Velocity velocity;
    public Acceleration acceleration;
    private Acceleration accelFilter;
    private ArrayList<Acceleration> accelerations;

    public Integrator() {
        this.velocity = new Velocity(DistanceUnit.METER, 0, 0, 0, System.nanoTime());
        this.position = new Position(DistanceUnit.METER, 0, 0, 0, System.nanoTime());
        accelerations = new ArrayList<>();
    }

    public void update(float[] values) {
        Acceleration accel = new Acceleration(DistanceUnit.METER, values[0], values[1], values[2], System.nanoTime());
        if (accelFilter == null) {
            if (accelerations.size() < 100) {
                accelerations.add(accel);
            } else {
                float x = 0, y = 0, z = 0;
                for (Acceleration a : accelerations) {
                    x += a.xAccel;
                    y += a.yAccel;
                    z += a.zAccel;
                }
                x /= 100;
                y /= 100;
                z /= 100;
                accelFilter = new Acceleration(DistanceUnit.METER, x, y, z, 0);
            }
        } else {
            accel = minus(accel, accelFilter);
            accel = plus(acceleration, divide(minus(accel, acceleration), 1.2));
            if (acceleration != null) {
                Velocity velocDelta = meanIntegrate(accel, acceleration);
                acceleration = accel;

                Velocity velocPrev = velocity;
                velocity = plus(velocity, velocDelta);

                Position positDelta = meanIntegrate(velocity, velocPrev);
                position = plus(position, positDelta);
            } else {
                acceleration = accel;
            }
        }
    }

    private static Acceleration divide(Acceleration acceleration, double divisor) {
        return new Acceleration(acceleration.unit, acceleration.xAccel / divisor, acceleration.yAccel / divisor, acceleration.zAccel / divisor, acceleration.acquisitionTime);
    }
}
