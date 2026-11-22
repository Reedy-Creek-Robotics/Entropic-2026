package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig driveConfig = new MecanumConfig(
            config -> {
                config.frontLeftName.set("lf");
                config.backLeftName.set("lr");
                config.frontRightName.set("rf");
                config.backRightName.set("rr");
                config.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                config.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                config.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                config.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
            }
    );

    public static PinpointConfig pinpointLocalizerConstants = new PinpointConfig(config -> {
        config.name.set("pinpoint");
        config.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        config.xPodOffset.set(3.550415);
        config.yPodOffset.set(-5.926254);
        config.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        config.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        config.globalDistanceUnit.set(DistanceUnit.INCH);
        config.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            config -> {
                Controller primaryTranslationalForward = Controller.proportional(0.2610255034418326);
                Controller secondaryTranslationalForward = Controller.proportional(0.09644194879184975);
                Controller primaryTranslationalLateral = Controller.proportional(0.4783511199465328);
                Controller secondaryTranslationalLateral = Controller.proportional(0.17673795704291356);

                config.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                config.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                config.coast.set(Controller.proportionalFeedforward(0.014063896226450554));
                config.brake.set(Controller.proportionalFeedforward(0.01195431179248297));

                config.headingFeedback.set(Controller.proportional(5.047395539028139));
                config.headingBrakeCoefficients.set(Vector2D.cartesian(0.06323262623543441, 0.00965015701786254));

                config.linearBrakeCoefficients.set(Matrix.diag(0.09406119534916778, 0.06704262632592088));
                config.quadraticBrakeCoefficients.set(Matrix.diag(0.001882613150783811, 0.002366380257804166));

                config.maxAchievableForwardVelocity.set(73.69684941725365);
                config.maxAchievableStrafeVelocity.set(56.23630201054167);
                config.naturalForwardDeceleration.set(30.715531964429815);
                config.naturalStrafeDeceleration.set(58.951812383391726);
            }
    );
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, pinpointLocalizerConstants),
                new Mecanum(h, driveConfig),
                new Foresight(foresightConfig)
        );
    }
}

/*
lr, lf, rf, rr
pinpoint
 */