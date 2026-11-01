package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.components.Shooter;
import org.firstinspires.ftc.teamcode.game.Controller;

//@Disabled
@TeleOp
public class ShooterVelocityTest extends OpMode {

    Controller controller;

    DcMotorEx shooter1;

    VoltageSensor voltage;

    double power1 = 100;
    double avgInactiveCurrent = 1;

    boolean reversed = false;
    boolean active;

    int velocityTolerance = 50;

    static PIDFCoefficients pidf = new PIDFCoefficients(600, 3, 0, 0, MotorControlAlgorithm.PIDF);

//    private static double SHOOTER_CURRENT_THRESHOLD = 0.5;

    @Override
    public void init() {

        shooter1 = hardwareMap.get(DcMotorEx.class, "shooter1");

        controller = new Controller(gamepad1);
        voltage = hardwareMap.voltageSensor.iterator().next();
    }

    @Override
    public void loop() {
        telemetry.addLine("Controls:");
        telemetry.addLine("NORTH to start");
        telemetry.addLine("SOUTH to run transfer up");
        telemetry.addLine("LEFT STICK to reverse");
        telemetry.addLine("DPAD UP/DOWN to change power x100");
        telemetry.addLine("DPAD LEFT/RIGHT to change power x20");
        telemetry.addLine("------------------------");

//        setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        if(controller.isPressed(Controller.Button.DPAD_UP)){
            power1 += 100;
        }else if(controller.isPressed(Controller.Button.DPAD_DOWN)){
            power1 -= 100;
        }

        if(controller.isPressed(Controller.Button.DPAD_RIGHT)){
            power1 += 20;
        }else if(controller.isPressed(Controller.Button.DPAD_LEFT)){
            power1 -= 20;
        }

        if(power1 > 2240) power1 = 2240;
        if(power1 < 0) power1 = 0;

        if(controller.isPressed(Controller.Button.LEFT_STICK_BUTTON)){
            reversed = !reversed;
        }

        if(controller.isPressed(Controller.Button.SOUTH)){
            active = !active;
        }

        telemetry.addData("Active", active);
        telemetry.addData("Reversed", reversed);
        telemetry.addData("Set Power (tps)", power1);

        if(active){
            shooter1.setDirection(reversed ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
            shooter1.setVelocity(power1);
        }else{
            shooter1.setVelocity(0);
            shooter1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        /*

         */

        telemetry.addData("Velocity 1", shooter1.getVelocity());
        telemetry.addData("Current", shooter1.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Voltage", voltage.getVoltage());
        telemetry.addData("Avg Inactive", avgInactiveCurrent);

        telemetry.update();
    }

//    private boolean isShot(){
//        return shooter.getCurrent(CurrentUnit.AMPS) > baseline + SHOOTER_CURRENT_THRESHOLD || timeoutTimer.milliseconds() > TIMEOUT_THRESHOLD;
//    }

    public void setPIDFCoefficients(DcMotor.RunMode runMode, PIDFCoefficients coefficients) {
        PIDFCoefficients compensatedCoefficients = new PIDFCoefficients(
                coefficients.p, coefficients.i, coefficients.d,
                coefficients.f * 12 / voltage.getVoltage()
        );

        shooter1.setPIDFCoefficients(runMode, compensatedCoefficients);
    }
}