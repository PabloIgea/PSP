package edu.thepower.u3comunicacionEnRed;
import com.sun.source.tree.Tree;

import java.util.*;


public class U3P07ReasignacionPuestos {

    private static String[] nombres = {"Genesis", "Pablo", "Luisa", "Alejandro",
            "Sergio G.", "Mario", "Astrid", "Esteban", "Víctor", "Claudia", "Sergio M.", "Marcos",
            "David", "Sebastián", "Aarón", "Johan"};
    private static List<String> alumnos = Arrays.asList(nombres);
    private static List<Integer> puestos = new ArrayList<>();
    private static Map<Integer, String> asignaciones = new TreeMap<>();
    private static final int MAX_ALUMNOS = 16;

    public static void main(String[] args) {
        for(int i = 1; i <= MAX_ALUMNOS; i++){
            puestos.add(i);
        }

        System.out.println("Reasignando puestos");
        Collections.shuffle(alumnos);
        Collections.shuffle(puestos);
        System.out.println("Resultados del sorteo:");

        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < MAX_ALUMNOS; i++){
            System.out.print("El puesto para el alumno " + alumnos.get(i) + " es... (pulse una tecla): ");
            sc.nextLine();
            System.out.print(puestos.get(i) + "\n");
            asignaciones.put(puestos.get(i), alumnos.get(i));
        }
        System.out.println("Sorteo finalizado, este es el resultado:");

        for(Map.Entry e : asignaciones.entrySet()){
            System.out.println(e.getKey() + ":" + e.getValue() );
        }
        //asignaciones.entrySet().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue() ));
    }
}
