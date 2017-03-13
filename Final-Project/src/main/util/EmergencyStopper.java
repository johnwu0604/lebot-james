package main.util;

import lejos.hardware.Button;

/**
 * A class to stop our code in emergency situations
 */
public class EmergencyStopper extends Thread {

    public EmergencyStopper() {

    }

    public void run() {
        while (true) {
            Button.waitForAnyPress();
            System.exit(0);
        }
    }



}
