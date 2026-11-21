package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.follower.Follower;
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
        config.xPodOffset.set(-5.847556647353285);
        config.yPodOffset.set(3.9112601693220963);
        config.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        config.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        config.globalDistanceUnit.set(DistanceUnit.INCH);
        config.offsetUnits.set(DistanceUnit.INCH);
    });
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, pinpointLocalizerConstants),
                new Mecanum(h, driveConfig),
                null
//                new Foresight(foresightConfig)
        );
    }
}

/*
lr, lf, rf, rr
pinpoint
 */