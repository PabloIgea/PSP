package edu.thepower.u2programacion.multithread;

// Clase principal que demuestra el uso de monitores para evitar condiciones de carrera
public class U2P04CondicionDeCarreraMonitores {

    // Variable estática compartida entre todos los hilos:
    // 'contador' almacena el valor entero que será modificado por varios hilos simultáneamente.
    // Es estática para que se pueda compartir entre todos los métodos y hilos de la clase.
    // En el dominio del problema esta variable representa el recurso crítico
    // cuyo acceso concurrente queremos proteger.
    private static int contador = 0;

    // Método estático y sincronizado para modificar el contador:
    // 'num' es el valor a sumar o restar al contador, según el hilo lo invoque con un valor positivo o negativo.
    // La sincronización en el método asegura que sólo un hilo pueda ejecutar este método a la vez,
    // lo que previene inconsistencias causadas por accesos concurrentes ("condición de carrera").
    public static synchronized void incrementarContador(int num) {
        contador += num;
    }

    // Método estático y sincronizado para leer el valor actual de 'contador':
    // Permite obtener el valor compartido de forma segura, evitando leer durante una modificación concurrente.
    public static synchronized int getContador() {
        return contador;
    }

    // Método principal que inicia los hilos y gestiona la ejecución del programa:
    public static void main(String[] args) {

        // Constante que define el número total de iteraciones para cada hilo.
        // En el dominio del problema representa la intensidad del acceso concurrente simulado.
        final int ITERACIONES = 1_000_000;

        // Constante que indica el valor por el cual se incrementa o decrementa el contador en cada iteración.
        // Este valor se usa para hacer variaciones significativas en el contador con cada operación.
        final int VALOR = 10;

        // BLOQUE: Definición y creación del primer hilo (incrementator)
        // Hilo encargado de incrementar el valor del contador.
        // Ejecuta 1_000_000 veces el incremento de 'VALOR'.
        Thread incrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion incrementador");

            for (int i = 0; i < ITERACIONES; i++) {
                // En cada iteración, incrementa el contador usando el método sincronizado.
                // La variable 'i' controla el número de repeticiones para simular carga concurrente.
                incrementarContador(VALOR);
            }
            System.out.println("Acabando ejecucion incrementador");
        });

        // BLOQUE: Definición y creación del segundo hilo (decrementator)
        // Hilo encargado de decrementar el valor del contador.
        // Ejecuta 1_000_000 veces el decremento de 'VALOR'.
        Thread decrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion decrementador");

            for (int i = 0; i < ITERACIONES; i++) {
                // En cada iteración, decrementa el contador usando el método sincronizado.
                // La variable 'i' es nuevamente el control de bucle para simular alta concurrencia.
                incrementarContador(-VALOR);
            }
            System.out.println("Acabando ejecucion decrementador");
        });

        // BLOQUE: Inicio de los hilos
        // Se lanzan ambos hilos, comenzando la ejecución concurrente sobre el recurso compartido.
        incrementator.start();
        decrementator.start();

        // BLOQUE: Sincronización con los hilos principales y manejo de excepciones
        // Se espera a que ambos hilos terminen antes de continuar.
        // Esto asegura que el valor final del contador se muestre sólo después de que todos los accesos hayan terminado.
        try {
            incrementator.join();
            decrementator.join();
        } catch (InterruptedException e) {
            // Manejo de interrupciones: En este caso, si ocurre una interrupción en los hilos,
            // se lanza una excepción de Runtime para indicar error en la ejecución concurrente.
            throw new RuntimeException(e);
        }

        // BLOQUE: Presentación del resultado final
        // Imprime el valor final del contador, que debería ser 0 si la sincronización ha evitado la condición de carrera.
        System.out.println("El valor final de contador es: " + getContador());

    }
}
