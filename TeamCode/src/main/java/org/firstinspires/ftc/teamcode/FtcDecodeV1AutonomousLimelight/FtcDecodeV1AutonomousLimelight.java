package org.firstinspires.ftc.teamcode.FtcDecodeV1AutonomousLimelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Limelight Auto")
public class FtcDecodeV1AutonomousLimelight extends LinearOpMode {
    // components
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;

    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;
    private Servo   kickServo = null;

    private Limelight3A limelight = null;

    // frame variables
    double lsx = 0; // wheels movement (forward backward left right)
    double lsy = 0;
    double rsx = 0; // wheels orientation (turn left turn right)

    LLResult limelightResult = null;


    /// STARTING FUNCTION ///
    @Override
    public void runOpMode()
    {
        initComponents();

        // loop
        while (opModeIsActive())
        {
            opModeFrame();
            sleep(10);
        }

        deInitComponents();
    }





    /// LOOP FUNCTIONS ///

// The loop that is called until the opmode is turned off.
// A call/the processing of this function is called a frame
    private void opModeFrame()
    {
        resetFrameVariables();

        limelightResult = limelight.getLatestResult();
        if (limelightResult == null || !limelightResult.isValid())
        {
            // limelightResult is invalid, and the rest needs it so stop the function here
            telemetry.addData("Limelight result", "ERROR!");
            return;
        } else {telemetry.addData("Limelight result", "received");}



        useFrameVariables();
    }


// Set the movement position in a frame
    private void move(double lsxA, double lsyA, double rsxA)
    {
        lsx = lsxA;
        lsy = lsyA;
        rsx = rsxA;
    }

// Reset the variables to their default value, should only be called at the very start of the frame
    private void resetFrameVariables()
    {
        lsx = 0;
        lsy = 0;
        rsx = 0;

        limelightResult = null;
    }
// Uses the frame variables
    private void useFrameVariables()
    {
        // MOVEMENT
        leftFrontDrive.setPower(lsy - lsx - rsx);
        leftBackDrive.setPower(lsy + lsx - rsx);
        rightFrontDrive.setPower(lsy + lsx + rsx);
        rightBackDrive.setPower(lsy - lsx + rsx);
    }








    /// INIT/DE-INIT FUNCTIONS ///

// De-initializes components (turns them off if needed)
    private void deInitComponents()
    {
        limelight.stop();
    }
// Initializes/configures components
    private void initComponents()
    {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        outtakeMotor = hardwareMap.get(DcMotor.class, "shooter");
        kickServo = hardwareMap.get(Servo.class, "kick");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // configure components
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
    }
}
