package com.cnebrera.uc3.tech.lesson3.driver;


public class Practice3Executable {


    public static void main(final String[] args) throws InterruptedException {
        Publisher publicador= new Publisher("aeron:udp?endpoint=224.0.1.1:40456|interface=192.168.1.0/24",2 );
        Suscriptor suscriptor= new Suscriptor("aeron:udp?endpoint=224.0.1.1:40456|interface=192.168.1.0/24",2 );

        Thread.sleep(2000);

        Thread s=new Thread(()->{

            suscriptor.subscribe();

        });
        Thread p=new Thread(()->{

            publicador.publish();

        });

        p.start();
        s.start();
        p.join();
        s.join();
        Suscriptor.Histograma.outputPercentileDistribution(System.out,1.0);

    }


}
