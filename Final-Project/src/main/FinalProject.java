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
import main.resource.FieldConstants;
import main.util.EmergencyStopper;
import main.util.FieldMapper;
import main.wifi.WifiConnection;
import main.wifi.WifiProperties;
import main.resource.ShootingConstants;

import java.util.Map;

/**
 * Main class for vehicle
 *
 * @author JohnWu
 */
public class FinalProject {

    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "A" ));
    private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "D" ));
    private static final EV3LargeRegulatedMotor leftLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "C" ));
    private static final EV3LargeRegulatedMotor rightLaunchMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort( "B" ));
    private static final SampleProvider forwardUltrasonicSensor = ( new EV3UltrasonicSensor( LocalEV3.get().getPort( "S1" ) ) ).getMode("Distance");
    private static final SampleProvider leftUltrasonicSensor = ( new EV3UltrasonicSensor( LocalEV3.get().getPort( "S4" ) ) ).getMode("Distance");
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
//        EmergencyStopper emergencyStopper = new EmergencyStopper();
//        emergencyStopper.start();
//
//        retrieveStartingParameters();
//
//        // notify profs we have received parameters
//        Sound.beepSequenceUp();
//
//        if ( parameters.getForwardTeam() == 11 ) {
//            // instantiate objects
//            LightSensor leftLightSensor = new LightSensor( leftColorSensor );
//            LightSensor rightLightSensor = new LightSensor( rightColorSensor );
//            FieldMapper fieldMapper = new FieldMapper(parameters);
//            Odometer odometer = new Odometer(leftMotor,rightMotor,fieldMapper);
//            Navigator navigator = new Navigator(leftMotor,rightMotor,odometer);
//            OdometerDisplay odometerDisplay = new OdometerDisplay(odometer,t);
//            OdometerCorrection odometerCorrection = new OdometerCorrection( navigator, odometer, leftLightSensor, rightLightSensor );
//            Launcher launcher = new Launcher( leftLaunchMotor, rightLaunchMotor, navigator, odometerCorrection );
//            Localizer localizer = new Localizer( odometer, forwardUltrasonicSensor, navigator, parameters.getForwardCorner() );
//            // start odometry threads
//            odometer.start();
//            odometerDisplay.start();
//            // run localization
//            localizer.run();
//            // notify profs localization has completed
//            Sound.beepSequenceUp();
//            // start odometry correction
//            odometerCorrection.start();
//
//            try { Thread.sleep( 1000 ); } catch( Exception e ){}
//
//            doBetaDemo( navigator, launcher ); //code to travel to shooting position and fire ball
//
//            navigator.travelToSquare(odometer.getFieldMapper().getMapping()[0][0]);
//        }

        /**
         * Uncomment for non-wifi code
         */
        EmergencyStopper emergencyStopper = new EmergencyStopper();
        emergencyStopper.start();

        int[] defenderZone = {4,4};
        int[] ballDispenserPosition  = {-1,6};
        Parameters parameters = new Parameters();
        parameters.setForwardCorner(1);
        parameters.setForwardLine(7);
        parameters.setForwardTeam(11);
        parameters.setDefenderZone(defenderZone);
        parameters.setBallDispenserPosition(ballDispenserPosition);
        parameters.setBallDispenserOrientation("E");


        // map field
        FieldMapper fieldMapper = new FieldMapper( parameters );

        // instantiate timed threads (will start/stop throughout program)
        LightSensor leftLightSensor = new LightSensor( leftColorSensor );
        LightSensor rightLightSensor = new LightSensor( rightColorSensor );
        UltrasonicSensor forwardUSSensor = new UltrasonicSensor( forwardUltrasonicSensor );
        UltrasonicSensor leftUSSensor = new UltrasonicSensor( leftUltrasonicSensor );
        leftLightSensor.start();
        rightLightSensor.start();
        forwardUSSensor.start();
        leftUSSensor.start();

        // instantiate continuous threads (don't stop during the program)
        Odometer odometer = new Odometer( leftMotor, rightMotor, fieldMapper);
        OdometerDisplay odometerDisplay = new OdometerDisplay( odometer, t );
        odometer.start();
        odometerDisplay.start();

        // instantiate movement controllers
        ObstacleAvoider obstacleAvoider = new ObstacleAvoider( forwardUSSensor, odometer );
        ObstacleMapper obstacleMapper = new ObstacleMapper( leftUSSensor, odometer );
        Navigator navigator = new Navigator( leftMotor, rightMotor, odometer, obstacleAvoider, obstacleMapper);
        OdometerCorrection odometerCorrection = new OdometerCorrection( navigator, odometer, leftLightSensor, rightLightSensor );

        // instantiate offense controllers
        Launcher launcher = new Launcher( leftLaunchMotor, rightLaunchMotor, navigator, odometer );
        BallRetriever ballRetriever = new BallRetriever( launcher, odometer, navigator, odometerCorrection );

        Sound.beep();

        // localize
        Localizer localizer = new Localizer( odometer, forwardUSSensor, navigator, getStartingCorner( parameters ) );
        localizer.start();

        // signal end of localization
        Sound.beepSequence();

        odometerCorrection.start(); // timed thread - waits until further instruction to actually start

        try { Thread.sleep( 1000 ); } catch( Exception e ){}

        playOffenseWithMapping( navigator, odometer, obstacleMapper, ballRetriever, launcher );
//        playOffense( navigator, odometer, ballRetriever, launcher );
//        playDefense( navigator, odometer );

        int buttonChoice = Button.waitForAnyPress();
        System.exit(0);
    }

    /**
     * A method to play offense with field mapping and strict avoidance (used in third round)
     *
     * @param navigator
     * @param odometer
     * @param obstacleMapper
     * @param ballRetriever
     * @param launcher
     */
    private static void playOffenseWithMapping( Navigator navigator, Odometer odometer, ObstacleMapper obstacleMapper,
                                                BallRetriever ballRetriever,
                                                Launcher launcher ) {
        navigator.setObstacleMappingNeeded( true );
        obstacleMapper.startRunning();
        ballRetriever.getBall();
    }

    /**
     * A method to play offense without field mapping and minimal avoidance (used in first/second round)
     *
     * @param navigator
     * @param odometer
     * @param ballRetriever
     * @param launcher
     */
    private static void playOffense( Navigator navigator, Odometer odometer, BallRetriever ballRetriever, Launcher launcher ) {
        ballRetriever.getBall();
    }

    /**
     * A method to play defense
     *
     * @param navigator
     * @param odometer
     */
    private static void playDefense( Navigator navigator, Odometer odometer ) {
        navigator.travelToSquare( odometer.getFieldMapper().getMapping()[3][3] );
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
