package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LimelightAutonomous {
    // this function is so we can use the opmode functions like telemetry
    private LinearOpMode op = null;
    public LimelightAutonomous(LinearOpMode providedOp) {op = providedOp;}

    private static LimelightAutonomous single_instance = null;

    final private static Control control = Control.getInstance();

    // MAIN FUNCTIONS //

    /// Center robot to code
    public void lookAtCode(boolean toggle)
    {

    }

    /// Retrieve a ball
    public void retrieveBall()
    {

    }

    /// frame
    public void frame()
    {

    }

    // HELPER FUNCTIONS //

    /// Returns the LimelightAutonomous singleton
    public static synchronized LimelightAutonomous getInstance(LinearOpMode providedOp)
    {
        if (single_instance == null)
            single_instance = new LimelightAutonomous(providedOp);

        return single_instance;
    }
}
