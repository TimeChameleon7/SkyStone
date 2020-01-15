package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@SuppressWarnings("WeakerAccess")
public class Controller {

    //todo four directions then moveRel that calls them
    //todo rotation decelerate when close
    //todo logging methods

    private final DcMotor[] motors;
    final Servo arm;
    private final Servo hand;
    private final Telemetry telemetry;
    private final BNO055IMU imu;
    private double power;
    private float rotateAccuracy;
    private boolean logging;
    private boolean flipped;

    public Controller(LinearOpMode mode, boolean useSensors, int averageOver) {
        HardwareMap map = mode.hardwareMap;
        motors = new DcMotor[4];
        motors[0] = get(map, "one");
        motors[1] = get(map, "two");
        motors[2] = get(map, "three");
        motors[3] = get(map, "four");
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm = get(map, "servoArm");
        hand = get(map, "servoOne");
        telemetry = mode.telemetry;

        this.rotateAccuracy = .1f;
        this.power = 1;
        logging = false;

        if (useSensors) {
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
            parameters.accelerationIntegrationAlgorithm = new SmoothingIntegrator(averageOver);
            imu = get(map, "imu");
            imu.initialize(parameters);
            imu.startAccelerationIntegration(null, null, 1);
        } else {
            imu = null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(HardwareMap map, String name) {
        return (T) map.get(name);
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

    public float getRotateAccuracy() {
        return rotateAccuracy;
    }
    public void setRotateAccuracy(float rotateAccuracy) {
        if (rotateAccuracy < 0)
            throw new IllegalArgumentException("rotateAccuracy must be positive.");
        this.rotateAccuracy = rotateAccuracy;
    }

    public boolean isLogging() {
        return logging;
    }
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for (DcMotor motor : motors)
            motor.setZeroPowerBehavior(behavior);
    }

    public void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void setMotorsPower(double power, DcMotor...motors) {
        for (DcMotor motor : motors) motor.setPower(power);
    }

    public void armUp(double seconds) {
        arm.setPosition(0);
        sleepThenBrake(seconds, arm);
    }
    public void armDown(double seconds) {
        arm.setPosition(1);
        sleepThenBrake(seconds, arm);
    }
    public void holdArmDown(double seconds) {
        arm.setPosition(1);
        sleep(seconds);
    }
    public void closeHand() {
        hand.setPosition(0);
        sleepThenBrake(1, hand);
    }
    public void openHand() {
        hand.setPosition(1);
        sleepThenBrake(1, hand);
    }
    private void sleepThenBrake(double seconds, Servo servo) {
        sleep(seconds);
        servo.setPosition(.5);
    }

    public BotController moveByTime() {
        abstract class Core extends BotController {
            @Override
            public BotController move(Direction direction, double seconds) {
                DcMotor[] motors = getMotors(direction);
                motors[0].setPower(power);
                motors[1].setPower(-power);
                sleep(seconds);
                setMotorsPower(0, motors);
                return this;
            }

            @Override
            public BotController move(Direction direction1, double seconds1, Direction direction2, double seconds2) {
                if (direction1 == direction2) throw new IllegalArgumentException("Directions may not be the same");
                if (direction1.opposite() == direction2) throw new IllegalArgumentException("Directions may not be opposite of each other");

                if (seconds1 > seconds2) {
                    Direction tempDir = direction1;
                    direction1 = direction2;
                    direction2 = tempDir;
                    double tempSec = seconds1;
                    seconds1 = seconds2;
                    seconds2 = tempSec;
                }

                DcMotor[] motors1 = getMotors(direction1);
                DcMotor[] motors2 = getMotors(direction2);
                setMotorsPower(power, motors1[0], motors2[0]);
                setMotorsPower(-power, motors1[1], motors2[1]);
                sleep(seconds1);
                setMotorsPower(0, motors1);
                sleep(seconds2 - seconds1);
                setMotorsPower(0, motors2);
                return this;
            }

            abstract protected DcMotor[] getMotors(Direction direction);
        }
        if (flipped) {
            return new Core() {
                @Override
                public BotController rotate(Direction direction, double seconds) {
                    switch (direction) {
                        case LEFT:  setMotorsPower(-power, motors); break;
                        case RIGHT: setMotorsPower(power, motors);  break;
                        default: throw new IllegalArgumentException("Direction may only be LEFT or RIGHT for rotations.");
                    }
                    sleep(seconds);
                    setMotorsPower(0, motors);
                    return this;
                }

                @Override
                protected DcMotor[] getMotors(Direction direction) {
                    switch (direction) {
                        case FORWARD:   return new DcMotor[]{motors[0], motors[2]};
                        case REVERSE:   return new DcMotor[]{motors[2], motors[0]};
                        case LEFT:      return new DcMotor[]{motors[1], motors[3]};
                        case RIGHT:     return new DcMotor[]{motors[3], motors[1]};
                        default:        throw new IllegalArgumentException("Direction is null");
                    }
                }
            };
        } else {
            return new Core() {
                @Override
                public BotController rotate(Direction direction, double seconds) {
                    switch (direction) {
                        case LEFT:  setMotorsPower(power, motors);  break;
                        case RIGHT: setMotorsPower(-power, motors); break;
                        default: throw new IllegalArgumentException("Direction may only be LEFT or RIGHT for rotations.");
                    }
                    sleep(seconds);
                    setMotorsPower(0, motors);
                    return this;
                }

                @Override
                protected DcMotor[] getMotors(Direction direction) {
                    switch (direction) {
                        case FORWARD:   return new DcMotor[]{motors[0], motors[2]};
                        case REVERSE:   return new DcMotor[]{motors[2], motors[0]};
                        case LEFT:      return new DcMotor[]{motors[3], motors[1]};
                        case RIGHT:     return new DcMotor[]{motors[1], motors[3]};
                        default:        throw new IllegalArgumentException("Direction is null");
                    }
                }
            };
        }
    }

    abstract class BotController {

        public double getPower() {
            return power;
        }
        public BotController setPower(double power) {
            Controller.this.setPower(power);
            return this;
        }

        public float getRotateAccuracy() {
            return rotateAccuracy;
        }
        public BotController setRotateAccuracy(float rotateAccuracy) {
            Controller.this.setRotateAccuracy(rotateAccuracy);
            return this;
        }

        public boolean isLogging() {
            return logging;
        }
        public BotController setLogging(boolean logging) {
            Controller.this.setLogging(logging);
            return this;
        }

        public BotController setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
            Controller.this.setZeroPowerBehavior(behavior);
            return this;
        }

        public BotController sleep(double seconds) {
            Controller.this.sleep(seconds);
            return this;
        }

        public BotController armUp(double seconds) {
            Controller.this.armUp(seconds);
            return this;
        }
        public BotController armDown(double seconds) {
            Controller.this.armDown(seconds);
            return this;
        }
        public BotController holdArmDown(double seconds) {
            Controller.this.holdArmDown(seconds);
            return this;
        }
        public BotController closeHand() {
            Controller.this.closeHand();
            return this;
        }
        public BotController openHand() {
            Controller.this.openHand();
            return this;
        }

        /**
         * Rotates the bot using the number defined towards the direction defined.
         *
         * @param direction Direction to rotate the bot towards.
         *                  Implementations only need to operate with
         *                   Directions {@code LEFT} & {@code RIGHT},
         *                   and may handle {@code FORWARD} & {@code REVERSE}
         *                   as they wish.
         * @param x usage is determined by the implementation.
         *
         * @return {@code this}.
         */
        public abstract BotController rotate(Direction direction, double x);

        /**
         * Moves the bot using the number defined towards the direction defined.
         *
         * @param direction Direction to move the bot towards.
         * @param x usage is determined by the implementation.
         *
         * @return {@code this}.
         */
        public abstract BotController move(Direction direction, double x);

        /**
         * Moves the bot using the first number defined towards the direction defined, and moves the
         *  bot using the second number towards the second direction defined at the same time.
         *
         * @param direction1 First direction to move the bot towards.
         * @param x usage is determined by the implementation.
         * @param direction2 Second direction to move the bot towards.
         * @param y usage is determined by the implementation.
         * @return {@code this}.
         */
        public abstract BotController move(Direction direction1, double x, Direction direction2, double y);
    }
}
