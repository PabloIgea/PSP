package edu.thepower.examenOctubre;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class generarPedidosConcurrentesCorregido {

    static class Pedido {
        private static AtomicInteger generadorID;
        private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        private String idPedido;
        private String cliente;
        private long fecha;


        //1.Iniciador de clase, se ejecuta una vez al iniciar el primer objeto
        static {
            generadorID = new AtomicInteger(0);
        }

        public Pedido(String cliente) {
            this.idPedido = String.valueOf(generadorID.incrementAndGet());
            this.cliente = cliente;
            this.fecha = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Pedido con ID " + idPedido + ", cliente " + cliente + ", fecha " + sdf.format(fecha);
        }

    }

    public static void main(String[] args) {
        final int MAX_THREADS = 10;
        final int MAX_PEDIDOS = 10;
        List<Pedido> pedidos = new ArrayList<Pedido>();
        List<Thread> threads = new ArrayList<Thread>();
        Map<String, AtomicInteger> pedidosPorCliente = new ConcurrentHashMap<>();
        Random random = new Random();

        //Declaración y ejecución de los threads
        for (int i = 0; i < MAX_THREADS; i++) {

            Thread hilo = new Thread(()-> {
                for (int j = 0; j < MAX_PEDIDOS; j++) {
                    //Crear Cliente aleatorio
                    String cliente = "Cliente-" + random.nextInt(10);
                    Pedido pedido = new Pedido("Cliente- " + random.nextInt(10));

                    synchronized (pedidos) {
                        pedidos.add(pedido);
                    }
                    //System.out.println(pedido);
                    //Sumamos uno a la cantidad de pedidos del cliente
                    pedidosPorCliente.computeIfAbsent(cliente,k ->  new AtomicInteger()).incrementAndGet();
                }
            });
            //Ejecución del hilo y almacenamiento en lista de threads
            hilo.start();
            threads.add(hilo);
        }

        System.out.println("Todos los threads están en ejecución");
        //2.Esperando a que todos los hilos acabem
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Todos los threads han concluido");

        //3.Mostrar Estadísticas
        System.out.println("***PEDIDOS REALIZADOS POR LOS CLIENTES: ");
        for (Pedido pedido : pedidos) {
            System.out.println(pedido);
        }
        System.out.println("***NÚMERO TOTAL DE PEDIDOS POR LOS CLIENTES: " +  pedidos.size());
        System.out.println("***CANTIDAD DE PEDIDOS POR CADA CLIENTE: ");

        int contador = 0;

        //Primero forma de hacerlo
        for (String cliente : pedidosPorCliente.keySet()) {

            System.out.println("El cliente " + cliente + " ha realizado " + pedidosPorCliente.get(cliente).get());
            contador += pedidosPorCliente.get(cliente).get();
        }
        System.out.println("Numero total de pedidos por clientes" + contador);

        //Esto ya es para flipàos para sumar los valores de un mapa
        int flipadez = pedidosPorCliente.values().stream().mapToInt(p -> p.get()).sum();
        System.out.println(flipadez);

        //Segunda forma de hacerlo
       /* pedidosPorCliente.forEach((k, v) -> {
            System.out.println("El cliente " + k + " ha realizado " + v.get());
            //contador += v.get();
        });*/



    }

}
