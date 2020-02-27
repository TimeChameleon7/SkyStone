package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Low Level Bot Controller, uses a bot's hardware for basic movement functionality, and
 * makes that functionality easily understandable.
 */
@SuppressWarnings("WeakerAccess")
public class BotController {
    /**
     * The bot's motor used to move the robot along the ground.
     */
    private final DcMotor[] motors;
    /**
     * The Servo that, when powered, moves the arm up or down.
     */
    private final Servo arm;
    /**
     * The Servo that, when powered, opens and closes the hand.
     */
    private final Servo hand;
    private final double[] weights;

    /**
     * Sets all fields as well as sets the motors into the brake behavior.
     *
     * @param map the map to retrieve the bot's hardware from.
     */
    public BotController(HardwareMap map) {
        motors = new DcMotor[4];
        motors[0] = get(map, "one");
        motors[1] = get(map, "two");
        motors[2] = get(map, "three");
        motors[3] = get(map, "four");
        for (DcMotor motor : motors) motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        weights = new double[4];
        for (int i = 0; i < weights.length; i++) weights[i] = 1;
        arm = get(map, "servoArm");
        hand = get(map, "servoOne");
    }

    /**
     * map.get helper.
     */
    @SuppressWarnings("unchecked")
    static <T> T get(HardwareMap map, String name) {
        return (T) map.get(name);
    }

    public void setWeight(int i, double weight) {
        if (weight < 0 || weight > 1) throw new IllegalArgumentException("weight must be on [0,1]");
        weights[i] = weight;
    }

    public double getWeight(int i) {
        return weights[i];
    }

    public void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Moves the arm up for {@code seconds} seconds.
     *
     * @param seconds The length of time to move the arm upward for.
     */
    public void armUp(double seconds) {
        arm.setPosition(0);
        sleepThenBrake(seconds, arm);
    }
    /**
     * Moves the arm down for {@code seconds} seconds.
     *
     * @param seconds The length of time to move the arm downward for.
     */
    public void armDown(double seconds) {
        arm.setPosition(1);
        sleepThenBrake(seconds, arm);
    }
    /**
     * Moves the arm down continually, the method will return after {@code seconds} seconds.
     *
     * @param seconds The amount of time to wait before returning.
     */
    public void holdArmDown(double seconds) {
        arm.setPosition(1);
        sleep(seconds);
    }
    /**
     * Closes the hand.
     */
    public void closeHand() {
        hand.setPosition(0);
        sleepThenBrake(1, hand);
    }
    /**
     * Opens the hand.
     */
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
        double[] weights = getWeights(direction);
        motors[0].setPower(power * weights[0]);
        motors[1].setPower(-power * weights[1]);
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

    private double[] getWeights(Direction direction) {
        switch (direction) {
            case FORWARD:   return new double[]{weights[0], weights[2]};
            case REVERSE:   return new double[]{weights[2], weights[0]};
            case LEFT:      return new double[]{weights[3], weights[1]};
            case RIGHT:     return new double[]{weights[1], weights[3]};
            default:        throw new IllegalArgumentException("Direction is null");
        }
    }
}
