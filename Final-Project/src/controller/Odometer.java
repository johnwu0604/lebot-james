package controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import resource.Constants;

/**
 * Odometer object used to keep track of vehicle position at all times.
 *
 * @author JohnWu
 */
public class Odometer extends Thread {

    // objects
    private EV3LargeRegulatedMotor leftMotor, rightMotor;
    private Object lock;

    // variables
    private double x, y, theta;
    private int currentLeftMotorTachoCount, currentRightMotorTachoCount,
            prevLeftMotorTachoCount, prevRightMotorTachoCount;

    // constants
    private double TRACK_LENGTH, WHEEL_CIRCUM;
    private long ODOMETER_PERIOD;

    /**
     * Default constructor for an odometer object.
     *
     * @param leftMotor
     * @param rightMotor
     */
    public Odometer( EV3LargeRegulatedMotor leftMotor , EV3LargeRegulatedMotor rightMotor ) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        lock = new Object();
        x = 0.0;
        y = 0.0;
        theta = 0.0;
        currentLeftMotorTachoCount = 0;
        currentRightMotorTachoCount = 0;
        prevLeftMotorTachoCount = 0;
        prevRightMotorTachoCount = 0;
        TRACK_LENGTH = Constants.TRACK_LENGTH;
        WHEEL_CIRCUM = Math.PI*2*Constants.WHEEL_RADIUS;
        ODOMETER_PERIOD = Constants.ODOMETER_PERIOD;
    }

    /**
     * Our main odometer thread.
     */
    public void run() {
        long updateStart, updateEnd;

        while ( true ) {
            updateStart = System.currentTimeMillis();

            // Get current tachometer values
            currentLeftMotorTachoCount = leftMotor.getTachoCount();
            currentRightMotorTachoCount = rightMotor.getTachoCount();

            // Compare it with the previous value to get the change
            int leftDeltaTacho = currentLeftMotorTachoCount - prevLeftMotorTachoCount;
            int rightDeltaTacho = currentRightMotorTachoCount - prevRightMotorTachoCount;

            // Use our change in rotation values to calculate displacement of each wheel
            double leftMotorDisplacement = WHEEL_CIRCUM*leftDeltaTacho/360;
            double rightMotorDisplacement = WHEEL_CIRCUM*rightDeltaTacho/360;

            // change in angle of our vehicle
            double thetaChange = ( leftMotorDisplacement - rightMotorDisplacement ) / TRACK_LENGTH;
            // change in distance of our vehicle
            double displacement = ( leftMotorDisplacement + rightMotorDisplacement ) / 2;

            prevLeftMotorTachoCount = currentLeftMotorTachoCount;
            prevRightMotorTachoCount = currentRightMotorTachoCount;


            synchronized ( lock ) {
                // update odometer values
                theta += thetaChange;
                x += displacement*Math.sin( theta );
                y += displacement*Math.cos( theta );

            }

            // ensure that the odometer only runs once every period
            updateEnd = System.currentTimeMillis();
            if ( updateEnd - updateStart < ODOMETER_PERIOD ) {
                try {
                    Thread.sleep( ODOMETER_PERIOD - ( updateEnd - updateStart ) );
                } catch ( InterruptedException e ) {
                    // there is nothing to be done here because it is not
                    // expected that the odometer will be interrupted by
                    // another thread
                }
            }
        }
    }

    /**
     * A method to get the x-coordinate of the our vehicle position.
     *
     * @return
     */
    public double getX() {
        double result;
        synchronized ( lock ) {
            result = x;
        }
        return result;
    }

    /**
     * A method to get the y-coordinate of the our vehicle position.
     *
     * @return
     */
    public double getY() {
        double result;
        synchronized ( lock ) {
            result = y;
        }
        return result;
    }

    /**
     * A method to get the theta of the our vehicle position.
     *
     * @return
     */
    public double getTheta() {
        double result;
        synchronized ( lock ) {
            result = theta;
        }
        return result;
    }

}
