/*
    Control

    Singleton to give control to components like motors to separate classes
    thx https://www.geeksforgeeks.org/java/singleton-class-java/
 */

package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Control {
    private static Control single_instance = null;

    // constants
    private static final double KICK_POSITION = 10.0;
    private static final double KICK_REST_POSITION = 0.0;

    // components
    public DcMotor leftFrontDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightBackDrive = null;

    public DcMotor intakeMotor = null;
    public DcMotor outtakeMotor = null;
    public Servo   kickServo = null; // thing that lifts the ball up to get shot

    public DcMotor parkLeft = null; // these motors keep the ball in place
    public DcMotor parkRight = null;

    public Limelight3A limelight = null;

    // frame variables

    // movement
    public double lsx = 0; // wheels movement (forward backward left right)
    public double lsy = 0; // this moves forward and backward
    public double rsx = 0; // wheels orientation (turn left turn right)

    // park
    public double parkPower = 0;

    // kick
    public boolean kickToggle = false;

    // outtake
    public double outtakePower = 0;

    // intake
    public double intakePower = 0;



    // CONTROL FUNCTIONS //

    /// Set the wheels lsx, lsy & rsx
    public void drive(double lsxA, double lsyA, double rsxA)
    {
        lsx = lsxA;
        lsy = lsyA;
        rsx = rsxA;
    }

    /// Set the wheels lsx, lsy & rsx with a modifier
    /// lsx, lsy & rsx are intended to be -1 to 1 like a joystick position
    public void drive(double lsxA, double lsyA, double rsxA, double mod)
    {
        drive(lsxA*mod, lsyA*mod, rsxA*mod);
    }

    /// Set the park power
    public void park(double power)
    {
        parkPower = power;
    }

    /// Set whether currently the kick is up
    public void kick(boolean toggle)
    {
        kickToggle = toggle;
    }

    /// Set the outtake power
    public void outtake(double power)
    {
        outtakePower = power;
    }

    /// Set the intake power
    public void intake(double power)
    {
        intakePower = power;
    }



    // FRAME FUNCTIONS //

    /// Use the frame variables to the components
    public void frame()
    {
        // movement
        leftFrontDrive.setPower(lsy - lsx - rsx);
        leftBackDrive.setPower(lsy + lsx - rsx);
        rightFrontDrive.setPower(lsy + lsx + rsx);
        rightBackDrive.setPower(lsy - lsx + rsx);

        // park
        parkLeft.setPower(parkPower);
        parkRight.setPower(parkPower);

        // kick
        kickServo.setPosition(kickToggle ? KICK_POSITION : KICK_REST_POSITION);

        // outtake
        outtakeMotor.setPower(outtakePower);

        // intake
        intakeMotor.setPower(intakePower);
    }

    /// Reset the frame variables back to their regular off state
    public void resetFrame()
    {
        lsx = 0;
        lsy = 0;
        rsx = 0;

        parkPower = 0;

        kickToggle = false;

        outtakePower = 0;

        intakePower = 0;
    }



    // HELPER FUNCTIONS //

    /// Initializes components to their variables & base configurations
    public void initComponents()
    {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        outtakeMotor = hardwareMap.get(DcMotor.class, "shooter");
        kickServo = hardwareMap.get(Servo.class, "kick");

        parkLeft = hardwareMap.get(DcMotor.class, "leftPark");
        parkRight = hardwareMap.get(DcMotor.class, "rightPark");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // configurations
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        parkLeft.setDirection(DcMotor.Direction.FORWARD);
        parkRight.setDirection(DcMotor.Direction.FORWARD);
//        parkLeft.setPower(0.8);
//        parkRight.setPower(0.8);

        telemetry.addData("Control Status", "initialized");
    }

    /// Deinitializes components (turning them off)
    public void deInitComponents()
    {
        limelight.stop();

        // I don't know if components automatically turn off so turn them off forcefully
        resetFrame();
        frame();
    }

    /// Returns the Control singleton
    public static synchronized Control getInstance()
    {
        if (single_instance == null)
            single_instance = new Control();

        return single_instance;
    }
}
