package main.resource;

/**
 * A resource class which stores all our constants related to shooting
 *
 * @author DurhamAbric
 */
public class ShootingConstants {

    /**
     * angle to raise arm to retireve ball
     */
//    public static final int BALL_RETRIEVAL_ANGLE = 47;
    public static final int BALL_RETRIEVAL_ANGLE = 44;

    /**
     * angle to retract arm to have it vertical
     */
    public static final int VERTICAL_ANGLE = -70;

    /**
     * slow acceleration to lower ball without dropping it
     */
    public static final int BALL_LOWERING_ACCELERATION = 50;

    /**
     * acceleration to launch a ball
     */
    public static final int LAUNCH_MOTOR_ACCELERATION = 1000000;

    /**
     * acceleration to move launch arm
     */
    public static final int LAUNCH_MOTOR_RETRACTION_ACCELERATION = 400;

    /**
     * speed to move launch arm
     */
    public static final int LAUNCH_MOTOR_RETRACTION_SPEED = 700;

    /**
     * angle to rotate arm before firing
     */
    public static final int LAUNCH_RETRACTION_ROM = -260;

    /**
     * angle to rotate arm to fire 4 squares
     */
    public static final int LAUNCH_ROM_4 = 100;

    /**
     * angle to rotate arm to fire 5 squares
     */
    public static final int LAUNCH_ROM_5 = 110;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_SPEED_5 = 400;

    /**
     * angle to rotate arm to fire 6 squares
     */
    public static final int LAUNCH_ROM_6 = 120;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_SPEED_6 = 400;

    /**
     * angle to rotate arm to fire 7 squares
     */
    public static final int LAUNCH_ROM_7 = 130;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_SPEED_7 = 400;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_ROM_8 = 130;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_SPEED_8 = 550;

    /**
     * angle to rotate arm to fire 8+ squares
     */
    public static final int LAUNCH_ROM_MAX = 130;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_SPEED_MAX = 700;
}
