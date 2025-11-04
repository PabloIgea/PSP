public class Apuntes {
}
/*1) EJERCICIO 1 – FÁBRICA DE COCHES (threads que producen, guardan en lista y luego estadísticas)

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Coche {
    private final int id;         // identificador único del coche
    private final String modelo;  // datos del dominio

    public Coche(int id, String modelo) {
        this.id = id;
        this.modelo = modelo;
    }

    public int getId() { return id; }
    public String getModelo() { return modelo; }
}

public class FabricaCoches {

    // lista compartida donde todos los empleados guardan los coches fabricados
    // la envolvemos con Collections.synchronizedList para evitar condición de carrera en add()
    private static final List<Coche> coches =
            Collections.synchronizedList(new ArrayList<>());

    // contador sencillo para crear IDs únicos
    private static int contadorId = 0;

    // metodo synchronized para generar IDs sin pisarnos
    private static synchronized int generarId() {
        return ++contadorId;
    }

    public static void main(String[] args) {

        int numEmpleados = 10;     // 10 threads
        int cochesPorEmpleado = 100; // cada uno hace 100 → 1000 coches en total
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numEmpleados; i++) {

            Thread t = new Thread(() -> {
                for (int j = 0; j < cochesPorEmpleado; j++) {
                    int id = generarId();               // ID seguro
                    Coche c = new Coche(id, "Modelo-X");
                    coches.add(c);                      // add thread-safe por la lista sincronizada
                }
            }, "Empleado-" + i);

            threads.add(t);
            t.start();
        }

        // IMPORTANTE: esperar a que todos terminen antes de hacer estadísticas
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // ESTADÍSTICAS
        System.out.println("Total de coches fabricados: " + coches.size());
        System.out.println("Último ID generado: " + contadorId);
    }
}

Qué te van a querer ver aquí:
creación de varios threads,


evitar condición de carrera al añadir a la lista,


join() antes de “estadísticas”,


algún recurso común (ID, lista).


────────────────────
2) EJERCICIO 1 – BIBLIOTECA (threads que registran préstamos)
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Prestamo {
    private final String usuario;
    private final String libro;

    public Prestamo(String usuario, String libro) {
        this.usuario = usuario;
        this.libro = libro;
    }
    public String getUsuario() { return usuario; }
    public String getLibro() { return libro; }
}

public class BibliotecaConcurrente {

    // lista compartida de préstamos
    private static final List<Prestamo> prestamos =
            Collections.synchronizedList(new ArrayList<>());

    private static final String[] LIBROS = {
            "Java 21", "Redes", "Sistemas", "BBDD", "Concurrencia"
    };

    public static void main(String[] args) {

        int numUsuariosConcurrentes = 8;
        int prestamosPorUsuario = 50;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numUsuariosConcurrentes; i++) {
            String nombreUsuario = "usuario-" + i;

            Thread t = new Thread(() -> {
                for (int j = 0; j < prestamosPorUsuario; j++) {
                    // escogemos un libro cualquiera
                    String libro = LIBROS[j % LIBROS.length];
                    prestamos.add(new Prestamo(nombreUsuario, libro));
                }
            });
            threads.add(t);
            t.start();
        }

        // esperar a que todos acaben
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

        // ESTADÍSTICAS simples
        System.out.println("Total de préstamos registrados: " + prestamos.size());

        // por ejemplo, cuántas veces se ha prestado “Java 21”
        long java21 = prestamos.stream()
                .filter(p -> p.getLibro().equals("Java 21"))
                .count();
        System.out.println("Veces que se prestó 'Java 21': " + java21);
    }
}

Mismo patrón que la fábrica, pero con otro dominio (biblioteca). Así puedes elegir en el examen el que más te inspire.
────────────────────
3) EJERCICIO 2 – CONCESIONARIO (transferencias entre dos almacenes evitando deadlock)
Este es el equivalente al de las cuentas corrientes, pero con coches en 2 almacenes.
package edu.thepower.u2programacionconcurrentemultihilo;

class AlmacenCoches {
    private int stock;   // nº de coches en este almacén

    public AlmacenCoches(int stock) {
        this.stock = stock;
    }

    public void sacar(int n) {
        if (stock >= n) {
            stock -= n;
        }
    }

    public void meter(int n) {
        stock += n;
    }

    public int getStock() { return stock; }
}

public class ConcesionarioSinDeadlock {

    // TRANSFERIR COCHES ENTRE DOS ALMACENES
    public static void transferir(AlmacenCoches origen, AlmacenCoches destino, int unidades) {
        // ordenar SIEMPRE igual para evitar deadlock
        AlmacenCoches primero = origen.hashCode() < destino.hashCode() ? origen : destino;
        AlmacenCoches segundo = origen.hashCode() < destino.hashCode() ? destino : origen;

        synchronized (primero) {
            synchronized (segundo) {
                origen.sacar(unidades);
                destino.meter(unidades);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AlmacenCoches a1 = new AlmacenCoches(500);
        AlmacenCoches a2 = new AlmacenCoches(500);

        // hilo 1: mueve coches A1 -> A2
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++)
                transferir(a1, a2, 1);
        });

        // hilo 2: mueve coches A2 -> A1
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++)
                transferir(a2, a1, 2);
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Stock A1: " + a1.getStock());
        System.out.println("Stock A2: " + a2.getStock());
        System.out.println("Stock total: " + (a1.getStock() + a2.getStock())); // debe ser 1000
    }
}

Claves que puedes explicar:
“si bloqueo A y luego B en un hilo, y B y luego A en otro → deadlock”.


“por eso ordeno los locks por hashCode”.


“el total no cambia”.


────────────────────
4) EJERCICIO 2 – ALMACÉN / BIBLIOTECA DE EJEMPLARES (evitar deadlock con recursos “Libro”)
Otra casuística por si el profe se pone creativo con otro dominio:
class LibroCompartido {
    private int ejemplares;

    public LibroCompartido(int ejemplares) {
        this.ejemplares = ejemplares;
    }

    public void prestar(int n) {
        if (ejemplares >= n) {
            ejemplares -= n;
        }
    }

    public void devolver(int n) {
        ejemplares += n;
    }

    public int getEjemplares() {
        return ejemplares;
    }
}

public class BibliotecaSinDeadlock {

    // mover ejemplares de un libro a otro (p. ej. redistribución entre sucursales)
    public static void mover(LibroCompartido origen, LibroCompartido destino, int n) {
        // orden fijo → no deadlock
        LibroCompartido primero = origen.hashCode() < destino.hashCode() ? origen : destino;
        LibroCompartido segundo = origen.hashCode() < destino.hashCode() ? destino : origen;

        synchronized (primero) {
            synchronized (segundo) {
                origen.prestar(n);     // o "quitar" de origen
                destino.devolver(n);   // o "añadir" a destino
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LibroCompartido l1 = new LibroCompartido(300);
        LibroCompartido l2 = new LibroCompartido(300);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++)
                mover(l1, l2, 1);
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++)
                mover(l2, l1, 2);
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Libro 1: " + l1.getEjemplares());
        System.out.println("Libro 2: " + l2.getEjemplares());
        System.out.println("Total: " + (l1.getEjemplares() + l2.getEjemplares())); // debe mantenerse
    }
}

Cómo usar esto en el examen
Si te cae el de “fabrica X objetos con hilos y luego saca estadísticas”


nombras una lista compartida,


la proteges (Collections.synchronizedList(...) o bloque synchronized),


creas N threads,


cada uno mete M objetos,


guardas los threads en un List<Thread>,


al final join() a todos,


luego haces el recuento.


Si te cae el de “evita deadlock con dos recursos”


recibes origen y destino,


los ordenas por hashCode()


bloqueas siempre en ese orden


haces la operación (retirar/ingresar o sacar/meter)


imprimes total final.


Aquí tienes la versión CHULETA de examen, súper resumida y práctica — todo lo esencial sin comentarios largos, para recordar la estructura y los puntos que tienes que escribir o explicar.
────────────────────
🧩 EJERCICIO 1 – PRODUCCIÓN CON THREADS (fábrica)
import java.util.*;

class Coche {
    int id; String modelo;
    Coche(int id, String modelo){ this.id=id; this.modelo=modelo; }
}

public class FabricaCoches {
    static List<Coche> lista = Collections.synchronizedList(new ArrayList<>());
    static int contador = 0;
    static synchronized int genId(){ return ++contador; }

    public static void main(String[] args) throws InterruptedException {
        int empleados=10, cochesPorEmpleado=100;
        List<Thread> hilos = new ArrayList<>();

        for(int i=0;i<empleados;i++){
            Thread t = new Thread(() -> {
                for(int j=0;j<cochesPorEmpleado;j++)
                    lista.add(new Coche(genId(),"Modelo-X"));
            });
            hilos.add(t); t.start();
        }

        for(Thread t:hilos) t.join();
        System.out.println("Total coches: "+lista.size());
    }
}

✅ Puntos clave:
Collections.synchronizedList, join(), synchronized para IDs, estadística final con lista.size().
────────────────────
📚 EJERCICIO 1 – BIBLIOTECA (otra casuística)
import java.util.*;

class Prestamo { String u,l; Prestamo(String u,String l){this.u=u;this.l=l;} }

public class Biblioteca {
    static List<Prestamo> lista = Collections.synchronizedList(new ArrayList<>());
    static String[] libros = {"Java","Redes","BBDD"};

    public static void main(String[] a) throws InterruptedException {
        List<Thread> hilos=new ArrayList<>();
        for(int i=0;i<8;i++){
            String usuario="U"+i;
            Thread t=new Thread(()->{
                for(int j=0;j<50;j++)
                    lista.add(new Prestamo(usuario, libros[j%libros.length]));
            });
            hilos.add(t); t.start();
        }
        for(Thread t:hilos)t.join();
        System.out.println("Total préstamos: "+lista.size());
    }
}
────────────────────
🚗 EJERCICIO 2 – CONCESIONARIO (evitar deadlock)
class Almacen {
    int stock;
    Almacen(int s){stock=s;}
    void sacar(int n){if(stock>=n)stock-=n;}
    void meter(int n){stock+=n;}
    int get(){return stock;}
}

public class Concesionario {
    static void transferir(Almacen o,Almacen d,int n){
        Almacen a=o.hashCode()<d.hashCode()?o:d;
        Almacen b=o.hashCode()<d.hashCode()?d:o;
        synchronized(a){ synchronized(b){ o.sacar(n); d.meter(n); } }
    }

    public static void main(String[] a)throws InterruptedException{
        Almacen a1=new Almacen(500),a2=new Almacen(500);
        Thread t1=new Thread(()->{for(int i=0;i<1000;i++)transferir(a1,a2,1);});
        Thread t2=new Thread(()->{for(int i=0;i<1000;i++)transferir(a2,a1,2);});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Total:"+ (a1.get()+a2.get()));
    }
}

✅ Clave:
ordenar bloqueos por hashCode() → sin deadlock.

────────────────────
🏭 EJERCICIO 2 – ALMACÉN (misma lógica, distinto contexto)
class Stock {
    int unidades;
    Stock(int u){unidades=u;}
    void quitar(int n){if(unidades>=n)unidades-=n;}
    void poner(int n){unidades+=n;}
    int get(){return unidades;}
}

public class Almacen {
    static void mover(Stock o,Stock d,int n){
        Stock p=o.hashCode()<d.hashCode()?o:d;
        Stock s=o.hashCode()<d.hashCode()?d:o;
        synchronized(p){ synchronized(s){ o.quitar(n); d.poner(n); } }
    }

    public static void main(String[] a)throws InterruptedException{
        Stock s1=new Stock(300),s2=new Stock(300);
        Thread t1=new Thread(()->{for(int i=0;i<500;i++)mover(s1,s2,1);});
        Thread t2=new Thread(()->{for(int i=0;i<500;i++)mover(s2,s1,2);});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Total: "+(s1.get()+s2.get()));
    }
}

🧠 RESUMEN EXPRESS DE EXAMEN

Concepto
Qué debes recordar
Thread
new Thread(() -> {...}).start();
Condición de carrera
Varios hilos acceden/modifican el mismo recurso sin sincronización.
Solución
synchronized, AtomicInteger, o Collections.synchronizedList.
join()
Esperar a que todos los hilos terminen antes de calcular estadísticas.
Deadlock
Dos hilos se bloquean esperando los recursos del otro.
Solución al deadlock
Bloquear en el mismo orden (hashCode() o ID).
Estadísticas
.size(), sumas o recuentos después de join().








APLICACIONES CLIENTE SERVIDOR
SERVICIOS DE RED
SEGURIDAD, ENCRIPTACIÓN, CLAVES PÚBLICAS... PRIVADAS…
*/