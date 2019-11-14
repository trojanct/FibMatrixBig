import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;


public class FibMatrixBig {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    static String ResultsFolderPath = "/home/cody/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    static int numberOfTrials = 100;



    public static void main( String[] args)
    {
        //calling full experiment with text file names, running the three experiments as usual for consistency
        runFullExperiment("FibMatrixBig-Exp1-ThrowAway.txt");
        runFullExperiment("FibMatrixBig-Exp2.txt");
        runFullExperiment("FibMatrixBig-Exp3.txt");


        //My testing code used testing the fullest amount in the command bar
        /*int x = 93;
        long result;
        for(int i = 1; i <= x; i ++)
        {
            //System.out.println("using number   "+ i);
            result = fibonacciFunction(i);
            System.out.println(result);
        }*/

    }
    // modified timing code given to us, changed so that input sizes were reduced and went one at a time
    static void runFullExperiment(String resultsFileName){


        BigInteger bigResult;
        long result = 0;
        long inputsize = 1;
        try {

            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {

            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...

        }



        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial



        resultsWriter.println("#InputSize    AverageTime      Fib(x)returned result      x input"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        //uses the x instead of the input
        for(int i = 0 ; i < 93; i++) {

            long batchElapsedTime = 0;


            System.gc();




            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            BatchStopwatch.start(); // comment this line if timing trials individually


            // run the tirals
            for (long trial = 0; trial < numberOfTrials; trial++)
            {

                //call to the fibonaccifuntion gave more descriptive name than fib
                bigResult = fibonacciFunction(i+1);
                result = bigResult.longValue();



            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            // tying to get doubling ratio to work.
            //double doublingRatio = (double) averageTimePerTrialInBatch / (double) prevTimePerTrial;
            // prevTimePerTrial = averageTimePerTrialInBatch;


            /* print data for this size of input average time and fibonacci result*/
            if(i > 0) {
                inputsize = (long) (Math.log(i) / Math.log(2) + 1);
            }
            resultsWriter.printf("%12d  %15.2f  %20d %15d\n",inputsize, averageTimePerTrialInBatch, result,i );
            // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }

    }



    public static BigInteger fibonacciFunction(int x)
    {



        int i;
        int y;
        BigInteger total = new BigInteger("0");
        long result;
        BigInteger m00 = new BigInteger("1");
        BigInteger m01 = new BigInteger("1");
        BigInteger m10 = new BigInteger("1");
        BigInteger m11 = new BigInteger("0");
        // getting rid of the zero right away
        if(x == 1)
        {
            return m11;
        }

        //making the matrix that will be changed the most and the temp matrix so that the original
        //matrix can be used again
        //bit array to keep track of the 8 bit that will be used
        BigInteger matrix[][] = new BigInteger[2][2];
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[1][0] = m10;
        matrix[1][1] = m11;

        BigInteger tempMatrix[][] = new BigInteger[2][2];
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[1][0] = m10;
        matrix[1][1] = m11;


        long powerBitArray[] = {0,0,0,0,0,0,0,0};
        //function call to see what binary number we are working with
        powerBitArray = bitFinder(x,powerBitArray);
        int count = 1;

        //goes through all 8 bits checks to see if there is a one if there is will then add it to the
        //multiplication result
        for( i = 0; i < 8; i ++)
        {
            if(powerBitArray[i] == 1)
            {
                y = powerOfY( i);
                //System.out.println("y equatls "+ y);
                if(count == 1)
                {
                    //needed a base if there is only one so the result won't be zero
                    matrix = multiplyMatrix(y);
                    count ++;
                }
                else {

                    //Used to multiply the amount already recived
                    tempMatrix = multiplyMatrix(y);
                    //System.out.println("results "+ result);
                    matrix = multiplyResuts(matrix, tempMatrix);
                }



            }

        }
        // gets the bottom right of the matrix were the fibonacci number is
        total = matrix[1][1];
        return total;






    }
    // This function is used to fill the bit array so that we know which powers to multiply together
    public static long[] bitFinder(int y, long bits[])
    {
        //128 straight foward because it is the max
        if( y == 128)
        {
            bits[7] = 1;
            //System.out.println("bit placed");
            return bits;
        }
        // most code except the last go down the bit list of 8 to 1 bit to see if the number
        // can be subtracted from the starting number in order to figure out the binary number used
        //also included testing lines to see if it was grabbing the right bits
        if(y <= 127 && y >= 64)
        {
            //System.out.println("bit 6");
            y = y - 64;
            bits[6]= 1;
        }
        if(y <=  63 && y >= 32 )
        {
            //System.out.println("new 5");
            y = y - 32 ;
            bits[5]= 1;
        }
        if(y <= 31 && y >= 16)
        {
            //System.out.println("new 4");
            y = y - 16;
            bits[4]= 1;

        }
        if(y <=  15 && y >= 8)
        {
            //System.out.println("new 3");
            y = y - 8 ;
            bits[3]= 1;
        }
        if(y <= 7 && y >= 4 )
        {
            //System.out.println("new 2");
            y = y - 4;
            bits[2]= 1;
        }
        if(y <= 3 && y >= 2)
        {
            //System.out.println("new 1");
            y = y - 2;
            bits[1]= 1;
        }
        // if one remains than it just gives zero a one because this is the end.
        if (y == 1)
        {
            //System.out.println("new 0");
            bits[0] = 1;
        }

        //returns the cash
        return bits;
    }

    //determines the power of y exponent for any bit that had a one in it
    public static int powerOfY( int y)
    {
        int j;
        int product = 1;

        //simple exponent finding
        for(j = 0; j < y; j++ )
        {
            product = product * 2;
        }
        return product;
    }
    public static BigInteger[][] multiplyMatrix( int y )
    {
        int k;
        //matrix filled with starting numbers and same matrix is as well so te same numbers can be used again.
        BigInteger matrix[][] = new BigInteger[2][2];
        BigInteger m00 = new BigInteger("1");
        BigInteger m01 = new BigInteger("1");
        BigInteger m10 = new BigInteger("1");
        BigInteger m11 = new BigInteger("0");
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[1][0] = m10;
        matrix[1][1] = m11;

        BigInteger samematrix[][] = new BigInteger[2][2];
        BigInteger m002 = new BigInteger("1");
        BigInteger m012 = new BigInteger("1");
        BigInteger m102 = new BigInteger("1");
        BigInteger m112 = new BigInteger("0");
        matrix[0][0] = m002;
        matrix[0][1] = m012;
        matrix[1][0] = m102;
        matrix[1][1] = m112;

        BigInteger matrixInput1, matrixInput2, matrixInput3;

        //filling input holders to be used to fill other part of the matrix because of the pattern of growth
        matrixInput1 = matrix[0][0];
        matrixInput2 = matrix[0][1];
        matrixInput3 = matrix[1][1];

        for(k = 2; k < y+1; k++)
        {
            //multiplys the first matrix and then the inputs that are stored fill the other matrix positions since
            //this part is linear
            //matrix[0][0] = matrix [0][0].multiply(sameMatrix[0][0]).add(sameMatrix[1][0]. matrix[1][0]);



            m00 = (m00.multiply(m002)).add((m102.multiply(m10)));
            m01 = matrixInput1;
            m10 = matrixInput1;
            m11 = matrixInput2;

            //filling the matrix inputs with new data to be put in the right and bottom corners.
            matrixInput1 = m00;
            matrixInput2 = m01;
            matrixInput3 = m11;


        }
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[1][0] = m10;
        matrix[1][1] = m11;

        return matrix;

    }
    //multiplying two of the reults from different exponent powers
    public static BigInteger[][] multiplyResuts (BigInteger matrixOne[][],BigInteger matrixTwo[][] )
    {
        //temp given such as they are temporary holders of the result as not to mess with
        // the multiplication that need the same numbers more than once


        BigInteger a,b,c,d,e,f,g,h;
        a = matrixOne[0][0];
        b = matrixOne[1][0];
        c = matrixOne[1][1];
        d = matrixOne[0][1];
        e = matrixTwo[0][0];
        f = matrixTwo[1][0];
        g = matrixTwo[1][1];
        h = matrixTwo[0][1];



        // Finding the multiplication results between 2 matricies.
        //temp1a= matrixOne[0][0] * matrixTwo[0][0] +matrixOne[0][1]*matrixTwo[1][0];
        //temp2a = matrixOne[1][0] * matrixTwo[0][0] +matrixOne[1][1]*matrixTwo[1][0];
        //temp3a = matrixOne[0][0] * matrixTwo[0][1] +matrixOne[0][1]*matrixTwo[1][1];
        //temp4a = matrixOne[1][0] * matrixTwo[0][1] +matrixOne[1][1]*matrixTwo[1][1];

        BigInteger temp1 = (a.multiply(e)).add((d.multiply(f)));
        BigInteger temp2 = (b.multiply(e)).add((c.multiply(f)));
        BigInteger temp3 = (a.multiply(h)).add((d.multiply(g)));
        BigInteger temp4 = (b.multiply(h)).add((c.multiply(g)));

        //System.out.println(temp1);
        matrixOne[0][0] = temp1;
        matrixOne[1][0] = temp2;
        matrixOne[0][1] = temp3;
        matrixOne[1][1] = temp4;


        return matrixOne;
    }


}