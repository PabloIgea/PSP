package edu.thepower.u2programacion.multithread;

public class U2P04CondicionDeCarreraMonitores {

    private static int contador = 0;

    public static synchronized void incrementarContador(int num) {
        contador += num;
    }

    public static synchronized int getContador() {
        return contador;
    }

    public static void main(String[] args) {/*

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

        System.out.println("El valor final de contador es: " + getContador());*/

    }

}


