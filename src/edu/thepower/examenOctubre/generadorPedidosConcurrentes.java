package edu.thepower.examenOctubre;

import java.text.SimpleDateFormat;
import java.util.*;

class Pedido {
    private  int id;
    private String nombre;
    private long fecha = System.currentTimeMillis();


    public Pedido(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        formato.format(fecha);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public long getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Pedido{" + "id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + '}';
    }

}


public class generadorPedidosConcurrentes {

    private static final List<Pedido> pedidos = Collections.synchronizedList(new ArrayList<Pedido>());
    private static  final Map<String, Pedido> pedidosMap = new HashMap<String, Pedido>();
    private static  int contadorId = 0;


    private static synchronized int getContadorId(){
        return contadorId++;
    }


    public static void main(String[] args) {
        int numPedidos = 5;
        List<Thread> threads= new ArrayList<>();

        for (int i = 0; i < numPedidos; i++) {

            Thread t = new Thread(() -> {
               for (int j = 0; j < numPedidos; j++) {
                   int id = getContadorId();
                   Pedido p = new Pedido(id, "Probando");
                   pedidos.add(p);
               }
            }, "Pedido- " + i);

            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Total de pedidos hechos: " + pedidos.size());
        System.out.println("Último ID generado: " + contadorId);
    }
}
