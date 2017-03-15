package main.object;

import lejos.hardware.lcd.TextLCD;
import main.controller.Odometer;
import main.resource.Constants;

/**
 * An object class that outputs values to our odometry display
 *
 * @author JohnWu
 */
public class OdometerDisplay extends Thread {
    private Odometer odometer;
    private TextLCD t;

    /**
     * Our main constructor class
     *
     * @param odometer the odometer controller used in the robot
     * @param textLCD the LCD Screen
     */
    public OdometerDisplay(Odometer odometer, TextLCD textLCD) {
        this.odometer = odometer;
        this.t = textLCD;
    }

    /**
     * The main thread
     */
    public void run() {
        long displayStart, displayEnd;
        double[] position = new double[3];

        // clear the display once
        t.clear();

        while (true) {
            displayStart = System.currentTimeMillis();

            // clear the lines for displaying odometry information
            t.drawString("X:              ", 0, 0);
            t.drawString("Y:              ", 0, 1);
            t.drawString("T:              ", 0, 2);

            // get the odometry information
            odometer.updatePosition(position, new boolean[] { true, true, true });

            // display odometry information
            for (int i = 0; i < 3; i++) {
                t.drawString(formattedDoubleToString(position[i], 2), 3, i);
            }

            // throttle the OdometerDisplay
            displayEnd = System.currentTimeMillis();
            if (displayEnd - displayStart < Constants.ODOMETER_PERIOD) {
                try {
                    Thread.sleep(Constants.ODOMETER_PERIOD - (displayEnd - displayStart));
                } catch (InterruptedException e) {
                    // there is nothing to be done here because it is not
                    // expected that OdometerDisplay will be interrupted
                    // by another thread
                }
            }
        }
    }

    /**
     * A method which formats the double value to properly be displayed on the screen
     *
     * @param x the double value to be formatted
     * @param places the amount of decimal places
     * @return
     */
    private static String formattedDoubleToString(double x, int places) {
        String result = "";
        String stack = "";
        long t;

        // put in a minus sign as needed
        if (x < 0.0)
            result += "-";

        // put in a leading 0
        if (-1.0 < x && x < 1.0)
            result += "0";
        else {
            t = (long)x;
            if (t < 0)
                t = -t;

            while (t > 0) {
                stack = Long.toString(t % 10) + stack;
                t /= 10;
            }

            result += stack;
        }

        // put the decimal, if needed
        if (places > 0) {
            result += ".";

            // put the appropriate number of decimals
            for (int i = 0; i < places; i++) {
                x = Math.abs(x);
                x = x - Math.floor(x);
                x *= 10.0;
                result += Long.toString((long)x);
            }
        }

        return result;
    }

}