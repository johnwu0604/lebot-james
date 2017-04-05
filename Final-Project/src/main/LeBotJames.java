package main;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import main.controller.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.object.LightSensor;
import main.object.OdometerDisplay;
import main.object.UltrasonicSensor;
import main.resource.ShootingConstants;
import main.util.EmergencyStopper;
import main.util.FieldMapper;
import main.wifi.WifiConnection;
import main.wifi.WifiProperties;

import java.util.Map;

/**
 * Main class for vehicle
 *
 * @author JohnWu
 */
public class LeBotJames {

    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "A" ));
    private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "D" ));
    private static final EV3LargeRegulatedMotor leftLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "C" ));
    private static final EV3LargeRegulatedMotor rightLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "B" ));
    private static final SampleProvider forwardUltrasonicSensor = ( new EV3UltrasonicSensor( LocalEV3.get().getPort( "S1" ) ) ).getMode("Distance");
    private static final SampleProvider leftColorSensor = ( new EV3ColorSensor( LocalEV3.get().getPort("S2") ) ).getMode("Red");
    private static final SampleProvider rightColorSensor = ( new EV3ColorSensor( LocalEV3.get().getPort("S3") ) ).getMode("Red");

    private static Parameters parameters;

    /**
     * The main class of the robot
     *
     * @param args
     */
    public static void main(String[] args) {

        @SuppressWarnings("main/resource")
        final TextLCD t = LocalEV3.get().getTextLCD();
        /**
         * Uncomment for wifi code
         */
        EmergencyStopper emergencyStopper = new EmergencyStopper();
        emergencyStopper.start();

        retrieveStartingParameters();
        Sound.beepSequenceUp();


        // map field
        FieldMapper fieldMapper = new FieldMapper( parameters );

        // instantiate timed threads (will start/stop throughout program)
        LightSensor leftLightSensor = new LightSensor( leftColorSensor );
        LightSensor rightLightSensor = new LightSensor( rightColorSensor );
        UltrasonicSensor forwardUSSensor = new UltrasonicSensor( forwardUltrasonicSensor );
        leftLightSensor.start();
        rightLightSensor.start();
        forwardUSSensor.start();

        // instantiate continuous threads (don't stop during the program)
        Odometer odometer = new Odometer( leftMotor, rightMotor, fieldMapper);
        OdometerDisplay odometerDisplay = new OdometerDisplay( odometer, t );
        odometer.start();
        odometerDisplay.start();

        // instantiate movement controllers
        ObstacleAvoider obstacleAvoider = new ObstacleAvoider( forwardUSSensor, odometer );
        Navigator navigator = new Navigator( leftMotor, rightMotor, odometer, obstacleAvoider );
        OdometerCorrection odometerCorrection = new OdometerCorrection( navigator, odometer, leftLightSensor, rightLightSensor );

        // instantiate offense controllers
        Launcher launcher = new Launcher( leftLaunchMotor, rightLaunchMotor, navigator, odometer, odometerCorrection );
        BallRetriever ballRetriever = new BallRetriever( launcher, odometer, navigator, odometerCorrection );

        // localize
        Localizer localizer = new Localizer( odometer, forwardUSSensor, navigator, getStartingCorner( parameters ) );
        localizer.start();

        // signal end of localization
        Sound.beepSequence();

        odometerCorrection.start(); // timed thread - waits until further instruction to actually start

        try { Thread.sleep( 1000 ); } catch( Exception e ){}

        if( parameters.getForwardTeam() == 11 ){
            playOffense( navigator, ballRetriever, launcher );
        } else {
            playDefense( navigator, odometer, launcher );
        }


        /**
         * Uncomment for non-wifi code
         */
//        EmergencyStopper emergencyStopper = new EmergencyStopper();
//        emergencyStopper.start();
//
//        int[] defenderZone = {4,4};
//        int[] ballDispenserPosition  = {11,2};
//        Parameters parameters = new Parameters();
//        parameters.setForwardCorner(2);
//        parameters.setForwardLine(8);
//        parameters.setForwardTeam(11);
//        parameters.setDefenderZone(defenderZone);
//        parameters.setBallDispenserPosition(ballDispenserPosition);
//        parameters.setBallDispenserOrientation("W");
//
//
//        // map field
//        FieldMapper fieldMapper = new FieldMapper( parameters );
//
//        // instantiate timed threads (will start/stop throughout program)
//        LightSensor leftLightSensor = new LightSensor( leftColorSensor );
//        LightSensor rightLightSensor = new LightSensor( rightColorSensor );
//        UltrasonicSensor forwardUSSensor = new UltrasonicSensor( forwardUltrasonicSensor );
//        leftLightSensor.start();
//        rightLightSensor.start();
//        forwardUSSensor.start();
//
//        // instantiate continuous threads (don't stop during the program)
//        Odometer odometer = new Odometer( leftMotor, rightMotor, fieldMapper);
//        OdometerDisplay odometerDisplay = new OdometerDisplay( odometer, t );
//        odometer.start();
//        odometerDisplay.start();
//
//        // instantiate movement controllers
//        ObstacleAvoider obstacleAvoider = new ObstacleAvoider( forwardUSSensor, odometer );
//        Navigator navigator = new Navigator( leftMotor, rightMotor, odometer, obstacleAvoider );
//        OdometerCorrection odometerCorrection = new OdometerCorrection( navigator, odometer, leftLightSensor, rightLightSensor );
//
//        // instantiate offense controllers
//        Launcher launcher = new Launcher( leftLaunchMotor, rightLaunchMotor, navigator, odometer, odometerCorrection );
//        BallRetriever ballRetriever = new BallRetriever( launcher, odometer, navigator, odometerCorrection );
//
//        // localize
//        Localizer localizer = new Localizer( odometer, forwardUSSensor, navigator, getStartingCorner( parameters ) );
//        localizer.start();
//
//        // signal end of localization
//        Sound.beepSequence();
//
//        odometerCorrection.start(); // timed thread - waits until further instruction to actually start
//
//        try { Thread.sleep( 1000 ); } catch( Exception e ){}
//
//        if( parameters.getForwardTeam() == 11 ){
//            playOffense( navigator, ballRetriever, launcher );
//        } else if ( parameters.getDefenseTeam() == 11 ){
//            playDefense( navigator, odometer, launcher );
//        }

        int buttonChoice = Button.waitForAnyPress();
        System.exit(0);
    }

    /**
     * A method to play offense with field mapping and strict avoidance (used in third round)
     *
     * @param navigator
     * @param ballRetriever
     * @param launcher
     */
    private static void playOffense(Navigator navigator, BallRetriever ballRetriever, Launcher launcher ) {

        ballRetriever.getBall();
        Sound.buzz();
        navigator.travelToShootingPosition();
        Sound.beep();
        launcher.launchBall();      //1 ball launched

        navigator.returnToBallDispenser();
        ballRetriever.getBall();
        navigator.returnToShootingPosition();
        launcher.launchBall(); //2 balls launched

        navigator.returnToBallDispenser();
        ballRetriever.getBall();
        navigator.returnToShootingPosition();
        launcher.launchBall(); //3 balls launched
    }


    /**
     * A method to play defense
     *
     * @param navigator
     * @param odometer
     * @param launcher
     */
    private static void playDefense( Navigator navigator, Odometer odometer, Launcher launcher)  {

        double startTime = System.currentTimeMillis();
        int y = 10-parameters.getDefenderZone()[1];

        navigator.travelToSquare( odometer.getFieldMapper().getMapping()[5][y] );
        launcher.rotateLaunchMotors(ShootingConstants.VERTICAL_ANGLE);
        while(System.currentTimeMillis() - startTime < 7*60*1000){
            navigator.moveSquareX(1);
            navigator.moveSquareX(-1);
        }


    }

    /**
     * A method to retrieve the starting parameters inputted by the server
     */
    private static void retrieveStartingParameters() {

        WifiConnection conn = new WifiConnection( WifiProperties.SERVER_IP, WifiProperties.TEAM_NUMBER,
                WifiProperties.ENABLE_DEBUG_WIFI_PRINT );
        parameters = new Parameters();

        try {
            Map data = conn.getData();
            parameters.setForwardTeam( ( (Long) data.get( "FWD_TEAM" ) ).intValue() );
            parameters.setDefenseTeam( ( (Long) data.get( "DEF_TEAM" ) ).intValue() );
            parameters.setForwardCorner( ( (Long) data.get( "FWD_CORNER" ) ).intValue() );
            parameters.setDefenseCorner( ( (Long) data.get( "DEF_CORNER" ) ).intValue() );
            parameters.setForwardLine( ( (Long) data.get( "d1" ) ).intValue() );
            parameters.setDefenderZone( new int[]{ ( (Long) data.get( "w1" ) ).intValue(), ( (Long) data.get( "w2" ) ).intValue() } );
            parameters.setBallDispenserPosition( new int[]{ ( (Long) data.get( "bx" )).intValue(), ( (Long) data.get( "by" )).intValue() });
            parameters.setBallDispenserOrientation( (String) data.get( "omega" ) );
        } catch ( Exception e ) {
            // Error occurred
        }
    }

    /**
     * A method to get our starting corner
     *
     * @param parameters
     */
    private static int getStartingCorner( Parameters parameters ) {
        if ( parameters.getForwardTeam() == WifiProperties.TEAM_NUMBER ) {
            return parameters.getForwardCorner();
        }
        return parameters.getDefenseCorner();
    }
}