package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public final class AutoModes {
    private AutoModes(){}

    private static Controller startSequence(LinearOpMode mode, boolean useSensors) {
        Controller controller = new Controller(mode, useSensors);
        mode.telemetry.addData("Status", "Initialized");
        mode.telemetry.update();
        mode.waitForStart();
        mode.telemetry.addData("Status", "Started");
        mode.telemetry.update();
        return controller;
    }

    @Autonomous
    public static class MoveLeftFoundation extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, false);

            controller.moveByTime()
                    .move(Direction.FORWARD, 200)
                    .move()
        }
    }
}
