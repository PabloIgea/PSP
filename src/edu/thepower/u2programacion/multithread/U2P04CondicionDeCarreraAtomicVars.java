package edu.thepower.u2programacion.multithread;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// Programa que demuestra cómo evitar condiciones de carrera utilizando una variable atómica.
// Una condición de carrera ocurre cuando varios hilos acceden y modifican una misma variable al mismo tiempo.
// Aquí usamos AtomicInteger para que las operaciones sobre el contador sean atómicas (seguras en entornos concurrentes).
public class U2P04CondicionDeCarreraAtomicVars {

    // Variable compartida entre hilos que representa un contador global.
    // Se usa AtomicInteger en lugar de int o Integer para garantizar operaciones atómicas sin bloqueos explícitos.
    // Es estática para que todos los métodos de la clase accedan al mismo contador compartido.
    private static AtomicInteger contador = new AtomicInteger(0);

    // Metodo que incrementa el contador en un valor dado.
    // Se usa addAndGet() en vez de get()+set() porque combina ambas operaciones en una única acción atómica.
    public static void incrementarContador(int num) {
        contador.addAndGet(num);
    }

    // Metodo que devuelve el valor actual del contador.
    // get() es atómico y devuelve el valor más actualizado sin riesgo de inconsistencia.
    public static int getContador() {
        return contador.get();
    }

    public static void main(String[] args) {

        // --- BLOQUE DE CONFIGURACIÓN INICIAL ---
        // ITERACIONES define cuántas veces cada hilo repetirá su operación (gran número para simular carga concurrente).
        // VALOR indica cuánto se incrementa o decrementa el contador en cada iteración.
        final int ITERACIONES = 1_000_000;
        final int VALOR = 10;

        // --- BLOQUE DE CREACIÓN DEL HILO INCREMENTADOR ---
        // Este hilo incrementa el contador global en cada iteración.
        // Representa una operación concurrente que “añade” valor al recurso compartido.
        Thread incrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion incrementador");

            // Bucle que incrementa el contador muchas veces para probar la concurrencia.
            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(VALOR);
            }

            System.out.println("Acabando ejecucion incrementador");
        });

        // --- BLOQUE DE CREACIÓN DEL HILO DECREMENTADOR ---
        // Este hilo realiza la operación inversa: resta el mismo valor en cada iteración.
        // Simula otro proceso que compite por modificar el mismo recurso.
        Thread decrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion decrementador");

            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(-VALOR);
            }

            System.out.println("Acabando ejecucion decrementador");
        });

        // --- BLOQUE DE EJECUCIÓN DE HILOS ---
        // Se inician ambos hilos concurrentemente.
        // Ambos modifican el contador a ritmo distinto, pero la variable atómica evita inconsistencias.
        incrementator.start();
        decrementator.start();

        try {
            // join() asegura que el hilo principal espere a que ambos terminen antes de continuar.
            incrementator.join();
            decrementator.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // --- BLOQUE FINAL ---
        // Como las operaciones son atómicas, el valor final debería ser 0.
        // Esto verifica que sin condiciones de carrera, las operaciones concurrentes se compensan correctamente.
        System.out.println("El valor final de contador es: " + getContador());

    }



}
/* *****APUNTES EXAMEN******
Random -> crear numeros aleatorios
 r.newInt(0,10)
 List ArrayList -> guardar coches

 Hacer por camtodad de un objeto
 dividir la cantidad en hilos
 hilos en una lista para lanzarlos y luego terminarlos con join
 estadisticas de veeces que ha ocurrido algo
 ojo finalizar threads--> con join

1 Ejercicio. Ej. Una factoria que nos pide hacer mil coches, un cobjeto llamado coche que tenmdremos que instanciar tantas veces como sea necesario, cada empleado es un thread cada empleado hace 100 cochches, se guardan en un array list, luego una estadistica de n veces que hemos hecho algo. COndiciones de carreras, Threads, Estadisticas. Para sacar estadisticas hay que asegurarse de que los threads se acaben con join, guardando previamente en un arraylist

    Random random = new Random();
    System.out.println("Numero aleatorio entre 0 y 9" + random.nextInt(0,10));

 2do ejercico --> Deadlock
 */