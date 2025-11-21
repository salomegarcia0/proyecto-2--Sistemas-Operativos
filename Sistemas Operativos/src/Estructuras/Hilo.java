/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import main.*;
/**
 *
 * @author pjroj
 */
public class Hilo extends Thread {
    private int tiempoSimulado;

    public Hilo( int tiempoSimulado) {
        this.tiempoSimulado = tiempoSimulado;
    }

    @Override
    //necesito luego de cada thread aumenta un contador de instrucciOnes que se han llevado actualmente
    public void run() {
        try {
            //System.out.println("Proceso " + nombreProceso + " EJECUTANDO BLOQUE: " + MARProceso);
            System.out.println("Ejecutando hilo");
            Thread.sleep(tiempoSimulado);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }
}

