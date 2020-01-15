package org.firstinspires.ftc.newteamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Direction;
import org.firstinspires.ftc.teamcode.SmoothingIntegrator;

public class Controller {

    //todo rotation decelerate when close

    /**
     * A low level BotController, alternatively the Controller class could
     * extend BotController, but this is not done to prevent accidental
     * incorrect method usage.
     */
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

    public void armUp(double seconds) {
        bot.armUp(seconds);
    }
    public void armDown(double seconds) {
        bot.armDown(seconds);
    }
    public void holdArmDown(double seconds) {
        bot.holdArmDown(seconds);
    }
    public void closeHand() {
        bot.closeHand();
    }
    public void openHand() {
        bot.openHand();
    }

    public TimeBasedMovements moveByTime() {return new TimeBasedMovements();}
    private class TimeBasedMovements {
        public TimeBasedMovements move(Direction direction, double seconds) {
            bot.move(direction, power);
            bot.sleep(seconds);
            bot.move(direction, 0);
            return this;
        }

        public TimeBasedMovements rotate(Direction direction, double seconds) {
            bot.rotate(direction, power);
            bot.sleep(seconds);
            bot.rotate(direction, 0);
            return this;
        }

        public TimeBasedMovements sleep(double seconds) {
            Controller.this.sleep(seconds);
            return this;
        }

        public TimeBasedMovements armUp(double seconds) {
            Controller.this.armUp(seconds);
            return this;
        }
        public TimeBasedMovements armDown(double seconds) {
            Controller.this.armDown(seconds);
            return this;
        }
        public TimeBasedMovements holdArmDown(double seconds) {
            Controller.this.holdArmDown(seconds);
            return this;
        }
        public TimeBasedMovements closeHand() {
            Controller.this.closeHand();
            return this;
        }
        public TimeBasedMovements openHand() {
            Controller.this.openHand();
            return this;
        }
    }
}
