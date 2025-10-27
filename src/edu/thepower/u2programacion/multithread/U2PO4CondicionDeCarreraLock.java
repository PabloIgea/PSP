package edu.thepower.u2programacion.multithread;

import java.util.concurrent.locks.ReentrantLock;

public class U2PO4CondicionDeCarreraLock {

    // Estado compartido entre todos los hilos del programa:
    // - Tipo: int (primitivo), ámbito: estático de clase (visible en todos los métodos estáticos).
    // - Propósito en el dominio: representa un contador global que distintos hilos quieren modificar simultáneamente.
    //   Es el "recurso crítico" cuya integridad queremos proteger de condiciones de carrera.
    private static int contador = 0;

    // Mecanismo de sincronización:
    // - Tipo: ReentrantLock, ámbito: estático de clase (un único candado para proteger 'contador' en toda la clase).
    // - Propósito en el dominio: garantiza exclusión mutua alrededor de la sección crítica (la actualización de 'contador').
    //   Con 'lock()' se entra en la región crítica; con 'unlock()' se libera, evitando que dos hilos modifiquen 'contador' a la vez.
    private static ReentrantLock candado = new ReentrantLock();

    // Bloque lógico: operación de actualización segura del estado compartido
    // - Propósito: modificar 'contador' de forma thread-safe sumando 'num' (positivo para incrementar, negativo para decrementar).
    // - Diseño: se imprime entrada y salida para trazabilidad, se adquiere el candado antes de tocar el estado,
    //   y se libera en 'finally' para asegurar liberación incluso si hay excepciones.
    public static void incrementarContador(int num) {

        // Trazabilidad: ayuda a estudiar el flujo de ejecución y a depurar concurrencia
        System.out.println("Entrando en incrementarContador");

        // Entrada en sección crítica: ningún otro hilo puede ejecutar el bloque protegido simultáneamente
        candado.lock();

        try {
            // Sección crítica:
            // - Operación: contador = contador + num
            // - Dominio: aplica la modificación pedida por el hilo (p.ej., VALOR o -VALOR) sobre el recurso compartido.
            // - Atomizada por el candado: evita interleavings peligrosos (lectura-modificación-escritura interferida).
            contador += num;
        } finally {
            // Liberación del candado: garantiza progreso de otros hilos y previene deadlocks por excepciones
            candado.unlock();
        }

        // Trazabilidad de salida de la operación
        System.out.println("Saliendo de incrementarContador");
    }

    // Bloque lógico: lectura del estado compartido
    // - Propósito: exponer el valor actual de 'contador' para reporte final.
    // - Nota: aquí no se usa candado; en este diseño, la lectura se hace al final cuando los hilos ya han terminado.
    //   Si se leyera concurrentemente, convendría proteger con el mismo candado o usar 'volatile' / AtomicInteger según el caso.
    public static int getContador() {

        System.out.println("Entrando en getContador");

        System.out.println("Saliendo de getContador");
        return contador;
    }

    // Bloque lógico: configuración y orquestación de hilos
    // - Variables de configuración:
    //   - ITERACIONES (final, ámbito local main): cuántas veces cada hilo aplica su operación; en el dominio, el “tamaño” de la carga concurrente.
    //   - VALOR (final, ámbito local main): magnitud de cada modificación del contador; define el impacto por iteración.
    public static void main(String[] args) {

        final int ITERACIONES = 1_000_000; // volumen de trabajo concurrente (prueba de estrés)
        final int VALOR = 10;              // paso de actualización por iteración

        // Bloque lógico: hilo incrementador
        // - Tipo: Thread con lambda Runnable
        // - Propósito en el dominio: simula un productor de incrementos sobre el contador compartido.
        // - Comportamiento: repite 'ITERACIONES' veces la suma de 'VALOR' con sincronización interna de la operación.
        Thread incrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion incrementador");

            // Bucle de trabajo concurrente: aplica incrementos seguros
            for (int i = 0; i < ITERACIONES; i++) {
                // Entra en la operación que usa el candado para proteger la actualización
                incrementarContador(VALOR);
            }
            System.out.println("Acabando ejecucion incrementador");
        });

        // Bloque lógico: hilo decrementador
        // - Tipo: Thread con lambda Runnable
        // - Propósito en el dominio: simula un consumidor que compensa los incrementos, aplicando decrementos equivalentes.
        // - Comportamiento: repite 'ITERACIONES' veces la suma de '-VALOR' (equivale a restar 'VALOR') de forma segura.
        Thread decrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion decrementador");

            // Bucle de trabajo concurrente: aplica decrementos seguros
            for (int i = 0; i < ITERACIONES; i++) {
                // Misma operación protegida, pero con valor negativo para restar
                incrementarContador(-VALOR);
            }
            System.out.println("Acabando ejecucion decrementador");
        });

        // Arranque concurrente: ambos hilos comienzan su trabajo en paralelo
        incrementator.start();
        decrementator.start();

        try {
            // Coordinación: el hilo principal espera a que ambos hilos terminen.
            // - Dominio: asegura que el reporte final se haga cuando no hay más actualizaciones en curso.
            incrementator.join();
            decrementator.join();
        } catch (InterruptedException e) {
            // Manejo simple: propaga como RuntimeException (en apuntes, podrías optar por restaurar el estado de interrupción)
            throw new RuntimeException(e);
        }

        // Reporte final: lectura del estado compartido ya estable (no concurrente)
        System.out.println("El valor final de contador es: " + getContador());

    }
}

