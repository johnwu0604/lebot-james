package main.util;

import main.Parameters;
import main.object.Square;
import main.resource.FieldConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test the logic in the Field Mapper class
 *
 * @author JohnWu
 */
public class FieldMapperTest {

    FieldMapper fieldMapper;

    @Before
    public void setup() {

        int[] defenderZone = {4,4};
        int[] ballDispenserPosition  = {-1,4};
        // define parameters
        Parameters parameters = new Parameters();
        parameters.setForwardCorner(1);
        parameters.setForwardLine(7);
        parameters.setForwardTeam(11);
        parameters.setDefenderZone(defenderZone);
        parameters.setBallDispenserPosition(ballDispenserPosition);
        parameters.setBallDispenserOrientation("N");
        // create field mapping
        fieldMapper = new FieldMapper(parameters);
    }

    @Test
    public void testIsOffense() {
        // when
        boolean isOffense = fieldMapper.isOffense();
        // then
        Assert.assertTrue( isOffense );
    }

    @Test
    public void testCalculateCenterCoordinate() {
        double[] expectedCenterCoordinate = { -0.5 * FieldConstants.SQUARE_LENGTH, 3.5 * FieldConstants.SQUARE_LENGTH };
        // given
        Square square = new Square( 0, 4 );
        // when
        double[] actualCenterCoordinate = fieldMapper.calculateCenterCoordinate( square );
        // then
        Assert.assertEquals( expectedCenterCoordinate.length, actualCenterCoordinate.length );
        for ( int i = 0; i < actualCenterCoordinate.length; i++ ) {
            Assert.assertEquals( expectedCenterCoordinate[i], actualCenterCoordinate[i], 0 );
        }
    }

    @Test
    public void testIsInGoalRegion() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isInGoalRegion = fieldMapper.isInGoalRegion( square );
        // then
        Assert.assertEquals( false, isInGoalRegion );
    }

    @Test
    public void testIsInOffensRegion() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isInOffenseRegion = fieldMapper.isInOffenseRegion( square );
        // then
        Assert.assertEquals( true, isInOffenseRegion );
    }

    @Test
    public void testIsInDefenseRegion() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isInDefenseRegion = fieldMapper.isInDefenseRegion( square );
        // then
        Assert.assertEquals( true, isInDefenseRegion );
    }

    @Test
    public void testIsSquareAllowed() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isSquareAllowed = fieldMapper.isSquareAllowed( square );
        // then
        Assert.assertEquals( true, isSquareAllowed );
    }

    @Test
    public void testIsIntialObstacle() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isInitialObject = fieldMapper.isSquareAllowed( square );
        // then
        Assert.assertEquals( true, isInitialObject );
    }

    @Test
    public void testIsBallDispenser() {
        // given
        Square square = new Square( 0, 4 );
        // when
        boolean isBallDispenser = fieldMapper.isBallDispenser( square );
        // then
        Assert.assertEquals( true, isBallDispenser );
    }

    @Test
    public void testGetNorthLine() {
        // given
        Square square = new Square( 0, 4 );
        // when
        double northLine = fieldMapper.getNorthLine( square );
        // then
        Assert.assertEquals( 4 * FieldConstants.SQUARE_LENGTH, northLine, 0 );
    }

    @Test
    public void testGetSouthLine() {
        // given
        Square square = new Square( 0, 4 );
        // when
        double southLine = fieldMapper.getSouthLine( square );
        // then
        Assert.assertEquals( 3 * FieldConstants.SQUARE_LENGTH, southLine, 0 );
    }

    @Test
    public void testGetEastLine() {
        // given
        Square square = new Square( 0, 4 );
        // when
        double eastLine = fieldMapper.getEastLine( square );
        // then
        Assert.assertEquals( 0, eastLine, 0 );
    }

    @Test
    public void testGetWestLine() {
        // given
        Square square = new Square( 0, 4 );
        // when
        double westLine = fieldMapper.getWestLine( square );
        // then
        Assert.assertEquals( -1 * FieldConstants.SQUARE_LENGTH, westLine, 0 );
    }


}
