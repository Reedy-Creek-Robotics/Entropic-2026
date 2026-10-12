package org.firstinspires.ftc.teamcode.opmodes;

import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_X;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_Y;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.RIGHT_STICK_X;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.components.subComponents.BaseComponent;
import org.firstinspires.ftc.teamcode.components.subComponents.RobotContext;
import org.firstinspires.ftc.teamcode.game.Controller;

import java.util.Map;

@TeleOp(name = "RI5W basic tele")
public class BasicShootDrive extends OpMode {
    RobotContext robotContext;
    Robot robot;

    protected Controller driver;
    protected Controller meta;

    Map<Controller.Button, ButtonEffect> controlMapDriver;
    Map<Controller.Button, ButtonEffect> controlMapMeta;


    @Override
    public void init() {
        robotContext = BaseComponent.createRobotContext(this);

        robot = new Robot(this, false);
        driver = new Controller(gamepad1);
        meta = new Controller(gamepad2);

        robot.init();

        controlMapDriver = Map.of(
                Controller.Button.NORTH, this::shoot,
                Controller.Button.DPAD_UP, this::intake,
                Controller.Button.DPAD_DOWN, this::purge
        );
        controlMapMeta = Map.of(

        );
    }

    @Override
    public void loop() {
        /* DRIVER CONTROLS */

        //drive, strafe, and turning
        double drive, strafe, turn;
        drive = driver.analogValue(LEFT_STICK_Y);
        strafe = driver.analogValue(LEFT_STICK_X);
        turn = driver.analogValue(RIGHT_STICK_X);
        robot.getDriveTrain().drive(drive, strafe, turn);

        // run through all driver buttons
        for (Controller.Button button: controlMapDriver.keySet()) {
            if (driver.isButtonDown(button)) {
                controlMapDriver.get(button).run();
            }
        }

        /* META CONTROLS */

        // run through all meta buttons
        for (Controller.Button button: controlMapDriver.keySet()) {
            if (meta.isButtonDown(button)) {
                controlMapDriver.get(button).run();
            }
        }

        /* UPDATE TELEMETRY */

        telemetry.addData("alliance", robotContext.getAlliance());
        robot.update();
    }

    /**
     * The Action functional interface stores the effect
     */
    @FunctionalInterface
    private interface ButtonEffect {
        public void run();
    }
    private void noAction(){}
    private void shoot() {
        //TODO: Implement
    }
    private void intake() {
        robot.getIntake().setIntakePower(1);
    }
    private void purge() {
        robot.getIntake().setIntakePower(-1);
    }

}
