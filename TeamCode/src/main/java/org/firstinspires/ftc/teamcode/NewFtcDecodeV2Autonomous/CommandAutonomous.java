package org.firstinspires.ftc.teamcode.NewFtcDecodeV2Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class CommandAutonomous {
    // this function is so we can use the opmode functions like telemetry
    private LinearOpMode op = null;
    public CommandAutonomous(LinearOpMode providedOp)
    {
        op = providedOp;
        limelightAutonomous = LimelightAutonomous.getInstance(op);
    }

    final private Control control = Control.getInstance();
    private LimelightAutonomous limelightAutonomous = null;

    private static final double INTAKE_SPEED = 0.8;
    /** Flywheel (outtake) runs at this power for the whole autonomous; set once at start. */
    private static final double FLYWHEEL_POWER = 0.6;
//    private static final double KICK_POSITION = 10.0;
//    private static final double KICK_REST_POSITION = 0.0;
//    private static final int PARK_POSITION_ZERO = 0;
//    private static final int PARK_POSITION_TARGET = 1000;

    /** Power scale for autonomous drive (spoofed stick = 1.0, then scaled by this). */
    private static final double AUTO_DRIVE_POWER = 1.0;

    public enum CommandType { MOVE, ROTATE, SHOOT, INTAKE, CENTER, NOP }

    public enum Direction { FORWARD, BACKWARD, LEFT, RIGHT }

    public static class Command {
        public final CommandType type;
        public final Direction direction;
        public final double timeSec;

        public Command(CommandType type, Direction direction, double timeSec) {
            this.type = type;
            this.direction = direction;
            this.timeSec = timeSec;
        }
    }

    /**
     * Spoofs TeleOp drive: same formula as gamepad (lsx = -left_stick_x, lsy = left_stick_y, rsx = right_stick_x).
     * Pass stick values in range [-1, 1]; they are scaled by AUTO_DRIVE_POWER.
     */
    private void setDriveFromSticks(double lsy, double lsx, double rsx) {
//        double mod = AUTO_DRIVE_POWER;
        control.drive(lsx, lsy, rsx, AUTO_DRIVE_POWER);
//        leftFrontDrive.setPower((lsy - lsx - rsx)*mod);
//        leftBackDrive.setPower((lsy + lsx - rsx)*mod);
//        rightFrontDrive.setPower((lsy + lsx + rsx)*mod);
//        rightBackDrive.setPower((lsy - lsx + rsx)*mod);
    }

    private void stopDrive() {
        setDriveFromSticks(0, 0, 0);
    }

    /** Run one command for its duration (direction + time). */
    private void runCommand(Command cmd) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        switch (cmd.type) {
            case MOVE: {
                double lsy = 0, lsx = 0;
                switch (cmd.direction) {
                    case FORWARD:  lsy = 1;  break;
                    case BACKWARD: lsy = -1; break;
                    case LEFT:     lsx = 1;  break;
                    case RIGHT:    lsx = -1; break;
                }
                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    // switch (cmd.direction) {
                    //     case FORWARD:  lsy = 2;  break;
                    // }
                    setDriveFromSticks(lsy, lsx, 0);
                    op.telemetry.update();
                }
                stopDrive();
                break;
            }
            case ROTATE: {
                double rsx = (cmd.direction == Direction.LEFT) ? -1 : 1;
                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    setDriveFromSticks(0, 0, rsx);
                    op.telemetry.update();
                }
                stopDrive();
                break;
            }
            case SHOOT: {
                // Flywheel already running; just trigger kick and hold for duration
                //intakeMotor.setPower(INTAKE_SPEED);
                control.kick(true);
//                kickServo.setPosition(KICK_POSITION);

                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    op.telemetry.update();
                }

                control.kick(false);
//                kickServo.setPosition(KICK_REST_POSITION);
                break;
            }
            case INTAKE: {
                double power = (cmd.direction == Direction.FORWARD) ? INTAKE_SPEED : -INTAKE_SPEED;
//                intakeMotor.setPower(power);
                control.intake(power);

                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    // hold intake for duration; opModeIsActive() checked so stop works
                    op.telemetry.update();
                }

                control.intake(0);
                break;
            }
            case CENTER: {
                limelightAutonomous.lookAtCode(true);

                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    op.telemetry.update();
                }

                limelightAutonomous.lookAtCode(false);
            }
            case NOP:
                while (op.opModeIsActive() && timer.seconds() < cmd.timeSec) {
                    // wait without blocking op mode stop
                    op.telemetry.update();
                }
                break;
        }
    }

    /// Run the command auto
    public void run() {
//        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
//        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
//        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
//        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");
//
//        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
//        outtakeMotor = hardwareMap.get(DcMotor.class, "shooter");
//        kickServo = hardwareMap.get(Servo.class, "kick");
//        parkLeft = hardwareMap.get(DcMotor.class, "leftPark");
//        parkRight = hardwareMap.get(DcMotor.class, "rightPark");
//
//        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
//        outtakeMotor.setDirection(DcMotor.Direction.REVERSE);
//
//        parkLeft.setDirection(DcMotor.Direction.FORWARD);
//        parkRight.setDirection(DcMotor.Direction.FORWARD);
//        parkLeft.setPower(0.8);
//        parkRight.setPower(0.8);
        control.park(0.8);

//        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
//        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
//        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
//        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        op.telemetry.addData("Command status", "Initialized");
        op.telemetry.update();

        op.waitForStart();

        // Flywheel (outtake) stays on at one speed for entire autonomous
//        outtakeMotor.setPower(FLYWHEEL_POWER);
        control.outtake(FLYWHEEL_POWER);

        // ----- Command sequence: add your MOVE, ROTATE, SHOOT, INTAKE, CENTER, NOP here -----
        Command[] sequence = {
                new Command(CommandType.MOVE, Direction.FORWARD, 0.8),
                new Command(CommandType.NOP, Direction.FORWARD, 2),
                new Command(CommandType.CENTER, Direction.FORWARD, 1), // hi
                new Command(CommandType.SHOOT, Direction.FORWARD, 3),
                new Command(CommandType.NOP, Direction.FORWARD, 1),
                new Command(CommandType.INTAKE, Direction.FORWARD, 3),
                new Command(CommandType.NOP, Direction.FORWARD, 1),
                new Command(CommandType.SHOOT, Direction.FORWARD, 3),
        };

        for (Command cmd : sequence) {
            if (!op.opModeIsActive()) break;
            runCommand(cmd);
        }

//        while (opModeIsActive()) {
        op.telemetry.addData("Command status", "Command auto complete");
        op.telemetry.update();
//        }
    }
}
