package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.Frame;
import com.vuforia.Image;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

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

    private static ControllerScanner startSeeingSequence(
            LinearOpMode mode,
            boolean useSensors,
            int x,
            int y,
            int width,
            int height
    ) throws InterruptedException {
        Context context = mode.hardwareMap.appContext;
        Telemetry telemetry = mode.telemetry;
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
        int tfodMonitorViewId = context.getResources().getIdentifier(
                "tfodMonitorViewId", "id", context.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        TFObjectDetector tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        Controller controller = new Controller(mode, useSensors);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        mode.waitForStart();

        telemetry.addData("Status", "Scanning");
        telemetry.update();

        tfod.activate();
        Frame frame = vuforia.getFrameQueue().take();
        Image image = frame.getImage(0);
        tfod.deactivate();

        SkyStoneScanner scanner =
                new SkyStoneScanner(image, x, y, width, height)
                .getLines(11, 10);
        telemetry.addData("Status", "Started");
        telemetry.update();
        return new ControllerScanner(controller, scanner);
    }

    @Autonomous(group = "Test", name = "Test")
    public static class Test extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            Controller controller = startSequence(this, true);
            controller.holdArmDown(1);
            for (int i = 0; i < 5; i++) {
                controller.moveByTime()
                        .move(Direction.FORWARD, 2.5)
                        .sleep(1)
                        .move(Direction.REVERSE, 2.5)
                        .sleep(1);
            }
        }
    }

    @Autonomous(group = "Foundation", name = "Foundation Left")
    public static class FoundationLeft extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, false));
        }

        static void go(Controller controller) {
            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .5)
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

    @Autonomous(group = "Foundation", name = "Foundation Right")
    public static class FoundationRight extends LinearOpMode {

        @Override
        public void runOpMode() throws InterruptedException {
            FoundationLeft.go(startSequence(this, false).flip());
        }
    }

    @Autonomous(group = "SkyStone", name = "SkyStone Left")
    public static class SkyStonesLeft extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            ControllerScanner controllerScanner = startSeeingSequence(this, true, 360, 0, 90, 280);
            if (controllerScanner.scanner.fitsBetween(144, 280, 3)) {
                logIntent("1");
                StonesLeft1.go(controllerScanner.controller);
            } else if (controllerScanner.scanner.fitsBetween(23, 151, 3)) {
                logIntent("2");
                StonesLeft2.go(controllerScanner.controller);
            } else {
                logIntent("3");
                StonesLeft3.go(controllerScanner.controller);
            }
        }

        private void logIntent(String value) {
            telemetry.addData("Left", value);
            telemetry.update();
        }
    }

    @Autonomous(group = "SkyStone", name = "SkyStone Right")
    public static class SkyStonesRight extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            ControllerScanner controllerScanner = startSeeingSequence(this, true, 360, 0, 90, 280);
            if (controllerScanner.scanner.fitsBetween(0, 101, 3)) {
                logIntent("1");
                StonesRight1.go(controllerScanner.controller);
            } else if (controllerScanner.scanner.fitsBetween(96, 225, 3)) {
                logIntent("2");
                StonesRight2.go(controllerScanner.controller);
            } else {
                logIntent("3");
                StonesRight3.go(controllerScanner.controller);
            }
        }

        private void logIntent(String value) {
            telemetry.addData("Right", value);
            telemetry.update();
        }
    }

    @Disabled
    @Autonomous(group = "Stones Left", name = "SkyStone Left 1")
    public static class StonesLeft1 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);
            //89 instead of 90 to fix tilt on long reverse time

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .25)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .move(Direction.FORWARD, 1.3)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.5)
                    .setPower(1)
                    .move(Direction.REVERSE, .4)
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
                    .holdArmDown(.7)
                    .move(Direction.REVERSE, 1.9)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3)
                    .armUp(.6)
                    .move(Direction.REVERSE, .4)
                    .sleep(.3);
        }
    }

    @Disabled
    @Autonomous(group = "Stones Left", name = "SkyStone Left 2")
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
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .sleep(.3)
                    .move(Direction.RIGHT, .26)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.2)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.7)
                    .move(Direction.REVERSE, 1.75)
                    .setPower(1)
                    .moveBySensor().gotoOrientation(Direction.LEFT, .41, "towards bridge").moveByTime()
                    .sleep(.3)
                    .move(Direction.FORWARD, 2.2)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation("towards bridge").moveByTime()
                    //first stone is placed
                    .move(Direction.REVERSE, .5)
                    .armDown(.3)
                    .move(Direction.REVERSE, 2.8)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation(Direction.RIGHT, .41, "towards blocks").moveByTime()
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.7)
                    .setPower(1)
                    .move(Direction.REVERSE, .45)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 3.3)
                    .armUp(.5)
                    .move(Direction.REVERSE, .4);
        }
    }

    @Disabled
    @Autonomous(group = "Stones Left", name = "SkyStone Left 3")
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
                    .move(Direction.FORWARD, 1.4)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .sleep(.3)
                    .setPower(.5)
                    .move(Direction.RIGHT, 1.6)
                    .setPower(1)
                    .sleep(.3)
                    .move(Direction.FORWARD, .2)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.2)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.7)
                    .setPower(1)
                    .rotate(Direction.LEFT, .41)
                    .move(Direction.FORWARD, 2.8)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6);
        }
    }

    @Autonomous(group = "Stones Right", name = "SkyStone Right 1")
    public static class StonesRight1 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.flip();
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .move(Direction.LEFT, .5)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .move(Direction.FORWARD, 1.3)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.5)
                    .setPower(1)
                    .move(Direction.REVERSE, .4)
                    .rotate(Direction.LEFT, .42)
                    .sleep(.3)
                    .move(Direction.FORWARD, 2)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6)
                    .armDown(.3);
        }
    }

    @Autonomous(group = "Stones Right", name = "SkyStone Right 2")
    public static class StonesRight2 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.flip();
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);

            controller.moveByTime()
                    .move(Direction.FORWARD, .2)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .sleep(.3)
                    .move(Direction.RIGHT, .26)
                    .sleep(.3)
                    .move(Direction.FORWARD, 1.2)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.3)
                    .holdArmDown(.7)
                    .move(Direction.REVERSE, 1.6)
                    .setPower(1)
                    .moveBySensor().gotoOrientation(Direction.LEFT, .41, "towards bridge").moveByTime()
                    .sleep(.3)
                    .move(Direction.FORWARD, 2.2)
                    .armUp(.7)
                    .moveBySensor().gotoOrientation("towards bridge").moveByTime()
                    //first stone is placed
                    .move(Direction.REVERSE, .5)
                    .armDown(.3);
        }
    }

    @Autonomous(group = "Stones Right", name = "SkyStone Right 3")
    public static class StonesRight3 extends LinearOpMode {
        @Override
        public void runOpMode() throws InterruptedException {
            go(startSequence(this, true));
        }

        static void go(Controller controller) {
            controller.flip();
            controller.moveBySensor()
                    .saveOrientation("towards blocks")
                    .saveOrientation("towards bridge", Direction.LEFT, 89);

            controller.moveByTime()
                    .move(Direction.FORWARD, 1.4)
                    .sleep(.3)
                    .moveBySensor().gotoOrientation("towards blocks").moveByTime()
                    .sleep(.3)
                    .setPower(.5)
                    .move(Direction.RIGHT, 1.45)
                    .setPower(1)
                    .sleep(.3)
                    .move(Direction.FORWARD, .2)
                    .setPower(.3)
                    .move(Direction.FORWARD, 1.1)
                    .holdArmDown(.5)
                    .move(Direction.REVERSE, 1.9)
                    .setPower(1)
                    .rotate(Direction.LEFT, .42)
                    .move(Direction.FORWARD, 2.8)
                    .armUp(.7)
                    .move(Direction.REVERSE, .6);
        }
    }

    @Autonomous(group = "SensorTest", name = "Sensor Test")
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
                //attempt smoothing
                final float[] prevValues = new float[3];
                final Integrator integrator = new Integrator();
                SensorEventListener listener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        integrator.update(event.values);
                        telemetry
                                .addData("Accel X", "%+f", integrator.acceleration.xAccel)
                                .addData("Accel Y", "%+f", integrator.acceleration.yAccel)
                                .addData("Accel Z", "%+f", integrator.acceleration.zAccel)
                                .addData("Accel A", "%d", integrator.acceleration.acquisitionTime)
                                .addData("Veloc X", "%+f", integrator.velocity.xVeloc)
                                .addData("Veloc Y", "%+f", integrator.velocity.yVeloc)
                                .addData("Veloc Z", "%+f", integrator.velocity.zVeloc)
                                .addData("Veloc A", "%d", integrator.velocity.acquisitionTime)
                                .addData("Posit X", "%+f", integrator.position.x)
                                .addData("Posit Y", "%+f", integrator.position.y)
                                .addData("Posit Z", "%+f", integrator.position.z)
                                .addData("Posit A", "%d", integrator.position.acquisitionTime);
                        telemetry.update();
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {
                    }
                };
                manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                c.moveByTime()
                        .sleep(120);
                manager.unregisterListener(listener);
            }
        }
    }

    /**
     * Basic pairing class for usage with the sight-based modes.
     */
    private static class ControllerScanner {
        final Controller controller;
        final SkyStoneScanner scanner;

        private ControllerScanner(Controller controller, SkyStoneScanner scanner) {
            this.controller = controller;
            this.scanner = scanner;
        }
    }
}
