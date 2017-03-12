package main;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import main.controller.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.model.Parameters;
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
    private static final SampleProvider forwardUltrasonicSensor = ( new EV3UltrasonicSensor( LocalEV3.get().getPort( "S1" ) ) ).getMode("Distance");

    private static Parameters parameters;

    public static void main(String[] args) {

        @SuppressWarnings("main/resource")
        final TextLCD t = LocalEV3.get().getTextLCD();
        /**
         * Uncomment for wifi code
         */
//        retrieveStartingParameters();
//
//        if ( parameters.getForwardTeam() == 11 ) {
//            Odometer odometer = new Odometer(leftMotor,rightMotor);
//            Navigator navigator = new Navigator(leftMotor,rightMotor,odometer);
//            OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
//            odometer.start();
//            odometryDisplay.start();
//
//            Localizer localizer = new Localizer( odometer, forwardUltrasonicSensor, navigator, parameters.getForwardCorner() );
//            localizer.run();
//        }

        /**
         * Uncomment for non-wifi code
         */
        Odometer odometer = new Odometer(leftMotor,rightMotor);
        Navigator navigator = new Navigator(leftMotor,rightMotor,odometer);
        OdometerDisplay odometerDisplay = new OdometerDisplay(odometer,t);
        odometer.start();
        odometerDisplay.start();

        Localizer localizer = new Localizer( odometer, forwardUltrasonicSensor, navigator, 2 );
        localizer.run();


        int buttonChoice = Button.waitForAnyPress();
        System.exit(0);
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
