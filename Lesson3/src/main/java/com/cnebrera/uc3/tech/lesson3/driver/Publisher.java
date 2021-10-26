package com.cnebrera.uc3.tech.lesson3.driver;

import io.aeron.Publication;
import io.aeron.Aeron;
import org.HdrHistogram.Histogram;
import org.agrona.BufferUtil;
import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;

import java.util.concurrent.TimeUnit;


public class Publisher
{

    // Creamos el canal, habrá que cambiarlo cuando sea TCP o IPC.

    private static String CHANNEL;
    private static  int STREAM_ID;
    final Publication publication;
    final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(512, BitUtil.CACHE_LINE_LENGTH));

    // Ejecución.
    public Publisher(String channel , int stream) {
        CHANNEL = channel;
        STREAM_ID = stream;
        final Aeron.Context ctx = new Aeron.Context();
        final Aeron aeron = Aeron.connect(ctx);
        publication= aeron.addPublication(CHANNEL, STREAM_ID);

    }
        public void publish () {

            // Mensaje de publicacion inicial
           System.out.println("Publishing to " + CHANNEL + " on stream id " + STREAM_ID);

            // Enviando mensajes. Guardamos en la cadena el tiempo del envio que pasaremos al suscriptor
            /*final String message = "Hello World!-"+System.nanoTime();
            final byte[] messageBytes = message.getBytes();
            buffer.putBytes(0, messageBytes);*/
            final long WaitTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (!publication.isConnected())
            {
                if ((WaitTime - System.nanoTime()) < 0)
                {
                    System.out.println("There is not any suscriptor connected");
                    return;
                }
            }
            final long expectedTimeBetweenCalls = TimeUnit.SECONDS.toNanos(1) / 1000;
            long nextCallTime = System.nanoTime();

            for(int i = 0; i < 5000; i++){
                long time= System.nanoTime();
                while(time < nextCallTime){
                    time=System.nanoTime();
                };

                //Create new variable delay
                long startTime=System.nanoTime();
                long delay=startTime-nextCallTime;
                final String message = "Hello World!-"+(startTime+delay);
                final byte[] messageBytes = message.getBytes();
                buffer.putBytes(0, messageBytes);
                final long result = publication.offer(buffer, 0, messageBytes.length);

                nextCallTime+=expectedTimeBetweenCalls;

                // Analizamos resultado.

                if (result < 0L) {
                    if (result == Publication.BACK_PRESSURED) {
                        System.out.println("Offer failed due to back pressure");
                    } else if (result == Publication.NOT_CONNECTED) {
                        System.out.println("Offer failed because publisher is not yet connected to subscriber");
                    } else if (result == Publication.ADMIN_ACTION) {
                        System.out.println("Offer failed because of an administration action in the system");
                    } else if (result == Publication.CLOSED) {
                        System.out.println("Offer failed publication is closed");

                    } else {
                        System.out.println("Offer failed due to unknown reason");
                    }
                }
            }

            }
        }






