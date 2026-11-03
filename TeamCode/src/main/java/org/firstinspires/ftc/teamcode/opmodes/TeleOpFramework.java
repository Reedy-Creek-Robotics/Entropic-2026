package org.firstinspires.ftc.teamcode.opmodes;

import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_X;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_Y;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.RIGHT_STICK_X;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.components.core.BaseComponent;
import org.firstinspires.ftc.teamcode.components.core.RobotContext;
import org.firstinspires.ftc.teamcode.game.Controller;

import java.util.Map;

@TeleOp(name = "TeleOpFramework: Do Not Run")
public class TeleOpFramework extends OpMode{
    protected RobotContext robotContext;
    protected Robot robot;
    protected Controller driver;
    protected Controller meta;

    protected boolean isDriverRelative = false;
    /*
    Control maps are maps of each controller button to its effect as a function
     */
    protected Map<Controller.Button, ButtonEffect> controlMapDriver;
    protected Map<Controller.Button, ButtonEffect> controlMapMeta;

    @Override
    public void init() {
        robotContext = BaseComponent.createRobotContext(this);

        robot = new Robot(this, false);
        driver = new Controller(gamepad1);
        meta = new Controller(gamepad2);

        robot.init();
    }

    @Override
    public void loop() {
        // drivetrain movement
        double drive, strafe, turn;
        drive = driver.analogValue(LEFT_STICK_Y);
        strafe = driver.analogValue(LEFT_STICK_X);
        turn = driver.analogValue(RIGHT_STICK_X);
        if (! isDriverRelative)
            robot.getDriveTrain().drive(drive, strafe, turn);
        else
            robot.getDriveTrain().driverRelative(drive, strafe, turn);

        // BUTTONS
        // run through all driver buttons
        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapDriver.entrySet()) {
            Controller.Button button = control.getKey();
            ButtonEffect effect = control.getValue();

            if (driver.isButtonDown(button))
                effect.run();
        }

        // run through all meta buttons
        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapMeta.entrySet()) {
            Controller.Button button = control.getKey();
            ButtonEffect effect = control.getValue();

            if (meta.isButtonDown(button))
                effect.run();

        }

        /* UPDATE ROBOT*/
        robot.update();
    }

    @FunctionalInterface
    protected interface ButtonEffect {void run();}
}
