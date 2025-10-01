package edu.thepower.u1programacion.multiproceso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class U1P04ContadorVocal {

    private void contarVocal(char vocal, String archivo) {

        int contador = 0;

        try (BufferedReader in = new BufferedReader( new FileReader(archivo))) {

            String line;
            while ((line = in.readLine()) != null) {

                for (int i = 0; i < line.length(); i++) {

                    if (line.charAt(i) == vocal)
                        contador++;
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + archivo);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error en lectura de archivo: " + archivo);
            throw new RuntimeException(e);
        }

        System.out.println(contador);

    }


    public static void main(String[] args) {

        U1P04ContadorVocal test = new U1P04ContadorVocal();

        test.contarVocal('a', "./resources/vocales.txt");
    }
    // Que las vocales salgan de los argumentos desde eñ array de strings del main --Como con los números--
    //Crear ejecutarContadorVocales con 5 procesos

}
