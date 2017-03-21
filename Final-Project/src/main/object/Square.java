package main.object;

/**
 * An object which represents the characteristics of each square on our field
 */
public class Square {

    private int[] squarePosition;
    private double[] centerCoordinate;
    private boolean allowed;
    private boolean obstacle;
    private boolean shootingPosition;
    private double northLine;
    private double southLine;
    private double eastLine;
    private double westLine;

    public Square( int x, int y) {
        squarePosition = getSquarePositionArray( x, y );
    }

    /**
     * A method to get the position array of the square
     *
     * @param x
     * @param y
     * @return position vector
     */
    public static int[] getSquarePositionArray( int x, int y ) {
        int[] position = new int[2];
        position[0] = x;
        position[1] = y;
        return position;
    }

    public int[] getSquarePosition() {
        return squarePosition;
    }

    public void setSquarePosition(int[] squarePosition) {
        this.squarePosition = squarePosition;
    }

    public double[] getCenterCoordinate() {
        return centerCoordinate;
    }

    public void setCenterCoordinate(double[] centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isShootingPosition() {
        return shootingPosition;
    }

    public void setShootingPosition(boolean shootingPosition) {
        this.shootingPosition = shootingPosition;
    }

    public double getNorthLine() {
        return northLine;
    }

    public void setNorthLine(double northLine) {
        this.northLine = northLine;
    }

    public double getSouthLine() {
        return southLine;
    }

    public void setSouthLine(double southLine) {
        this.southLine = southLine;
    }

    public double getEastLine() {
        return eastLine;
    }

    public void setEastLine(double eastLine) {
        this.eastLine = eastLine;
    }

    public double getWestLine() {
        return westLine;
    }

    public void setWestLine(double westLine) {
        this.westLine = westLine;
    }
}
