package edu.thepower.u2programacion.multithread;

public class U2P03SleepingThreads implements Runnable {


    @Override
    public void run() {

        String nombreTh = "[" + Thread.currentThread().getName() + "] " ;

        try {
            System.out.println( nombreTh + "Iniciando ejecución");
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println( nombreTh +  " DESPERTANDO 1");
        }

        //Otra forma de dormir al hilo --> Meter en un bucle infinito

        while(!Thread.interrupted()) {

        }

    }

    public static void main(String[] args) {

        String nombreTh = "[" + Thread.currentThread().getName() + "] " ;
        Thread hilo = new Thread( new U2P03SleepingThreads(), "Sleeping Thread");
        hilo.start();



        System.out.println( nombreTh + "Iniciando ejecucion");
        for (int i = 0; i < 2 ; i++) {

            System.out.println( nombreTh + "Sleeping Thread 5 secs");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println( nombreTh + "Despertando al thread durmiente");
            hilo.interrupt();
        }

    }

}
