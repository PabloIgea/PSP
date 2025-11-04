package edu.thepower.examenOctubre;

public class PSPT1P2Corregido {
    static class Impresora {
    public synchronized void imprimir(String doc) {
        System.out.println(Thread.currentThread().getName() + " imprime: " +
                doc);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {

        }
    }
}
    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored){

            }
        }
    }
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

//Para corregirlo habria que ordenar los locks aqui pero no me va la cabeza, lo he intentado pero estoy francamente agotado
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

        try {
            tA.join();
            tB.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Añadidos joints para intenrar solucionarlo
    }
}
