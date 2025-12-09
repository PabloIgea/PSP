package edu.thepower.u4serviciosEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U4P02SelfHealingServer {
    public static void main(String[] args) {

        while (true){
            try{
                arrancarServidor();
            }catch (Exception e){
                System.err.println("Servidor fuera de servicio");
                System.err.println("Reiniciando en 2 segundos");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
        }

    }


    private static void arrancarServidor(){

        try (ServerSocket socket = new ServerSocket(2408)){

            System.out.println("Escuchando en el puerto: " + socket.getLocalPort());
            Thread killer = new Thread(() -> {
                System.out.println("Thread asesino iniciandose");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }

                try {
                    socket.close();
                } catch (IOException e) {

                }
            });

            killer.start();

            while (true){
                Socket sc = socket.accept();
                Thread t = new Thread (() -> {

                    try(BufferedReader bf = new BufferedReader( new InputStreamReader(sc.getInputStream()));
                        PrintWriter pw = new PrintWriter(sc.getOutputStream(), true)) {
                        String linea;
                        while ((linea = bf.readLine()) != null) {

                            System.out.println("Recibido de cliente: " + linea);
                            pw.println(linea.toLowerCase());
                        }

                    } catch (IOException e ){
                        System.err.println("Error: " + e.getMessage());
                    }

                });
                t.start();
            }


        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
