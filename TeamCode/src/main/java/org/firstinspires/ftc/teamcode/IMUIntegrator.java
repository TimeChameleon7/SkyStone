package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.NavUtil;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import static org.firstinspires.ftc.robotcore.external.navigation.NavUtil.*;

public class IMUIntegrator implements BNO055IMU.AccelerationIntegrator {
    private Acceleration acceleration;
    private Velocity velocity;
    private Position position;
    private Acceleration[] accelerations;
    private Acceleration baseline;
    private int index;

    public IMUIntegrator(int baselineSize) {
        if (baselineSize <= 0) throw new IllegalArgumentException("baselineSize must be > 0");
        accelerations = new Acceleration[baselineSize];
    }

    @Override
    public void initialize(BNO055IMU.Parameters parameters, Position initialPosition, Velocity initialVelocity) {
        this.position = initialPosition == null ? new Position(DistanceUnit.METER, 0, 0, 0, System.nanoTime()) : initialPosition;
        this.velocity = initialVelocity == null ? new Velocity(DistanceUnit.METER, 0, 0, 0, System.nanoTime()) : initialVelocity;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Velocity getVelocity() {
        return velocity;
    }

    @Override
    public Acceleration getAcceleration() {
        return acceleration;
    }

    @Override
    public void update(Acceleration currAccel) {
        if (baseline == null) {
            if (index == accelerations.length) {
                baseline = new Acceleration(DistanceUnit.METER, 0, 0, 0, 0);
                for (Acceleration acceleration : accelerations) {
                    baseline.xAccel += acceleration.xAccel;
                    baseline.yAccel += acceleration.yAccel;
                    baseline.zAccel += acceleration.zAccel;
                }
                baseline = divide(baseline, accelerations.length);
                acceleration = currAccel;
            } else {
                accelerations[index++] = currAccel;
            }
        } else {
            //subtract the baseline
            currAccel = minus(currAccel, baseline);
            //smooth the acceleration input
            currAccel = plus(acceleration, divide(minus(currAccel, acceleration), 1.2));

            Velocity velocDelta = meanIntegrate(currAccel, acceleration);
            acceleration = currAccel;

            Velocity velocPrev = velocity;
            velocity = plus(velocity, velocDelta);

            Position positDelta = meanIntegrate(velocity, velocPrev);
            position = plus(position, positDelta);
        }
    }

    private static Acceleration divide(Acceleration acceleration, double divisor) {
        return new Acceleration(acceleration.unit, acceleration.xAccel / divisor, acceleration.yAccel / divisor, acceleration.zAccel / divisor, acceleration.acquisitionTime);
    }
}
