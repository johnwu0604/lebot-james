package main;

import main.controller.Navigator;
import main.controller.Odometer;
import main.controller.OdometryDisplay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.wifi.WifiConnection;
import main.wifi.WifiProperties;

import java.util.Map;

/**
 * Main class for vehicle
 *
 * @author JohnWu
 */
public class FinalProject {

    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

    public static void main(String[] args) {

        /**
         * Uncomment for wifi code
         */
//        System.out.println("Running..");
//
//        // Initialize WifiConnection class
//        WifiConnection conn = new WifiConnection( WifiProperties.SERVER_IP, WifiProperties.TEAM_NUMBER,
//                WifiProperties.ENABLE_DEBUG_WIFI_PRINT );
//
//        // Connect to server and get the data, catching any errors that might
//        // occur
//        try {
//			/*
//			 * getData() will connect to the server and wait until the user/TA
//			 * presses the "Start" button in the GUI on their laptop with the
//			 * data filled in. Once it's waiting, you can kill it by
//			 * pressing the upper left hand corner button (back/escape) on the EV3.
//			 * getData() will throw exceptions if it can't connect to the server
//			 * (e.g. wrong IP address, server not running on laptop, not connected
//			 * to WiFi router, etc.). It will also throw an exception if it connects
//			 * but receives corrupted data or a message from the server saying something
//			 * went wrong. For example, if TEAM_NUMBER is set to 1 above but the server expects
//			 * teams 17 and 5, this robot will receive a message saying an invalid team number
//			 * was specified and getData() will throw an exception letting you know.
//			 */
//            Map data = conn.getData();
//
//            // Example 1: Print out all received data
//            System.out.println("Map:\n" + data);
//
//            // Example 2 : Print out specific values
//            int fwdTeam = ((Long) data.get("FWD_TEAM")).intValue();
//            System.out.println("Forward Team: " + fwdTeam);
//
//            int w1 = ((Long) data.get("w1")).intValue();
//            System.out.println("Defender zone size w1: " + w1);
//
//            // Example 3: Compare value
//            String orientation = (String) data.get("omega");
//            if (orientation.equals("N")) {
//                System.out.println("Orientation is North");
//            }
//            else {
//                System.out.println("Orientation is not North");
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//        }
//
//        // Wait until user decides to end program
//        Button.waitForAnyPress();


        /**
         * Uncomment for regular code without wifi.
         */

//        @SuppressWarnings("main/resource")
//        final TextLCD t = LocalEV3.get().getTextLCD();
//        t.clear();
//        t.drawString("Hello World", 0, 0);
//        int buttonChoice = Button.waitForAnyPress();
//        if ( buttonChoice == Button.ID_UP ) {
//            Odometer o = new Odometer(leftMotor,rightMotor);
//            Navigator n = new Navigator(leftMotor,rightMotor,o);
//            OdometryDisplay odometryDisplay = new OdometryDisplay(o,t);
//            o.start();
//            odometryDisplay.start();
//            n.travelTo(0,30);
//            n.travelTo(30,0);
//        }
//        System.exit(0);
    }
}
