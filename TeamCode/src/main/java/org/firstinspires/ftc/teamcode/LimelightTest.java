package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "", group = "")
public class LimelightTest extends LinearOpMode {
    private Limelight3A limelight;

    @Override
    public void runOpMode()
    {
        // init
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.setMsTransmissionInterval(11); // what does this do?

        limelight.pipelineSwitch(0); // what does this do?

        /*
         * Starts polling for data.
         * what??? does it start scanning? turn on the camera? do both?
         */
        limelight.start();

        while (opModeIsActive())
        {
            next:
                sleep(10); // nvm i cant figure out goto lol

            LLResult result = limelight.getLatestResult();
            if (result == null) {
                continue next;
            }
        }

        // if this below runs it means the opmode has been canceled so de-init
        limelight.stop(); // well opposite of start i guess (what does this do?)

    } // end method runOpmode()
}
