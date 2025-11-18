package edu.thepower.u3comunicacionEnRed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class Validacion{

    public static int validarPuerto(String [] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException("Debe ingresar unico argumento");
        }
        int puerto = Integer.parseInt(args[0]);

        if (puerto < 1024 || puerto > 65535) {
            throw new IllegalArgumentException("Debe ingresar un puerto entre 1024 y 65535");
        }

        return puerto;
    }

}

public class U3P00EchoServer {


    public static void main(String[] args) {

        int puerto = 0;

        try{

            puerto = Validacion.validarPuerto(args);

        } catch (Exception e ) {
            System.out.println("Error al intentar enviar el servidor: " + e.getMessage());
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(puerto);){
            System.err.println("Servidor Iniciado en puerto: " + puerto);

            Socket socket = serverSocket.accept();

            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String linea;
            while ((linea = br.readLine()) != null){

                System.out.println("Recibido de cliente: " + linea);
                pw.println(linea.toLowerCase());
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
