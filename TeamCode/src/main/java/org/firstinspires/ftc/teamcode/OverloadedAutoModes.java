package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.Arrays;
import java.util.List;

public final class OverloadedAutoModes {
    private OverloadedAutoModes(){}

    private static OverloadedController startSequence(LinearOpMode mode) {
        OverloadedController controller = new OverloadedController(mode);
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
            OverloadedController c = startSequence(this);

            c
                    .holdArmDown(2000);
        }
    }

    @Autonomous
    public static class Brick extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            OverloadedController c = startSequence(this);

            c
                    .timeForward(1600)
                    .setPower(.3)
                    .timeForward(200)
                    .holdArmDown(500)
                    .timeReverse(150)
                    .setPower(1)
                    .timeRotLeft(400)
                    .timeForward(2000)
                    .armUp(700)
                    .timeLeft(300)
                    .timeReverse(500)
                    .armDown(300)
                    .timeReverse(1800)
                    .armUp(700)
                    .timeRotRight(400)
                    .setPower(.3)
                    .timeForward(1200)
                    .holdArmDown(500)
                    .sleep(500)
                    .timeReverse(270)
                    .setPower(1)
                    .timeRotLeft(400)
                    .timeForward(2300)
                    .armUp(700)
                    .timeReverse(500);
        }
    }

    @Autonomous
    public static class MoveLeftFoundation extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            OverloadedController c = startSequence(this);

            c.timeForward(200);
            c.timeLeft(450);
            c.timeForward(1600);
            c.arm.setPosition(1);
            c.sleep(1200);
            c.setPower(.5);
            c.timeReverse(4800);
            c.armUp(700);
            c.setPower(1);
            c.timeForward(200);
            c.timeRight(2600);
        }
    }

    @Autonomous
    public static class MoveRightFoundation extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            OverloadedController c = startSequence(this);

            c.timeForward(200);
            c.timeRight(450);
            c.timeForward(1600);
            c.arm.setPosition(1);
            c.sleep(1200);
            c.setPower(.5);
            c.timeReverse(4800);
            c.armUp(700);
            c.setPower(1);
            c.timeForward(200);
            c.timeLeft(2600);
        }
    }

    @Disabled
    @Autonomous
    public static class TensorFlow extends LinearOpMode {

        @SuppressWarnings("SpellCheckingInspection")
        private static final String VUFORIA_KEY =
                "AW7zmbr/////AAABma7Jq+OAOU1CuaonIFUo0/xJJUyI2A02nsbVBSLuwjlJM3+Po0DLAtKsPIaRZkLN0rYB" +
                        "cSHwhv3r3NmFOMqvOx7Wa88CX+uDNWQhrYOAc27kw3usgqIGWmHpO/1onWmWEv0u6hQX/69KUsN/" +
                        "51vAKJrrd58/KOAlSVlLsQH4K5uI0qT0EAVh1FYCd46wG7pBlTdLcDH1QYzSyeDvPklhNEFMRvUE" +
                        "BpOd9eF1gMunhIagFnSBjA1c89ylVx3RDAlsirW3N97jtzd/Eq3Sr0aznz+7Gar5OtxRUtuoCBMf" +
                        "kAfwkgqtHppySXbRcMGaaC+VtLbeNWWjWTWBczIcoqVH1DqPG5NDn/sP+X9hMV7q+MUA";

        @SuppressLint("DefaultLocale")
        @Override
        public void runOpMode() {
            VuforiaLocalizer vuforia = initVuforia();
            TFObjectDetector detector = initDetector(vuforia);

            telemetry.addData(">", "Press Play to start op mode");
            telemetry.update();
            waitForStart();

            if (opModeIsActive()) {
                while (opModeIsActive()) {
                    List<Recognition> updatedRecognitions = detector.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i++), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                        }
                        telemetry.update();
                    }
                }
            }

            detector.shutdown();
        }

        private VuforiaLocalizer initVuforia() {
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
            parameters.vuforiaLicenseKey = VUFORIA_KEY;
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
            return ClassFactory.getInstance().createVuforia(parameters);
        }

        private TFObjectDetector initDetector(VuforiaLocalizer vuforia) {
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                    "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfodParameters.minimumConfidence = 0.8;
            TFObjectDetector tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset("Skystone.tflite", "Stone", "Skystone");
            tfod.activate();
            return tfod;
        }
    }

    @Disabled
    @Autonomous
    public static class SensorTest extends LinearOpMode {
        @Override
        public void runOpMode() {
            OverloadedController c = startSequence(this);

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
            c
                    .setPower(.3)
                    .timeRight(1000)
                    .timeLeft(1000)
                    .timeForward(1000)
                    .timeReverse(1000);
            manager.unregisterListener(listener);
        }

        private void updateStatus(Object o) {
            telemetry.addData("Status", o);
            telemetry.update();
            sleep(1000);
        }
    }
}
