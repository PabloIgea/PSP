package edu.thepower.u2programacion.multithread;


class ThreadImplemented implements Runnable {

    @Override
    public void run() {

        //Declaracion de thread mediante implementacion de interface Runnable
        System.out.println( "El nombre del hilo es: " + Thread.currentThread().getName());
        System.out.println( "El id del hilo es: " + Thread.currentThread().threadId());
    }
}


//Declaracion de thread mediante herencia de la clase padre "Thread"
public class U2P01CreacionThread extends Thread {

    //Este es el codigo qye se ejecuta cuando lanzo el thread
    @Override
    public void run(){
        System.out.println( "El nombre del hilo es: " + Thread.currentThread().getName());
        System.out.println( "El id del hilo es: " + Thread.currentThread().threadId());
    }

    public static void main (String[] args){

        //Instanciacion de Thread mediante expresion lambda
        Thread t1 = new Thread(() -> {
            System.out.println( "El nombre del hilo es: " + Thread.currentThread().getName());
            System.out.println( "El id del hilo es: " + Thread.currentThread().threadId());
        } , "Thread Lambda ");

        //Instanciacion de Thread mediante herencia
        Thread t2 = new Thread(new U2P01CreacionThread(), "Thread Heredero" );


        for (int i = 0; i <= 5; i++) {

            Thread t3 = new Thread(new ThreadImplemented(), "Threat implementado");
            t3.start();
        }

        //Iniciar los Threads que se ejecutan en paralelo
        t1.start();
        t2.start();

    }

}
