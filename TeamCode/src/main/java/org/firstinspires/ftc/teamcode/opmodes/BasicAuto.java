package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.Robot;

@Autonomous
public class BasicAuto extends LinearOpMode {
    ElapsedTime timer;
    Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this);
        timer = new ElapsedTime();
        robot.init();

        waitForStart();

        robot.getShooter().closeGate();
        robot.getShooter().setVelocity(1000);
        while(robot.getShooter().getVelocity() < 980 && opModeIsActive()){
            telemetry.addLine("Waiting for velocity...");
            telemetry.update();
        }
        robot.getShooter().openGate();
        robot.getIntake().setCombinedPower(1);
        timer.reset();
        while(timer.milliseconds() < 10000 && opModeIsActive()){
            telemetry.addLine("Shooting...");
            telemetry.update();
        }
        timer.reset();
        robot.getDriveTrain().drive(0.5, 0, 0);
        while(timer.milliseconds() < 500 && opModeIsActive()){
            telemetry.addLine("Driving...");
            telemetry.update();
        }
    }
}
