package main;

/**
 * An object that represents input paramters from the server.
 *
 * @author JohnWu
 */
public class Parameters {

    private int forwardTeam;
    private int defenseTeam;
    private int forwardCorner;
    private int defenseCorner;
    private int forwardLine;
    private int[] defenderZone;
    private int[] ballDispenserPosition;
    private String ballDispenserOrientation;

    /**
     * The main constructor method
     */
    public Parameters() {
        defenderZone = new int[2];
        ballDispenserPosition = new int[2];
    }

    /**
     * A method to get the forward team
     *
     * @return
     */
    public int getForwardTeam() {
        return forwardTeam;
    }

    /**
     * A method to set the forward team
     *
     * @param forwardTeam
     */
    public void setForwardTeam(int forwardTeam) {
        this.forwardTeam = forwardTeam;
    }

    /**
     * A method to get the defense team
     *
     * @return
     */
    public int getDefenseTeam() {
        return defenseTeam;
    }

    /**
     * A method to set the defense team
     *
     * @param defenseTeam
     */
    public void setDefenseTeam(int defenseTeam) {
        this.defenseTeam = defenseTeam;
    }

    /**
     * A method to get the forward corner
     *
     * @return
     */
    public int getForwardCorner() {
        return forwardCorner;
    }

    /**
     * A method to set the forward corner
     *
     * @param forwardCorner
     */
    public void setForwardCorner(int forwardCorner) {
        this.forwardCorner = forwardCorner;
    }

    /**
     * A method to get the defense corner
     *
     * @return
     */
    public int getDefenseCorner() {
        return defenseCorner;
    }

    /**
     * A method to set the defense corner
     *
     * @param defenseCorner
     */
    public void setDefenseCorner(int defenseCorner) {
        this.defenseCorner = defenseCorner;
    }

    /**
     * A method to get the forward line
     *
     * @return
     */
    public int getForwardLine() {
        return forwardLine;
    }

    /**
     * A method to set the forward line
     *
     * @param forwardLine
     */
    public void setForwardLine(int forwardLine) {
        this.forwardLine = forwardLine;
    }

    /**
     * A method to get the defender zone
     *
     * @return
     */
    public int[] getDefenderZone() {
        return defenderZone;
    }

    /**
     * A method to set the defender zone
     *
     * @param defenderZone
     */
    public void setDefenderZone(int[] defenderZone) {
        this.defenderZone = defenderZone;
    }

    /**
     * A method to get the ball dispenser position
     *
     * @return
     */
    public int[] getBallDispenserPosition() {
        return ballDispenserPosition;
    }

    /**
     * A method to set the ball dispenser position
     *
     * @param ballDispenserPosition
     */
    public void setBallDispenserPosition(int[] ballDispenserPosition) {
        this.ballDispenserPosition = ballDispenserPosition;
    }

    /**
     * A method to get the ball dispenser orientation
     *
     * @return
     */
    public String getBallDispenserOrientation() {
        return ballDispenserOrientation;
    }

    /**
     * A method to set the ball dispenser orientation
     *
     * @param ballDispenserOrientation
     */
    public void setBallDispenserOrientation(String ballDispenserOrientation) {
        this.ballDispenserOrientation = ballDispenserOrientation;
    }
}
