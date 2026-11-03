package org.firstinspires.ftc.teamcode.testing;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.game.Controller;

import java.util.Map;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.opmodes.TeleOpFramework;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

@TeleOp(name = "shoot with drive")
public class ShootWithDriveTest extends TeleOpFramework {
    // April Tag Stuff
    //TODO: ensure that whatever position the april tag spits out actually aligns with pedro pathing
    private Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 0, 0, 0);
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -90, 0, 0);
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private Pose curPos = new Pose(0, 0, 0);


    // Shooter Variables
    int shooterSpeed = 1000;
    boolean shooterOn = false;
    boolean gateOpen = false;
    boolean autoVelocity = false;


    public void init() {
        super.init();

        initAprilTag();

        // set control maps
        controlMapDriver = Map.of(
                Controller.Button.DPAD_UP, this::velocityUp,
                Controller.Button.DPAD_DOWN, this::velocityDown,
                Controller.Button.RIGHT_BUMPER, this::setGateOpen,
                Controller.Button.LEFT_BUMPER, this::setGateOpen,
                Controller.Button.SOUTH, this::shooterEnabled,
                Controller.Button.NORTH, this::manualVelocityEnabled
        );
        controlMapMeta = Map.of(
                // add meta controls here
        );
    }

    public void loop() {
        super.loop(); // run through all nonanalog controls plus driving

        // update gate
        if (gateOpen) {robot.getShooter().openGate();}
        else {robot.getShooter().closeGate();}
        gateOpen = false; // reset gate for next iteration

        // establish apriltag localization
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());
        if (! currentDetections.isEmpty()) { // only using one tag for basics
            AprilTagDetection detection = currentDetections.get(1);
//            curPos = detection.robotPose.getPosition();
            curPos = new Pose(detection.robotPose.getPosition().x,
                    detection.robotPose.getPosition().y,
                    detection.robotPose.getOrientation().getYaw());
        }

        // set shooter speed
        if (autoVelocity) {
            Pose hive = robot.getTurret().getTargetGoal();
            double distanceToHive = Math.sqrt(Math.pow(curPos.getX() - hive.getX(), 2) + Math.pow(curPos.getY() - hive.getY(), 2));
            robot.getShooter().setVelocity(distanceToVelocity(distanceToHive));
        } else {
            robot.getShooter().setVelocity(shooterSpeed);
        }

        // manually update turret position since robot doesn't currently store position
        robot.getTurret().setCurrentPosition(curPos);

        /* TELEMETRY */
        telemetry.addData("Alliance", robotContext.getAlliance());
        telemetry.addLine("Shooter On: " +  shooterOn + ". Target TPS: " + shooterSpeed);
    }

    // BUTTON EFFECTS
    private void shooterEnabled() {shooterOn = !shooterOn;}
    private void manualVelocityEnabled() {autoVelocity = !autoVelocity;}
    private void setGateOpen() {this.gateOpen = true;}
    private void velocityUp() {shooterSpeed += 100;}
    private void velocityDown() {shooterSpeed -= 100;}

    /*
    EVERYTHING ELSE
     */

    /**
     * calculates the ideal TPS based on distance from goal
     * @param distance in meters from the goal
     * @return ticks per second for the motor to travel
     */
    private static final double shootingAngle = 1.3089969389957472; // in radians
    private static final double gravity = 9.80665;
    private static final double shooterHeight = 0.3;
    private int distanceToVelocity(double distance) {
        // see this graph for more details: https://www.desmos.com/calculator/wjnyngr8t2
        // TODO: do testing to update these constants with real values
        //      current values were copied from last year, which won't be the same
        double height = 13.0 * 0.0254; // height of shooter in meters
        double dist = distance * 0.0254 - 0.538349; // dist from shooter center to goal in meters

        double denominator = (Math.sin(2 * shootingAngle) * dist -
                2 * height * Math.pow(Math.cos(shootingAngle), 2));
        double velocity = dist * Math.sqrt(gravity / denominator);

        return (int) (velocity * 226.2636); // return the velocity in ticks per second
    }

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setCameraPose(cameraPosition, cameraOrientation).build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
    }
}
