package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public final class AutoModes {
    private AutoModes(){}

    private static Controller startSequence(LinearOpMode mode, boolean useSensors, int averageOver) {
        Controller controller = new Controller(mode, useSensors, averageOver);
        mode.telemetry.addData("Status", "Initialized");
        mode.telemetry.update();
        mode.waitForStart();
        mode.telemetry.addData("Status", "Started");
        mode.telemetry.update();
        return controller;
    }

    @Autonomous
    public static class Test extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, false, 0);

            //MoveLeftFoundation with combined movements
            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .move(Direction.LEFT, .45, Direction.FORWARD, 1.6)
                    .holdArmDown(1.2)
                    .setPower(.5)
                    .move(Direction.REVERSE, 4.8)
                    .armUp(.7)
                    .setPower(1)
                    .move(Direction.FORWARD, .2, Direction.RIGHT, 2.6);
        }
    }

    @Autonomous
    public static class MoveLeftFoundation extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, false, 0));
        }

        static void go(Controller controller) {
            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .move(Direction.LEFT, .45)
                    .move(Direction.FORWARD, 1.6)
                    .holdArmDown(1.2)
                    .setPower(.5)
                    .move(Direction.REVERSE, 4.8)
                    .armUp(.7)
                    .setPower(1)
                    .move(Direction.FORWARD, .2)
                    .move(Direction.RIGHT, 2.6);
        }
    }

    @Autonomous
    public static class MoveRightFoundation extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            MoveLeftFoundation.go(startSequence(this, false, 0).flip());
        }
    }

    @Autonomous
    public static class MoveLeftStones extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, false, 0));
        }

        static void go(Controller controller) {
            controller.moveByTime()
                    .move(Direction.FORWARD, 1.6)
                    .setPower(.3)
                    .move(Direction.FORWARD, .2)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, .15)
                    .setPower(1)
                    .rotate(Direction.LEFT, .4)
                    .move(Direction.FORWARD, 2)
                    .armUp(.7)
                    .move(Direction.LEFT, .3)
                    .move(Direction.REVERSE, .5)
                    .armDown(.3)
                    .move(Direction.REVERSE, 1.8)
                    .armUp(.7)
                    .rotate(Direction.RIGHT, .4)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.5)
                    .sleep(.5)
                    .move(Direction.REVERSE, .27)
                    .setPower(1)
                    .rotate(Direction.LEFT, .4)
                    .move(Direction.FORWARD, 2.3)
                    .armUp(.7)
                    .move(Direction.REVERSE, .5);

        }
    }

    @Autonomous
    public static class MoveRightStones extends  LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            MoveLeftStones.go(startSequence(this, false, 0).flip());
        }
    }
}
