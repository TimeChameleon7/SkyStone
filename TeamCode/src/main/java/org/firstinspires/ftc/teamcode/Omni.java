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
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


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

@TeleOp(name="Omni", group="Linear Opmode")

public class Omni extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor motorOne;
    private DcMotor motorTwo;
    private DcMotor motorThree;
    private DcMotor motorFour;
    private Servo servoHand;
    private Servo servoArm;
    private Servo servoDump;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        motorOne  = hardwareMap.get(DcMotor.class, "one");
        motorTwo  = hardwareMap.get(DcMotor.class, "two");
        motorThree  = hardwareMap.get(DcMotor.class, "three");
        motorFour  = hardwareMap.get(DcMotor.class, "four");
        servoHand = hardwareMap.get(Servo.class, "servoDump");
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        servoDump = hardwareMap.get(Servo.class, "servoOne");


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        motorOne.setDirection(DcMotor.Direction.FORWARD);
        motorTwo.setDirection(DcMotor.Direction.FORWARD);
        motorThree.setDirection(DcMotor.Direction.REVERSE);
        motorFour.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        double turnPower = .5;
        
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double vertical = -gamepad1.right_stick_y;
            double horizontal   =  gamepad1.right_stick_x;
            float triggerL = gamepad1.left_trigger;
            float triggerR = gamepad1.right_trigger;

            if (vertical == 0 && horizontal == 0) {
                if (triggerL != 0) {
                    motorOne.setPower(triggerL);
                    motorTwo.setPower(triggerL);
                    motorThree.setPower(-triggerL);
                    motorFour.setPower(-triggerL);
                } else if (triggerR != 0) {
                    motorOne.setPower(-triggerR);
                    motorTwo.setPower(-triggerR);
                    motorThree.setPower(triggerR);
                    motorFour.setPower(triggerR);
                } else if (gamepad1.left_bumper){
                    motorOne.setPower(turnPower);
                    motorTwo.setPower(turnPower);
                    motorThree.setPower(-turnPower);
                    motorFour.setPower(-turnPower);
                } else if (gamepad1.right_bumper){
                    motorOne.setPower(-turnPower);
                    motorTwo.setPower(-turnPower);
                    motorThree.setPower(turnPower);
                    motorFour.setPower(turnPower);
                } else {
                    motorOne.setPower(0);
                    motorTwo.setPower(0);
                    motorThree.setPower(0);
                    motorFour.setPower(0);
                }
            } else {
                motorOne.setPower(vertical);
                motorTwo.setPower(horizontal);
                motorThree.setPower(vertical);
                motorFour.setPower(horizontal);
            }

            if(gamepad2.right_trigger>0){
                servoDump.setPosition(.8);
            }else if(gamepad2.left_trigger>0){
                servoDump.setPosition(.3);
            }else{
                servoDump.setPosition(.5);
            }

            if(gamepad2.dpad_up){
                servoArm.setPosition(6);
            }else if(gamepad2.dpad_down){
                servoArm.setPosition(0);
            }else{
                servoArm.setPosition(.5);
            }

            if(gamepad2.right_bumper){
                servoHand.setPosition(.3);
            }else if(gamepad2.left_bumper){
                servoHand.setPosition(.8);
            }else{
                servoHand.setPosition(.5);
            }


            // Show the elapsed game time.
            telemetry.addData("Elapsed", "%.2f", runtime.seconds());
            telemetry.update();
        }
    }
}
