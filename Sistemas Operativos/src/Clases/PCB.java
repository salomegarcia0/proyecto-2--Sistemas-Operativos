/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Tipos_de_Datos.*;
import Estructuras.*;

/**
 * Clase para crear los procesos de la simualacion
 * @author salom
 */
public class PCB {

    private int procesoID;
    private String procesoNombre;
    //El archivo al cual se le hará la operacion CRUD
    private Archivo archivo;
    private EstadoProceso estadoActual;
    //CRUD
    private TipoProceso tipoProceso;
    /*
    SOLO APLICA PARA ELIMINAR, MODIFICAR Y LEER
    Será una especie de copia de lista de bloques del archivo, de tal forma de que sea mas facil a la hora de ordenar dicha lista en el caso de politicas SSTF Y SCAN donde se busca
    */
    private ListaEnlazada listaBloques;
    //private int PC;
    private int MAR;
    
    /*
    será la cantidad de tiempo que ha pasado en ejecucion, se iran sumando
    los tiempos del ciclo de reloj para ello, todo en ms
    */
    private long tiempoEnCPU;     

    public PCB(int procesoID, String procesoNombre, Archivo archivo, TipoProceso tipoProceso) {
        this.procesoID = procesoID;
        this.procesoNombre = procesoNombre;
        this.archivo = archivo;
        this.tipoProceso = tipoProceso;
        //this.PC = PC;
        //este valor ira aumentando de tal forma que podemos saber cuando bloques se han leido hasta el momento del archivo completo, importante
        //sobretodo para saber en las operaciones de insercion cuandos bloques faltan insertar en SD
        this.MAR = 0; //numero de lectura de bloques actuales por asi decirlo
        this.tiempoEnCPU = 0;
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

    public Archivo getArchivo() {
        return archivo;
    }

    public void setArchivo(Archivo archivo) {
        this.archivo = archivo;
    }
    
    
}
