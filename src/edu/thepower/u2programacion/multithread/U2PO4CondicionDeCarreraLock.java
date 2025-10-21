package edu.thepower.u2programacion.multithread;

import java.util.concurrent.locks.ReentrantLock;

public class U2PO4CondicionDeCarreraLock {

    private static int contador = 0;


    //Los objetos de tipo Reentrant Lock permiten bloquear un trozo de codigo hasta que se ejecute
    //El bloqueo se hace con lock y el desbloqueo con unlock y nos garantiza exclusión mutua
    private static ReentrantLock candado = new ReentrantLock();

    public static void incrementarContador(int num) {


        System.out.println("Entrando en incrementarContador");

        candado.lock();

        try {
            contador += num;
        } finally {
            candado.unlock();
        }

        System.out.println("Saliendo de incrementarContador");
    }

    public static int getContador() {

        System.out.println("Entrando en getContador");

        System.out.println("Saliendo de getContador");
        return contador;
    }

    public static void main(String[] args) {

        final int ITERACIONES = 1_000_000;
        final int VALOR = 10;

        Thread incrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion incrementador");

            for (int i = 0; i < ITERACIONES; i++) {

                incrementarContador(VALOR);
            }
            System.out.println("Acabando ejecucion incrementador");
        });

        Thread decrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion decrementador");

            for (int i = 0; i < ITERACIONES; i++) {

                incrementarContador(-VALOR);
            }
            System.out.println("Acabando ejecucion decrementador");
        });

        incrementator.start();
        decrementator.start();

        try {
            incrementator.join();
            decrementator.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("El valor final de contador es: " + getContador());

    }
}
