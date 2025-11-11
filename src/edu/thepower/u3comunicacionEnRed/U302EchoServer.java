package edu.thepower.u3comunicacionEnRed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class U302EchoServer {

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String getFecha(){

        return sdf.format(System.currentTimeMillis());
    }

    public static void main(String[] args) {

        Thread heartBeat = new Thread(() -> {
           while (true) {
               System.out.println(getFecha() + " Servidor activo");
               System.out.println(System.currentTimeMillis());
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
        heartBeat.setDaemon(true);
        try (ServerSocket server = new ServerSocket(1025)) {
            heartBeat.start();

            System.out.println(getFecha() +" Servidor Iniciado en puerto: 1025");

            Socket socket = server.accept();
            System.out.println(getFecha() +" Recibida solictud de comunicacion");

            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out,true);
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(" Recibido del cliente: " + line);
                writer.println(line.toLowerCase());
            }

        } catch (IOException e) {
            System.out.println("Error al entrar al servidor: " + e.getMessage());
        }
    }

}
