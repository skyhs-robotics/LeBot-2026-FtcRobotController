package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "FtcDecodeV2 Autonomous")
public class NewFtcDecodeV2Autonomous extends LinearOpMode {
    final private Control control = Control.getInstance(this);

    final private CommandAutonomous commandAutonomous = new CommandAutonomous(this);
    final private LimelightAutonomous limelightAutonomous = LimelightAutonomous.getInstance(this);

    @Override
    public void runOpMode()
    {
        // init
        control.initComponents();
        limelightAutonomous.init();

        waitForStart();

        // i need a thread because this yields
        new Thread(() -> {
            commandAutonomous.run();
            // when it's finished do limelight loop
            while (opModeIsActive()) limelightAutonomous.shootBall();
        }).start();

        // loop
        while (opModeIsActive())
        {
//            control.resetFrame(); // reset

            limelightAutonomous.frame();

            control.frame(); // turn on components

            telemetry.update();
            sleep(10);
        }

        // de-init
        control.deInitComponents();
    }
}
