package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "FtcDecodeV2 Autonomous")
public class NewFtcDecodeV2Autonomous extends LinearOpMode {
    final private Control control = Control.getInstance();

    final private CommandAutonomous commandAutonomous = new CommandAutonomous(this);
    final private LimelightAutonomous limelightAutonomous = LimelightAutonomous.getInstance(this);

    @Override
    public void runOpMode()
    {
        // init
        control.initComponents();

        // asynchronously run commandAutonomous because it yields
        new Thread(commandAutonomous::run).start();

        // loop
        while (opModeIsActive())
        {
            control.resetFrame(); // reset

            limelightAutonomous.frame();

            control.frame(); // in-act motors

            telemetry.update();
            sleep(10);
        }

        // de-init
        control.deInitComponents();
    }
}
