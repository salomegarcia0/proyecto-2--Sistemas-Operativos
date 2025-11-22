/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Tipos_de_Datos.*;
import Estructuras.*;
import Main.FileExplorer;

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
    //Un booleano para solo realiza la copia de la lista una vez.
    private boolean copiaRealizada;
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
        this.listaBloques = new ListaEnlazada();
        this.copiaRealizada = false;
    }
    
    public void ejecutar(){
        if (FileExplorer.getPolitica() == TipoPolitica.FIFO){
            System.out.println("Politica FIFO");
            FIFO();
        } else if (FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
            System.out.println("Politica C-SCAN");
            C_SCAN();
        } else if (FileExplorer.getPolitica() == TipoPolitica.SSTF){
            System.out.println("Politica SSFT");
        } else if (FileExplorer.getPolitica() == TipoPolitica.SCAN)
            System.out.println("Politica SCAN");
    }
    
    public void FIFO(){
        //Primero se verifica la lista de bloques del archivo fue copiada
        
        if(copiaRealizada == false){
            ListaEnlazada copia = new ListaEnlazada();
            Nodo nodoOriginal = archivo.getBlockList().getHead();
            while (nodoOriginal != null){
                System.out.println("Ordenando");
             
            }
        }
        
        if (tipoProceso == TipoProceso.CREAR){
            System.out.println("CREAR");
            
        } else if (tipoProceso == TipoProceso.MODIFICAR){
            System.out.println("MODIFICAR");
            
        } else if (tipoProceso == TipoProceso.ELIMINAR){
            System.out.println("ELIMINARR");
            
        } else if (tipoProceso == TipoProceso.LEER){
            System.out.println("LEER");
        }
    }
    
    public void C_SCAN(){
        
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

    public ListaEnlazada getListaBloques() {
        return listaBloques;
    }

    public void setListaBloques(ListaEnlazada listaBloques) {
        this.listaBloques = listaBloques;
    }
    
    
}
