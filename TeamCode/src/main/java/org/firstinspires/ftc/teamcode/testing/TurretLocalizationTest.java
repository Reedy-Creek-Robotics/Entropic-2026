package org.firstinspires.ftc.teamcode.testing;

import com.pedropathing.math.Pose;
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

@TeleOp(name = "Turret Localization Test")
public class TurretLocalizationTest extends TeleOpFramework {
    // April Tag Stuff
    //TODO: ensure that whatever position the april tag spits out actually aligns with pedro pathing
    private Position cameraPosition = new Position(DistanceUnit.INCH,
            8, 9, 3, 0);
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -45, 0, 0);
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private Pose curPos = new Pose(36, 24, 0);


    // Shooter Variables
    int shooterSpeed = 1000;
    boolean shooterOn = false;
    boolean gateOpen = false;
    boolean autoVelocity = false;
    boolean enableTurret;
    boolean useAprilTags = false;


    @Override
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
                Controller.Button.NORTH, this::manualVelocityEnabled,
                Controller.Button.WEST, this::turretEnabled
        );
        controlMapMeta = Map.of(
                // add meta controls here3
                Controller.Button.DPAD_DOWN, this::positionDown,
                Controller.Button.DPAD_UP, this::positionUp,
                Controller.Button.DPAD_LEFT, this::positionLeft,
                Controller.Button.DPAD_RIGHT, this::positionRight,
                Controller.Button.NORTH, this::aprilTagsEnabled
        );
    }

    @Override
    public void loop() {  // update gate
        if (gateOpen) {robot.getShooter().openGate();}
        else {robot.getShooter().closeGate();}
        gateOpen = false; // reset gate for next iteration

        // establish apriltag localization
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
        if (useAprilTags && ! currentDetections.isEmpty()) { // only using one tag for basics
            AprilTagDetection detection = currentDetections.get(0);
//            curPos = FTCCoordinates.INSTANCE.convertToPedro(new Pose(detection.robotPose.getPosition().x, detection.robotPose.getPosition().y, detection.robotPose.getOrientation().getYaw()));
            // convert FTC detection position into pedropathing pose
            // see here for more details: https://pedropathing.com/docs/pathing/reference/coordinates
            curPos = new Pose(
                    detection.robotPose.getPosition().y + 72,
                    72 - detection.robotPose.getPosition().x,
                    detection.robotPose.getOrientation().getYaw() - Math.PI / 2
            );
        }

        // set shooter speed
        Pose hive = robot.getTurret().getTargetGoal();
        double distanceToHive = Math.sqrt(Math.pow(curPos.x() - hive.x(), 2) + Math.pow(curPos.y() - hive.y(), 2));
        if (autoVelocity) {
            robot.getShooter().setVelocity(distanceToVelocity(distanceToHive));
        } else {
            robot.getShooter().setVelocity(shooterSpeed);
        }

        // manually update turret position since robot doesn't currently store position
        robot.getTurret().setCurrentPosition(curPos);

        /* TELEMETRY */
        telemetry.addData("Alliance", robotContext.getAlliance());
        telemetry.addLine("Shooter On: " +  shooterOn + "\tTarget TPS: " + shooterSpeed);
        telemetry.addLine("Target goal: " + hive);
        telemetry.addLine("Position: " + curPos);
        telemetry.addLine("Using April Tags: " + useAprilTags);
        telemetry.addLine("April Tag Detections: " + currentDetections.size());
        telemetry.addLine("Turret Target angle: " + robot.getTurret().getTargetDegrees());
        telemetry.addLine("Turret Current Degree: " + robot.getTurret().getPositionDegrees());

        super.loop();
    }

    // BUTTON EFFECTS
    private void shooterEnabled() {shooterOn = !shooterOn;}
    private void turretEnabled() {robot.getTurret().setAutoMove(! robot.getTurret().getAutoMove());}
    private void manualVelocityEnabled() {autoVelocity = !autoVelocity;}
    private void setGateOpen() {this.gateOpen = true;}
    private void velocityUp() {shooterSpeed += 100;}
    private void velocityDown() {shooterSpeed -= 100;}

    private void positionRight() {curPos = new Pose(curPos.x() + 12, curPos.y(), 0);}
    private void positionLeft() {curPos = new Pose(curPos.x() - 12, curPos.y(), 0);}
    private void positionUp() {curPos = new Pose(curPos.x(), curPos.y()  + 12, 0);}
    private void positionDown() {curPos = new Pose(curPos.x(), curPos.y() - 12, 0);}
    private void aprilTagsEnabled() {
        if (useAprilTags) {
            // round current pose to the nearest 12 inches
            curPos = new Pose(
                    12.0 * Math.round(curPos.x() / 12.0),
                    12.0 * Math.round(curPos.y() / 12.0),
                    0);
        }
        useAprilTags = ! useAprilTags;
    }
    /*
    EVERYTHING ELSE
     */

    /**
     * calculates the ideal TPS based on distance from goal
     * @param distance in meters from the goal
     * @return ticks per second for the motor to travel
     */
    private int distanceToVelocity(double distance) {
        double gravity = 9.80665;
        double shooterHeight = 0.3;
        double shootingAngle = 1.3089969389957472; // in radians
        // see this graph for more details: https://www.desmos.com/calculator/wjnyngr8t2
        // TODO: do testing to update these constants with real values.
        //      current values were copied from last year, which won't be the same
        double height = 13.0 * 0.0254; // height of shooter in meters
        double dist = distance * 0.0254 - 0.538349; // dist from shooter center to goal in meters

        double denominator = Math.sin(2 * shootingAngle) * dist -
                2 * height * Math.pow(Math.cos(shootingAngle), 2);
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
