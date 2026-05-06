package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class LimelightAutonomous {
    // this function is so we can use the opmode functions like telemetry
    final private LinearOpMode op;
    public LimelightAutonomous(LinearOpMode providedOp) {op = providedOp;}

    private static LimelightAutonomous single_instance = null;

    final private static Control control = Control.getInstance();

    // enums
    public enum Mode {APRIL_TAG_CENTER, BALL_RETRIEVE, NONE}

    // constants
    static final private double APRIL_TAG_CENTER_THRESHOLD = 2; // in degrees
    static final private double BALL_CENTER_THRESHOLD = 2; // in degrees
    static final private double TURN_POWER = 1;
    private static final double FLYWHEEL_POWER = 0.6;
    private static final double INTAKE_SPEED = 0.8;

    // variables
    private boolean centeredBall = false;
    private double lastBallTa = 0;
    private boolean aprilTagCentered = false;
    public Mode currentMode = Mode.NONE;

    // MAIN FUNCTIONS //

    /// Center robot rotation to AprilTag
    public void lookAtTag(boolean toggle)
    {
        aprilTagCentered = false;

        if (!toggle)
        {
            currentMode = Mode.NONE;
            control.drive(0, 0, 0);
            return;
        }

        // look at tag
        control.drive(0, 0, -TURN_POWER); // turn towards known AprilTag location
        currentMode = Mode.APRIL_TAG_CENTER;
        control.limelight.pipelineSwitch(1);
    }

    /// Retrieve a ball
    /// Yields until ball is "shot" & is turning back to AprilTag
    public void shootBall()
    {
        currentMode = Mode.BALL_RETRIEVE;
        control.limelight.pipelineSwitch(0);

        centeredBall = false;
        control.drive(0, 0, TURN_POWER); // turn towards known ball locations

        while (!centeredBall) op.sleep(1);

        long movementMs = (long)(1000*(lastBallTa /100));

        // move forward and take in ball
        control.park(INTAKE_SPEED);
        control.intake(INTAKE_SPEED);
        control.drive(0, 1, 0);

        op.sleep(movementMs);

        // move backward
        control.intake(0);
        control.drive(0, -1, 0);

        op.sleep(movementMs);

        // turn towards AprilTag
        control.drive(0, 0, 0);
        lookAtTag(true);

        while (!aprilTagCentered) op.sleep(1);

        lookAtTag(false);

        // fire now that it's centered
        control.kick(true);
        op.sleep(500);
        control.outtake(FLYWHEEL_POWER);

        op.sleep(1000);

        // back to regular unmoving state
        control.resetFrame();
    }

    /// frame
    public void frame()
    {
        op.telemetry.addData("Limelight mode", currentMode.name());

        if (currentMode == Mode.NONE) return;

        switch (currentMode) {
            case APRIL_TAG_CENTER: {
                LLResult result = control.limelight.getLatestResult();

                if (result == null || !result.isValid() || result.getPipelineIndex() != 0) return;

                double xOffset = result.getTx();

                if (Math.abs(xOffset) <= APRIL_TAG_CENTER_THRESHOLD) {
                    control.drive(0, 0, 0);
                    aprilTagCentered = true;
                    return;
                }

                control.drive(0, 0, TURN_POWER * Math.signum(xOffset));
            }
            case BALL_RETRIEVE: {
                if (centeredBall) return;

                LLResult result = control.limelight.getLatestResult();

                if (result == null || !result.isValid() || result.getPipelineIndex() != 1) return;

                double xOffset = result.getTx();
                lastBallTa = result.getTa();

                if (Math.abs(xOffset) <= BALL_CENTER_THRESHOLD)
                {
                    centeredBall = true;
                    control.drive(0, 0, 0);
                    return;
                }

                control.drive(0, 0, TURN_POWER * Math.signum(xOffset));
            }
        }
    }

    // HELPER FUNCTIONS //

    /// Initialize Limelight
    public void init()
    {
        Limelight3A limelight = control.limelight;

        limelight.setPollRateHz(11);
    }

    /// Returns the LimelightAutonomous singleton
    public static synchronized LimelightAutonomous getInstance(LinearOpMode providedOp)
    {
        if (single_instance == null)
            single_instance = new LimelightAutonomous(providedOp);

        return single_instance;
    }
}
