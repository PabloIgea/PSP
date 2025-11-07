package edu.thepower.u2programacion.multithread;

// ----------------------------------------------------------------------
// PROGRAMA: Ejemplo de uso de Thread Pool con ExecutorService en Java
// ----------------------------------------------------------------------
// Este programa crea un pool de 10 hilos fijos para ejecutar 50 tareas/concurrencias,
// usando ExecutorService y ConcurrentHashMap. Para cada hilo que ejecuta tareas,
// lleva la cuenta de cuántas veces ha trabajado y muestra los resultados al final.
// ----------------------------------------------------------------------

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class U2P07ThreadsPool {

    // ---------------------------------------------------------------
    // Constante global: Número máximo de hilos en el pool
    // ---------------------------------------------------------------
    // MAX_POOL_SIZE limita el número de hilos concurrentes disponibles.
    // Esto evita la sobrecarga del sistema y simula un entorno controlado.
    // Es de tipo int, estático (de clase) y final (constante).
    final static int MAX_POOL_SIZE = 10;

    public static void main(String[] args) {

        // -----------------------------------------------------------
        // 1. Inicialización del pool de hilos y el almacenamiento concurrente:
        // -----------------------------------------------------------

        // 'pool' es nuestro pool fijo de hilos, gestionado automáticamente:
        // Permite repartir las tareas entre 10 hilos trabajando simultáneamente.
        ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

        // 'poolsMaps' es un mapa concurrente (hilo seguro) que asocia el nombre
        // de cada hilo (clave String) con un contador atómico (AtomicInteger)
        // de las tareas que ha ejecutado dicho hilo. Sirve para monitorizar el
        // reparto de tareas. Es de ámbito local al main.
        Map<String, AtomicInteger> poolsMaps = new ConcurrentHashMap<>();

        // -----------------------------------------------------------
        // 2. Envío de 50 tareas al pool de hilos:
        // -----------------------------------------------------------
        // Cada tarea será atendida por el siguiente hilo libre,
        // y se contabilizan las veces que cada hilo participa.
        for (int i = 0; i < 50; i++) {
            pool.submit(() -> {
                // La siguiente línea incrementa (de forma atómica y segura)
                // el contador correspondiente al hilo ejecutor.
                // Si el hilo nunca había trabajado, inicializa su contador en 0.
                poolsMaps
                        .computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger())
                        .incrementAndGet();

                // Mensaje de salida indicando qué hilo está ejecutando la tarea.
                System.out.println("[ " + Thread.currentThread().getName() + " saludos " + " ]");
            });
        }

        System.out.println("****************************");

        // -----------------------------------------------------------
        // 3. Finalización ordenada del ThreadPool:
        // -----------------------------------------------------------
        // pool.shutdown() avisa que no se aceptarán más tareas y que
        // los hilos deben finalizar luego de acabar las tareas en cola.
        pool.shutdown();

        try {
            // pool.awaitTermination espera hasta 10 segundos a que terminen las tareas.
            // Si no han acabado, pool.shutdownNow() fuerza la interrupción de las tareas pendientes.
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Si alguien interrumpe el hilo principal mientras espera, también fuerza el cierre inmediato.
            pool.shutdownNow();
        }

        // -----------------------------------------------------------
        // 4. Impresión del resultado de las ejecuciones:
        // -----------------------------------------------------------
        System.out.println("\n********************************************");

        // Para cada entrada del mapa, muestra cuántas veces se ha ejecutado cada hilo
        poolsMaps.forEach((key, value) -> {
            System.out.println("[ " + key + " se ha ejecutado: " + value.get() + " veces ]");
        });

        System.out.println("********************************************");

        // Suma el total de ejecuciones de tareas entre todos los hilos y lo muestra por pantalla.
        // Utiliza stream para sumar eficientemente todos los contadores.
        System.out.println("\nTotal de ejecuciones threads: "
                + poolsMaps.values().stream().mapToInt(v -> v.get()).sum());

    }
}
