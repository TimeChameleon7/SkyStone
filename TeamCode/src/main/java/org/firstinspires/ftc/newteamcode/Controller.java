package org.firstinspires.ftc.newteamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.SmoothingIntegrator;

public class Controller {

    //todo rotation decelerate when close

    private final BotController bot;
    private final BNO055IMU imu;
    private double power;
    private float rotateAccuracy;
    private boolean flipped;

    public Controller(LinearOpMode mode, boolean useSensors) {
        bot = new BotController(mode.hardwareMap);

        if (useSensors) {
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
            parameters.accelerationIntegrationAlgorithm = new SmoothingIntegrator(5);
            imu = BotController.get(mode.hardwareMap, "imu");
            imu.initialize(parameters);
            imu.startAccelerationIntegration(null, null, 1);
        } else {
            imu = null;
        }
    }

    public Controller sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }
}
