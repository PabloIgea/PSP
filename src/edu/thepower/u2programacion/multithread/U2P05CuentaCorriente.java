package edu.thepower.u2programacion.multithread;


class CuentaCorriente {

    private float saldo;

    public CuentaCorriente(float saldo) {
        this.saldo = saldo;
    }
    public float getSaldo() {
        return saldo;
    }

    public void retirar(float saldo) {
        if (saldo > this.saldo) {
            saldo -= saldo;
        }
    }

    public void ingresar(float saldo) {
        this.saldo += saldo;
    }

}

public class U2P05CuentaCorriente {

    public static void transferir(CuentaCorriente origen, CuentaCorriente destino, float importe) {
        CuentaCorriente aux1 = origen.hashCode()<destino.hashCode()?origen:destino;
        CuentaCorriente aux2 = origen.hashCode()<destino.hashCode()?destino:origen;

        synchronized (aux1) {
            synchronized (aux2) {
                origen.retirar(importe);
                origen.ingresar(importe);
            }
        }

    }

    public static void main(String[] args) {
        CuentaCorriente cc1 = new CuentaCorriente(100_000);
        CuentaCorriente cc2 = new CuentaCorriente(100_000);

        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 1000; i++) {
                transferir(cc1, cc2, 10);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                transferir(cc2, cc1, 20);
            }
        });
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Saldo final cuenta 1: " + cc1.getSaldo());
        System.out.println("Saldo final cuenta 2: " + cc2.getSaldo());
        System.out.println("Saldo total:" + cc1.getSaldo() + cc2.getSaldo());
    }

}
