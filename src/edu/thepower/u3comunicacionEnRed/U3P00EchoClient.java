package edu.thepower.u3comunicacionEnRed;

import java.io.*;
import java.net.Socket;

public class U3P00EchoClient {

    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost" , 4000)){


            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            pw.println("Esto es una prueba ");
            System.out.println("Escuchando con el servidor: " + br.readLine());

        } catch(IOException e) {
            System.err.println("Error en la conexion con  el servidor: " + e.getMessage());
        }

        System.out.println("Comunicación finalizada");

    }
}
