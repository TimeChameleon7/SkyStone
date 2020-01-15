package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.NavUtil;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class SmoothingIntegrator implements BNO055IMU.AccelerationIntegrator {
    private Position position;
    private Velocity velocity;
    private Acceleration acceleration;
    private Acceleration accelSums;
    private int added;
    private final int averageOver;

    public SmoothingIntegrator(int averageOver) {
        if (averageOver <= 0) throw new IllegalArgumentException("averageOver must be a positive number.");
        this.averageOver = averageOver;
        accelSums = new Acceleration(DistanceUnit.METER, 0, 0, 0, 0);
        added = 0;
    }


    @Override
    public void initialize(BNO055IMU.Parameters parameters, Position initialPosition, Velocity initialVelocity) {
        this.position = new Position(DistanceUnit.METER, 0, 0, 0, 0);
        this.velocity = new Velocity(DistanceUnit.METER, 0, 0, 0, 0);
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
    public void update(Acceleration linearAcceleration) {
        if (added < averageOver) {
            accelSums = NavUtil.plus(accelSums, linearAcceleration);
            added++;
        } else {
            Acceleration accelPrev = acceleration;
            acceleration = new Acceleration(
                    DistanceUnit.METER,
                    accelSums.xAccel /= averageOver,
                    accelSums.yAccel /= averageOver,
                    accelSums.zAccel /= averageOver,
                    accelSums.acquisitionTime
            );
            accelSums = new Acceleration(DistanceUnit.METER, 0, 0, 0, 0);
            added = 0;

            if (accelPrev != null) {
                Velocity velocPrev = velocity;
                velocity = NavUtil.plus(velocity, NavUtil.meanIntegrate(acceleration, accelPrev));
                position = NavUtil.plus(position, NavUtil.meanIntegrate(velocity, velocPrev));
            }
        }
    }
}
