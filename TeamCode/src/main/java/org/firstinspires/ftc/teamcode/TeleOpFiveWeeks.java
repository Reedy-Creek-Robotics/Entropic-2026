package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.components.Intake;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.game.Controller;

public class TeleOpFiveWeeks extends OpMode {

    Robot robot;

    Intake intake;

    Controller controller;

    @Override
    public void init() {
        robot = new Robot(this);
        intake = robot.getIntake();
        controller = new Controller(gamepad1);
    }

    @Override
    public void loop() {
        robot.getDriveTrain().drive(
                controller.leftStickY(),
                controller.leftStickX(),
                controller.rightStickX()
        );

//        intake.setIntakePower(controller.rightTrigger()-controller.leftTrigger());

//        intake.setTransferPower();
    }
}
