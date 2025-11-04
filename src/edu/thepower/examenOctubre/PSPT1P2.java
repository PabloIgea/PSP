package edu.thepower.examenOctubre;

public class PSPT1P2 {

    static class Impresora {

        /*  Metodo público y synchronized para asegurarse que sólo un hilo pueda ejecutar este metodo a la vez, lo que previene
         *   inconsistencias causadas por accesos concurrentes(condicion de carrera). Este metodo se encarga de imprimir en conso
         *   la el hilo en cuestion y se asegura de esperar mediante sleep que duerme el hilo hasta que se despierte, cosa
         *   que no pasa
         *    */

    public synchronized void imprimir(String doc) {
        System.out.println(Thread.currentThread().getName() + " imprime: " +
                doc);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {

        }
    }
}

/*Igual que lo de arriba pero es una clase en vez un codigo*/
    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored){

            }
        }
    }

    /*Cuando se ejecuta el codigo, se queda procesando de forma indefinida, lo cual no es el objetivo del
    * programa para nada
    * */

    public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();
        Thread tA = new Thread(() -> {
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                impresora.imprimir("Documento A");
                synchronized (scanner) {
                    scanner.scan("Documento A");
                }
            }
        }, "Tarea-A");


        Thread tB = new Thread(() -> {
            synchronized (scanner) {
                System.out.println(Thread.currentThread().getName() + " acceso a escáner...");
                scanner.scan("Documento B");
                synchronized (impresora) {
                    impresora.imprimir("Documento B");
                }
            }
        }, "Tarea-B");
        tA.start();
        tB.start();
    }
}
/*Cuando se ejecuta el codigo, se queda procesando de forma indefinida, lo cual no es el objetivo del
 * programa para nada. Es posible que esto se deba a que se esta produciendo el fenomeno conocido como
 * deadlock, ya que ambos hilos estan intentando acceder de forma simultanea
 * */