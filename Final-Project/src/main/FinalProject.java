package main;

import main.controller.Navigator;
import main.controller.Odometer;
import main.controller.OdometryDisplay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Created by JohnWu on 2017-02-25.
 */
public class FinalProject {

    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

    public static void main(String[] args) {


        @SuppressWarnings("main/resource")
        final TextLCD t = LocalEV3.get().getTextLCD();
        t.clear();
        t.drawString("Hello World", 0, 0);
        int buttonChoice = Button.waitForAnyPress();
        if ( buttonChoice == Button.ID_UP ) {
            Odometer o = new Odometer(leftMotor,rightMotor);
            Navigator n = new Navigator(leftMotor,rightMotor,o);
            OdometryDisplay odometryDisplay = new OdometryDisplay(o,t);
            o.start();
            odometryDisplay.start();
            n.travelTo(0,30);
            n.travelTo(30,0);
        }
        System.exit(0);
    }
}
