package edu.thepower.u2programacion.multithread;

// -----------------------------------------------------------------------------
// PROGRAMA: Ejemplo de uso de demonios (threads en background) y monitoreo en Java
// -----------------------------------------------------------------------------
// Este programa lanza 3 threads. Dos ejecutan tareas periódicas mientras un "thread demonio"
// hace de latido (heartbeat), confirmando constantemente que el programa sigue activo.
// Cuando los dos hilos principales terminan su trabajo, el demonio finaliza automáticamente.
// Útil para simular servicios de bajo nivel, monitorización de servidores o procesos autónomos.
// -----------------------------------------------------------------------------

public class U2P06ServiciosDemon {

    // -------------------------------------------------------------------------
    // Constante global que determina el instante de finalización de t1 y t2.
    // Calculada como el tiempo actual más 10 segundos (10.000 milisegundos).
    // Limita la vida útil de los threads principales y la ejecución general.
    // -------------------------------------------------------------------------
    final static long TIEMPO_MAXIMO = System.currentTimeMillis() + 10000;

    public static void main(String[] args) {

        // ---------------------------------------------------------------------
        // Thread t1: Muestra un mensaje cada 500ms durante 10 segundos.
        // Representa un posible servicio/monitor que realiza comprobaciones periódicas.
        // - Variable local al main.
        // ---------------------------------------------------------------------
        Thread t1 = new Thread(() -> {
            while (TIEMPO_MAXIMO > System.currentTimeMillis()) {
                System.out.println("Iniciando t1");
                try {
                    Thread.sleep(500); // Detiene el hilo 500ms entre ejecuciones
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // ---------------------------------------------------------------------
        // Thread t2: Similar a t1, pero con un mensaje cada 1000ms (1 segundo).
        // Puede simular otro tipo de tarea regular en paralelo, por ejemplo, recopilar logs.
        // - Variable local al main.
        // ---------------------------------------------------------------------
        Thread t2 = new Thread(() -> {
            while (TIEMPO_MAXIMO > System.currentTimeMillis()) {
                System.out.println("Iniciando t2");
                try {
                    Thread.sleep(1000); // Detiene el hilo 1 segundo entre ejecuciones
                } catch (InterruptedException e) {
                    // En este ejemplo, si ocurre una interrupción simplemente se ignora.
                }
            }
        });

        // ---------------------------------------------------------------------
        // Thread heartBeat (demonio): Su misión es imprimir un "latido" cada 100ms,
        // demostrando que sigue activo. Idró a modo de servicio de background.
        // Es un demonio, así que termina automáticamente cuando los otros threads acaban.
        // Tiene sentido en servicios o sistemas autónomos (ej:
        // confirmación de vida para sistemas críticos, satélites, etc).
        // - Variable local al main.
        // ---------------------------------------------------------------------
        Thread heartBeat = new Thread(() -> {
            while (true) {
                System.out.println("Pum Pum ··> heartBeat");
                try {
                    Thread.sleep(100); // Espera 100ms antes de mostrar el siguiente latido
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        heartBeat.setDaemon(true); // Esto convierte heartBeat en un thread tipo demonio,
        // lo que significa que se detendrá automáticamente
        // cuando todos los threads 'normales' (t1,t2) terminen.

        System.out.println("Inicio ejecucion Threads");

        // ---------------------------------------------------------------------
        // Lanzamiento de los threads principales y el demonio:
        // - El orden de ejecución puede afectar la sincronización de los mensajes.
        // - El heartbeat (demonio) será lanzado después de los dos hilos principales.
        // ---------------------------------------------------------------------
        t1.start();
        // t1.setPriority(Thread.MIN_PRIORITY); // Ejemplo de ajuste de prioridad (comentado, no se usa aquí)
        t2.start();
        heartBeat.start();

        System.out.println("Threads ejecutandose");
    }
}
