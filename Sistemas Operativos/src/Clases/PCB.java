/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Lector.CabezalReversa;
import Lector.CabezalNormal;
import javax.swing.JOptionPane;
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
    //El nombreArchivo tendra que setearse como el nombre del archivo cuando se realice una operacion CRUD, si es de modificar este 
    //sera diferente al nombre del archivo ya que este se seteara luego.
    private String nombreArchivo;
    //index del bloque donde se realizara una operacion CRUD
    private int bloque;
    //El archivo al cual se le hará la operacion CRUD
    private Archivo archivo;
    private EstadoProceso estadoActual;
    //CRUD
    private TipoProceso tipoProceso;  
    /*
    será la cantidad de tiempo que ha pasado en ejecucion, se iran sumando
    los tiempos del ciclo de reloj para ello, todo en ms
    */
    private long tiempoEnCPU;     

    /*
    String nombreArchivo (se cambia para la operacion modificar, de resto sera el mismo nombre)
    int bloque (se coloca -1 cuando cuando se hace la operacion de insertar ya que es el unico caso donde no se puede tener un bloque asignado)
    */
    public PCB(int procesoID, String procesoNombre, String nombreArchivo, int bloque, Archivo archivo, TipoProceso tipoProceso) {
        this.procesoID = procesoID;
        this.procesoNombre = procesoNombre;
        this.nombreArchivo = nombreArchivo;
        this.bloque = bloque;
        this.archivo = archivo;
        this.tipoProceso = tipoProceso;
        this.tiempoEnCPU = 0;
    }
    
    public void ejecutar(){
        CabezalNormal cabezalNormal = new CabezalNormal();
        CabezalReversa cabezalReversa = new CabezalReversa();
        
        if (tipoProceso == TipoProceso.CREAR){
            System.out.println("CREAR");
            long inicio = System.currentTimeMillis();
            //true = indica que el proceso ya realizo la operacion
            //false = indica que el proceso no realizo la operacion
            boolean operacionCompletada = false;
            if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                operacionCompletada = cabezalNormal.insertInfo(archivo);
            } else {
                operacionCompletada = cabezalReversa.insertInfo(archivo);
            }
                    
            long fin = System.currentTimeMillis();
            long tiempoSimulado = (int)(fin - inicio);//tiempo simulado
                    
            //se le suma al tiempoEnCPU el tiempo en ms del ciclo completado
            tiempoEnCPU = tiempoEnCPU + tiempoSimulado;
            
            /*
            -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
            acontinuacion
            -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
            -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
            -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
            */
            if (FileExplorer.isProcesoBloqueado() == true ){
                if(operacionCompletada == true){
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Insersion de bloque nuevo completada");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false) {
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                    System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.BLOQUEADO;
                    
                }
            } else if (FileExplorer.isProcesoBloqueado() == false ){
                if(operacionCompletada == true){
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Insersion de bloque nuevo completada");
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false){
                    System.out.println("a llorar 1");
                }
            }
            
            
        } else if (tipoProceso == TipoProceso.ELIMINAR){
            System.out.println("ELIMINAR");
            long inicio = System.currentTimeMillis();
            //true = indica que el proceso ya realizo la operacion
            //false = indica que el proceso no realizo la operacion
            boolean operacionCompletada = false;
            if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                operacionCompletada = cabezalNormal.eliminarInfo(bloque,archivo);
            } else {
                operacionCompletada = cabezalReversa.eliminarInfo(bloque,archivo);
            }
            
            long fin = System.currentTimeMillis();
            long tiempoSimulado = (int)(fin - inicio);//tiempo simulado
                    
            //se le suma al tiempoEnCPU el tiempo en ms del ciclo completado
            tiempoEnCPU = tiempoEnCPU + tiempoSimulado;
            
            /*
            -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
            acontinuacion
            -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
            -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
            -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
            */
            if (FileExplorer.isProcesoBloqueado() == true ){
                if(operacionCompletada == true){
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Eliminacion de bloque completada");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false) {
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                    System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.BLOQUEADO;
                }
            } else if (FileExplorer.isProcesoBloqueado() == false ){
                if(operacionCompletada == true){
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Eliminacion de bloque completada");
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false){
                    System.out.println("a llorar 2");
                }
            }
        } else if (tipoProceso == TipoProceso.LEER){
            System.out.println("LEER");
            long inicio = System.currentTimeMillis();
            //true = indica que el proceso ya realizo la operacion
            //false = indica que el proceso no realizo la operacion
            boolean operacionCompletada = false;
            if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                operacionCompletada = cabezalNormal.leerInfo(bloque);
            } else {
                operacionCompletada = cabezalReversa.leerInfo(bloque);
            }    
            
            long fin = System.currentTimeMillis();
            long tiempoSimulado = (int)(fin - inicio);//tiempo simulado
                    
            //se le suma al tiempoEnCPU el tiempo en ms del ciclo completado
            tiempoEnCPU = tiempoEnCPU + tiempoSimulado;
            
            /*
            -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
            acontinuacion
            -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
            -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
            -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
            */
            if (FileExplorer.isProcesoBloqueado() == true ){
                if(operacionCompletada == true){
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Lectura de bloque completada");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false) {
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                    System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.BLOQUEADO;
                }
            } else if (FileExplorer.isProcesoBloqueado() == false ){
                if(operacionCompletada == true){
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("Lectura de bloque completada");
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false){
                    System.out.println("a llorar 3");
                }
            }
            
        } else if (tipoProceso == TipoProceso.MODIFICAR)
            System.out.println("MODIFICAR");
            long inicio = System.currentTimeMillis();
            //true = indica que el proceso ya realizo la operacion
            //false = indica que el proceso no realizo la operacion
            boolean operacionCompletada = false;
            if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                operacionCompletada = cabezalNormal.modificarInfo(bloque, nombreArchivo);
            } else {
                operacionCompletada = cabezalReversa.modificarInfo(bloque, nombreArchivo);
            }            
            long fin = System.currentTimeMillis();
            long tiempoSimulado = (int)(fin - inicio);//tiempo simulado
                    
            //se le suma al tiempoEnCPU el tiempo en ms del ciclo completado
            tiempoEnCPU = tiempoEnCPU + tiempoSimulado;
            
            /*
            -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
            acontinuacion
            -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
            -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
            -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
            */
            if (FileExplorer.isProcesoBloqueado() == true ){
                if(operacionCompletada == true){
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("La modificacion del bloque de '" + archivo.getName() + "' a '" + nombreArchivo + "' se a completado");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false) {
                    System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                    System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                    //se setea el ProcesoBloqeuado a false
                    FileExplorer.setProcesoBloqueado(false);
                    //se cambia el estado actual del proceso a Bloqueado
                    estadoActual = EstadoProceso.BLOQUEADO;
                }
            } else if (FileExplorer.isProcesoBloqueado() == false ){
                if(operacionCompletada == true){
                    System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                    System.out.println("La modificacion del bloque de '" + archivo.getName() + "' a '" + nombreArchivo + "' se a completado");
                    estadoActual = EstadoProceso.TERMINADO;
                } else if (operacionCompletada == false){
                    System.out.println("a llorar 4");
                }
            }
    }  
    
//    public boolean haTerminadoCrear(){
//        return archivo.getSize() == archivo.getBlockList().getSize();
//    }
//    
//    public boolean haTerminado(){
//        return listaBloques.isEmpty();
//    }
    
    public void bloquear(){
        if (estadoActual == EstadoProceso.LISTO){
            estadoActual = EstadoProceso.BLOQUEADO;
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
