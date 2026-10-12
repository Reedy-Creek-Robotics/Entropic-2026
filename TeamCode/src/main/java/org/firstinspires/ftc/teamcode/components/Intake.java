package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.components.subComponents.BaseComponent;
import org.firstinspires.ftc.teamcode.components.subComponents.Command;
import org.firstinspires.ftc.teamcode.components.subComponents.RobotContext;
import org.firstinspires.ftc.teamcode.util.HardwareUtil;
import org.firstinspires.ftc.teamcode.util.LogCatUtil;

//@Configurable
public class Intake extends BaseComponent {

    LogCatUtil log;
    HardwareUtil hardwareUtil;

    Servo intakeServo;

    DcMotorEx transferMotor;

    Robot robot;

    /**
     * false - red (tag 24)<br>
     * true - blue (tag 20)
     */
    boolean alliance;

    /**
     * Will be appended to the prefix defined in LogCatUtil
     */
    static String logTag = "Intake";

    public Intake(RobotContext context, Robot robot) {
        super(context);

        log = new LogCatUtil(logTag);

        hardwareUtil = new HardwareUtil(log, hardwareMap);

        this.alliance = context.alliance;

        this.robot = robot;

        intakeServo = hardwareUtil.getServo("intake");
        transferMotor = hardwareUtil.getMotorEx("transfer");
    }

    @Override
    public void init() {
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setIntakePower(double power){
        intakeServo.setPosition((power+1)/2);
    }

    public void runIntakeCommand(double power){
        robot.executeCommand(new RunIntake(power));
    }

    public void timedIntake(double power, double timeMs){
        robot.executeCommand(new TimedIntake(power, timeMs));
    }

    public void setTransferPower(double power){
        transferMotor.setPower(power);
    }

    public void runTransferCommand(double power){
        robot.executeCommand(new RunTransfer(power));
    }

    public void timedTransfer(double power, double timeMs){
        robot.executeCommand(new TimedTransfer(power, timeMs));
    }

    private class RunIntake implements Command {
        double power;

        public RunIntake(double power){
            this.power = power;
        }

        @Override
        public void start() {
            setIntakePower(power);
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean update() {
            return true;
        }
    }

    private class TimedIntake implements Command {
        ElapsedTime timer;
        double power;
        double timeMs;

        public TimedIntake(double power, double timeMs){
            this.power = power;
            this.timeMs = timeMs;
            timer = new ElapsedTime();
        }

        @Override
        public void start() {
            setIntakePower(power);
        }

        @Override
        public void stop() {
            setIntakePower(0);
        }

        @Override
        public boolean update() {
            return timer.milliseconds() >= timeMs;
        }
    }

    private class RunTransfer implements Command {
        double power;

        public RunTransfer(double power){
            this.power = power;
        }

        @Override
        public void start() {
            setTransferPower(power);
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean update() {
            return true;
        }
    }

    private class TimedTransfer implements Command {
        ElapsedTime timer;
        double power;
        double timeMs;

        public TimedTransfer(double power, double timeMs){
            this.power = power;
            this.timeMs = timeMs;
            timer = new ElapsedTime();
        }

        @Override
        public void start() {
            setTransferPower(power);
        }

        @Override
        public void stop() {
            setTransferPower(0);
        }

        @Override
        public boolean update() {
            return timer.milliseconds() >= timeMs;
        }
    }

}
