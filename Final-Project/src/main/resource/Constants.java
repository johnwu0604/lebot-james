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
    public static final int VEHICLE_FORWARD_SPEED_LOW = 200;

    /**
     * Vehicle rotation speed
     */
    public static final int VEHICLE_ROTATE_SPEED = 150;

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
     * Hold time for correcting sensor to catch up to the spot that other motor decelerated to
     */
    public static final long LINE_DETECTION_HOLD_TIME = 50;

    /**
     * Post correction hold time to account for deceleration go over line
     */
    public static final long CORRECTION_HOLD_TIME = 700;

    /**
     * Distance from forward sensor to middle of vehicle (cm)
     */
    public static final float FORWARD_SENSOR_DISTANCE = 7;

    /**
     * Threshold for detecting a line using light sensor
     */
    public static final double LINE_DETECTION_LIGHT_THRESHOLD = 0.4;

    /**
     * acceleration to launch a ball
     */
    public static final int LAUNCH_MOTOR_ACCELERATION = 1000000;

    /**
     * acceleration to move launch arm
     */
    public static final int LAUNCH_MOTOR_RETRACTION_ACCELERATION = 500;

    /**
     * speed to move launch arm
     */
    public static final int LAUNCH_MOTOR_RETRACTION_SPEED = 750;

    /**
     * angle to rotate arm before firing
     */
    public static final int LAUNCH_RETRACTION_ROM = -250;

    /**
     * angle to rotate arm to fire 5 squares
     */
    public static final int LAUNCH_ROM_5 = 130;

    /**
     * angle to rotate arm to fire 6 squares
     */
    public static final int LAUNCH_ROM_6 = 135;

    /**
     * angle to rotate arm to fire 7 squares
     */
    public static final int LAUNCH_ROM_7 = 135;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_ROM_8 = 135;

    /**
     * Threshold for reaching a point
     */
    public static final double POINT_REACHED_THRESHOLD = 2.0;

    public static final double CORRECTION_MAX_TIME = 1000;

    public static final double SENSOR_TO_TRACK_DISTANCE = 6.1;

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
    public static final double TARGET_CENTER_X_COORDINATE = 5*SQUARE_LENGTH;
    public static final double TARGET_CENTER_Y_COORDINATE = 10*SQUARE_LENGTH;


}
