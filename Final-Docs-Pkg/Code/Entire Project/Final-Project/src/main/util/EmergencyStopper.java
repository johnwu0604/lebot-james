package main.util;

import lejos.hardware.Button;

/**
 * A class to stop our code in emergency situations
 *
 * @author JohnWu
 */
public class EmergencyStopper extends Thread {

    /**
     * The main constructor class
     */
    public EmergencyStopper() {

    }

    /**
     * The main thread
     */
    public void run() {
        while (true) {
            Button.waitForAnyPress();
            System.exit(0);
        }
    }



}
