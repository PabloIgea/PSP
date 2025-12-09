package edu.thepower.u3comunicacionEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

public class U3P08ClienteUDP {

    public static void main(String[] args) {

        try(DatagramSocket ds = new DatagramSocket()) {

            String mensaje = "Mona" ;
            byte[] data = mensaje.getBytes();

            InetAddress host = InetAddress.getByName("127.0.0.1");
            int puerto = 2100;

            DatagramPacket dp = new DatagramPacket(data, data.length, puerto);

            ds.send(dp);

            //Recepcion del ACK del servidor
            byte[] data2 = new byte[1024];

            DatagramPacket dp2 = new DatagramPacket(data2, data2.length);

            ds.receive(dp2);

            String mensaje2 = new String(dp2.getData(), 0, dp2.getLength());
            System.out.println("Mensaje recibido: " + mensaje2);

        }catch (IOException e ){
            System.out.println("Error: " + e.getMessage());
        }

    }

}
