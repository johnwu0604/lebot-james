import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by JohnWu on 2017-02-20.
 */
public class ExcelToLatex {

    public static void main(String[] args) throws Exception {
        String[][] array = convertCSVToMatrix( "resource/sample.csv" );
        System.out.print(array);

        convertToLatex( array );
    }


    public static String[][] convertCSVToMatrix(String filePath) throws Exception {
        // Read all
        CSVReader csvReader = new CSVReader(new FileReader(new File(filePath)));
        List<String[]> list = csvReader.readAll();

        // Convert to 2D array
        String[][] dataArr = new String[list.size()][];
        dataArr = list.toArray(dataArr);

        return dataArr;
    }

    public static void convertToLatex( String[][] table ) throws Exception {
        PrintWriter writer = new PrintWriter("resource/result.txt", "UTF-8");

        writer.println( "\\begin{center}" );

        writer.print( "   \\begin{tabular}{|" );
        for( int i=0; i<table[0].length; i++ ) {
            writer.print(" c ");
        }
        writer.println( "|}" );


        writer.println( "           \\hline" );

        writer.print( "           ");
        for( int i=0; i<table[0].length; i++ ) {
            if( i==table[i].length -1 ) {
                writer.print( table[0][i] );
            } else {
                writer.print( table[0][i] + " & ");
            }
        }
        writer.println(" \\\\ [0.5ex] \\hline");

        for( int i=1; i<table.length; i++ ) {
            writer.print( "           " );
            for( int j=0; j<table[i].length; j++ ) {
                if( j==table[i].length -1 ) {
                    writer.print( table[i][j] );
                } else {
                    writer.print( table[i][j] + " & ");
                }
            }
            writer.println(" \\\\");
            writer.println( "           \\hline" );
        }

        writer.println( "           \\hline" );


        writer.println( "   \\end{tabular}" );
        writer.println( "\\end{center}" );

        writer.println( "" );
        writer.println( "" );
        writer.println( "" );

        // data analysis
        writer.println( "\\begin{itemize}" );
        writer.println( "" );
        writer.println( "   \\item Mean: " );
        writer.println( "" );
        writer.println( "   $\\mu$ =  $\\frac{1}{N}\\sum_{i=1}^{N} \\Delta X$ ;\\\\ \\\\" );
        writer.println( "   Where: \\\\" );
        writer.println( "   N: number of trials\\\\" );
        writer.println( "   X: data sample \\\\" );
        writer.println( "" );
        writer.println( "       \\begin{itemize}" );
        writer.println( "" );

        for( int i=1; i<table[0].length; i++ ) {
            writer.print( "         \\item ");
            writer.print(table[0][i]);
            writer.print(": $\\mu$ = ");
            writer.println( findAverage(table, i) );
        }

        writer.println( "" );
        writer.println( "       \\end{itemize}" );
        writer.println( "" );


        writer.println( "   \\item Standard Deviation: " );
        writer.println( "       $\\sigma$ =  $\\sqrt{\\frac{1}{N}\\sum_{i=1}^{N} (x_i-\\mu)^{2}}$ ;\\\\ \\\\" );
        writer.println( "       Where:\\\\" );
        writer.println( "       N: number of trials\\\\" );
        writer.println( "       $x_i$: data sample \\\\" );
        writer.println( "       $\\mu$: the mean \\\\" );
        writer.println( "" );

        writer.println( "       \\begin{itemize}" );
        writer.println( "" );

        for( int i=1; i<table[0].length; i++ ) {
            writer.print( "         \\item ");
            writer.print(table[0][i]);
            writer.print(": $\\sigma$ = ");
            writer.println( findDeviation(table, i, findAverage(table,i)) );
        }

        writer.println( "       \\end{itemize}" );
        writer.println( "\\end{itemize}" );






        writer.close();
    }

    public static double findAverage( String[][] matrix, int column ) {
        double sum = 0;
        for ( int i=1; i<matrix.length; i++ ) {
            sum += Double.parseDouble( matrix[i][column] );
        }
        return sum/( matrix.length-1 );
    }

    public static double findDeviation( String[][] matrix, int column, double mean ) {
        double sum = 0;
        for ( int i=1; i<matrix.length; i++ ) {
            sum += Math.pow(Double.parseDouble( matrix[i][column] ) - mean, 2);
        }
        return Math.sqrt( sum/(matrix.length-1) );
    }

}
