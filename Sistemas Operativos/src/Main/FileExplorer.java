/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;
import java.util.Random;
import Clases.*;
import Estructuras.*;
import Tipos_de_Datos.*;

/**
 *
 * @author pjroj
 */
public class FileExplorer {
    private static SD SD;
    private static int sizeBloque = 100; //el valor del "tamaño de los bloques, es base a esto que es simularemos la "memoria disponible" en el SD
    private static Cola listo;
    private static Cola colaListos;
    private static PCB procesoEnEjecucion;
    private static Cola colaBloqueados;
    private static Cola colaTerminado;
    /*
    Variables booleanas para saber la politica que esta activa;
    */
    private static TipoPolitica politica = TipoPolitica.FIFO ; //inicialmente sera fifo
    
    //para definir el tiempo que dura una lectura en ms (queremos inicialmente 1000ms = 1seg)
    private static int ciclo_reloj = 1000;
    //para saber cuantas lecturas se han completado hasta el momento
    //de modo que cuando llegue a a ser igual que el ioExceptionCycle, el proceso se pase a bloqueado
    private static int countLecturas;
    //booleano para saber si el proceso en ejecucion se bloqueo
    private static boolean procesoBloqueado;
    //ioExceptionCycle: cada cuantas lecturas ocurre una interrupcion de E/S
    //Cada 15 lecturas (asi lo definimos inicialmente)
    private static int ioExceptionCycle = 15;
    //cuanto tiempo estara bloqueado el proceso cuando ocurre una operacion de E/S
    //Bloqueado durante 5 lecturas (asi lo definimos inicialmente)
    private static int ioCompletionTime = 5;
    //Reloj global del sistema
    private static long reloj_global;
    
    /*
    Una vez creado un proceso se encola a la cola de listos y se le coloca el estado LISTO
    */
    public static void agregarProcesoListo(PCB proceso){
        //Primero se cambia el estado del proceso a 
        proceso.setEstadoActual(EstadoProceso.LISTO);
        colaListos.enColar(proceso);
        
    }
    
    /*
    Se seleccionaria un proceso
    */
    public static void seleccionarProceso(){
        //si la cola de listos no esta vacio se selecciona un proceso en base a la politica
        if(!colaListos.isEmpty()){
            
            /*
            El primer proceso en entrar es el primero en ser atendido
            */
            if (politica == TipoPolitica.FIFO){
                FileExplorer.setProcesoEnEjecucion(colaListos.desColarInicio());
                
            /*
            El ultimo proceso en entrar es el primero en ser atendido    
            */    
            } else if (politica == TipoPolitica.LIFO){
                //se desencola el ultimo proceso que llego
                FileExplorer.setProcesoEnEjecucion(colaListos.desColarFinal());
                
            /*
            Esta politica elige un proceso de la cola de Listos aleatoriamente  
            */   
            } else if (politica == TipoPolitica.PA){
                Random rand = new Random();
                //genera un valor random entre 0 y el tamaño de la lista menos 1
                int numero = rand.nextInt(colaListos.getSize());
                //se busca por index del numero que obtuvimos anteriormente y desencolamos de la cola de listos
                //el proceso que este en esa posicion para ser el nuevo proceso en ejecutar
                FileExplorer.setProcesoEnEjecucion(colaListos.desColarIntermedio(numero));
                
                
            } else if (politica == TipoPolitica.SSTF){
                
            } else if (politica == TipoPolitica.SCAN){
                
            } else if (politica == TipoPolitica.C_SCAN){
                
            }
            
            procesoEnEjecucion.setEstadoActual(EstadoProceso.EJECUTANDO);

        } else {
            FileExplorer.setProcesoEnEjecucion(null);
        }
    }
        

    public static int getSizeBloque() {
        return sizeBloque;
    }

    public static void setSizeBloque(int sizeBloque) {
        FileExplorer.sizeBloque = sizeBloque;
    }

    public static int getCiclo_reloj() {
        return ciclo_reloj;
    }

    public static void setCiclo_reloj(int ciclo_reloj) {
        FileExplorer.ciclo_reloj = ciclo_reloj;
    }

    public static int getCountLecturas() {
        return countLecturas;
    }

    public static void setCountLecturas(int countLecturas) {
        FileExplorer.countLecturas = countLecturas;
    }

    public static int getIoCompletionTime() {
        return ioCompletionTime;
    }

    public static void setIoCompletionTime(int ioCompletionTime) {
        FileExplorer.ioCompletionTime = ioCompletionTime;
    }

    public static int getIoExceptionCycle() {
        return ioExceptionCycle;
    }

    public static void setIoExceptionCycle(int ioExceptionCycle) {
        FileExplorer.ioExceptionCycle = ioExceptionCycle;
    }
    
    public static SD getSD() {
        return SD;
    }

    public static void setSD(SD SD) {
        FileExplorer.SD = SD;
    }

    public static boolean isProcesoBloqueado() {
        return procesoBloqueado;
    }

    public static void setProcesoBloqueado(boolean procesoBloqueado) {
        FileExplorer.procesoBloqueado = procesoBloqueado;
    }

    public static TipoPolitica getPolitica() {
        return politica;
    }

    public static void setPolitica(TipoPolitica politica) {
        FileExplorer.politica = politica;
    }

    public static Cola getListo() {
        return listo;
    }

    public static void setListo(Cola listo) {
        FileExplorer.listo = listo;
    }

    public static Cola getColaListos() {
        return colaListos;
    }

    public static void setColaListos(Cola colaListos) {
        FileExplorer.colaListos = colaListos;
    }

    public static PCB getProcesoEnEjecucion() {
        return procesoEnEjecucion;
    }

    public static void setProcesoEnEjecucion(PCB procesoEnEjecucion) {
        FileExplorer.procesoEnEjecucion = procesoEnEjecucion;
    }

    public static Cola getColaBloqueados() {
        return colaBloqueados;
    }

    public static void setColaBloqueados(Cola colaBloqueados) {
        FileExplorer.colaBloqueados = colaBloqueados;
    }

    public static Cola getColaTerminado() {
        return colaTerminado;
    }

    public static void setColaTerminado(Cola colaTerminado) {
        FileExplorer.colaTerminado = colaTerminado;
    }

    public static long getReloj_global() {
        return reloj_global;
    }

    public static void setReloj_global(long reloj_global) {
        FileExplorer.reloj_global = reloj_global;
    }
    
    
}
