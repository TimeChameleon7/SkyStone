package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Arrays;

public class AutoModes {
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
    public static class Test extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, true);

            controller.moveBySensor()
                    .saveOrientation("start")
                    .sleep(2)
                    .gotoOrientation("start");
        }
    }

    @Autonomous
    public static class FoundationLeft extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, false));
        }

        static void go(Controller controller) {
            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .45)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.6)
                    .holdArmDown(1.2)
                    .setPower(.5)
                    .move(Direction.REVERSE, 4.8)
                    .armUp(.7)
                    .setPower(1)
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, 2.6);
        }
    }

    @Autonomous
    public static class FoundationRight extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            FoundationLeft.go(startSequence(this, false).flip());
        }
    }

    @Autonomous
    public static class StonesRight extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, false);

            controller.flip().moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .1)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.2)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, .8)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2)
                    .armUp(.7)
                    //first stone is placed
                    .move(Direction.LEFT, .3)
                    .sleep(.3)
                    .move(Direction.REVERSE, .5)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2)
                    .armUp(.7)
                    .rotate(Direction.RIGHT, .42)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.45)
                    .holdArmDown(.5)
                    .sleep(.3)
                    .move(Direction.REVERSE, .5)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.3)
                    .armUp(.7)
                    .move(Direction.REVERSE, .5)
                    //second stone is placed
                    .armDown(.2)
                    //.rotate(Direction.RIGHT, .01)
                    .move(Direction.REVERSE, 2.1)
                    .armUp(.5)
                    .rotate(Direction.RIGHT, .41)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.3);
        }
    }

    @Autonomous
    public static class StonesLeft1 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.moveBySensor().saveOrientation("start");

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .15)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.1)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.4)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2.4)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation("start").moveByTime()
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.3)
                    .move(Direction.REVERSE, 1.4)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3)
                    .armUp(.6)
                    .move(Direction.REVERSE, .4)
                    .sleep(.3);
        }
    }

    @Autonomous
    public static class StonesLeft2 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, false);

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .3)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.5)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.4)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.7)
                    .setPower(1)
                    .rotate(Direction.LEFT, .45)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2.1)
                    .armUp(.7)
                    //first stone is placed
                    .move(Direction.LEFT, .3)
                    .sleep(.3)
                    .move(Direction.REVERSE, .5)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2)
                    .armUp(.7)
                    .rotate(Direction.RIGHT, .42)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.45)
                    .holdArmDown(.5)
                    .sleep(.3)
                    .move(Direction.REVERSE, .5)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.3)
                    .armUp(.7)
                    .move(Direction.REVERSE, .5)
                    //second stone is placed
                    .armDown(.2)
                    //.rotate(Direction.RIGHT, .01)
                    .move(Direction.REVERSE, 2.1)
                    .armUp(.5)
                    .rotate(Direction.RIGHT, .41)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.3);
        }
    }

    @Autonomous
    public static class StonesLeft3 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, false));
        }

        static void go(Controller controller) {
            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .7)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.4)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.7)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3.0);
                  /*  .armUp(.7)
                    .move(Direction.REVERSE, .6)
                    .armDown(.3)
                    .move(Direction.REVERSE, 1.5)
                    .sleep(.3)
                    .rotate(Direction.RIGHT, .01)
                    .move(Direction.REVERSE, 1.5)
                    .armUp(.7)
                    .rotate(Direction.RIGHT, .41)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.9)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3.6)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6)
                    .armDown(.3);

                    */
        }
    }



    @Disabled
    @Autonomous
    public static class SensorTest extends LinearOpMode {
        @Override
        public void runOpMode() {
            Controller c = startSequence(this, false);

            SensorManager manager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor == null) {
                updateStatus("Gyro is null");
            }
            SensorEventListener listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    updateStatus(Arrays.toString(event.values));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {}
            };
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            c.moveByTime()
                    .sleep(10);
            manager.unregisterListener(listener);
        }

        private void updateStatus(Object o) {
            telemetry.addData("Status", o);
            telemetry.update();
            sleep(1000);
        }
    }
}
