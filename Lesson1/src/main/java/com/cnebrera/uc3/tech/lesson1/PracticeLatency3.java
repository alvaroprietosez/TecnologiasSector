package com.cnebrera.uc3.tech.lesson1;

import com.cnebrera.uc3.tech.lesson1.simulator.BaseSyncOpSimulator;
import com.cnebrera.uc3.tech.lesson1.simulator.SyncOpSimulSleep;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.HdrHistogram.*;


/**
 * Third practice, measure accumulated latency with multiple threads
 */
public class PracticeLatency3
{
    /** Number of consumer threads to run */
    final static int NUM_THREADS = 2;
    /** Number of executions per thread */
    final static int NUM_EXECUTIONS = 100;
    /** Expected max executions per second */
    final static int MAX_EXPECTED_EXECUTIONS_PER_SECOND = 50;
    // Create histogram3=non accumulated histogram_3_accu=accumulated
    public static Histogram histogram_3=new SynchronizedHistogram(100,100000,1);
    public static Histogram histogram_3_accu= new SynchronizedHistogram(100,100000,1);
    /**
     * Main method to run the practice
     * @param args command line arument
     */
    public static void main(String [] args)
    {
        // Histograma sin latencias acumuladas
        histogram_3.setAutoResize(true);
        histogram_3_accu.setAutoResize(true);
        runCalculations();

    }

    /**
     * Run the practice calculations
     */
    private static void runCalculations()
    {
        // Create a sleep time simulator, it will sleep for 10 milliseconds in each call
        BaseSyncOpSimulator syncOpSimulator = new SyncOpSimulSleep(10);

        // List of threads
        List<Runner> runners = new LinkedList<>();

        // Create the threads and start them
        for (int i = 0; i < NUM_THREADS; i ++)
        {
            final Runner runner = new Runner(syncOpSimulator);
            runners.add(runner);
            new Thread(runner).start();
        }
        //Runners run
        //runners.forEach(Runner::run);

        // Wait for runners to finish
        runners.forEach(Runner::waitToFinish);

        // TODO Show the percentile distribution of the latency calculation of each executeOp call for all threads
        histogram_3.outputPercentileDistribution(System.out,1.0);
        histogram_3_accu.outputPercentileDistribution(System.out,1.0);
    }

    /**
     * The runner that represent a thread execution
     */
    private static class Runner implements Runnable
    {
        /** The shared operation simulator */
        final BaseSyncOpSimulator syncOpSimulator;

        /** True if finished */
        volatile boolean finished = false;

        /**
         * Create a new runner
         *
         * @param syncOpSimulator shared operation simulator
         */
        private Runner(BaseSyncOpSimulator syncOpSimulator)
        {
            this.syncOpSimulator = syncOpSimulator;
        }

        @Override
        public void run()
        {
            // Calculate the expected time between consecutive calls, considering the number of executions per second
            final long expectedTimeBetweenCalls = TimeUnit.SECONDS.toMillis(1) / MAX_EXPECTED_EXECUTIONS_PER_SECOND;

            // Calculate the next call time, the first time should be immediate
            long nextCallTime = System.currentTimeMillis();

            // Execute the operation the required number of times//Practica 3
            for(int i = 0; i < NUM_EXECUTIONS; i++)
            {
                // Wait until there is the time for the next call
                while(System.currentTimeMillis() < nextCallTime);

                // Execute the operation, it will sleep for 10 milliseconds
                long startTime=System.currentTimeMillis();
                //Create new variable, delay
                long delay=startTime-nextCallTime;
                syncOpSimulator.executeOp();
                long TotalTime=System.currentTimeMillis()-startTime;
                histogram_3.recordValue(TotalTime);
                histogram_3_accu.recordValue(TotalTime+delay);

                // Calculate the next time to call execute op
                nextCallTime += expectedTimeBetweenCalls;
            }

            finished = true;
        }

        /** Wait for the runner execution to complete */
        public void waitToFinish()
        {
            while(!this.finished)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
