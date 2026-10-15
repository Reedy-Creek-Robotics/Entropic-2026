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

@TeleOp(name = "RI5W basic tele")
public class BasicShootDrive extends OpMode {
    RobotContext robotContext;
    Robot robot;

    int shooterSpeed = 1500;

    boolean shooterOn = false;

    protected Controller driver;
    protected Controller meta;

    // Control maps map each button to its functionality for clean assignment and quick looping
    // analog inputs will need to be handled separately
//    Map<Controller.Button, ButtonEffect> controlMapDriver = Map.of(
//            Controller.Button.NORTH, this::shoot,
//            Controller.Button.DPAD_DOWN, this::purge,
//            Controller.Button.DPAD_UP, this::intake,
//    );
//    Map<Controller.Button, ButtonEffect> controlMapMeta = Map.of(
//            // add meta controls here
//    );


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
        /* DRIVER CONTROLS */
        //drive, strafe, and turning
        double drive, strafe, turn;
        drive = driver.analogValue(LEFT_STICK_Y);
        strafe = driver.analogValue(LEFT_STICK_X);
        turn = driver.analogValue(RIGHT_STICK_X);
        robot.getDriveTrain().drive(drive, strafe, turn);

        if(driver.isButtonDown(Controller.Button.RIGHT_BUMPER) || driver.isButtonDown(Controller.Button.LEFT_BUMPER)){
            robot.getShooter().openGate();
        }else{
            robot.getShooter().closeGate();
        }

        double intakePower = driver.rightTrigger()-driver.leftTrigger();

        robot.getIntake().setCombinedPower(intakePower);

        if(driver.isPressed(Controller.Button.DPAD_UP)){
            shooterSpeed += 100;
        }

        if(driver.isPressed(Controller.Button.DPAD_DOWN)){
            shooterSpeed -= 100;
        }

        if(driver.isPressed(Controller.Button.A)){
            shooterOn = !shooterOn;
        }

        robot.getShooter().setVelocity(shooterOn ? shooterSpeed : 0);

//        // run through all driver buttons
//        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapDriver.entrySet()) {
//            if (driver.isButtonDown(control.getKey())) {
//                control.getValue().run();
//            }
//        }
//
//        /* META CONTROLS */
//        // run through all meta buttons
//        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapMeta.entrySet()) {
//            if (meta.isButtonDown(control.getKey())) {
//                control.getValue().run();
//            }
//        }

        /* UPDATE OTHER STUFF */
        telemetry.addData("alliance", robotContext.getAlliance());
        telemetry.addData("Shooter On", shooterOn);
        robot.update();
    }

    /**
     * The ButtonEffect functional interface stores the effect of a button press as a method
     */
    @FunctionalInterface
    private interface ButtonEffect {
        public void run();
    }
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
