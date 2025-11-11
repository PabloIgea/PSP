package edu.thepower.u3comunicacionEnRed;

import java.io.*;
import java.net.Socket;

public class U302EchoClient {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost",1025 )){
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os,true);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            pw.println("ECHO SERVIDOR");

            String line = null;
            while((line = br.readLine()) != null){

                System.out.println("Recibido del servidor: "+ line);

            }
        } catch (IOException e) {
            System.out.println("Error al entrar al servidor");
        }

    }
}
