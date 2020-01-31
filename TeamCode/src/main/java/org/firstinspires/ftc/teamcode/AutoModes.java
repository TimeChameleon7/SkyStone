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
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;

public class AutoModes {
    private AutoModes(){}
    //todo diagrams
    private static Controller startSequence(LinearOpMode mode, boolean useSensors) {
        Controller controller = new Controller(mode, useSensors);
        mode.telemetry.addData("Status", "Initialized");
        mode.telemetry.update();
        mode.waitForStart();
        mode.telemetry.addData("Status", "Started");
        mode.telemetry.update();
        return controller;
    }

    private static TFObjectDetector getDetector(LinearOpMode mode) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        //noinspection SpellCheckingInspection
        parameters.vuforiaLicenseKey = "AW7zmbr/////AAABma7Jq+OAOU1CuaonIFUo0/xJJUyI2A02nsbVBSLuw" +
                "jlJM3+Po0DLAtKsPIaRZkLN0rYBcSHwhv3r3NmFOMqvOx7Wa88CX+uDNWQhrYOAc27kw3usgqIGWmHpO" +
                "/1onWmWEv0u6hQX/69KUsN/51vAKJrrd58/KOAlSVlLsQH4K5uI0qT0EAVh1FYCd46wG7pBlTdLcDH1Q" +
                "YzSyeDvPklhNEFMRvUEBpOd9eF1gMunhIagFnSBjA1c89ylVx3RDAlsirW3N97jtzd/Eq3Sr0aznz+7G" +
                "ar5OtxRUtuoCBMfkAfwkgqtHppySXbRcMGaaC+VtLbeNWWjWTWBczIcoqVH1DqPG5NDn/sP+X9hMV7q+" +
                "MUA";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        int tfodMonitorViewId = mode.hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", mode.hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.5;
        TFObjectDetector tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset("Skystone.tflite", "Stone", "Skystone");
        return tfod;
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
    public static class StonesLeftTensor extends LinearOpMode {
        long start = System.currentTimeMillis();

        @Override
        public void runOpMode() throws InterruptedException {
            TFObjectDetector detector = getDetector(this);
            Controller controller = startSequence(this, true);
            detector.activate();
            ArrayList<Recognition> recognitions = new ArrayList<>();
            while (opModeIsActive() && System.currentTimeMillis() - start >= 500) {
                List<Recognition> updatedRecognitions = detector.getUpdatedRecognitions();
                if (updatedRecognitions!=null) {
                    ArrayList<Recognition> remove = new ArrayList<>();
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals("Stone")) remove.add(recognition);
                    }
                    updatedRecognitions.removeAll(remove);
                    recognitions.addAll(updatedRecognitions);
                }
            }
            detector.shutdown();
            double angle = 0;
            for (Recognition recognition : recognitions) {
                angle += recognition.estimateAngleToObject(AngleUnit.DEGREES);
            }
            if (recognitions.size() != 0) {
                angle /= recognitions.size();
                telemetry.addData("Average Angle", "%.3f", angle);
                if (5 <= angle && angle <= 7) {
                    telemetry.addData("Stones Left", "1");
                    telemetry.update();
                    StonesLeft1.go(controller);
                } else {
                    telemetry.addData("Stones Left", "2");
                    telemetry.update();
                    StonesLeft2.go(controller);
                }
            } else {
                telemetry.addData("Stones Left", "3");
                telemetry.update();
                StonesLeft3.go(controller);
            }
        }
    }

    @Disabled
    @Autonomous
    public static class StonesRightTensor extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, true);

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
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .26)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.3)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.7)
                    .move(Direction.REVERSE, 1.4)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2.1)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation("towards bridge").moveByTime()
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
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);


            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.RIGHT, .65)
                    .sleep(.3)
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
                    .moveBySensor().gotoOrientation(Direction.RIGHT, .41,"towards blocks").moveByTime()
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
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            StonesLeft1.go(controller.flip());
        }
    }

    @Autonomous
    public static class StonesRight2 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            StonesLeft2.go(controller.flip());
        }
    }

    @Autonomous
    public static class StonesRight3 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            StonesLeft3.go(controller.flip());
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
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                        telemetry.addData("  confidence", recognition.getConfidence());
                        telemetry.addData("  dimensions", "%d %d",
                                recognition.getImageWidth(), recognition.getImageHeight());
                        telemetry.addData("  angle", "%.3f", recognition.estimateAngleToObject(AngleUnit.DEGREES));
                    }
                    telemetry.update();
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
            tfodParameters.minimumConfidence = 0.5;
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
