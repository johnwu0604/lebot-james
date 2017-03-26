package main.util;

import main.Parameters;
import main.object.Square;
import main.resource.*;
import main.wifi.WifiProperties;

import java.util.*;

/**
 * A utility class that creates a mapping of our field based on its parameters
 *
 * @author JohnWu
 */
public class FieldMapper {

    // properties
    private Square[][] squares;
    private Square[] ballDispenserApproach;
    private Parameters parameters;

    /**
     * Main intialization method
     *
     * @param parameters the given to the robot
     */
    public FieldMapper( Parameters parameters ) {
        this.parameters = parameters;
        this.squares = new Square[12][12];
        mapField();
    }

    /**
     * A method to map our field
     */
    public void mapField(){
        for ( int x = 0; x < 12; x++ ) {
            for (int y = 0; y < 12; y++) {
                Square square = new Square( x, y );
                square.setCenterCoordinate( calculateCenterCoordinate( square ) );
                square.setAllowed( isSquareAllowed( square ) );
                square.setObstacle( isInitialObstacle( square ) );
                square.setShootingPosition( isShootingPosition( square ) );
                square.setNorthLine( getNorthLine( square ) );
                square.setSouthLine( getSouthLine( square ) );
                square.setEastLine( getEastLine( square ) );
                square.setWestLine( getWestLine( square ) );
                squares[x][y] = square;
            }
        }
        ballDispenserApproach = calculateBallDispenserApproach();
    }


    /**
     * A method that returns a boolean indicating whether the robot is playing offense or not
     *
     * @return whether robot is playing offense
     */
    public boolean isOffense() {
        return parameters.getForwardTeam() == WifiProperties.TEAM_NUMBER ? true : false;
    }


    /**
     * A method to calculate the center coordinate of a square
     *
     * @param square
     * @return position vector
     */
    public double[] calculateCenterCoordinate( Square square ) {
        double[] center = new double[2];
        center[0] = ( square.getSquarePosition()[0] - 0.5 ) * FieldConstants.SQUARE_LENGTH;
        center[1] = ( square.getSquarePosition()[1] - 0.5 ) * FieldConstants.SQUARE_LENGTH;
        return center;
    }

    /**
     * A method that determines if a square is part of the goal region
     *
     * @param square
     * @return whether square is in goal region
     */
    public boolean isInGoalRegion( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        // goal region boundaries are not inclusive
        int leftBoundary = ( ( 12 - parameters.getDefenderZone()[0] ) / 2 ) - 1;
        int rightBoundary = 11 - leftBoundary;
        int topBoundary = 11;
        int bottomBoundary = 10 - parameters.getDefenderZone()[1];
        // if they fall in the above boundaries, it is in the goal region
        if ( x > leftBoundary && x < rightBoundary && y < topBoundary && y > bottomBoundary ) {
            return true;
        }
        return false;
    }

    /**
     * A method that determines if a square is part of the offense region
     *
     * @param square
     * @return whether square is in offense region
     */
    public boolean isInOffenseRegion( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        // following defense area boundaries are inclusive
        int leftBoundary = 2 ;
        int rightBoundary = 9;
        int topBoundary = 10;
        int bottomBoundary = 11 - parameters.getForwardLine();
        // if they do not fall in the above boundaries, it is the offense region
        if ( x < leftBoundary || x > rightBoundary || y > topBoundary || y < bottomBoundary ) {
            return true;
        }
        return false;
    }

    /**
     * A method that determines if a square is part of the defense region
     *
     * @param square
     * @return whether square is in defense
     */
    public boolean isInDefenseRegion( Square square ) {
        if ( !isInGoalRegion( square ) && !isInOffenseRegion( square ) ) {
            return true;
        }
        return false;
    }

