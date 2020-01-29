package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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

    @Disabled
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
    public static class StonesLeft1 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);
            //89 instead of 90 to attempt to fix tilt on long reverse time

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .18)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .move(Direction.FORWARD, 1.3)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.5)
                    .setPower(1)
                    .move(Direction.REVERSE, .6)
                    .rotate(Direction.LEFT, .41)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation("towards bridge").moveByTime()
                    .move(Direction.REVERSE, .6)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2.6)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation(Direction.RIGHT, .41, "towards blocks").moveByTime()
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
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.moveBySensor().saveOrientation("towards blocks");

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .26)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .move(Direction.FORWARD, 1.3)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.4)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2.1)
                    .armUp(.7)
                    //first stone is placed
                    .move(Direction.REVERSE, .5)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2.8)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation(Direction.RIGHT, .41, "towards blocks").moveByTime()
                    .setPower(.3)
                    .move(Direction.FORWARD, 1)
                    .holdArmDown(.7)
                    .setPower(1)
                    .move(Direction.REVERSE, .4)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3.5)
                    .armUp(.5)
                    .move(Direction.REVERSE, .35);
        }
    }

    @Autonomous
    public static class StonesLeft3 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.moveBySensor().saveOrientation("towards block");


            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .65)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards block").moveByTime()
                    .move(Direction.FORWARD, 1.4)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.7)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.6)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6)
                    .armDown(.3)
                    .move(Direction.REVERSE, 1.5)
                    .sleep(.3)
                    .move(Direction.REVERSE, 1.5)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation(Direction.RIGHT, .41,"towards block").moveByTime()
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.35)
                    .holdArmDown(.5)
                    .setPower(1)
                    .move(Direction.REVERSE, .5)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3.2);
        }
    }

    @Autonomous
    public static class StonesRight1 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            StonesLeft1.go(startSequence(this, true).flip());
        }
    }

    @Autonomous
    public static class StonesRight2 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            StonesLeft2.go(startSequence(this, true).flip());
        }
    }

    @Autonomous
    public static class StonesRight3 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            StonesLeft3.go(startSequence(this, true).flip());
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
                telemetry.addData("Status", "sensor is null");
                telemetry.update();
                sleep(2000);
            } else {
                //todo attempt smoothing
                final float[] prevValues = new float[3];
                SensorEventListener listener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        float[] delta = new float[3];
                        for (int i = 0; i < 3; i++) {
                            delta[i] = event.values[i] - prevValues[i];
                        }
                        System.arraycopy(event.values, 0, prevValues, 0, 3);
                        telemetry.addData("X", "%+f", delta[0])
                                .addData("Y", "%+f", delta[1])
                                .addData("Z", "%+f", delta[2]);
                        telemetry.update();
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {
                    }
                };
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                c.moveByTime()
                        .sleep(10);
                manager.unregisterListener(listener);
            }
        }
    }
}
