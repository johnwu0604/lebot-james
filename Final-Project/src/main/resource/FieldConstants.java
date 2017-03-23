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
    public static final int TARGET_CENTER_Y = 6;
    public static final int TARGET_CENTER_X = 2;
    public static final double TARGET_CENTER_X_COORDINATE = TARGET_CENTER_X *SQUARE_LENGTH;
    public static final double TARGET_CENTER_Y_COORDINATE = TARGET_CENTER_Y *SQUARE_LENGTH;

}
