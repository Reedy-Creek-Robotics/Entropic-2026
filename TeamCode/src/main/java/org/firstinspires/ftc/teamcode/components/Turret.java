package org.firstinspires.ftc.teamcode.components;

import android.util.Size;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.InvertedFTCCoordinates;
import com.pedropathing.ftc.PoseConverter;
import com.pedropathing.geometry.PedroCoordinates;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS.Pose2D;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.components.core.BaseComponent;
import org.firstinspires.ftc.teamcode.components.core.RobotContext;
import org.firstinspires.ftc.teamcode.util.ArrayUtil;
import org.firstinspires.ftc.teamcode.util.HardwareUtil;
import org.firstinspires.ftc.teamcode.util.LogCatUtil;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class Turret extends BaseComponent {

    // GENERAL COMPONENT STUFF
    Robot robot;
    RobotContext context;
    HardwareUtil hardwareUtil;
    LogCatUtil log;
    static String logTag = "Turret"; //  Will be appended to the prefix defined in LogCatUtil


    // TURRET RANGES
    /**in degrees*/
    static double maxHeading = 90;
    /**in degrees*/
    static double minHeading = -180;
    double toleranceDeg = 5;
    double targetPos = 0;
    double targetDeg = 0;
    int turretOffset; // difference between soft readjusted position and true tick reading
    int turretPos;


    // MOTOR SPECIFICATIONS:
    DcMotorEx turretMotor;
    /**in RPM*/
    static int baseMotorSpeed = 1150;
    static double gearRatio = 2.75; // from gobilda website
    /**in kg per cm*/
    static double baseMotorTorque = 7.9;
    static double baseTicksPerRev = 145.1;
    static double baseTicksPerDeg =  baseTicksPerRev / 360.0;
    /**in RPM*/
    static double effectiveSpeed = baseMotorSpeed / gearRatio;
    /**in kg per cm*/
    static double effectiveTorque = baseMotorSpeed * gearRatio;
    static double effectiveTicksPerRev = baseTicksPerRev * gearRatio;
    static double effectiveTicksPerDeg = effectiveTicksPerRev / 360;

    // TARGET POSITIONS:
    static Pose redHive1 = new Pose(58, 52, 0);
    static Pose redHive2 = new Pose(58, 90, 180);
    static Pose blueHive1 = new Pose(84, 52, 0);
    static Pose blueHive2 = new Pose(84, 90, 180);
    Pose currentPosition = new Pose();
    Pose targetGoal;

    // AIMING MODES
    private enum MoveMethods {
        RUN_TO_POSITION,
        PID_IMPOSTER
    }
    MoveMethods moveMethod;
    boolean autoAim = true;
    boolean autoMove = true;

    TouchSensor resetSwitch;

    public Turret(RobotContext context, Robot robot) {
        super(context);

        this.context = context;

        log = new LogCatUtil(logTag);

        hardwareUtil = new HardwareUtil(log, hardwareMap);
        turretMotor = hardwareUtil.getMotorEx("turret");
        resetSwitch = hardwareUtil.getTouchSensor("resetSwitch");

        this.robot = robot;
    }

    @Override
    public void init() {
        super.init();

        // initialize motor
        MotorConfigurationType motorConfiguration = turretMotor.getMotorType().clone();
        motorConfiguration.setAchieveableMaxRPMFraction(1.0);
        motorConfiguration.setMaxRPM(baseMotorSpeed);
        motorConfiguration.setTicksPerRev(baseTicksPerRev);

        turretMotor.setMotorType(motorConfiguration);

        turretMotor.setTargetPositionTolerance((int) (baseTicksPerDeg * toleranceDeg));
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // initialize location
        currentPosition = new Pose(0, 0, 0); //TODO: interface with robot for location
        targetGoal = chooseTargetGoal(currentPosition);
    }

    @Override
    public void update(){
        // update location
//        currentPosition = robot.getPose(); TODO: update location based on robot
        turretPos = turretMotor.getCurrentPosition() + turretOffset;

        // update desired angle
        targetGoal = chooseTargetGoal(currentPosition);
        if (autoAim) {
            targetDeg = headingToGoalFromPose(currentPosition);
            normalizeTargetDegree();
        }

        // Move the turret with correct method
        moveTurret();

        // reset angle if touch sensor activates
        if(resetSwitch.isPressed()){
            resetEncoder();
        }

        // telemetry
//        telemetry.addLine(String.format("Turret Pos: %d / %d  (tick)", turretPos, (int) targetPos));
//        telemetry.addLine(String.format("Real Pos: %d | Offset: %d", turretMotor.getCurrentPosition(), turretOffset));
//        telemetry.addLine(String.format("Turret Pos: %3.1f / %3.1f  (deg)", getPositionDegrees(), targetDeg));
//        telemetry.addLine(String.format("Theta: %2.2f  (deg)", Math.toDegrees(theta)));
    }

    public void resetEncoder(){
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // Turret Movement
    private void moveTurret() {
        // check that autonomous movement is enabled
        if(! autoMove)
            return;

        // call the corresponding movement method
        switch (moveMethod) {
            case RUN_TO_POSITION:
                moveRtp();return;
            case PID_IMPOSTER:
                movePid();return;
        }
    }
    private void moveRtp(){
        turretMotor.setTargetPosition((int) targetPos);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(0.5);
    }
    private void movePid(){
        double ticksAway = Math.abs(turretPos - targetPos);
        int direction = turretPos < targetPos ? 1 : -1;

        // choose a motor power based on distance to travel
        double power;
        if (ticksAway <= 2)
            power = 0;
        else if (ticksAway <= 30)
            power = 0.05;
        else
            power = 0.2;

        turretMotor.setPower(power * direction);
    }


    private Pose chooseTargetGoal(Pose location) {
        if (context.alliance) { // if blue
            if (location.getY() < 72)
                return blueHive1;
            else
                return blueHive2;
        } else {
            if (location.getY() < 72)
                return redHive1;
            else
                return redHive2;
        }
    }
    public double headingToGoalFromPose(Pose pose){
        // calculate angle to goal from robot position
        double deltaY = targetGoal.getY() - pose.getY();
        double deltaX = targetGoal.getX() - pose.getX();
        double angleToGoal = Math.atan2(deltaY, deltaX);

        // adjust for current robot rotation
        return Math.toDegrees(pose.getHeading() - angleToGoal);
    }
    private void normalizeTargetDegree(){
        // normalize angle so that θ ∈ [-180, 180)
        double adjustedAngle = ((targetDeg + 180) % 360) - 180;
        if (adjustedAngle < -180) { // java modulus is dumb
            adjustedAngle += 360;
        }

        // bind the angle between the turret's range of motion
        adjustedAngle = Math.min(adjustedAngle, maxHeading);
        adjustedAngle = Math.max(adjustedAngle, minHeading);

        // update target degree and position
        targetDeg = adjustedAngle;
        targetPos = targetDeg * effectiveTicksPerDeg;
    }

    // Accessing Methods
    public void setTargetDegrees(double newTarget){
        targetDeg = newTarget;
        normalizeTargetDegree();
    }
    public void setTargetFromPose(Pose pose){setTargetDegrees(headingToGoalFromPose(pose));}
    public double getPositionDegrees(){return turretPos / effectiveTicksPerDeg;}
    public int getPositionTicks(){return turretPos;}
    public void setPositionTicks(int ticks){turretOffset = ticks - turretMotor.getCurrentPosition();}
    public boolean setAutoAim(boolean autoAim){return this.autoAim = autoAim;}
    public boolean setAutoMove(boolean autoMove){return this.autoMove = autoMove;}
    public boolean getAutoAim(){return autoAim;}
    public void setCurrentPosition(Pose pose) {this.currentPosition = pose;}
    public Pose getTargetGoal(){return targetGoal;};
    public DcMotorEx getTurretMotor() {return turretMotor;}
}
