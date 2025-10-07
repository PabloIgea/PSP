package edu.thepower.u1programacion.multiproceso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase para contar vocales en un archivo de texto
 * Recibe la vocal y el archivo como argumentos del main
 */
public class U1P04ContadorVocal {

    /**
     * Método que cuenta las ocurrencias de una vocal específica en un archivo
     * @param vocal La vocal a contar (se hace comparación case-insensitive)
     * @param archivo La ruta del archivo a procesar
     */
    private void contarVocal(char vocal, String archivo) {

        int contador = 0; // Inicializa el contador de vocales

        // Abre el archivo usando try-with-resources para cerrar automáticamente
        try (BufferedReader in = new BufferedReader( new FileReader(archivo))) {

            String line;
            // Lee el archivo línea por línea hasta el final
            while ((line = in.readLine()) != null) {

                // Recorre cada carácter de la línea actual
                for (int i = 0; i < line.length(); i++) {

                    // Compara el carácter actual con la vocal buscada (ignorando mayúsculas/minúsculas)
                    if (Character.toLowerCase(line.charAt(i)) == Character.toLowerCase(vocal))
                        contador++; // Incrementa el contador si encuentra la vocal
                }
            }

        } catch (FileNotFoundException e) {
            // Maneja el error si el archivo no existe
            System.err.println("Archivo no encontrado: " + archivo);
            throw new RuntimeException(e);
        } catch (IOException e) {
            // Maneja errores de lectura del archivo
            System.err.println("Error en lectura de archivo: " + archivo);
            throw new RuntimeException(e);
        }

        // Muestra el resultado del conteo para esta vocal
        System.out.println("Vocal '" + vocal + "': " + contador);
    }

    /**
     * Método que ejecuta 5 procesos separados para contar cada vocal
     * Cada proceso es una nueva instancia de esta misma clase
     * @param vocales Array con las 5 vocales a contar
     * @param archivo Ruta del archivo a procesar
     */
    private void ejecutarContadorVocales(String[] vocales, String archivo) {
        
        System.out.println("=== Iniciando 5 procesos para contar vocales ===");
        
        // Array para almacenar los 5 procesos
        Process[] procesos = new Process[5];
        
        try {
            // Crear y lanzar un proceso para cada vocal
            for (int i = 0; i < vocales.length; i++) {
                
                System.out.println("Iniciando proceso para vocal: " + vocales[i]);
                
                // Crear ProcessBuilder para ejecutar java con esta misma clase
                // Pasa como argumentos: la vocal y el archivo
                ProcessBuilder pb = new ProcessBuilder(
                    "java", 
                    "edu.thepower.u1programacion.multiproceso.U1P04ContadorVocal",
                    vocales[i],  // Primer argumento: la vocal
                    archivo      // Segundo argumento: el archivo
                );
                
                // Redirige la salida del proceso hijo a la consola principal
                pb.inheritIO();
                
                // Inicia el proceso y lo guarda en el array
                procesos[i] = pb.start();
            }
            
            // Esperar a que terminen todos los procesos
            System.out.println("\nEsperando a que terminen todos los procesos...");
            for (int i = 0; i < procesos.length; i++) {
                if (procesos[i] != null) {
                    int exitCode = procesos[i].waitFor(); // Espera a que termine el proceso
                    System.out.println("Proceso " + (i+1) + " terminado con código: " + exitCode);
                }
            }
            
            System.out.println("\n=== Todos los procesos han terminado ===");
            
        } catch (IOException e) {
            System.err.println("Error al crear procesos: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Proceso interrumpido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Método principal - punto de entrada del programa
     * 
     * Modo 1: Si recibe 2 argumentos (vocal + archivo) -> Cuenta esa vocal específica
     * Modo 2: Si recibe 6 argumentos (5 vocales + archivo) -> Lanza 5 procesos
     * Modo 3: Sin argumentos -> Usa valores por defecto
     */
    public static void main(String[] args) {

        // Crea una instancia de la clase
        U1P04ContadorVocal test = new U1P04ContadorVocal();

        if (args.length == 2) {
            // MODO 1: Proceso individual - cuenta una vocal específica
            // Este modo se usa cuando se llama desde ejecutarContadorVocales()
            char vocal = args[0].charAt(0);  // Primer argumento: la vocal
            String archivo = args[1];        // Segundo argumento: el archivo
            
            System.out.println("[Proceso individual] Contando vocal: " + vocal);
            test.contarVocal(vocal, archivo);
            
        } else if (args.length >= 6) {
            // MODO 2: Proceso principal - lanza 5 procesos hijos
            // Extrae las 5 primeras posiciones como vocales
            String[] vocales = {args[0], args[1], args[2], args[3], args[4]};
            String archivo = args[5]; // El sexto argumento es el archivo
            
            // Ejecuta los 5 procesos concurrentes
            test.ejecutarContadorVocales(vocales, archivo);
            
        } else {
            // MODO 3: Sin argumentos suficientes - usa valores por defecto
            System.out.println("Uso del programa:");
            System.out.println("  Modo 1: java U1P04ContadorVocal <vocal> <archivo>");
            System.out.println("  Modo 2: java U1P04ContadorVocal <vocal1> <vocal2> <vocal3> <vocal4> <vocal5> <archivo>");
            System.out.println("\nEjecutando con valores por defecto (Modo 2)...");
            
            // Valores por defecto: las 5 vocales
            String[] vocales = {"a", "e", "i", "o", "u"};
            String archivo = "./resources/vocales.txt";
            
            // Ejecuta con valores por defecto
            test.ejecutarContadorVocales(vocales, archivo);
        }
    }
}