    /**
     * A method to determine if a square is allowed based on boundaries
     *
     * @param square
     * @return whether the square is allowed
     */
    public boolean isSquareAllowed( Square square ) {
        if ( isOffense() ) {
            return isInOffenseRegion( square ) ? true : false;
        } else {
            return isInDefenseRegion( square ) ? true : false;
        }
    }

    /**
     * A method to determine if a square is an obstacle based on initial parameters
     *
     * @param square
     * @return whether the square contains an obstacle
     */
    public boolean isInitialObstacle( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        // if the square is in the location of the goal, consider it an obstacle
        if ( y == 11 && ( x == 5 || x == 6 ) ) {
            return true;
        }
        // if the square is in the location of the ball dispenser, consider it an obstacle
        if ( isBallDispenser( square ) ) {
            return true;
        }

        return false;
    }

    /**
     * A method to determine whether the ball dispenser is located on the square
     *
     * @param square
     * @return whether the ball dispenser is located on the square
     */
    public boolean isBallDispenser( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        int ballDispenserX = parameters.getBallDispenserPosition()[0];
        int ballDispenserY = parameters.getBallDispenserPosition()[1];
        // dispenser lies on east/west walls taking up two squares in y direction
        if ( ballDispenserX == -1 ) {
            if ( x == 0 && ( y == ballDispenserY || y == ballDispenserY + 1 ) ) {
                return true;
            }
        }
        else if ( ballDispenserX == 11 ) {
            if ( x == 11 && ( y == ballDispenserY || y == ballDispenserY + 1 ) ) {
                return true;
            }
        }
        // dispenser lies on north/south walls taking up two squares in x direction
        else if ( ballDispenserY == -1 ) {
            if ( y == 0 && ( x == ballDispenserX || x == ballDispenserX + 1 ) ) {
                return true;
            }
        }
        else if ( ballDispenserY == 11 ) {
            if ( y == 11 && ( x == ballDispenserX || x == ballDispenserX + 1 ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method to determine where or not the current square is one of our defined shooting positions
     *
     * @param square
     * @return whether the square is a shooting position
     */
    public boolean isShootingPosition( Square square ) {
        // TODO: After we figure out where out shooting positions are
        return false;
    }

    /**
     * A method to determine the north line coordinate of the square
     *
     * @param square
     * @return north line coordinate
     */
    public double getNorthLine( Square square ) {
        int y = square.getSquarePosition()[1];
        return y * FieldConstants.SQUARE_LENGTH;
    }

    /**
     * A method to determine the south line coordinate of the square
     *
     * @param square
     * @return south line coordinate
     */
    public double getSouthLine( Square square ) {
        int y = square.getSquarePosition()[1];
        return ( y - 1 ) * FieldConstants.SQUARE_LENGTH;
    }

    /**
     * A method to determine the east line coordinate of the square
     *
     * @param square
     * @return east line coordinate
     */
    public double getEastLine( Square square ) {
        int x = square.getSquarePosition()[0];
        return x * FieldConstants.SQUARE_LENGTH;
    }

    /**
     * A method to determine the west line coordinate of the square
     *
     * @param square
     * @return west line coordinate
     */
    public double getWestLine( Square square ) {
        int x = square.getSquarePosition()[0];
        return ( x - 1 ) * FieldConstants.SQUARE_LENGTH;
    }

    /**
     * A method that calculates our ball dispenser approach area based on parameters
     *
     * @return
     */
    public Square[] calculateBallDispenserApproach() {
        int ballDispenserX = parameters.getBallDispenserPosition()[0];
        int ballDispenserY = parameters.getBallDispenserPosition()[1];
        Square[] ballDispenserApproach = new Square[2];
        // dispenser lies on east/west walls taking up two squares in y direction
        if ( ballDispenserX == -1 || ballDispenserX == 11 ) {
            if ( ballDispenserX == -1 ) {
                ballDispenserApproach[0] = squares[1][ ballDispenserY ];
                ballDispenserApproach[1] = squares[1][ ballDispenserY + 1 ];
               // ballDispenserApproach[2] = squares[2][ ballDispenserY ];
               // ballDispenserApproach[3] = squares[2][ ballDispenserY + 1 ];
            }
            if ( ballDispenserX == 11 ) {
                ballDispenserApproach[0] = squares[10][ ballDispenserY ];
                ballDispenserApproach[1] = squares[10][ ballDispenserY + 1 ];
                //ballDispenserApproach[2] = squares[9][ ballDispenserY ];
                //ballDispenserApproach[3] = squares[9][ ballDispenserY + 1 ];
            }
        }
        // dispenser lies on north/south walls taking up two squares in x direction
        if ( ballDispenserY == -1 || ballDispenserY == 11 ) {
            if ( ballDispenserY == -1 ) {
                ballDispenserApproach[0] = squares[ ballDispenserX ][ 1 ];
                ballDispenserApproach[1] = squares[ ballDispenserX + 1 ][ 1];
                //ballDispenserApproach[2] = squares[ ballDispenserX ][ 2 ];
                //ballDispenserApproach[3] = squares[ ballDispenserX + 1 ][ 2 ];
            }
            if ( ballDispenserY == 11 ) {
                ballDispenserApproach[0] = squares[ ballDispenserX ][ 10 ];
                ballDispenserApproach[1] = squares[ ballDispenserX + 1 ][ 10 ];
                //ballDispenserApproach[2] = squares[ ballDispenserX ][ 9 ];
                //ballDispenserApproach[3] = squares[ ballDispenserX + 1 ][ 9 ];
            }
        }
        return ballDispenserApproach;
    }

    /**
     * A method to return out ball dispenser approach
     *
     * @return
     */
    public Square getBallDispenserApproach(int approachDirection) {
        if (approachDirection <= 0) {
            return squares[ballDispenserApproach[0].getSquarePosition()[0]][ballDispenserApproach[0].getSquarePosition()[1]];
        } else {
            return squares[ballDispenserApproach[1].getSquarePosition()[0]][ballDispenserApproach[1].getSquarePosition()[1]];
        }
    }

    /**
     * A method that returns the mapping for the field
     *
     * @return
     */
    public Square[][] getMapping() {
        return squares;
    }

    /**
     * A method that returns the parameters for the field
     *
     * @return
     */
    public Parameters getParameters() {return parameters;}

    /**
     * A method to get the square that the coordinate belongs too
     *
     * @return square of a coordinate
     */
    public Square getSquareOfCoordinate( double x, double y ) {
        for ( int i = 0; i < 12; i ++ ) {
            for ( int j = 0; j < 12; j++ ) {
                if ( isCoordinateInSquare( x, y, squares[i][j] ) ) {
                    return squares[i][j];
                }
            }
        }
        return null; // the square is out of bounds or too close to a boundary
    }

    /**
     * A method to determine if two coordinates are in the square
     *
     * @param x
     * @param y
     * @param square
     * @return whether the coordinate is in the square
     */
    public boolean isCoordinateInSquare( double x, double y, Square square ) {
        double leftBoundary = square.getWestLine() + ThresholdConstants.COORDINATE_IN_SQUARE;
        double rightBoundary = square.getEastLine() - ThresholdConstants.COORDINATE_IN_SQUARE;
        double upperBoundary = square.getNorthLine() - ThresholdConstants.COORDINATE_IN_SQUARE;
        double lowerBoundary = square.getSouthLine() + ThresholdConstants.COORDINATE_IN_SQUARE;
        if ( x > leftBoundary && x < rightBoundary && y < upperBoundary && y > lowerBoundary ) {
            return true;
        }
        return false;
    }

    /**
     * Determines whether a square is one of the squares along the wall or not
     *
     * @param square
     * @return whether it is an edge square
     */
    public boolean isEdgeSquare( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        if ( x == 0 || x == 11 || y == 0 || y == 11 ) {
            return true;
        } else {
            return false;
        }
    }


}
