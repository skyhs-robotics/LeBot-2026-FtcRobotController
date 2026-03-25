package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp
public class FtcDecodeV1Teleop extends LinearOpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    
    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;
    private Servo kickServo = null;
    private DcMotor parkLeft = null;
    private DcMotor parkRight = null;
    
    // Intake: hold button → motor at INTAKE_SPEED
    private static final double INTAKE_SPEED = 0.8;
    // Outtake: constantly running; button toggles between LOW and HIGH power
    private static final double OUTTAKE_POWER_0 = 0.0;
    private static final double OUTTAKE_POWER_1 = 0.7;
    private static final double OUTTAKE_POWER_2 = 0.9;
    // Outtake toggle debounce (in ms)
    private static final long OUTTAKE_DEBOUNCE_TIME = 150;
    
    private static final double INTTAKE_WHEEL_SPEED = 0.8; 
    
    // Kick servo positions (0–1); return to rest when getPosition() is at target (within tolerance)
    private static final double KICK_POSITION = 10.0;
    private static final double KICK_REST_POSITION = 0.0;
    // Park motors: encoder positions
    private static final int PARK_POSITION_ZERO = 0;
    private static final int PARK_RIGHT_POSITION_TARGET = 10000;
    private static final int PARK_LEFT_POSITION_TARGET = 10000;
    
    // Speed modifier
    private double mod = 0;
    private boolean modtoggle = false;
    boolean gp_lt = true;
    
    @Override
    public void runOpMode() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");
        
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        outtakeMotor = hardwareMap.get(DcMotor.class, "shooter");
        kickServo = hardwareMap.get(Servo.class, "kick");
        parkLeft = hardwareMap.get(DcMotor.class, "leftPark");
        parkRight = hardwareMap.get(DcMotor.class, "rightPark");
        
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);
        
        parkLeft.setDirection(DcMotor.Direction.FORWARD);
        parkRight.setDirection(DcMotor.Direction.FORWARD);
        parkLeft.setPower(INTTAKE_WHEEL_SPEED);
        parkRight.setPower(INTTAKE_WHEEL_SPEED);
        
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        
        waitForStart();
        
        // Outtake: toggle between low and high power (button press)
        
        long lastOuttakeTime = OUTTAKE_DEBOUNCE_TIME + 1;
        boolean lastKickButton = false;
        // Park: one button toggles extended / retracted
        boolean parkExtended = false;
        boolean lastParkButton = false;
        double outtakePower = 0;
        int outtakeToggle = 0;
        
        intakeMotor.setPower(0);
        
        mod = 1;
        
        while (opModeIsActive()) {
            // Speed changing logic
            // if (modtoggle) {
            //     mod = 0.5;
            // } else if (!modtoggle) { mod = 1; }
            
            // if (gamepad1.b == true && gp_lt) {
            //     modtoggle = !modtoggle;
            //     gp_lt = false;
            // } 
            
            // if (gamepad1.b == false && !gp_lt)
            // {
            //     gp_lt = true;
            // }
            
            // if (gamepad1.left_trigger > 0)
            // {
            //     mod = 0.3;
            // }
            
            // ----- INTAKE: hold button → DC motor at INTAKE_SPEED (gamepad2.right_bumper) -----
            if (gamepad1.right_bumper) {
                intakeMotor.setPower(INTAKE_SPEED);
            } else {
                intakeMotor.setPower(0);
            }
            
            // ----- OUTTAKE: always running; button toggles 0.7 vs 1.0 (gamepad2.left_bumper) -----
            if (gamepad1.x) {
                if (System.currentTimeMillis() - lastOuttakeTime > OUTTAKE_DEBOUNCE_TIME) {
                    outtakeToggle = outtakeToggle + 1;
                    if (outtakeToggle == 3) outtakeToggle = 0;
                }
                lastOuttakeTime = System.currentTimeMillis();
            }

            switch(outtakeToggle)
            {
                case 0:
                    outtakePower = OUTTAKE_POWER_0;
                    break;
                case 1:
                    outtakePower = OUTTAKE_POWER_1;
                    break;
                case 2:
                    outtakePower = OUTTAKE_POWER_2;
                    break;
            };
            
            outtakeMotor.setPower(outtakePower);
            
            // ----- KICK: Servo to position; return to rest when at target (gamepad2.a) -----
            if (gamepad1.a) {
                kickServo.setPosition(KICK_POSITION);

            }
            if (!gamepad1.a) { 
                kickServo.setPosition(KICK_REST_POSITION);
            }
            
            // ----- PARK (level 2): one button toggles extend/retract (gamepad2.x) -----
            // if (gamepad1.y && !lastParkButton) {
            //     parkExtended = !parkExtended;
            //     int righttarget = parkExtended ? PARK_RIGHT_POSITION_TARGET : PARK_POSITION_ZERO;
            //     int lefttarget = parkExtended ? PARK_LEFT_POSITION_TARGET : PARK_POSITION_ZERO;
            //     parkLeft.setTargetPosition(lefttarget);
            //     parkRight.setTargetPosition(righttarget);
            //     parkLeft.setPower(1.0);
            //     parkRight.setPower(1.0);
            //     lastParkButton = true;
            // }
            // if (!gamepad1.y) lastParkButton = false;
        
            double lsx = gamepad1.left_stick_x;
            double lsy =  gamepad1.left_stick_y;
            
            double rsx = gamepad1.right_stick_x;
            double rsy = gamepad1.right_stick_y;
            
            leftFrontDrive.setPower((lsy - lsx - rsx)*mod);
            leftBackDrive.setPower((lsy + lsx - rsx)*mod);
            rightFrontDrive.setPower((lsy + lsx + rsx)*mod);
            rightBackDrive.setPower((lsy - lsx + rsx)*mod);
            
            telemetry.update();
        }
    }
}