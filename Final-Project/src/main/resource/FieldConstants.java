package main.resource;

/**
 * A resource class that stores all field constants
 *
 * @author JohnWu
 */
public class FieldConstants {

    /**
     * Length of square
     */
    public static final double SQUARE_LENGTH = 30.48;

    /**
     * Starting x coordinate for corner 1
     */
    public static final double CORNER_ONE_X = 0.0;

    /**
     * Starting y coordinate for corner 1
     */
    public static final double CORNER_ONE_Y = 0.0;

    /**
     * Starting theta for corner 1
     */
    public static final double CORNER_ONE_THETA = Math.PI/2;

    /**
     * Starting x coordinate for corner 2
     */
    public static final double CORNER_TWO_X = 10*SQUARE_LENGTH;

    /**
     * Starting y coordinate for corner 2
     */
    public static final double CORNER_TWO_Y = 0.0;

    /**
     * Starting theta for corner 2
     */
    public static final double CORNER_TWO_THETA = 0.0;

    /**
     * Starting x coordinate for corner 3
     */
    public static final double CORNER_THREE_X = 10*SQUARE_LENGTH;

    /**
     * Starting y coordinate for corner 3
     */
    public static final double CORNER_THREE_Y = 10*SQUARE_LENGTH;

    /**
     * Starting theta for corner 3
     */
    public static final double CORNER_THREE_THETA = (3*Math.PI)/2;

    /**
     * Starting x coordinate for corner 4
     */
    public static final double CORNER_FOUR_X = 0.0;

    /**
     * Starting y coordinate for corner 4
     */
    public static final double CORNER_FOUR_Y = 10*SQUARE_LENGTH;

    /**
     * Starting theta for corner 4
     */
    public static final double CORNER_FOUR_THETA = Math.PI;

    /**
     * The position of the target center in the x coordinate (square units)
     */
    public static final int TARGET_CENTER_Y = 10;

    /**
     * The position of the target center in the y coordinate (square units)
     */
    public static final int TARGET_CENTER_X = 5;

    /**
     * The position of the target center in the x coordinate (cm)
     */
    public static final double TARGET_CENTER_X_COORDINATE = TARGET_CENTER_X *SQUARE_LENGTH;

    /**
     * The position of the target center in the y coordinate (cm)
     */
    public static final double TARGET_CENTER_Y_COORDINATE = TARGET_CENTER_Y *SQUARE_LENGTH;

}
