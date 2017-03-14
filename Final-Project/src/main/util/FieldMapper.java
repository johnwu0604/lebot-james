package main.util;

import main.Parameters;
import main.object.Square;
import main.resource.Constants;

/**
 * A utility class that creates a mapping of our field based on its parameters
 */
public class FieldMapper {

    // properties
    private Square[][] squares;
    private Parameters parameters;

    /**
     * Main intialization method
     *
     * @param parameters
     */
    public FieldMapper( Parameters parameters ) {
        this.parameters = parameters;
        if ( parameters.getForwardTeam() == 11 ) {
            mapOffenseBoundaries();
        } else {
            mapDefenseBoundaries();
        }

    }

    /**
     * A method that maps offense boundaries
     */
    public void mapOffenseBoundaries() {
        for ( int i = 0; i < 12; i++ ) {
            for (int k = 0; k < 12; k++) {
                Square square = new Square();
                if ( !isInDefenseRegion( i, k ) && !isGoal( i, k ) ) {
                    square.setAllowed(true);
                } else {
                    square.setAllowed(false);
                }
                square.setX(k);
                square.setY(i);
                square.setNorthPosition( i * Constants.SQUARE_LENGTH );
                square.setSouthPosition( ( i - 1 ) * Constants.SQUARE_LENGTH );
                square.setEastPosition( k * Constants.SQUARE_LENGTH );
                square.setWestPosition( ( k - 1 ) * Constants.SQUARE_LENGTH );
                squares[i][k] = square;
            }
        }
    }

    /**
     * A method that maps defense boundaries
     */
    public void mapDefenseBoundaries() {
        for ( int i = 0; i < 12; i++ ) {
            for (int k = 0; k < 12; k++) {
                Square square = new Square();
                if ( !isInOffenseRegion( i, k ) && !isInGoalRegion( i, k ) && !isGoal( i, k ) ) {
                    square.setAllowed(true);
                } else {
                    square.setAllowed(false);
                }
                square.setNorthPosition( i * Constants.SQUARE_LENGTH );
                square.setSouthPosition( ( i - 1 ) * Constants.SQUARE_LENGTH );
                square.setEastPosition( k * Constants.SQUARE_LENGTH );
                square.setWestPosition( ( k - 1 ) * Constants.SQUARE_LENGTH );
                squares[i][k] = square;
            }
        }
    }

    /**
     * A method that determines if a square is part of the goal region
     *
     * @param i
     * @param k
     * @return
     */
    public boolean isInGoalRegion( int i, int k ) {
        int left = ( 12 - parameters.getDefenderZone()[0] ) / 2 ;
        int right = left * 2;
        int up = 11;
        int down = 11 - parameters.getDefenderZone()[1];

        if ( i >= down && i < up && k >= left && k < right ) {
            return false;
        }
        return true;
    }

    /**
     * A method that determines if a square is part of the offense region
     *
     * @param i
     * @param k
     * @return
     */
    public boolean isInOffenseRegion( int i, int k ) {
        int left = 2 ;
        int right = 10;
        int up = 11 - parameters.getForwardLine();
        int down = 1;
        if ( i >= down && i < up && k >= left && k < right ) {
            return true;
        }
        return false;
    }

    /**
     * A method that determines if a square is part of the defense region
     *
     * @param i
     * @param k
     * @return
     */
    public boolean isInDefenseRegion( int i, int k ) {
        int left = 2 ;
        int right = 10;
        int up = 11;
        int down = 11 - parameters.getForwardLine();

        if ( i >= down && i < up && k >= left && k < right ) {
            return true;
        }
        return false;
    }

    /**
     * A method that determines if a square is part of the goal
     *
     * @param i
     * @param k
     * @return
     */
    public boolean isGoal( int i, int k ) {
        if ( i == 11 && ( k == 5 || k == 6 ) ) {
            return true;
        }
        return false;
    }

    /**
     * A method to retrieve the mapping
     *
     * @return
     */
    public Square[][] getMapping() {
        return squares;
    }

}
