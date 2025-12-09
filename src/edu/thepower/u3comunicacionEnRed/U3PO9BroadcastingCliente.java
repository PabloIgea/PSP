package edu.thepower.u3comunicacionEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U3PO9BroadcastingCliente {

    public static void main(String[] args) {

        int puertoServer = 8453;

        try (DatagramSocket ds = new DatagramSocket()){

            String mensaje = "Mensaje de Broadcast";
            byte[] data = mensaje.getBytes();

            InetAddress host = InetAddress.getByName("10.255.255.255");

            DatagramPacket dp = new DatagramPacket(data, data.length, host, puertoServer);

            ds.setBroadcast(true);
            ds.send(dp);

        }catch (IOException e ){

            System.err.println("Error: " + e.getMessage());
        }

    }
}
