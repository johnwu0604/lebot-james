package main.resource;

/**
 * A resource class which stores all our constants related to shooting
 *
 * @author DurhamAbric
 */
public class ShootingConstants {

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
    public static final int LAUNCH_RETRACTION_ROM = -260;

    /**
     * angle to rotate arm to fire 4 squares
     */
    public static final int LAUNCH_ROM_4 = 100;

    /**
     * angle to rotate arm to fire 5 squares
     */
    public static final int LAUNCH_ROM_5 = 120;

    /**
     * angle to rotate arm to fire 6 squares
     */
    public static final int LAUNCH_ROM_6 = 130;

    /**
     * angle to rotate arm to fire 7 squares
     */
    public static final int LAUNCH_ROM_7 = 135;

    /**
     * angle to rotate arm to fire 8 squares
     */
    public static final int LAUNCH_ROM_8 = 140;

    /**
     * angle to rotate arm to fire 8+ squares
     */
    public static final int LAUNCH_ROM_MAX = 145;
}
