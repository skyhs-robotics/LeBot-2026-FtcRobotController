package org.firstinspires.ftc.teamcode.TestFtcDecodeAutonomous;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// pipeline 0: ball detector
// pipeline 1: apriltag

enum Pipeline {
    BALLS_SCANNING,
    APRILTAG_SHOOTING
}

@Autonomous(name = "FtcDecodeV99 Autonomous")
public class TestFtcDecodeAutonomous extends LinearOpMode {
    // components
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;

    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;
    private Servo kickServo = null;

    private Limelight3A limelight = null;

    // constants

    // Kick servo positions (0–1); return to rest when getPosition() is at target (within tolerance)
    private static final double KICK_POSITION = 10.0;
    private static final double KICK_REST_POSITION = 0.0;

    // variables
    private Pipeline currentPipeline = Pipeline.BALLS_SCANNING;

    double lsx = 0; // movement
    double lsy = 0;
    double rsx = 0; // orientation

    double kickPosition = KICK_REST_POSITION;
    double outtakePower = 0;

    long shotDuration = 0;
    long shotTime = 0;
    double shotPower = 0;

    @Override
    public void runOpMode() {
        // component init
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        outtakeMotor = hardwareMap.get(DcMotor.class, "shooter");
        kickServo = hardwareMap.get(Servo.class, "kick");

        // init-ing components
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        changePipeline(currentPipeline); // init default pipeline

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        // loop
        while (opModeIsActive()) {
            sleep(10);

            lsx = 0; // movement
            lsy = 0;

            rsx = 0; // orientation

            outtakePower = 0;
            kickPosition = KICK_REST_POSITION;

            if (shotTime + shotDuration >= System.currentTimeMillis()) {
                // shooting
                kickPosition = KICK_POSITION;
                outtakePower = shotPower;
            }

            LLResult llResult = limelight.getLatestResult();
            if (!ComputedLLResult.goodLLResult(llResult)) {
                ComputedLLResult computedLLResult = new ComputedLLResult(llResult);

                switch (currentPipeline) {
                    case BALLS_SCANNING:    ballsScanningFrame(computedLLResult);
                    case APRILTAG_SHOOTING: aprilTagShootingFrame(computedLLResult);
                }
            } else {
                switch (currentPipeline) {
                    case BALLS_SCANNING:    ballsScanningFrame();
                    case APRILTAG_SHOOTING: aprilTagShootingFrame();
                }
            }

            leftFrontDrive.setPower(lsy - lsx - rsx);
            leftBackDrive.setPower(lsy + lsx - rsx);
            rightFrontDrive.setPower(lsy + lsx + rsx);
            rightBackDrive.setPower(lsy - lsx + rsx);

            kickServo.setPosition(kickPosition);
            outtakeMotor.setPower(outtakePower);
        }

        // de-init
        limelight.stop();
    }

    private void ballsScanningFrame()
    {
        // look for balls

    }
    private void ballsScanningFrame(ComputedLLResult computedLLResult)
    {
        // found a ball, move towards it
        double targetX = computedLLResult.targetX;
        double distanceAlpha = computedLLResult.targetDistanceAlpha;

        rsx = 1 * Math.signum(targetX);

        if (targetX >= -2 & targetX <= 2)
        {

        }
    }
    private void aprilTagShootingFrame()
    {
        // look for april tag

    }
    private void aprilTagShootingFrame(ComputedLLResult computedLLResult)
    {
        // found april tag, rotate towards it, or if centered then shoot at it
        double targetX = computedLLResult.targetX;
        double distanceAlpha = computedLLResult.targetDistanceAlpha; // 4: really close, 0.1: far

        rsx = 1 * Math.signum(targetX);

        if (targetX >= -4 & targetX <= 4)
        {
           // centered, shoot at it for 2 seconds
            shootBall(distanceAlpha, 2000);
        }
    }

    // helper functions

    // lift the kicker and turn on the outtake for variable milliseconds
    private void shootBall(double outtakePower, long milliseconds)
    {
        shotTime = System.currentTimeMillis();
        shotDuration = milliseconds;
        shotPower = outtakePower;
    }

    private void changePipeline(Pipeline pipeline)
    {
        switch(pipeline) {
            case BALLS_SCANNING:
                limelight.pipelineSwitch(0);
            case APRILTAG_SHOOTING:
        }       limelight.pipelineSwitch(1);
    }
}