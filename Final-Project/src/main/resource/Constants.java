package main.resource;

/**
 * Stores all the constant variables in our code
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
    public static final int VEHICLE_ACCELERATION = 200;

    /**
     * Odometer update period, in milliseconds
     */
    public static final long ODOMETER_PERIOD = 25;

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
    public static final double LOCALIZATION_WALL_DISTANCE = 30;

    /**
     * Value in localization for the noise margin
     */
    public static final double LOCALIZATION_NOISE_MARGIN = 3;

    /**
     * Localization sensor reading interval (ms)
     */
    public static final long LOCALIZATION_SENSOR_READING_PERIOD = 50;

    /**
     * Coordinate of each corner on the field
     */
    public static final double CORNER_ONE_X = 0.0;
    public static final double CORNER_ONE_Y = 0.0;

}