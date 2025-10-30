package edu.thepower.u2programacion.multithread;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Este programa cuenta, de forma concurrente, cuántas veces aparece cada vocal
 * (a, e, i, o, u) en un archivo de texto de entrada. Cada vocal se procesa en
 * su propio hilo (Runnable) y escribe el resultado en un archivo de salida
 * independiente (p.ej., salida/a.txt). Luego, el hilo principal espera a que
 * terminen todos los hilos (join), lee los archivos generados y suma el total
 * de vocales encontradas.
 *
 * Decisiones de diseño:
 * - Un hilo por vocal: permite paralelizar el conteo y practicar sincronización
 *   mediante join sin necesidad de compartir memoria.
 * - Se contemplan vocales acentuadas mapeándolas a su vocal base (á, é, í, ó, ú).
 * - Salida por archivo: cada hilo escribe su propio resultado, lo que evita
 *   condiciones de carrera en memoria compartida.
 */
public class U2P02ContadorVocalThread implements Runnable {

    /*
     * ATRIBUTOS DE INSTANCIA (DOMINIO Y FUNCIÓN)
     * ------------------------------------------
     * - archivo (String): ruta al archivo de texto que se va a leer. Especifica la
     *   fuente de datos para el conteo. Alcance: instancia (cada Runnable sabe qué leer).
     * - salida (String): directorio de salida donde se escribirá el resultado de este hilo.
     *   Permite separar responsabilidades: cada hilo produce un archivo propio.
     * - vocal (char): vocal objetivo que este hilo debe contar. Concreta el "subproblema"
     *   que resuelve el hilo (divide y vencerás).
     */
    private String archivo;
    private String salida;
    private char vocal;

    /*
     * VOCALES (MAPA ESTÁTICO)
     * -----------------------
     * Mapa de vocal base -> vocal acentuada equivalente. Permite contar tanto la versión
     * sin tilde como con tilde de la misma vocal (p.ej., 'a' y 'á').
     * - static final: compartido por todas las instancias, inmutable en referencia.
     * - Tipo (Map<Character, Character>): expresa relación 1 a 1 entre base y acentuada.
     */
    private static final Map<Character, Character> VOCALES;

    static {
        VOCALES = new HashMap();              // Estructura para mapear vocal base a su versión acentuada
        VOCALES.put('a', 'á');                // 'a' <-> 'á'
        VOCALES.put('e', 'é');
        VOCALES.put('i', 'í');
        VOCALES.put('o', 'ó');
        VOCALES.put('u', 'ú');
    }

    /*
     * CONSTRUCTOR (CONFIGURA EL SUBPROBLEMA DEL HILO)
     * -----------------------------------------------
     * Recibe:
     * - vocal: qué vocal contará este Runnable.
     * - archivo: de dónde leer el texto.
     * - salida: directorio destino para escribir el conteo.
     * Guarda estos parámetros para que run() pueda realizar su trabajo sin depender del exterior.
     */
    public U2P02ContadorVocalThread(char vocal, String archivo, String salida) {

        this.vocal = vocal;       // Variable de dominio: objetivo de conteo de este hilo
        this.archivo = archivo;   // Fuente de datos (ruta del archivo a procesar)
        this.salida = salida;     // Directorio donde se escribirá el resultado para esta vocal
    }

    /*
     * LÓGICA DEL HILO (run)
     * ---------------------
     * Lee el archivo línea a línea (en minúsculas para normalizar), recorre cada carácter
     * y si coincide con la vocal objetivo (con o sin tilde), incrementa un contador.
     * Al finalizar, escribe el resultado en un archivo con nombre {salida}{vocal}.txt
     * y muestra logs de inicio/fin con el nombre del hilo.
     */
    @Override
    public void run(){

        int contador = 0; // Contador local al hilo: no hay datos compartidos -> evita sincronización explícita
        System.out.println("[" + Thread.currentThread().getName() + "] iniciando cuenta vocal " + vocal);

        // try-with-resources: asegura el cierre del lector incluso ante excepciones
        try (BufferedReader in = new BufferedReader(new FileReader(archivo))){
            String line = "";

            // Mientras haya líneas por leer (readLine() != null)
            while ((line = in.readLine()) != null){

                line = line.toLowerCase(); // Normaliza a minúsculas para comparar con el mapa de vocales base

                // Recorre carácter a carácter la línea actual
                for(int i = 0; i < line.length(); i++){
                    // Si el carácter es la vocal base o su versión acentuada, suma 1
                    if(line.charAt(i) == vocal || line.charAt(i) == VOCALES.get(vocal)){
                        contador++;
                    }
                }
            }

        }catch (FileNotFoundException e){
            System.err.println("Archivo "+ archivo+ " no encontrado");
            throw new RuntimeException();
        }catch (IOException e){
            System.err.println("Error en lectura de archivo " + archivo);
            throw new RuntimeException();
        }
        System.out.println("[" + Thread.currentThread().getName() + "] finalizada cuenta vocal " + vocal);

        // Escribe el resultado del conteo de este hilo a su archivo dedicado (p.ej., salida/a.txt)
        try (BufferedWriter out = new BufferedWriter(new FileWriter(salida + vocal + ".txt"))) {

            out.write(String.valueOf(contador)); // Persistimos un solo número (conteo)

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo " + vocal + ".txt");
        }
    }

