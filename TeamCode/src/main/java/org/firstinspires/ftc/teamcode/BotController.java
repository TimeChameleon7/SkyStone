package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class BotController {
    DcMotor motorOne;
    DcMotor motorTwo;
    DcMotor motorThree;
    DcMotor motorFour;
    Servo servoArm;
    Servo servoDump;
    Servo servoHand;
    //todo Alfredo IMU register "BNO055IMU"
    double speed;

    BotController(HardwareMap hardwareMap) {
        motorOne  = hardwareMap.get(DcMotor.class, "one");
        motorTwo  = hardwareMap.get(DcMotor.class, "two");
        motorThree  = hardwareMap.get(DcMotor.class, "three");
        motorFour  = hardwareMap.get(DcMotor.class, "four");
        servoHand = hardwareMap.get(Servo.class, "servoOne");
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        servoDump = hardwareMap.get(Servo.class, "servoDump");
        speed = 1;
    }
    
    public void armUp(long millis) {
        servoArm.setPosition(0);
        sleep(millis);
        servoArm.setPosition(.5);
    }
    
    public void armDown(long millis) {
        servoArm.setPosition(1);
        sleep(millis);
        servoArm.setPosition(.5);
    }
    
    public void forward(long millis) {
        motorOne.setPower(-speed);
        motorThree.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void reverse(long millis) {
        motorOne.setPower(speed);
        motorThree.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void left(long millis) {
        motorTwo.setPower(-speed * .85);
        motorFour.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void right(long millis) {
        motorTwo.setPower(speed * .85);
        motorFour.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void rightTurn(long millis) {
        motorOne.setPower(-speed);
        motorThree.setPower(-speed);
        motorTwo.setPower(-speed);
        motorFour.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void leftTurn(long millis) {
        motorOne.setPower(speed);
        motorThree.setPower(speed);
        motorTwo.setPower(speed);
        motorFour.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void closeJaw() {
        servoHand.setPosition(0);
        sleep(1000);
    }
    
    public void openJaw() {
        servoHand.setPosition(.8);
        sleep(1000);
    }
    
    public void brake() {
        motorOne.setPower(0);
        motorTwo.setPower(0);
        motorThree.setPower(0);
        motorFour.setPower(0);
    }
    
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {}
    }
}
