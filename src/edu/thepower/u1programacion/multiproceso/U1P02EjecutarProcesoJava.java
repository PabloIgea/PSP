package edu.thepower.u1programacion.multiproceso;

import java.io.*;

public class U1P02EjecutarProcesoJava {

    private static final String JAVA = "java";
    private static final String VERSION = "-version";

    public static void main(String[] args) {

        //Clase para ejecutar programas
        ProcessBuilder pb = new ProcessBuilder(JAVA, VERSION);
        //1.El redirect sirve para enviar la informacion
        /*pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
        pb.start()
        } catch (IOException e) {
        } */

        //2.Mediante el buffer
        /*try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }

            br = new BufferedReader( new InputStreamReader(p.getErrorStream()));
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }


        } catch (IOException e) {
            System.err.println("Error al iniciar el proceso ");
            e.printStackTrace();
        }*/

        //3. Volcar salida a fichero
        pb.redirectOutput(new File("./resources/salida.txt"));
        pb.redirectError(new File("./resources/error.txt"));

        try {
            pb.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    }