    public static void main(String[] args) {

        /*
         * BLOQUE: PREPARACIÓN DEL ENTORNO DE EJECUCIÓN
         * --------------------------------------------
         * - threads: lista para guardar referencias a los hilos creados. Necesaria para
         *   poder invocar join() después y esperar a que terminen (sin esta lista, no podríamos).
         * - ARCHIVO_ENTRADA: ruta al texto que se analizará.
         * - DIR_SALIDA: carpeta donde cada hilo escribirá su conteo (un archivo por vocal).
         */
        // COLECCION PARA ALMACENAR LAS REFERENCIAS A LOS THREADS
        List<Thread> threads = new ArrayList();

        final String ARCHIVO_ENTRADA = "./resources/vocales.txt"; // Constante de configuración de entrada
        final String DIR_SALIDA = "./salida/";                    // Constante de configuración de salida

        /*
         * BLOQUE: CREACIÓN / LIMPIEZA DEL DIRECTORIO DE SALIDA
         * ----------------------------------------------------
         * Asegura que la carpeta 'salida' exista y esté limpia para esta ejecución:
         * - Si no existe, la crea.
         * - Si ya existe, borra su contenido para evitar mezclar resultados antiguos.
         */
        // Creacion del directorio salida
        File dirSalida = new File("salida");

        if(dirSalida.mkdir()) {
            System.out.println("El directorio de salida se ha creado correctamente");
        } else {
            System.err.println("El directorio de salida ya existe");
            for (File file : dirSalida.listFiles()) {
                file.delete(); // Eliminamos ficheros previos para no contaminar resultados
            }
        }

        /*
         * BLOQUE: LANZAMIENTO DE HILOS (UN HILO POR VOCAL)
         * ------------------------------------------------
         * Por cada vocal definida en el mapa VOCALES (claves: a,e,i,o,u):
         * - Se crea un Thread con un Runnable U2P02ContadorVocalThread parametrizado
         *   con esa vocal y rutas de entrada/salida.
         * - Se guarda la referencia del hilo en 'threads'.
         * - Se inicia el hilo con start() para ejecutar su run() en paralelo.
         */
        for(char v : VOCALES.keySet()){
            Thread hilo = new Thread(new U2P02ContadorVocalThread(v, ARCHIVO_ENTRADA, DIR_SALIDA));

            // AÑADIMOS CADA HILO EN LA COLECCIÓN
            threads.add(hilo);

            hilo.start(); // Arranque asíncrono: la JVM planifica su ejecución concurrente
        }

        /*
         * BLOQUE: SINCRONIZACIÓN (ESPERAR A QUE ACABEN LOS HILOS)
         * -------------------------------------------------------
         * Antes de procesar la salida, es imprescindible esperar a que todos los hilos
         * terminen de escribir sus archivos. join() es un "await": bloquea el hilo
         * principal hasta que el hilo invocado finaliza.
         */
        // PROCESAMOS LA SALIDA DE LOS THREADS PARA CONTAR EL NUMERO TOTAL DE VOCALES
        // ANTES DE EJECUTAR ESTA PARTE, HAY QUE ESPERAR A QUE TERMINEN LOS PROCESOS DE CREACION DE ARCHIVOS
        for (Thread t : threads){
            try {
                // METODO JOIN: METODO "AWAIT", ESPERA A QUE TERMINE CADA THEARD PARA TERMINAR EL BUCLE
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        /*
         * BLOQUE: AGREGACIÓN FINAL (LECTURA DE RESULTADOS Y SUMA TOTAL)
         * -------------------------------------------------------------
         * Lee los archivos generados (uno por vocal), convierte el contenido a número
         * y acumula el total. También informa del conteo por vocal para trazabilidad.
         */
        int contador = 0; // Acumulador global de todas las vocales

        for (char v : VOCALES.keySet()) {

            BufferedReader br = null; // Recurso para leer el archivo de resultado de cada vocal

            try {
                br = new BufferedReader(new FileReader("./salida/" + v + ".txt"));
                int numero = Integer.parseInt(br.readLine()); // Cada archivo contiene un solo entero
                System.out.println("El numero de (" + v + ") es: " + numero);
                contador += numero; // Agregamos al total
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException e) {
                System.err.println("El archivo" + "./salida/" + v + ".txt" + " no contenia un numero");
            } finally {
                try {
                    br.close(); // Cierre explícito del recurso abierto arriba
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        // Informe final tras la agregación de todos los resultados
        System.out.println("El numero total de vocales es: " + contador);
    }
}
