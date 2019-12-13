package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class AutoModeHolder {
    private static BotController startSequence(LinearOpMode opMode) {
        BotController controller = new BotController(opMode);
        opMode.telemetry.addData("Status", "Initialized");
        opMode.telemetry.update();
        opMode.waitForStart();
        opMode.telemetry.addData("Status", "Started");
        opMode.telemetry.update();
        return controller;
    }

    @Autonomous
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.speed = .3;
            c.right(1000);
            c.left(1000);
            c.forward(1000);
            c.reverse(1000);

            c.unregister();
        }
    }

    @Autonomous
    public static class ParkEdgeLeft extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.armDown(340);
            c.left(700);
        }
    }

    @Autonomous
    public static class ParkEdgeRight extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.armDown(340);
            c.right(700);
        }
    }

    @Autonomous
    public static class ParkMidLeft extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.forward(1000);
            c.armDown(340);
            c.left(700);
        }
    }

    @Autonomous
    public static class ParkMidRight extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.forward(1000);
            c.armDown(340);
            c.right(700);
        }
    }

    @Autonomous
    public static class MoveRightFoundation extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.forward(200);
            c.sleep(700);
            c.right(550);
            c.sleep(700);
            c.forward(1500);
            c.servoArm.setPosition(1);
            c.sleep(1200);
            c.speed = .5;
            c.reverse(5000);
            c.speed = .77;
            c.sleep(1000);
            c.armUp(1000);
            c.sleep(1000);
            c.forward(200);
            c.left(2000);
            c.armDown(230);
            c.left(1500);
        }
    }

    @Autonomous
    public static class MoveLeftFoundation extends LinearOpMode {
        @Override
        public void runOpMode() {
            BotController c = startSequence(this);

            c.forward(200);
            c.sleep(700);
            c.left(550);
            c.sleep(700);
            c.forward(1500);
            c.servoArm.setPosition(1);
            c.sleep(1200);
            c.speed = .5;
            c.reverse(5000);
            c.speed = .77;
            c.sleep(1000);
            c.armUp(1000);
            c.sleep(1000);
            c.forward(200);
            c.right(2000);
            c.armDown(230);
            c.right(1500);
        }
    }
}