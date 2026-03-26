package org.firstinspires.ftc.teamcode.TestFtcDecodeAutonomous;

import com.qualcomm.hardware.limelightvision.LLResult;

///  maybe this class isn't too useful but this removes a small bit of boilerplate I don't like
public class ComputedLLResult {
    // constructor
    public ComputedLLResult(LLResult llResult)
    {
        assert goodLLResult(llResult) : "Bad LLResult";

        this.targetX             = llResult.getTx(); // How far left or right the target is (degrees)
        this.targetY             = llResult.getTy(); // How far up or down the target is (degrees)
        this.targetDistanceAlpha = llResult.getTa(); // How big the target looks (0%-100% of the image)
    }

    // static methods
    public static boolean goodLLResult(LLResult llResult)
    {return llResult != null && llResult.isValid();}

    // attributes
    public double targetX;
    public double targetY;
    public double targetDistanceAlpha;
}
