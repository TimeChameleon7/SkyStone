package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.NavUtil;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

@SuppressWarnings("WeakerAccess")
public class Controller {

    //todo four directions then moveRel that calls them
    //todo rotation deaccelerate when close
    //todo logging methods
    //todo possible addition of opModeIsActive checking during sleeps or during logging calls

    private final DcMotor[] motors;
    final Servo arm;
    private final Servo hand;
    private final Telemetry telemetry;
    private final BNO055IMU imu;
    private final boolean useSensors;
    private double power;
    private float rotateAccuracy;
    private boolean logging;

    public Controller(LinearOpMode mode, boolean useSensors) {
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

        this.useSensors = useSensors;
        this.rotateAccuracy = .1f;
        this.power = 1;
        logging = false;

        if (useSensors) {
            BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
            parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
            parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
            parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
            parameters.accelerationIntegrationAlgorithm = new BNO055IMU.AccelerationIntegrator() {
                private Position position;
                private Velocity velocity;
                private Acceleration acceleration;

                @Override
                public void initialize(BNO055IMU.Parameters parameters, Position initialPosition, Velocity initialVelocity) {
                    this.position = new Position(DistanceUnit.METER, 0, 0, 0, 0);
                    this.velocity = new Velocity(DistanceUnit.METER, 0, 0, 0, 0);
                    this.acceleration = null;
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
                    if (acceleration != null) {
                        Acceleration accelPrev = acceleration;
                        Velocity velocPrev = velocity;
                        acceleration = linearAcceleration;

                        Velocity velocDelta = NavUtil.meanIntegrate(acceleration, accelPrev);
                        velocity = NavUtil.plus(velocity, velocDelta);

                        Position positDelta = NavUtil.meanIntegrate(velocity, velocPrev);
                        position = NavUtil.plus(position, positDelta);
                    } else {
                        acceleration = linearAcceleration;
                    }
                }
            };
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

    public double getPower() {
        return power;
    }
    public Controller setPower(double power) {
        if (power < 0 || power > 1)
            throw new IllegalArgumentException("power outside of bounds [0, 1].");
        this.power = power;
        return this;
    }

    public float getRotateAccuracy() {
        return rotateAccuracy;
    }
    public Controller setRotateAccuracy(float rotateAccuracy) {
        if (rotateAccuracy < 0)
            throw new IllegalArgumentException("rotateAccuracy must be positive.");
        this.rotateAccuracy = rotateAccuracy;
        return this;
    }

    public boolean isLogging() {
        return logging;
    }
    public Controller setLogging(boolean logging) {
        this.logging = logging;
        return this;
    }

    public Controller setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for (DcMotor motor : motors)
            motor.setZeroPowerBehavior(behavior);
        return this;
    }

    public Controller sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    private void setMotorsPower(double power) {
        for (DcMotor motor : motors) motor.setPower(power);
    }

    public Controller armUp(long millis) {
        arm.setPosition(0);
        return sleepThenBrake(millis, arm);
    }
    public Controller armDown(long millis) {
        arm.setPosition(1);
        return sleepThenBrake(millis, arm);
    }
    public Controller holdArmDown(long millis) {
        arm.setPosition(1);
        sleep(millis);
        return this;
    }
    public Controller closeHand() {
        hand.setPosition(0);
        return sleepThenBrake(1000, hand);
    }
    public Controller openHand() {
        hand.setPosition(1);
        return sleepThenBrake(1000, hand);
    }
    private Controller sleepThenBrake(long millis, Servo servo) {
        sleep(millis);
        servo.setPosition(.5);
        return this;
    }

    public BotController moveByTime() {
        return new BotController() {
            @Override
            public BotController rotate(Direction direction, double seconds) {
                switch (direction) {
                    case LEFT:
                        setMotorsPower(power);
                        sleep((long) (seconds * 1000));
                        setMotorsPower(0);
                        break;
                    case RIGHT:
                        setMotorsPower(-power);
                        sleep((long) (seconds * 1000));
                        setMotorsPower(0);
                    default:
                        throw new IllegalArgumentException("Direction may only be LEFT or RIGHT for rotations.");
                }
                return this;
            }

            @Override
            public BotController move(Direction direction, double seconds) {
                switch (direction) {
                    case FORWARD:   move(seconds, motors[0], motors[2]); break;
                    case REVERSE:   move(seconds, motors[2], motors[0]); break;
                    case LEFT:      move(seconds, motors[3], motors[1]); break;
                    case RIGHT:     move(seconds, motors[1], motors[3]); break;
                }
                return this;
            }

            private void move(double seconds, DcMotor normalMotor, DcMotor reversedMotor) {
                normalMotor.setPower(power);
                reversedMotor.setPower(-power);
                sleep((long) (seconds * 1000));
                normalMotor.setPower(0);
                reversedMotor.setPower(0);
            }
        };
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

        public BotController sleep(long millis) {
            Controller.this.sleep(millis);
            return this;
        }

        public BotController armUp(long millis) {
            Controller.this.armUp(millis);
            return this;
        }
        public BotController armDown(long millis) {
            Controller.this.armDown(millis);
            return this;
        }
        public BotController holdArmDown(long millis) {
            Controller.this.holdArmDown(millis);
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
         * @param x usage is determined by the implementation.
         * @param direction Direction to rotate the bot towards.
         *                  Implementations only need to operate with
         *                   Directions {@code LEFT} & {@code RIGHT},
         *                   and may handle {@code FORWARD} & {@code REVERSE}
         *                   as they wish.
         *
         * @return {@code this}.
         */
        public abstract BotController rotate(Direction direction, double x);

        /**
         * Moves the bot using the number defined towards the direction defined.
         *
         * @param x usage is determined by the implementation.
         * @param direction Direction to move the bot towards.
         *
         * @return {@code this}.
         */
        public abstract BotController move(Direction direction, double x);
    }
}
