package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulRndPark;
import org.HdrHistogram.Histogram;
import java.util.concurrent.TimeUnit;
import org.HdrHistogram.*;


/**
 * First practice, measure latency on a simple operation
 */
public class PracticeLatency1
{
    /**
     * Main method to run the practice
     * @param args command line arument
     */
    public static void main(String [] args)
    {
        runCalculations();

    }

    /**
     * Run the practice calculations
     */
    private static void runCalculations()
    {
        // Create a random park time simulator
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulRndPark(TimeUnit.NANOSECONDS.toNanos(100), TimeUnit.MICROSECONDS.toNanos(100));

        Histogram histogram_1= new SynchronizedHistogram(100,100000L,1);
        histogram_1.setAutoResize(true);
        // Execute the operation lot of times
        for(int i = 0; i < 100000; i++)
        {
            long startTime=System.nanoTime();
            syncOpSimulator.executeOp();
            long endTime=System.nanoTime()-startTime;
            //Para convertir a micros, dividimos entre 1000
            histogram_1.recordValue(endTime);
        }
        histogram_1.outputPercentileDistribution(System.out,1000.0);

        // TODO Show the percentile distribution of the latency calculation of each executeOp call
    }
}
