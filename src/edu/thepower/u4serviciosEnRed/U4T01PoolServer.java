package edu.thepower.u4serviciosEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class U4T01PoolServer {

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(1);

        try (ServerSocket socket = new ServerSocket(2408)){

            System.out.println("Escuchando en el puerto: " + socket.getLocalPort());
            while (true){
                Socket sc = socket.accept();
                pool.submit(() -> {

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
            }


        } catch (IOException e) {
            System.err.println("Error en el servidor");
        }
    }

}
