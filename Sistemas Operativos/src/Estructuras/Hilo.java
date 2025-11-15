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
    private String nombreProceso;
    private int MARProceso;
    private int tiempoSimulado;

    public Hilo(String nombreProceso,int MARProceso, int tiempoSimulado) {
        this.nombreProceso = nombreProceso;
        this.MARProceso = MARProceso;
        this.tiempoSimulado = tiempoSimulado;
    }

    @Override
    //necesito luego de cada thread aumenta un contador de instrucciiones que se han llevado actualmente, verificar si el procesos es IO que ocurra una
    //una interrumpcion para eso requiero el reloj en global
    public void run() {
        try {
            System.out.println("Proceso " + nombreProceso + " EJECUTANDO INSTRUCCION: " + MARProceso);
            Thread.sleep(tiempoSimulado);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }
}

