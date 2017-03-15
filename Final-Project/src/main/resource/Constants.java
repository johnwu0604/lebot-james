package main.resource;

/**
 * Stores all the constant variables in our code
 *
 * @author JohnWu
 */
public class Constants {

    /**
     * High speed for forward movement
     */
    public static final int VEHICLE_FORWARD_SPEED_HIGH = 400;

    /**
     * Low speed for forward movement
     */
    public static final int VEHICLE_FORWARD_SPEED_LOW = 150;

    /**
     * Vehicle rotation speed
     */
    public static final int VEHICLE_ROTATE_SPEED = 100;

    /**
     * Vehicle acceleration
     */
    public static final int VEHICLE_ACCELERATION = 500;

    /**
     * Odometer update period, in milliseconds
     */
    public static final long ODOMETER_PERIOD = 10;

    /**
     * Track length of vehicle, in centimetres
     */
    public static final double TRACK_LENGTH = 15.83;

    /**
     * Radius of wheel, in centimetres
     */
    public static final double WHEEL_RADIUS = 2.093;

    /**
     * Length of square
     */
    public static final double SQUARE_LENGTH = 30.48;

    /**
     * Value in localization that determines a wall has been detected
     */
    public static final double LOCALIZATION_WALL_DISTANCE = 50;

    /**
     * Value in localization for the noise margin
     */
    public static final double LOCALIZATION_NOISE_MARGIN = 3;

    /**
     * Sensor reading distance filter (max distance)
     */
    public static final int ULTRASONICSENSOR_MAX_DISTANCE = 200;

    /**
     * Localization sensor reading interval (ms)
     */
    public static final long ULTRASONICSENSOR_SENSOR_READING_PERIOD = 10;

    /**
     * Color sensor reading hold time to allow time for line to pass
     */
    public static final long COLOR_SENSOR_HOLD_TIME = 2000;

    /**
     * Distance from forward sensor to middle of vehicle (cm)
     */
    public static final float FORWARD_SENSOR_DISTANCE = 7;

    /**
     * Lower threshold for light sensor
     */
    public static final double LOWER_LIGHT_THRESHOLD = 0.4;

    /**
     * Upper threshold for light sensor
     */
    public static final double UPPER_LIGHT_THRESHOLD = 0.5;

    /**
     * Threshold for reaching a point
     */
    public static final double POINT_REACHED_THRESHOLD = 0.5;

    public static final double CORNER_ONE_X = 0.0;
    public static final double CORNER_ONE_Y = 0.0;
    public static final double CORNER_ONE_THETA = Math.PI/2;
    public static final double CORNER_TWO_X = 10*SQUARE_LENGTH;
    public static final double CORNER_TWO_Y = 0.0;
    public static final double CORNER_TWO_THETA = 0.0;
    public static final double CORNER_THREE_X = 10*SQUARE_LENGTH;
    public static final double CORNER_THREE_Y = 10*SQUARE_LENGTH;
    public static final double CORNER_THREE_THETA = (3*Math.PI)/2;
    public static final double CORNER_FOUR_X = 0.0;
    public static final double CORNER_FOUR_Y = 10*SQUARE_LENGTH;
    public static final double CORNER_FOUR_THETA = Math.PI;

}
