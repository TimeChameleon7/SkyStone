package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;

@SuppressWarnings("unused")
public class BotController {
    DcMotor motorOne;
    DcMotor motorTwo;
    DcMotor motorThree;
    DcMotor motorFour;
    Servo servoArm;
    Servo servoDump;
    Servo servoHand;
    //todo Alfredo IMU register "BNO055IMU"
    //https://stemrobotics.cs.pdx.edu/node/7265
    double speed;
    private Telemetry telemetry;
    private float[] gravity;
    private final SensorManager manager;
    private final SensorEventListener accelerationListener;

    BotController(OpMode opMode) {
        telemetry = opMode.telemetry;
        statusUpdate("Initializing Hardware");
        HardwareMap hardwareMap = opMode.hardwareMap;
        motorOne  = hardwareMap.get(DcMotor.class, "one");
        motorTwo  = hardwareMap.get(DcMotor.class, "two");
        motorThree  = hardwareMap.get(DcMotor.class, "three");
        motorFour  = hardwareMap.get(DcMotor.class, "four");
        servoHand = hardwareMap.get(Servo.class, "servoOne");
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        servoDump = hardwareMap.get(Servo.class, "servoDump");
        speed = 1;

        statusUpdate("Initializing Accelerometer");
        Context context = opMode.hardwareMap.appContext;
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerationListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float alpha = (float) .8;

                gravity = new float[] {
                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0],
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1],
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                };

                float[] linearAcceleration = new float[]{
                        event.values[0] - gravity[0],
                        event.values[1] - gravity[1],
                        event.values[2] - gravity[2]
                };

                statusUpdate(Arrays.toString(linearAcceleration));
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Sensor accelerator = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(accelerationListener, accelerator, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void statusUpdate(String string) {
        telemetry.addData("Status", string);
        telemetry.update();
    }

    public void unregister() {
        manager.unregisterListener(accelerationListener);
    }
    
    public void armUp(long millis) {
        servoArm.setPosition(0);
        sleep(millis);
        servoArm.setPosition(.5);
    }
    
    public void armDown(long millis) {
        servoArm.setPosition(1);
        sleep(millis);
        servoArm.setPosition(.5);
    }
    
    public void forward(long millis) {
        motorOne.setPower(-speed);
        motorThree.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void reverse(long millis) {
        motorOne.setPower(speed);
        motorThree.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void left(long millis) {
        motorTwo.setPower(-speed * .85);
        motorFour.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void right(long millis) {
        motorTwo.setPower(speed * .85);
        motorFour.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void rightTurn(long millis) {
        motorOne.setPower(-speed);
        motorThree.setPower(-speed);
        motorTwo.setPower(-speed);
        motorFour.setPower(-speed);
        sleep(millis);
        brake();
    }
    
    public void leftTurn(long millis) {
        motorOne.setPower(speed);
        motorThree.setPower(speed);
        motorTwo.setPower(speed);
        motorFour.setPower(speed);
        sleep(millis);
        brake();
    }
    
    public void closeJaw() {
        servoHand.setPosition(0);
        sleep(1000);
    }
    
    public void openJaw() {
        servoHand.setPosition(.8);
        sleep(1000);
    }
    
    public void brake() {
        motorOne.setPower(0);
        motorTwo.setPower(0);
        motorThree.setPower(0);
        motorFour.setPower(0);
    }
    
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
            unregister();
            Thread.currentThread().interrupt();
        }
    }
}
