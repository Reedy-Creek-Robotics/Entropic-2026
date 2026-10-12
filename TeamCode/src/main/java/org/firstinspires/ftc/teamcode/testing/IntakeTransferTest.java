package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.Intake;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.game.Controller;

@TeleOp(name="Intake/Transfer Test", group="testing")
public class IntakeTransferTest extends OpMode {

    Robot robot;

    Intake intake;

    Controller controllerA;
    Controller controllerB;

    @Override
    public void init() {
        robot = new Robot(this);
        robot.init();
        intake = robot.getIntake();
        controllerA = new Controller(gamepad1);
        controllerB = new Controller(gamepad2);
    }

    @Override
    public void loop() {
        robot.getDriveTrain().drive(
                controllerB.leftStickY(),
                controllerB.leftStickX(),
                controllerB.rightStickX()
        );

        intake.setIntakePower(controllerA.rightStickY());

        intake.setTransferPower(controllerA.leftStickY());

        robot.update();
    }
}
