package main.controller;

import lejos.hardware.Sound;
import main.Parameters;
import main.object.Square;
import main.resource.ShootingConstants;

/**
 * A class that locates, and retrieves ball from retriever
 *
 * @author Durham Abric
 */
public class BallRetriever {

    private Launcher launcher;
    private Navigator navigator;
    private Odometer odometer;

    public BallRetriever(Launcher launcher, Odometer odo, Navigator nav){
        this.launcher = launcher;
        this.navigator = nav;
        this.odometer = odo;

    }

    public void getBall(){

        Square currentApproachSquare = approachDispenser();
        alignToDispenser(currentApproachSquare);
        launcher.setLaunchMotorAcceleration(2*ShootingConstants.BALL_LOWERING_ACCELERATION);
        launcher.rotateLaunchMotors(ShootingConstants.BALL_RETRIEVAL_ANGLE);
        Sound.beep(); //Notify instructors we are ready to receive ball

        try { Thread.sleep( 5000 ); } catch( Exception e ){}

        launcher.setLaunchMotorAcceleration(ShootingConstants.BALL_LOWERING_ACCELERATION);
        launcher.rotateLaunchMotors(-ShootingConstants.BALL_RETRIEVAL_ANGLE);

        navigator.travelToSquare(chooseApproach());
        navigator.setCorrectionNeeded(true);

    }

    private Square approachDispenser(){

        Square approach = chooseApproach();
        navigator.travelToSquare(approach);
        return approach;

    }

    private void alignToDispenser(Square currentSquare){

        navigator.setCorrectionNeeded(false);
        launcher.retractArm();

        Parameters parameters = odometer.getFieldMapper().getParameters();
        String dispDirection = parameters.getBallDispenserOrientation();

        if (dispDirection.equals("N")){

            if (odometer.getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                navigator.travelToX(odometer.getCurrentSquare().getEastLine());
            } else {
                navigator.travelToX(odometer.getCurrentSquare().getWestLine());
            }

        } else if (dispDirection.equals("S")){

            if (odometer.getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                navigator.travelToX(odometer.getCurrentSquare().getEastLine());
            } else {
                navigator.travelToX(odometer.getCurrentSquare().getWestLine());
            }

        } else if (dispDirection.equals("E")){

            if (odometer.getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                navigator.travelToY(odometer.getCurrentSquare().getNorthLine());
            } else {
                navigator.travelToY(odometer.getCurrentSquare().getSouthLine());
            }

        } else if (dispDirection.equals("W")){

            if (odometer.getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                navigator.travelToY(odometer.getCurrentSquare().getNorthLine());
            } else {
                navigator.travelToY(odometer.getCurrentSquare().getSouthLine());
            }

        }

        if(dispDirection.equals("N")){
            navigator.turnTo(0);
        } else if (dispDirection.equals("S")){
            navigator.turnTo(Math.PI);
        } else if (dispDirection.equals("E")){
            navigator.turnTo(Math.PI / 2);
        } else if (dispDirection.equals("W")){
            navigator.turnTo(3*Math.PI/2);
        }

    }

    private Square chooseApproach(){

        Square approach1 = odometer.getFieldMapper().calculateBallDispenserApproach()[0];
        Square approach2 = odometer.getFieldMapper().calculateBallDispenserApproach()[1];

        double dist1 = Math.hypot(navigator.getComponentDistances(approach1)[0], navigator.getComponentDistances(approach1)[1]);
        double dist2 = Math.hypot(navigator.getComponentDistances(approach2)[0], navigator.getComponentDistances(approach2)[1]);

        if(dist1 <= dist2){
            return approach1;
        } else {
            return approach2;
        }
    }

}
