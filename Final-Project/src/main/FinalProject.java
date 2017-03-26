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
        int[] ballDispenserPosition  = {-1,4};
        Parameters parameters = new Parameters();
        parameters.setForwardCorner(1);
        parameters.setForwardLine(7);
        parameters.setForwardTeam(11);
        parameters.setDefenderZone(defenderZone);
        parameters.setBallDispenserPosition(ballDispenserPosition);
        parameters.setBallDispenserOrientation("N");


        // map field
        FieldMapper fieldMapper = new FieldMapper(parameters);

        // instantiate sensor objects
        LightSensor leftLightSensor = new LightSensor( leftColorSensor );
        LightSensor rightLightSensor = new LightSensor( rightColorSensor );
        UltrasonicSensor forwardUSSensor = new UltrasonicSensor( forwardUltrasonicSensor );
        UltrasonicSensor leftUSSensor = new UltrasonicSensor( leftUltrasonicSensor );

        // instantiate continuous threads
        Odometer odometer = new Odometer(leftMotor,rightMotor,fieldMapper);
        OdometerDisplay odometerDisplay = new OdometerDisplay(odometer,t);

        // instantiate movement controllers
        ObstacleAvoider obstacleAvoider = new ObstacleAvoider( forwardUSSensor, odometer );
        ObstacleMapper obstacleMapper = new ObstacleMapper( leftUSSensor, odometer );
        Navigator navigator = new Navigator(leftMotor,rightMotor,odometer,obstacleAvoider);
        OdometerCorrection odometerCorrection = new OdometerCorrection( navigator, odometer, leftLightSensor, rightLightSensor );

        // instantiate launcher
        Launcher launcher = new Launcher( leftLaunchMotor, rightLaunchMotor, navigator );

        // start threads (except correction, start that after localizing)
        leftLightSensor.start(); // waits until further instruction to actually start
        rightLightSensor.start(); // waits until further instruction to actually start
        forwardUSSensor.start(); // waits until further instruction to actually start
        leftUSSensor.start(); // waits until further instruction to actually start
        odometer.start(); // starts immediately
        odometerDisplay.start(); // starts immediately
        obstacleMapper.start(); // waits until further instruction to actually start


        // localize
        Localizer localizer = new Localizer( odometer, forwardUSSensor, navigator, 1 );
        localizer.start();

        odometerCorrection.start(); // waits until further instruction to actually start
        obstacleMapper.startRunning();

        try { Thread.sleep( 1000 ); } catch( Exception e ){}

        navigator.travelToSquare(odometer.getFieldMapper().getMapping()[3][1]);
        navigator.travelToSquare(odometer.getFieldMapper().getMapping()[3][0]);
        navigator.travelToSquare(odometer.getFieldMapper().getMapping()[1][1]);
        navigator.travelToSquare(odometer.getFieldMapper().getMapping()[0][0]);

        int buttonChoice = Button.waitForAnyPress();
        System.exit(0);
    }

    /**
     * A method to pass the beta demo
     *
     * @param navigator
     * @param launcher
     */
    public static void doBetaDemo( Navigator navigator, Launcher launcher ){
        navigator.travelToSquare(navigator.getOdometer().getFieldMapper().getMapping()[1][1]);

        launcher.retractArm();
        Sound.beep(); //Notify ball is ready to be placed
        try { Thread.sleep( 5000 ); } catch( Exception e ){}

        navigator.travelToSquare(navigator.getOdometer().getFieldMapper().getMapping()[5][1]);

        launcher.launchBall();
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
}
