package main.controller;

import lejos.hardware.Sound;
import main.Parameters;
import main.object.Square;

/**
 * Created by Durham Abric on 3/24/17.
 */

public class BallRetriever {

    private Launcher launcher;
    private Navigator navigator;
    private OdometerCorrection odometerCorrection;

    public BallRetriever(Launcher launcher, OdometerCorrection odoCorrection){
        this.launcher = launcher;
        this.navigator = launcher.getNavigator();
        this.odometerCorrection = odoCorrection;
    }

    public void getBall(){

        Square currentApproachSquare = approachDispenser();
        alignToDispenser(currentApproachSquare);
        launcher.setLaunchMotorAcceleration(100);
        launcher.rotateLaunchMotors(40);
        Sound.beep();

        try { Thread.sleep( 5000 ); } catch( Exception e ){}

        launcher.setLaunchMotorAcceleration(50);
        launcher.rotateLaunchMotors(-40);

        navigator.travelToSquare(chooseApproach());
        odometerCorrection.startRunning();

    }

    private Square approachDispenser(){

        double xCoordinate;
        double yCoordinate;

        Square approach = chooseApproach();
        navigator.travelToSquare(approach);
        return approach;
    }

    private void alignToDispenser(Square currentSquare){

        odometerCorrection.stopRunning();

        launcher.retractArm();

        Parameters parameters = navigator.getOdometer().getFieldMapper().getParameters();
        String dispDirection = parameters.getBallDispenserOrientation();

        if (dispDirection.equals("N")){

            if (navigator.getOdometer().getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                navigator.travelToX(navigator.getOdometer().getCurrentSquare().getEastLine());
            } else {
                navigator.travelToX(navigator.getOdometer().getCurrentSquare().getWestLine());
            }

        } else if (dispDirection.equals("S")){

            if (navigator.getOdometer().getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                navigator.travelToX(navigator.getOdometer().getCurrentSquare().getEastLine());
            } else {
                navigator.travelToX(navigator.getOdometer().getCurrentSquare().getWestLine());
            }

        } else if (dispDirection.equals("E")){

            if (navigator.getOdometer().getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                navigator.travelToY(navigator.getOdometer().getCurrentSquare().getNorthLine());
            } else {
                navigator.travelToY(navigator.getOdometer().getCurrentSquare().getSouthLine());
            }

        } else if (dispDirection.equals("W")){

            if (navigator.getOdometer().getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                navigator.travelToY(navigator.getOdometer().getCurrentSquare().getNorthLine());
            } else {
                navigator.travelToY(navigator.getOdometer().getCurrentSquare().getSouthLine());
            }

        }

        if(dispDirection.equals("N")){
            navigator.turnTo(0);
        } else if (dispDirection.equals("S")){
            navigator.turnTo(180);
        } else if (dispDirection.equals("E")){
            navigator.turnTo(90);
        } else if (dispDirection.equals("W")){
            navigator.turnTo(270);
        }

    }

    private Square chooseApproach(){

        int currentX = navigator.getOdometer().getCurrentSquare().getSquarePosition()[0];
        int currentY = navigator.getOdometer().getCurrentSquare().getSquarePosition()[1];

        Square approach1 = navigator.getOdometer().getFieldMapper().calculateBallDispenserApproach()[0];
        Square approach2 = navigator.getOdometer().getFieldMapper().calculateBallDispenserApproach()[1];

        int approach1X = approach1.getSquarePosition()[0];
        int approach1Y = approach1.getSquarePosition()[1];

        int approach2X = approach2.getSquarePosition()[0];
        int approach2Y = approach2.getSquarePosition()[1];

        double dist1 = Math.hypot((currentX - approach1X), (currentY-approach1Y));
        double dist2 = Math.hypot((currentX - approach2X), (currentY-approach2Y));

        if(dist1 >= dist2){
            return approach1;
        } else {
            return approach2;
        }
    }

}
