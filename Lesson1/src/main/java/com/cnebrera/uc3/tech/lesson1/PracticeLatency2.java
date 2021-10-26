package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulLongOperation;
import org.HdrHistogram.*;
/**
 * Second practice, measure latency with and without warmup
 */
public class PracticeLatency2
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
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulLongOperation();
        Histogram histogram_2= new Histogram(100,10000L,1);
        histogram_2.setAutoResize(true);
        // Execute the operation lot of times
        for(int i = 0; i < 200000; i++)
        {
            //Este primer bucle mostrará la ejecución en frío del sistema
            long startTime=System.nanoTime();
            syncOpSimulator.executeOp();
            long endTime=System.nanoTime()-startTime;
            histogram_2.recordValue(endTime);
        }
        histogram_2.outputPercentileDistribution(System.out,1.0);
        histogram_2.reset(); // Para evitar instanciar varias veces fuera de la funcion, hacemos este reset

        for(int i = 0; i < 200000; i++)
        {
            //Este bucle solo servirá para "calentar" la ejecución repitiendo la operación sin guaradarla.
            syncOpSimulator.executeOp();
        }
        for(int i = 0; i < 200000; i++)
        {
            long startTime=System.nanoTime();
            syncOpSimulator.executeOp();
            long endTime=System.nanoTime()-startTime;
            histogram_2.recordValue(endTime);
        }
        histogram_2.outputPercentileDistribution(System.out,1.0);
        // TODO Show min, max, mean and percentiles 99 and 99,9 with and without warm up
    }
}
