package edu.thepower.u2programacion.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class U2P04CondicionDeCarreraSemaforos  implements Runnable {

    private static long tiempoPrueba = System.currentTimeMillis() + 100;
    private static Semaphore serafin = new Semaphore(5, true);
    private static AtomicInteger contador = new AtomicInteger();
    //Concurrent HashMapo es una clase ThreadSafe que garantiza la exclusion mutua
    private static Map<String, Integer> mapa = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        List<Thread> lista = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            lista.add(new Thread(new U2P04CondicionDeCarreraSemaforos(), "Thread_ " + i));
            lista.get(i).start();
        }

        for(Thread h : lista) {

            try {
                h.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        System.out.println("***Uso del Serafín por los Threads***");

        for(String n : mapa.keySet()) {
            System.out.println("El thread " + n + " ha usado el semáforo: " + mapa.get(n) + "veces.");
        }

    }

    @Override
    public void run() {

        String nombre = "[" + Thread.currentThread() + "]";

        while (System.currentTimeMillis() < tiempoPrueba) {

            try {
                serafin.acquire();
                mapa.put(nombre,mapa.getOrDefault(nombre, 0) + 1);
                System.out.println(nombre + "Valor inserado en el mapa: " + mapa.get(nombre));
                System.out.println("Adquirido semáforo número: " + contador.incrementAndGet());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (contador.get() > 5)
                throw new RuntimeException("Semáforo sobrepasado");

            contador.decrementAndGet();
            serafin.release();
            System.out.println( nombre + "Semáforo liberado");
        }
    }
}


