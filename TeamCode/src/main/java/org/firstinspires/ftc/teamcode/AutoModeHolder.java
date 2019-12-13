package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Arrays;

public class AutoModeHolder {
    private static BotController startSequence(LinearOpMode opMode) {
        opMode.telemetry.addData("Status", "Initialized");
        opMode.telemetry.update();
        opMode.waitForStart();
        opMode.telemetry.addData("Status", "Started");
        opMode.telemetry.update();
        return new BotController(opMode.hardwareMap);
    }

    @Autonomous
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() {
            final BotController c = startSequence(this);

            Context context = hardwareMap.appContext;
            SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor == null) {
                updateStatus("Gyro is null");
            }
            waitForStart();
            SensorEventListener listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    updateStatus(Arrays.toString(event.values));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {}
            };
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            c.speed = .3;
            c.right(1000);
            c.left(1000);
            c.forward(1000);
            c.reverse(1000);
            manager.unregisterListener(listener);
        }

        private void updateStatus(Object o) {
            telemetry.addData("Status", o);
            telemetry.update();
            sleep(1000);
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