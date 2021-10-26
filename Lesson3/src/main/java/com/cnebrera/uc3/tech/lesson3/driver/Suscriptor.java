package com.cnebrera.uc3.tech.lesson3.driver;

import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.HdrHistogram.SynchronizedHistogram;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SigInt;
import org.HdrHistogram.Histogram;

public class Suscriptor {
    final int fragmentLimitCount = 7;
    //Creamos canal y stream_ID
    private final int streamId;
    private final String CHANNEL;
    final  Subscription subscription;
    static Histogram Histograma;

    public Suscriptor (String channel, int stream){
        CHANNEL=channel;
        streamId =stream;
        final Aeron.Context ctx = new Aeron.Context();
        final Aeron aeron = Aeron.connect(ctx);
        subscription = aeron.addSubscription(CHANNEL, streamId);
        Histograma= new SynchronizedHistogram(100,100000L,1);
        Histograma.setAutoResize(true);
    }


    public  void subscribe() {

        System.out.println("Subscribing to " + this.CHANNEL + " on stream id " + this.streamId);

        final FragmentHandler fragmentHandler =
                (buffer, offset, length, header) ->
                {
                    final byte[] data = new byte[length];
                    buffer.getBytes(offset, data);
                    String message= new String(data);
                    long startTime= Long.parseLong(message.split("-")[1]);
                    long TotalTime=System.nanoTime()-startTime;
                    /*System.out.println(String.format(
                            "Received message (%s) to stream %d from session %x term id %x term offset %d (%d@%d)",
                            message.split("-")[0], streamId, header.sessionId(),
                            header.termId(), header.termOffset(), length, offset));
                    */
                    Histograma.recordValue(TotalTime);

                };

       new Thread(() -> {
            final IdleStrategy idleStrategy = new BusySpinIdleStrategy();
            while (true)
            {
                final int result = this.subscription.poll(fragmentHandler, fragmentLimitCount);
                idleStrategy.idle(result);
            }
        }).start();

        }


    }



