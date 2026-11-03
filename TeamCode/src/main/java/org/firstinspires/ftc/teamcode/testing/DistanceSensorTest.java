package org.firstinspires.ftc.teamcode.testing;

import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_X;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.LEFT_STICK_Y;
import static org.firstinspires.ftc.teamcode.game.Controller.AnalogControl.RIGHT_STICK_X;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.teamcode.components.Robot;
import org.firstinspires.ftc.teamcode.components.core.BaseComponent;
import org.firstinspires.ftc.teamcode.components.core.RobotContext;
import org.firstinspires.ftc.teamcode.game.Controller;
import org.firstinspires.ftc.teamcode.geometry.Scalar;

import java.util.Map;

@TeleOp(name = "Distance Sensor test")
public class DistanceSensorTest extends OpMode {
    protected Controller driver;
    protected Controller meta;

    // Control maps map each button to its functionality for clean assignment and quick looping
    // analog inputs will need to be handled separately
    Map<Controller.Button, ButtonEffect> controlMapDriver = Map.of(
            Controller.Button.A, this::switchMode
    );
    Map<Controller.Button, ButtonEffect> controlMapMeta = Map.of(
            // add meta controls here
    );

    private AnalogInput laserAnalog;

    private final static double MAX_DISTANCE = 1.0;
    private final static double MAX_VOLTAGE = 3.3;
    private final static double DISTANCE_PER_VOLT = MAX_DISTANCE / MAX_VOLTAGE;

    private boolean isAnalog = false;

    @Override
    public void init() {
        driver = new Controller(gamepad1);
        meta = new Controller(gamepad2);

        laserAnalog = hardwareMap.get(AnalogInput.class, "laserAnalogInput");
    }

    @Override
    public void loop() {
        /* DRIVER CONTROLS */
        // run through all driver buttons
        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapDriver.entrySet()) {
            if (driver.isButtonDown(control.getKey())) {
                control.getValue().run();
            }
        }

        /* META CONTROLS */
        // run through all meta buttons
        for (Map.Entry<Controller.Button, ButtonEffect> control: controlMapMeta.entrySet()) {
            if (meta.isButtonDown(control.getKey())) {
                control.getValue().run();
            }
        }

        double laserVoltage = laserAnalog.getVoltage();
        double distance = laserVoltage * DISTANCE_PER_VOLT;
        telemetry.addData("Laser Voltage", laserVoltage);

        telemetry.addLine("Mode: " + (isAnalog? "Distance": "Beam Break"));
        if (isAnalog) {
            telemetry.addData("Calculated Distance (in):", distance * 1000.0 / 25.4);
        } else {
            telemetry.addData("Detected", laserVoltage > 1.0);
        }
    }

    /**
     * The ButtonEffect functional interface stores the effect of a button press as a method
     */
    @FunctionalInterface
    private interface ButtonEffect {void run();}

    private void switchMode(){
        this.isAnalog = ! this.isAnalog;
    }
}


