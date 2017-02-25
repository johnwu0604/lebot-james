package main;

import com.sun.tools.internal.jxc.apt.Const;
import controller.Navigator;
import controller.Odometer;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import resource.Constants;

/**
 * Created by JohnWu on 2017-02-25.
 */
public class FinalProject {

    private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
    private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

    public static void main(String[] args) {


        @SuppressWarnings("resource")
        final TextLCD t = LocalEV3.get().getTextLCD();
        t.clear();
        t.drawString("Hello World", 0, 0);
        int buttonChoice = Button.waitForAnyPress();
        if ( buttonChoice == Button.ID_UP ) {
            Odometer o = new Odometer(leftMotor,rightMotor);
            Navigator n = new Navigator(leftMotor,rightMotor,o);
            n.travelTo(0,90);
            n.travelTo(90,0);
        }
        System.exit(0);
    }
}
