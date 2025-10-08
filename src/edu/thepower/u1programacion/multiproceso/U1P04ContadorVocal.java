package edu.thepower.u1programacion.multiproceso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class U1P04ContadorVocal {

    private static final Map <Character,Character> VOCALES;

    static{
        VOCALES = new HashMap();
        VOCALES.put('a','á');
        VOCALES.put('e','é');
        VOCALES.put('i','í');
        VOCALES.put('o','ó');
        VOCALES.put('u','ú');
    }



    private void contarVocal(char vocal, String archivo) {

        int contador = 0;

        try (BufferedReader in = new BufferedReader( new FileReader(archivo))) {

            String line;
            while ((line = in.readLine()) != null) {

                line = line.toLowerCase();

                for (int i = 0; i < line.length(); i++) {

                    if (line.charAt(i) == vocal || line.charAt(i) == VOCALES.get(vocal) )
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

        test.contarVocal(args[0].charAt(0),args[1]);
    }
    // Que las vocales salgan de los argumentos desde eñ array de strings del main --Como con los números--
    //Crear ejecutarContadorVocales con 5 procesos

}
