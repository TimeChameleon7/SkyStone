package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class Controller {

    /**
     * A low level BotController, alternatively the Controller class could
     * extend BotController, but this is not done to prevent accidental
     * incorrect method usage.
     */
    private final BotController bot;
    private final BNO055IMU imu;
    private final LinearOpMode mode;
    private double power;
    private boolean flipped;
    private final HashMap<String, Float> orientationCheckpoints;
    private TimeBasedMovements timeBasedMovements;
    private SensorBasedMovements sensorBasedMovements;

    public Controller(LinearOpMode mode, boolean useSensors) {
        bot = new BotController(mode.hardwareMap);
        this.mode = mode;
        if (useSensors) {
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
            parameters.accelerationIntegrationAlgorithm = new SmoothingIntegrator(5);
            imu = BotController.get(mode.hardwareMap, "imu");
            imu.initialize(parameters);
            imu.startAccelerationIntegration(null, null, 1);
            orientationCheckpoints = new HashMap<>();
        } else {
            imu = null;
            orientationCheckpoints = null;
        }
        timeBasedMovements = null;
        sensorBasedMovements = null;
        power = 1;
    }

    public Controller flip() {
        flipped = !flipped;
        timeBasedMovements = null;
        sensorBasedMovements = null;
        return this;
    }

    public double getPower() {
        return power;
    }
    public void setPower(double power) {
        if (power < 0 || power > 1)
            throw new IllegalArgumentException("power outside of bounds [0, 1].");
        this.power = power;
    }

    public void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
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

    public TimeBasedMovements moveByTime() {
        if (timeBasedMovements == null) {
            if (flipped) {
                timeBasedMovements = new TimeBasedMovements() {
                    @Override
                    public TimeBasedMovements move(Direction direction, double seconds) {
                        return direction.isXAxis() ?
                                super.move(direction.opposite(), seconds) :
                                super.move(direction, seconds);
                    }

                    @Override
                    public TimeBasedMovements rotate(Direction direction, double seconds) {
                        return super.rotate(direction.opposite(), seconds);
                    }
                };
            } else {
                timeBasedMovements = new TimeBasedMovements();
            }
        }
        return timeBasedMovements;
    }
    public class TimeBasedMovements {
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
        public TimeBasedMovements setPower(double power) {
            Controller.this.setPower(power);
            return this;
        }

        public SensorBasedMovements moveBySensor() {
            return Controller.this.moveBySensor();
        }
    }

    public SensorBasedMovements moveBySensor() {
        if (sensorBasedMovements == null) {
            if (imu != null) {
                if (flipped) {
                    sensorBasedMovements = new SensorBasedMovements() {
                        @Override
                        public SensorBasedMovements move(Direction direction, double distance) {
                            return direction.isXAxis() ?
                                    super.move(direction.opposite(), distance) :
                                    super.move(direction, distance);
                        }

                        @Override
                        public SensorBasedMovements rotate(Direction direction, float dAngle) {
                            return super.rotate(direction.opposite(), dAngle);
                        }
                    };
                } else {
                    sensorBasedMovements = new SensorBasedMovements();
                }
            } else {
                throw new UnsupportedOperationException(
                        "useSensors must be true upon initialization of this class " +
                                "in order to use moveBySensor"
                );
            }
        }
        return sensorBasedMovements;
    }
    public class SensorBasedMovements {
        public SensorBasedMovements move(Direction direction, double distance) {
            throw new UnsupportedOperationException(
                    "sensor based movement is currently not supported"
            );
        }

        public SensorBasedMovements rotate(Direction direction, float dAngle) {
            return rotateAbs(absGoal(direction, dAngle));
        }

        public SensorBasedMovements rotate(Direction direction, float dAngle, double seconds) {
            final float goal = absGoal(direction, dAngle);
            bot.rotate(direction, power);
            bot.sleep(seconds);
            bot.rotate(direction, 0);
            return rotateAbs(goal);
        }

        private SensorBasedMovements rotateAbs(final float goal) {
            while (!mode.isStopRequested()) {
                float a = getAngle();
                float dist = distFromGoal(a, goal);
                if (dist == 0) break;
                double power = distBasedPower(dist);
                if (alignAngle(goal - a) < 0) bot.rotate(Direction.RIGHT, power);
                else bot.rotate(Direction.LEFT, power);
                sleep(.001);
            }
            bot.rotate(Direction.RIGHT, 0);
            return this;
        }

        public SensorBasedMovements saveOrientation(String name) {
            //noinspection ConstantConditions
            orientationCheckpoints.put(name, getAngle());
            return this;
        }
        public SensorBasedMovements saveOrientation(String name, Direction direction, float dAngle) {
            //noinspection ConstantConditions
            orientationCheckpoints.put(name, absGoal(flipped ? direction.opposite() : direction, dAngle));
            return this;
        }
        public SensorBasedMovements gotoOrientation(String name) {
            //noinspection ConstantConditions
            return rotateAbs(orientationCheckpoints.get(name));
        }
        public SensorBasedMovements gotoOrientation(Direction direction, double seconds, String name) {
            bot.rotate(flipped ? direction.opposite() : direction, power);
            bot.sleep(seconds);
            bot.rotate(direction, 0);
            return rotateAbs(orientationCheckpoints.get(name));
        }

        public SensorBasedMovements sleep(double seconds) {
            Controller.this.sleep(seconds);
            return this;
        }

        public SensorBasedMovements armUp(double seconds) {
            Controller.this.armUp(seconds);
            return this;
        }
        public SensorBasedMovements armDown(double seconds) {
            Controller.this.armDown(seconds);
            return this;
        }
        public SensorBasedMovements holdArmDown(double seconds) {
            Controller.this.holdArmDown(seconds);
            return this;
        }
        public SensorBasedMovements closeHand() {
            Controller.this.closeHand();
            return this;
        }
        public SensorBasedMovements openHand() {
            Controller.this.openHand();
            return this;
        }
        public SensorBasedMovements setPower(double power) {
            Controller.this.setPower(power);
            return this;
        }

        public TimeBasedMovements moveByTime() {
            return Controller.this.moveByTime();
        }

        private float absGoal(Direction direction, float dAngle) {
            if (direction == Direction.LEFT) return alignAngle(dAngle + getAngle());
            else if (direction == Direction.RIGHT) return alignAngle(getAngle() - dAngle);
            else throw new IllegalArgumentException("direction must be left or right");
        }
        private float getAngle() {
            //noinspection ConstantConditions
            return imu.getAngularOrientation().firstAngle;
        }
        private float alignAngle(float angle) {
            if (angle < -180) return angle + 360;
            if (angle > 180) return angle - 360;
            return angle;
        }
        private float distFromGoal(float a, float goal) {
            float dist = Math.abs(goal - a);
            if (dist <= 180) return dist;
            return a < goal ? Math.abs(goal - a - 360) : Math.abs(goal - a + 360);
        }
        private double distBasedPower(float dist) {
            return Math.max(getPower() * dist / 180, getPower() / 20);
        }
    }
}
