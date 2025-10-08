package edu.thepower.u2programacion.multithread;

public class U2P0ThreatTesty {

    public static void main (String[] args) {

        System.out.println( "El nombre del hilo es: " + Thread.currentThread().getName());

        System.out.println( "El id del hilo es: " + Thread.currentThread().threadId());

        System.out.println( "La prioridad del hilo es: " + Thread.currentThread().getPriority());

        System.out.println( "El estado del hilo es: " + Thread.currentThread().getState());

        System.out.println( "El grupo del hilo es: " + Thread.currentThread().getThreadGroup());

    }

}
