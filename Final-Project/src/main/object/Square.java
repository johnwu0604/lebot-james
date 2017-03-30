package main.object;

import java.util.Arrays;

/**
 * An object which represents the characteristics of each square on our field
 */
public class Square {

    private int[] squarePosition;
    private double[] centerCoordinate;
    private boolean allowed;
    private boolean obstacle;
    private int shootingPriority;
    private double northLine;
    private double southLine;
    private double eastLine;
    private double westLine;

    public Square(int x, int y) {
        squarePosition = getSquarePositionArray(x, y);
    }

    /**
     * An override method used to determine if two squares are equal
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        if (allowed != square.allowed) return false;
        if (obstacle != square.obstacle) return false;
        if (shootingPriority != square.shootingPriority) return false;
        if (Double.compare(square.northLine, northLine) != 0) return false;
        if (Double.compare(square.southLine, southLine) != 0) return false;
        if (Double.compare(square.eastLine, eastLine) != 0) return false;
        if (Double.compare(square.westLine, westLine) != 0) return false;
        if (!Arrays.equals(squarePosition, square.squarePosition)) return false;
        return Arrays.equals(centerCoordinate, square.centerCoordinate);
    }

    /**
     * An override method to compute the hashcode of the square
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Arrays.hashCode(squarePosition);
        result = 31 * result + Arrays.hashCode(centerCoordinate);
        result = 31 * result + (allowed ? 1 : 0);
        result = 31 * result + (obstacle ? 1 : 0);
        temp = Double.doubleToLongBits(northLine);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(southLine);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(eastLine);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(westLine);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * A method to get the position array of the square
     *
     * @param x
     * @param y
     * @return positionArray
     */
    public static int[] getSquarePositionArray(int x, int y) {
        int[] position = new int[2];
        position[0] = x;
        position[1] = y;
        return position;
    }

    /**
     * A method to get the position of the square
     *
     * @return squarePosition
     */
    public int[] getSquarePosition() {
        return squarePosition;
    }

    /**
     * A method to get the center coordinate of the square
     *
     * @return centerCoordiate
     */
    public double[] getCenterCoordinate() {
        return centerCoordinate;
    }

    /**
     * A method to set the center coordinate of the square
     *
     * @param centerCoordinate
     */
    public void setCenterCoordinate(double[] centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
    }

    /**
     * A method to get whether the square is allowed or not
     *
     * @return isAllowed
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * A method to set whether the square is allowed or not
     *
     * @param allowed
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    /**
     * A method to get whether the square is an obstacle or not
     *
     * @return isObstacle
     */
    public boolean isObstacle() {
        return obstacle;
    }

    /**
     * A method to declare the square as an obstacle
     *
     * @param obstacle
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * A method to get whether the square is a shooting position or not
     *
     * @return isShootingPosition
     */
    public boolean isShootingPosition() {
        if (shootingPriority == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * A method to set the square's shooting priority (0,3)
     *
     * @param priority
     */
    public void setShootingPriority(int priority) {
        this.shootingPriority = priority;
    }

    /**
     * A method to get the north line coordinate
     *
     * @return northLine
     */
    public double getNorthLine() {
        return northLine;
    }

    /**
     * A method to set the north line coordinate
     *
     * @param northLine
     */
    public void setNorthLine(double northLine) {
        this.northLine = northLine;
    }

    /**
     * A method to get the south line coordinate
     *
     * @return southLine
     */
    public double getSouthLine() {
        return southLine;
    }

    /**
     * A method to set the south line coordinate
     *
     * @param southLine
     */
    public void setSouthLine(double southLine) {
        this.southLine = southLine;
    }

    /**
     * A method to get the easy line coordinate
     *
     * @return eastLine
     */
    public double getEastLine() {
        return eastLine;
    }

    /**
     * A method to set the east line coordinate
     *
     * @param eastLine
     */
    public void setEastLine(double eastLine) {
        this.eastLine = eastLine;
    }

    /**
     * A method to get the west line coordinate
     *
     * @return westLine
     */
    public double getWestLine() {
        return westLine;
    }

    /**
     * A method to set the west line coordinate
     *
     * @param westLine
     */
    public void setWestLine(double westLine) {
        this.westLine = westLine;
    }

}
