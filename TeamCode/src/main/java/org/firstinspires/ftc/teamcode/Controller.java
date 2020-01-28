package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@SuppressWarnings("WeakerAccess")
public class Controller {

    //todo add sensor based rotation, decelerate when close

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
//todo String-HashMap based orientation checkpoints.
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
        } else {
            imu = null;
        }
        power = 1;
    }

    public Controller flip() {
        flipped = true;
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
        if (flipped) {
            return new TimeBasedMovements() {
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
            return new TimeBasedMovements();
        }
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
    }

    public SensorBasedMovements moveBySensor() {
        if (imu != null) {
            if (flipped) {
                return new SensorBasedMovements() {
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
                return new SensorBasedMovements();
            }
        } else {
            throw new UnsupportedOperationException(
                    "useSensors must be true upon initialization of this class " +
                            "in order to use moveBySensor"
            );
        }
    }
    public class SensorBasedMovements {
        public SensorBasedMovements move(Direction direction, double distance) {
            throw new UnsupportedOperationException(
                    "sensor based movement is currently not supported"
            );
        }

        public SensorBasedMovements rotate(Direction direction, float dAngle) {
            if (direction == Direction.LEFT) dAngle += getAngle();
            else if (direction == Direction.RIGHT) dAngle = getAngle() - dAngle;
            final float goal = alignAngle(dAngle);
            while (true) {
                float a = getAngle();
                float dist = distFromGoal(a, goal);
                if (dist == 0) break;
                double power = distBasedPower(dist);
                if (alignAngle(goal - a) < 0) bot.rotate(Direction.RIGHT, power);
                else bot.rotate(Direction.LEFT, power);
            }
            bot.rotate(Direction.RIGHT, 0);
            return this;
        }

        public SensorBasedMovements rotate(Direction direction, float dAngle, double seconds) {
            if (direction == Direction.LEFT) dAngle += getAngle();
            else if (direction == Direction.RIGHT) dAngle = getAngle() - dAngle;
            final float goal = alignAngle(dAngle);
            bot.rotate(direction, power);
            bot.sleep(seconds);
            bot.rotate(direction, 0);
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
