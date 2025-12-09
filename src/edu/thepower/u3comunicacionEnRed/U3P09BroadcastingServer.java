package edu.thepower.u3comunicacionEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U3P09BroadcastingServer {

    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket(8453)) {

            System.out.println("Esccuhando en el puerto: " + ds.getLocalPort());

            byte[] data = new byte[1024];

            DatagramPacket dp = new DatagramPacket(data, data.length);

            ds.receive(dp);

            String mensaje = new String(dp.getData(),0, dp.getLength());

            System.out.println("Recibido: " + mensaje);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage() );
        }
    }

}
