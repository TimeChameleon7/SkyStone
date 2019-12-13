/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

public class DepotAuto extends LinearOpMode {

    private DcMotor motorOne;
    private DcMotor motorTwo;
    private DcMotor motorThree;
    private DcMotor motorFour;
    private Servo servoHand;
    private Servo servoArm;
    private Servo servoDump;
    private double speed = 1;

    @Override
    public void runOpMode() {
        motorOne  = hardwareMap.get(DcMotor.class, "one");
        motorTwo  = hardwareMap.get(DcMotor.class, "two");
        motorThree  = hardwareMap.get(DcMotor.class, "three");
        motorFour  = hardwareMap.get(DcMotor.class, "four");
        servoHand = hardwareMap.get(Servo.class, "servoOne");
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        servoDump = hardwareMap.get(Servo.class, "servoDump");
        
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
        waitForStart();
        
        moveRedFoundation();
    }
    
    public void squareTest() {
        for (int i = 0; i < 10; i++) {
            forward(500);
            right(500);
            reverse(500);
            left(500);
        }
    }
    
    public void parkRight(boolean towardMid) {
        if (towardMid) forward(1400);
        armDown(340);
        right(700);
    }
    
    public void parkLeft(boolean towardMid) {
        if (towardMid) forward(1400);
        armDown(340);
        left(700);
    }
    
    public void moveRedFoundation() {
        forward(200);
        sleep(700);
        right(500);
        sleep(700);
        forward(1300);
        servoArm.setPosition(1);
        sleep(1200);
        speed = .3;
        reverse(3000);
        sleep(1000);
        armUp(1000);
        sleep(1000);
        forward(400);
        speed = .77;
        left(2000);
        armDown(230);
        left(1000);
    }
    
    public void moveBlueFoundation() {
        forward(200);
        sleep(700);
        left(500);
        sleep(700);
        forward(1300);
        servoArm.setPosition(1);
        sleep(1200);
        speed = .30;
        reverse(3000);
        sleep(1000);
        armUp(1000);
        sleep(1000);
        forward(400);
        speed = .77;
        right(2000);
        armDown(230);
        right(1000);
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
}