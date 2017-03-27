package main.resource;

/**
 * Stores all the threshold constant variables in our code
 *
 * @author JohnWu
 */
public class ThresholdConstants {
    
    /**
     * Value in localization that determines a wall has been detected
     */
    public static final double LOCALIZATION_WALL_DISTANCE = 100;

    /**
     * Value in localization for the noise margin
     */
    public static final double LOCALIZATION_NOISE_MARGIN = 3;

    /**
     * Sensor reading distance filter (max distance)
     */
    public static final int ULTRASONICSENSOR_MAX_DISTANCE = 235;

    /**
     * Threshold for detecting a line using light sensor
     */
    public static final double LINE_DETECTION = 0.4;

    /**
     * Threshold for reaching a point
     */
    public static final double POINT_REACHED = 2.5;

    /**
     * The maximum distance we want to track obstacles
     */
    public static final double OBSTACLE_TRACKING = 75;

    /**
     * The threshold for determining which square a coordinate is in.
     * Any coordinate closer than this distance to a line is too hard too tell the exact square.
     */
    public static final double COORDINATE_IN_SQUARE = 2.0;




}
