package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class IMUIntegrator implements BNO055IMU.AccelerationIntegrator {
    private Acceleration acceleration;
    private Velocity velocity;
    private Position position;
    private boolean baselined;
    private Acceleration[] accelerations;
    private Acceleration baseline;
    private int baselineSize;
    private int index;

    public IMUIntegrator(int baselineSize) {
        if (baselineSize <= 0) throw new IllegalArgumentException("baselineSize must be > 0");
        this.baselineSize = baselineSize;
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
    public void update(Acceleration linearAcceleration) {

    }
}
