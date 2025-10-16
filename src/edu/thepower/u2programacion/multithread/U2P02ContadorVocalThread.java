package edu.thepower.u2programacion.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class U2P02ContadorVocalThread implements Runnable {
    private char vocal;
    private String archivo;
    private String salida;

    public U2P02ContadorVocalThread(char vocal, String archivo, String salida) {

        this.vocal = vocal;
        this.archivo = archivo;
        this.salida = salida;

    }

    private static final Map <Character,Character> VOCALES;

    static{
        VOCALES = new HashMap();
        VOCALES.put('a','á');
        VOCALES.put('e','é');
        VOCALES.put('i','í');
        VOCALES.put('o','ó');
        VOCALES.put('u','ú');
    }

    @Override
    public void run() {

            int contador = 0;

            System.out.println("[]" +  Thread.currentThread().getName() + "] --> Iniciando cuenta vocal: " + vocal);

            try (BufferedReader in = new BufferedReader(new FileReader(archivo))) {

                String line;
                while ((line = in.readLine()) != null) {

                    line = line.toLowerCase();

                    for (int i = 0; i < line.length(); i++) {

                        if (line.charAt(i) == vocal || line.charAt(i) == VOCALES.get(vocal))
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

        System.out.println("[]" +  Thread.currentThread().getName() + "] --> Terminando cuenta vocal: " + vocal + " Numero total: " + contador);

            try (BufferedWriter bw = new BufferedWriter( new FileWriter(salida + vocal + ".txt"))){

            } catch (IOException e) {
                throw new RuntimeException(e);

            }

    }


    public static void main(String[] args) {

        final String ARCHIVO_ENTRADA = "./resources/vocales.txt";
        final String DIRECTORIO_SALIDA = "./salida/";

        List<Thread> procesos = new ArrayList<>();
        //Creacion directorio Salida
        File directorioSalida = new File("salida");
        if (directorioSalida.mkdir())
            System.out.println("El directorio de salida se ha creado satisfactoriamente");
        else {
            System.err.println("El directorio de salida ya existe, melón");
            for(File archivo : directorioSalida.listFiles()) {

                archivo.delete();

            }
        }

        for (char v : VOCALES.keySet()) {

            Thread thread = new Thread(new U2P02ContadorVocalThread(v, ARCHIVO_ENTRADA, DIRECTORIO_SALIDA));

            procesos.add(thread);

            thread.start();;

            for (Thread t : procesos) {

                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            int contador = 0;

            for (char v1 : VOCALES.keySet()) {

                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader("./salida" + v + ".txt"));
                    int numero = Integer.parseInt(br.readLine());
                    System.out.println("El número de (" + v + ") es: " + numero);
                    contador += numero;
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }catch (NumberFormatException e) {

                    System.err.println("El archivo" + "./salida" + v + ".txt" + " no contenía un número.");
                } finally {
                    try {
                        br.close();
                    }catch (IOException e) {
                        throw  new RuntimeException(e);
                    }
                }
            }

            System.out.println("");
            }

        }

    }
}
