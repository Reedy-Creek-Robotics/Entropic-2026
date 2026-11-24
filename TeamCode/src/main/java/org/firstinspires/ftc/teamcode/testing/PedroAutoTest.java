package org.firstinspires.ftc.teamcode.testing;

import com.pedropathing.follower.Follower;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.LogCatUtil;

import java.util.concurrent.TimeUnit;

@Autonomous(name = "pedro testing")
public class PedroAutoTest extends LinearOpMode {
    protected Robot robot;
    protected Follower follower;
    protected Timer pathTimer, opmodeTimer;
    /**
     * false - red<br>
     * true - blue
     */
    protected boolean alliance;
    protected boolean running = true;

    protected enum PathStates {
        SHOOTING,
        MOVE_LEFT,
        MOVE_TO_PARK,
        STOP
    }
    protected PathStates pathState = PathStates.SHOOTING;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose[] locations = {
            poseFactory.of(57.500, 12.0, 90),
            poseFactory.of(12.0, 12.0, 90),
            poseFactory.of(12.0, 108.0, 90)
    };
    private Path[] paths = {
        Paths.line(locations[0], locations[1]).linear(locations[0], locations[1]),
        Paths.line(locations[1], locations[2]).linear(locations[1], locations[2])
    };

    @Override
    public void runOpMode() {
        initRobot();
        follower.setPose(locations[0]);

        waitForStart();
        opmodeTimer.reset();

        while(running && opModeIsActive()) {
            robot.update();
            follower.update();
            runPath();
        }
    }

    public void runPath() {
        switch (pathState) {
            case SHOOTING:
                // add shooting code here
                if (!robot.isBusy() && !follower.isBusy() && opmodeTimer.get(TimeUnit.MILLISECONDS) > 5000) {
                    pathState = PathStates.MOVE_LEFT;
                    follower.follow(paths[0]);
                }
                break;
            case MOVE_LEFT:
                if (!robot.isBusy() && !follower.isBusy()) {
                    pathState = PathStates.MOVE_TO_PARK;
                    follower.follow(paths[1]);
                }
                break;
            case MOVE_TO_PARK:
                if (!robot.isBusy() && !follower.isBusy()) {
                    pathState = PathStates.STOP;
                    follower.stop();
                }
                break;
            case STOP:
                running = false;
                break;
        }

    }

    public void initRobot(){
        robot = new Robot(this, alliance);
        robot.init();

         follower = Constants.create(hardwareMap);
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.reset();
    }
}
