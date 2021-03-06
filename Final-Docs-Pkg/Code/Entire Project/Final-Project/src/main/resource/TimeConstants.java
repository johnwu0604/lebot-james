package main.resource;

/**
 * A resource class that stores the time constants
 *
 * @author JohnWu
 */
public class TimeConstants {

    /**
     * Odometer update period, in milliseconds
     */
    public static final long ODOMETER_PERIOD = 10;

    /**
     * Localization sensor reading interval (ms)
     */
    public static final long ULTRASONICSENSOR_SENSOR_READING_PERIOD = 10;

    /**
     * Light sensor reading interval (ms)
     */
    public static final long LIGHT_SENSOR_READING_PERIOD = 25;

    /**
     * Color sensor reading hold time to allow time for line to pass
     */
    public static final long COLOR_SENSOR_HOLD_TIME = 2000;

    /**
     * Hold time for correcting sensor to catch up to the spot that other motor decelerated to
     */
    public static final long LINE_DETECTION_HOLD_TIME = 100;

    /**
     * The max time odometry correction can happen for
     */
    public static final double CORRECTION_MAX_TIME = 1000;

    /**
     * The max time alignment can happen for ball retrieval
     */
    public static final double ALIGNMENT_MAX_TIME = 3000;

    /**
     * Hold time for alignment to retreive ball
     */
    public static final double ALIGNMENT_HOLD_TIME = 100;

    /**
     * The time to scan for obstacles
     */
    public static final double OBSTACLE_SCAN_TIME = 350;


}
