package edu.thepower.u2programacion.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

// Programa que simula el uso concurrente de un recurso compartido limitando el acceso mediante un semáforo.
// Cada hilo compite por adquirir un permiso del semáforo “serafin” para ejecutar una sección crítica.
// Mientras el semáforo tiene permisos disponibles, varios hilos pueden entrar simultáneamente, hasta el límite permitido.
// Se usan estructuras thread-safe (ConcurrentHashMap y AtomicInteger) para evitar condiciones de carrera en operaciones compartidas.
public class U2P04CondicionDeCarreraSemaforos implements Runnable {

    // --- VARIABLES COMPARTIDAS ENTRE TODOS LOS HILOS ---

    // Marca de tiempo que indica cuánto durará la prueba (100 ms desde el inicio).
    // Estática porque todos los hilos comparten el mismo límite de tiempo para ejecutarse.
    private static long tiempoPrueba = System.currentTimeMillis() + 100;

    // Semáforo que controla cuántos hilos pueden acceder simultáneamente a la sección crítica.
    // Tiene 5 permisos: solo cinco hilos pueden “entrar” a la vez.
    // El modo justo (true) garantiza que los hilos adquieran permisos en orden FIFO.
    private static Semaphore serafin = new Semaphore(5, true);

    // Contador atómico para rastrear cuántos hilos han adquirido el semáforo en un momento dado.
    // AtomicInteger se usa para que las operaciones de incremento/decremento sean seguras entre varios hilos.
    private static AtomicInteger contador = new AtomicInteger();

    // Mapa concurrente donde se registra cuántas veces cada hilo ha usado el semáforo.
    // ConcurrentHashMap evita colisiones entre hilos al modificar el mismo mapa sin bloqueos explícitos.
    private static Map<String, Integer> mapa = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        // --- BLOQUE PRINCIPAL DE CREACIÓN Y GESTIÓN DE HILOS ---

        // Lista donde se almacenan todos los hilos creados. Permite luego esperarlos con join().
        List<Thread> lista = new ArrayList<>();

        // Se crean 10 hilos, todos ejecutando la misma tarea (la clase implementa Runnable).
        // Cada hilo tiene un nombre identificador único “Thread_i” para registrar su actividad.
        for (int i = 0; i < 10; i++) {
            lista.add(new Thread(new U2P04CondicionDeCarreraSemaforos(), "Thread_ " + i));
            lista.get(i).start();
        }

        // Se espera a que todos los hilos terminen (join) antes de mostrar los resultados finales.
        for (Thread h : lista) {
            try {
                h.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // --- BLOQUE DE RESULTADOS ---

        System.out.println("***Uso del Serafín por los Threads***");

        // Se imprime cuántas veces cada hilo obtuvo el semáforo durante la simulación.
        for (String n : mapa.keySet()) {
            System.out.println("El thread " + n + " ha usado el semáforo: " + mapa.get(n) + " veces.");
        }

    }

    // --- BLOQUE DE EJECUCIÓN DE CADA HILO (implements Runnable) ---
    // El método run define la acción de cada hilo mientras dure la prueba.
    @Override
    public void run() {

        // Obtiene el nombre del hilo actual, sirve para identificar las entradas en el mapa.
        String nombre = "[" + Thread.currentThread() + "]";

        // Los hilos ejecutarán el bucle hasta que transcurra el tiempo de prueba (tiempoPrueba).
        while (System.currentTimeMillis() < tiempoPrueba) {

            try {
                // Sección crítica protegida por semáforo:

                // El hilo intenta adquirir un permiso del semáforo.
                // Si ya hay 5 hilos dentro, se bloquea hasta que alguno libere.
                serafin.acquire();

                // Actualiza el registro del hilo en el mapa.
                // Usa getOrDefault para sumar +1 a su contador interno de accesos.
                mapa.put(nombre, mapa.getOrDefault(nombre, 0) + 1);

                // Muestra que el hilo ha actualizado su entrada en el mapa.
                System.out.println(nombre + " Valor insertado en el mapa: " + mapa.get(nombre));

                // Incrementa el contador global de hilos que han accedido al recurso en ese instante.
                // El valor del contador permitirá comprobar que nunca hay más de 5 permisos activos.
                System.out.println("Adquirido semáforo número: " + contador.incrementAndGet());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Comprobación de seguridad: si el valor del contador supera 5, se lanza error.
            // Esto validaría que el semáforo no está funcionando correctamente.
            if (contador.get() > 5)
                throw new RuntimeException("Semáforo sobrepasado");

            // Sección de salida del recurso:
            // Cada hilo decrementa su participación activa y libera el semáforo para otro hilo.
            contador.decrementAndGet();
            serafin.release();

            // Mensaje de depuración que muestra que el hilo ha liberado el semáforo con éxito.
            System.out.println(nombre + " Semáforo liberado");
        }
    }
}
