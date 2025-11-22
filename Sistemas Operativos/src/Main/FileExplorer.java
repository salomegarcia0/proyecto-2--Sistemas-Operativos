/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;
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
    
}
