package edu.thepower.u3comunicacionEnRed;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class U302EchoClient {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost",1025 )){
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os,true);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String entrada;
            do {
                entrada = sc.nextLine();
                pw.println(entrada);
                String line = null;
                System.out.println("Recibido del servidor: "+ br.readLine());
            }while (!entrada.equals("0"));
        } catch (IOException e) {
            System.out.println("Error al entrar al servidor");
        }

    }
}
