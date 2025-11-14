/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Tipos_de_Datos.*;

/**
 * Clase para crear los procesos de la simualacion
 * @author salom
 */
public class PCB {

    private int procesoID;
    private String procesoNombre;
    private EstadoProceso estadoActual;
    private TipoProceso tipoProceso;
    private int PC;
    private int MAR;
    
    /*
    será la cantidad de tiempo que ha pasado en ejecucion, se iran sumando
    los tiempos del ciclo de reloj para ello, todo en ms
    */
    private long tiempoEnCPU;     

    public PCB(int procesoID, String procesoNombre) {
        this.procesoID = procesoID;
        this.procesoNombre = procesoNombre;
        this.PC = PC;
        this.MAR = MAR;
        this.tiempoEnCPU = tiempoEnCPU;
        this.tipoProceso = tipoProceso;
    }
    
    public void bloquear(){
        if (estadoActual == EstadoProceso.LISTO){
            estadoActual = EstadoProceso.BLOQUEADO;
            System.out.println(procesoNombre + "suspendido");
        }
    }
    
    public void reanudarBloqueado(){
        if (estadoActual == EstadoProceso.BLOQUEADO){
            estadoActual = EstadoProceso.LISTO;
        }
    }
    
    public int getProcesoID() {
        return procesoID;
    }

    public String getProcesoNombre() {
        return procesoNombre;
    }

    public void setProcesoNombre(String procesoNombre) {
        this.procesoNombre = procesoNombre;
    }

    public EstadoProceso getEstadoActual() {
        return estadoActual;
    }

    public int getPC() {
        return PC;
    }

    public int getMAR() {
        return MAR;
    }

    public TipoProceso getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(TipoProceso tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public long getTiempoEnCPU() {
        return tiempoEnCPU;
    }

    public void setTiempoEnCPU(long tiempoEnCPU) {
        this.tiempoEnCPU = tiempoEnCPU;
    }
    
    
    
}
