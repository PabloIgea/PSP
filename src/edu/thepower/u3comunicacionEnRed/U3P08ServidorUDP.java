package edu.thepower.u3comunicacionEnRed;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class U3P08ServidorUDP {
    public static void main(String[] args) {
        try (DatagramSocket ds = new DatagramSocket(2100)) {

            System.out.println("Servidor escuchando en el puerto " + ds.getLocalPort());

            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);

            // Esperar a que llegue un datagrama
            ds.receive(dp);

            String mensaje = new String(dp.getData(), 0, dp.getLength());
            System.out.println("Mensaje recibido: " + mensaje);

            // Respuesta al mensaje recibido
            String ack = "ACK - " + mensaje;
            byte[] data2 = ack.getBytes();

            InetAddress host = dp.getAddress();
            int puerto = dp.getPort();

            DatagramPacket dp2 = new DatagramPacket(data2, data2.length, host, puerto);

            // AQUÍ ESTABA EL ERROR: antes se hacía ds.send(dp);
            ds.send(dp2);

        } catch (IOException e) {
            System.err.println("Error en el servidor " + e.getMessage());
        }
    }
}

