/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;
import Clases.*;
import Estructuras.*;

/**
 *
 * @author pjroj
 */
public class FileExplorer {
    private static SD SD;
    private static Cola listo;
    private static Cola colaListos;
    private static PCB procesoEnEjecucion;
    private static Cola colaBloqueados;
    private static Cola colaTerminado;
    //para definir el tiempo que dura una lectura en ms (queremos inicialmente 1000ms = 1seg)
    private static int ciclo_reloj = 1000;
    //para saber cuantas lecturas se han completado hasta el momento
    //de modo que cuando llegue a a ser igual que el ioExceptionCycle, el proceso se pase a bloqueado
    private static int countInstrucciones;
    //ioExceptionCycle: cada cuantas lecturas ocurre una interrupcion de E/S
    //Cada 10 kecturas (asi lo definimos inicialmente)
    private static int ioExceptionCycle = 10;
    //cuanto tiempo estara bloqueado el proceso cuando ocurre una operacion de E/S
    //Bloqueado durante 5 lecturas (asi lo definimos inicialmente)
    private static int ioCompletionTime = 5;
    //Reloj global del sistema
    private static long reloj_global;
    
}
