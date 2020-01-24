package org.firstinspires.ftc.newteamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Direction;

/**
 * Low Level Bot Controller, uses a bot's hardware for basic movement functionality, and
 * makes that functionality easily understandable.
 */
@SuppressWarnings("WeakerAccess")
public class BotController {
    private final DcMotor[] motors;
    private final Servo arm;
    private final Servo hand;

    public BotController(HardwareMap map) {
        motors = new DcMotor[4];
        motors[0] = get(map, "one");
        motors[1] = get(map, "two");
        motors[2] = get(map, "three");
        motors[3] = get(map, "four");
        for (DcMotor motor : motors) motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm = get(map, "servoArm");
        hand = get(map, "servoOne");
    }

    @SuppressWarnings("unchecked")
    static <T> T get(HardwareMap map, String name) {
        return (T) map.get(name);
    }

    public void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
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

    private void setMotorsPower(double power, DcMotor...motors) {
        for (DcMotor motor : motors) motor.setPower(power);
    }

    /**
     * Rotates the bot toward the direction supplied with the power supplied.
     *
     * Call this method with the same direction and 0 power to stop the movement.
     *
     * @param direction Determines if the power supplied will be negated or not.
     * @param power Power to assign to the motors used.
     */
    public void rotate(Direction direction, double power) {
        switch (direction) {
            case LEFT:  setMotorsPower(power, motors);  break;
            case RIGHT: setMotorsPower(-power, motors); break;
            default: throw new IllegalArgumentException("Direction may only be LEFT or RIGHT for rotations.");
        }
    }

    /**
     * Moves the bot towards the direction supplied with the power supplied.
     *
     * Call this method with the same direction and 0 power to stop the movement.
     *
     * @param direction Determines which motors will be used to complete the operation.
     * @param power Power to assign to the motors used.
     */
    public void move(Direction direction, double power) {
        DcMotor[] motors = getMotors(direction);
        motors[0].setPower(power);
        motors[1].setPower(-power);
    }

    /**
     * Returns a 2 length array of DcMotors determined by the direction.
     * In order to achieve movement in the direction specified, the user must
     * set a positive power to the index 0 motor, and a negative power to the
     * index 1 motor.
     *
     * @param direction Determines which motors are returned
     * @return a 2 length array of DcMotors determined by the direction
     */
    private DcMotor[] getMotors(Direction direction) {
        switch (direction) {
            case FORWARD:   return new DcMotor[]{motors[0], motors[2]};
            case REVERSE:   return new DcMotor[]{motors[2], motors[0]};
            case LEFT:      return new DcMotor[]{motors[3], motors[1]};
            case RIGHT:     return new DcMotor[]{motors[1], motors[3]};
            default:        throw new IllegalArgumentException("Direction is null");
        }
    }
}
