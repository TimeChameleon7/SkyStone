package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

@SuppressWarnings("WeakerAccess")
public class OverloadedController {

    private final DcMotor[] motors;
    final Servo arm;
    private final Servo hand;
    private final Telemetry telemetry;
    private final BNO055IMU imu;
    private double power;
    private float rotateAccuracy;
    private boolean logging;

    public OverloadedController(LinearOpMode mode) {
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

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.accelerationIntegrationAlgorithm = new AccelerationIntegrator(5);
        imu = get(map, "imu");
        imu.initialize(parameters);
        imu.startAccelerationIntegration(null, null, 1);
        this.rotateAccuracy = .1f;
        this.power = 1;
        logging = false;
    }



    //General --------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private <T> T get(HardwareMap map, String name) {
        return (T) map.get(name);
    }

    public double getPower() {
        return power;
    }
    public OverloadedController setPower(double power) {
        if (power < 0 || power > 1)
            throw new IllegalArgumentException("power outside of bounds [0, 1].");
        this.power = power;
        return this;
    }

    public float getRotateAccuracy() {
        return rotateAccuracy;
    }
    public OverloadedController setRotateAccuracy(float rotateAccuracy) {
        if (rotateAccuracy < 0)
            throw new IllegalArgumentException("rotateAccuracy must be positive.");
        this.rotateAccuracy = rotateAccuracy;
        return this;
    }

    public boolean isLogging() {
        return logging;
    }
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public OverloadedController setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for (DcMotor motor : motors)
            motor.setZeroPowerBehavior(behavior);
        return this;
    }

    public OverloadedController sleep(long millis) {
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

    public OverloadedController armUp(long millis) {
        arm.setPosition(0);
        return sleepThenBrake(millis, arm);
    }
    public OverloadedController armDown(long millis) {
        arm.setPosition(1);
        return sleepThenBrake(millis, arm);
    }
    public OverloadedController holdArmDown(long millis) {
        arm.setPosition(1);
        sleep(millis);
        return this;
    }
    public OverloadedController closeHand() {
        hand.setPosition(0);
        return sleepThenBrake(1000, hand);
    }
    public OverloadedController openHand() {
        hand.setPosition(1);
        return sleepThenBrake(1000, hand);
    }
    private OverloadedController sleepThenBrake(long millis, Servo servo) {
        sleep(millis);
        servo.setPosition(.5);
        return this;
    }



    //Time Based Movements -------------------------------------------------------------------------

    public OverloadedController timeRotLeft(long millis) {
        return timeRot(millis, power);
    }
    public OverloadedController timeRotRight(long millis) {
        return timeRot(millis, -power);
    }
    private OverloadedController timeRot(long millis, double power) {
        setMotorsPower(power);
        sleep(millis);
        setMotorsPower(0);
        return this;
    }

    public OverloadedController timeForward(long millis) {
        return timeMove(millis, motors[0], motors[2]);
    }
    public OverloadedController timeReverse(long millis) {
        return timeMove(millis, motors[2], motors[0]);
    }
    public OverloadedController timeLeft(long millis) {
        return timeMove(millis, motors[3], motors[1]);
    }
    public OverloadedController timeRight(long millis) {
        return timeMove(millis, motors[1], motors[3]);
    }
    private OverloadedController timeMove(long millis, DcMotor normalMotor, DcMotor reversedMotor) {
        normalMotor.setPower(power);
        reversedMotor.setPower(-power);
        sleep(millis);
        normalMotor.setPower(0);
        reversedMotor.setPower(0);
        return this;
    }

    public TimeBasedMovements timeBasedMovements() {
        return new TimeBasedMovements();
    }
    private class TimeBasedMovements {
        public TimeBasedMovements rotLeft(long millis) {
            timeRotLeft(millis);
            return this;
        }
        public TimeBasedMovements rotRight(long millis) {
            timeRotRight(millis);
            return this;
        }

        public TimeBasedMovements forward(long millis) {
            timeForward(millis);
            return this;
        }
        public TimeBasedMovements reverse(long millis) {
            timeReverse(millis);
            return this;
        }
        public TimeBasedMovements left(long millis) {
            timeLeft(millis);
            return this;
        }
        public TimeBasedMovements right(long millis) {
            timeRight(millis);
            return this;
        }

        public OverloadedController overloadedController() {
            return OverloadedController.this;
        }
    }



    //Distance Based Movements ---------------------------------------------------------------------

    public OverloadedController distRotLeft(float angle) {
        return distRotAbs(alignGoal(getAngle() + angle));
    }
    public OverloadedController distRotRight(float angle) {
        return distRotLeft(-angle);
    }
    private OverloadedController distRotAbs(float g) {
        while (true) {
            float a = getAngle();
            if (isWithinRotationAccuracy(a, g)) break;
            //left and right possibly swapped
            if (alignGoal(g - a) < 0) setMotorsPower(-power);
            else setMotorsPower(power);
        }
        setMotorsPower(0);
        return this;
    }
    private float alignGoal(float g) {
        if (g < -180) return g + 360;
        if (g > 180) return g - 360;
        return g;
    }
    private float getAngle() {
        return imu.getAngularOrientation().firstAngle;
    }
    private boolean isWithinRotationAccuracy(float n, float goal) {
        return Math.abs(goal - n) <= rotateAccuracy;
    }

    public OverloadedController distForward(double distance) {
        return distMove(distance, motors[0], motors[2]);
    }
    public OverloadedController distReverse(double distance) {
        return distMove(distance, motors[2], motors[0]);
    }
    public OverloadedController distLeft(double distance) {
        return distMove(distance, motors[3], motors[1]);
    }
    public OverloadedController distRight(double distance) {
        return distMove(distance, motors[1], motors[3]);
    }
    private OverloadedController distMove(double distance, DcMotor normalMotor, DcMotor reversedMotor) {
        double goalDistance = Math.pow(distance, 2);
        Position posPrev = imu.getPosition();
        normalMotor.setPower(power);
        reversedMotor.setPower(-power);
        Position position = imu.getPosition();
        while (distanceSq(posPrev, position) < goalDistance) {
            sleep(1);
            telemetry.addData("Accel", "(%f, %f, %f)", position.x, position.y, position.z);
            telemetry.update();
        }
        normalMotor.setPower(0);
        reversedMotor.setPower(0);
        return this;
    }
    private double distanceSq(Position pos1, Position pos2) {
        //Square root calculations are expensive.
        //Not using Z, that's altitude, of which we don't care.
        return Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2);
    }


    public DistBasedMovements distBasedMovements() {
        return new DistBasedMovements();
    }
    private class DistBasedMovements {
        public DistBasedMovements rotLeft(float angle) {
            distRotLeft(angle);
            return this;
        }
        public DistBasedMovements rotRight(float angle) {
            distRotRight(angle);
            return this;
        }

        public DistBasedMovements forward(double distance) {
            distForward(distance);
            return this;
        }
        public DistBasedMovements reverse(double distance) {
            distReverse(distance);
            return this;
        }
        public DistBasedMovements left(double distance) {
            distLeft(distance);
            return this;
        }
        public DistBasedMovements right(double distance) {
            distRight(distance);
            return this;
        }

        public OverloadedController overloadedController() {
            return OverloadedController.this;
        }
    }
}
