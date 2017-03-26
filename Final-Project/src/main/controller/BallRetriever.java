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

    public BallRetriever(Launcher launcher){
        this.launcher = launcher;
        this.navigator = launcher.getNavigator();
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

    }

    private Square approachDispenser(){

        navigator.travelToSquare(chooseApproach());
        return chooseApproach();
    }

    private void alignToDispenser(Square currentSquare){

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

        Square approach1 = navigator.getOdometer().getFieldMapper().calculateBallDispenserApproach()[0];
        Square approach2 = navigator.getOdometer().getFieldMapper().calculateBallDispenserApproach()[1];

        double dist1 = Math.hypot(navigator.getComponentDistances(approach1)[0], navigator.getComponentDistances(approach1)[1]);
        double dist2 = Math.hypot(navigator.getComponentDistances(approach2)[0], navigator.getComponentDistances(approach2)[1]);

        if(dist1 >= dist2){
            return approach1;
        } else {
            return approach2;
        }
    }

}
