/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Lector.CabezalReversa;
import Lector.CabezalReversaCompleto;
import Lector.CabezalNormal;
import javax.swing.JOptionPane;
import Tipos_de_Datos.*;
import Estructuras.*;
import Main.FileExplorer;
import java.util.concurrent.ThreadLocalRandom;

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
//    private int bloque;
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
    /*
    será la cantidad de tiempo que ha pasado en ejecucion, se iran sumando
    los tiempos del ciclo de reloj para ello, todo en ms
    */
    private long tiempoEnCPU;     
    
    private static int contadorID = 1;
   
    /*
    String nombreArchivo (es el nuevo nombre del archivo,se cambia para la operacion modificar, de resto sera el mismo nombre del archivo)
    */
    public PCB(String procesoNombre, String nombreArchivo, Archivo archivo, TipoProceso tipoProceso) {
        this.procesoID = contadorID++;
        this.procesoNombre = procesoNombre;
        this.nombreArchivo = nombreArchivo;
        this.archivo = archivo;
        this.tipoProceso = tipoProceso;
        this.tiempoEnCPU = 0;
    }
    
    public void ejecutar(){
        CabezalNormal cabezalNormal = new CabezalNormal();
        CabezalReversa cabezalReversa = new CabezalReversa();
        CabezalReversaCompleto cabezalReversaCompleto = new CabezalReversaCompleto();
        
        
        if (tipoProceso == TipoProceso.CREAR){
            
            boolean stop = false;
            while(stop != true){
                System.out.println("CREAR");
                
                long inicio = System.currentTimeMillis();
                //true = indica que el proceso ya realizo la operacion
                //false = indica que el proceso no realizo la operacion
                boolean operacionCompletada = false;

                //para C_SCAN
                if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                    operacionCompletada = cabezalNormal.insertInfo(archivo);
                //para SCAN
                } else if (FileExplorer.getPolitica() == TipoPolitica.SCAN){
                    operacionCompletada = cabezalReversaCompleto.insertInfo(archivo);
                //para FIFO,LIFO Y SSTF
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
                        System.out.println("Insersion de bloque nuevo completada");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se verifica si es tamaño de bloques del archivo coincide con el tamaño de la lista de bloques del archivos
                        if(archivo.getSize() == archivo.getBlockList().getSize()){
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra un mensaje de que se completo la creacion
                            archivo.aplicarCreacion();
                            //se cambia el estado actual del proceso a Terminado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la insercion por completo de los archivo en el SD
                            stop = true;
                        //si es tamaño de bloques del archivo no coincide con el tamaño de la lista de bloques del archivos se bloquea
                        } else {
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                            System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.BLOQUEADO;
                            //se detiene el PCB ya que no se completo la insercion por completo de la lista de bloques del archivo
                            stop = true;
                        }

                    } else if (operacionCompletada == false) {
                        System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                        System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se cambia el estado actual del proceso a Bloqueado
                        estadoActual = EstadoProceso.BLOQUEADO;
                        //se detiene el PCB ya que no se completo la insercion por completo de la lista de bloques del archivo
                        stop = true;

                    }
                    
                } else if (FileExplorer.isProcesoBloqueado() == false ){
                    if(operacionCompletada == true){
                        System.out.println("Insersion de bloque nuevo completada");
                        //se verifica si es tamaño de bloques del archivo coincide con el tamaño de la lista de bloques del archivos
                        if(archivo.getSize() == archivo.getBlockList().getSize()){
                            System.out.println("El proceso ya termino por completo");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra un mensaje de que se completo la creacion
                            archivo.aplicarCreacion();
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la insercion por completo de los archivo en el SD
                            stop = true;
                        }
                        
                    } else if (operacionCompletada == false){
                        System.out.println("a llorar 1");
                    }
                }
            }
            
            
        } else if (tipoProceso == TipoProceso.ELIMINAR){
            
            boolean stop = false;
            while(stop != true){
                System.out.println("ELIMINAR");
                //pediremos la cabeza del ListaBloques que es una copia de la lista del archivo
                int index = (int) listaBloques.getHead().getElement();
                System.out.println("Bloque index: " + index);
                
                long inicio = System.currentTimeMillis();
                //true = indica que el proceso ya realizo la operacion
                //false = indica que el proceso no realizo la operacion
                boolean operacionCompletada = false;          
                
                //para C_SCAN
                if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                    operacionCompletada = cabezalNormal.eliminarInfo(index,archivo);
                //para SCAN
                } else if (FileExplorer.getPolitica() == TipoPolitica.SCAN){
                    operacionCompletada = cabezalReversaCompleto.eliminarInfo(index,archivo);
                //para FIFO,LIFO Y SSTF
                } else {
                    operacionCompletada = cabezalReversa.eliminarInfo(index,archivo);
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
                        System.out.println("Eliminacion de bloque completada");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        
                        //se elimina la cabeza de la lista copia de bloques
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por eliminar");
                        listaBloques.print();
                        
                        //verifica si la lista de bloques del archivo esta vacio
                        if(archivo.getBlockList().isEmpty() == true){
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra el mensaje de que se completo la eliminacion
                            archivo.aplicarEliminar();
                            
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.TERMINADO;
                            //termina el PCB porque completo la eliminacion
                            stop = true;
                            
                        } else{
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                            System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.BLOQUEADO;
                            //se detiene el PCB porque no completo la eliminacion
                            stop = true;
                        }
                        
                        
                    } else if (operacionCompletada == false) {
                        System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                        System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se cambia el estado actual del proceso a Bloqueado
                        estadoActual = EstadoProceso.BLOQUEADO;
                        //se detiene el PCB porque no completo la eliminacion
                        stop = true;
                    }
                    
                } else if (FileExplorer.isProcesoBloqueado() == false ){
                    if(operacionCompletada == true){
                        System.out.println("Eliminacion de bloque completada");
                        
                        //se elimina la cabeza de la lista copia de bloques
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por eliminar");
                        listaBloques.print();
                        
                        //verifica si la lista de bloques del archivo esta vacio
                        if(archivo.getBlockList().isEmpty() == true){
                            System.out.println("Eliminacion completada");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra el mensaje de que se completo la eliminacion
                            archivo.aplicarEliminar();
                            
                            estadoActual = EstadoProceso.TERMINADO;
                            stop = true;
                        }
                        
                    } else if (operacionCompletada == false){
                        System.out.println("a llorar 2");
                    }
                }
            }
            
        } else if (tipoProceso == TipoProceso.LEER){
            
            boolean stop = false;
            while(stop != true){
                System.out.println("LEER");
                //pediremos la cabeza del ListaBloques que es una copia de la lista del archivo
                int index = (int) listaBloques.getHead().getElement();
                System.out.println("Bloque index: " + index);
                
                long inicio = System.currentTimeMillis();
                //true = indica que el proceso ya realizo la operacion
                //false = indica que el proceso no realizo la operacion
                boolean operacionCompletada = false;        
                //para C_SCAN
                if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                    operacionCompletada = cabezalNormal.leerInfo(index);
                //para SCAN
                } else if (FileExplorer.getPolitica() == TipoPolitica.SCAN){
                    operacionCompletada = cabezalReversaCompleto.leerInfo(index);
                //para FIFO,LIFO Y SSTF
                } else {
                    operacionCompletada = cabezalReversa.leerInfo(index);
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
                        System.out.println("Lectura de bloque completada");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        
                        //se aumenta el contador de bloques leidos del archivo
                        archivo.setCountLectura(archivo.getCountLectura()+1);
                        //se elimina la cabeza de la lista copia de bloques
                        
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por leer");
                        listaBloques.print();
                        
                        //se verifica si el contador de bloques leidos es igual al tamaño total de bloques en los que esta divido el archivo
                        if(archivo.completeLectura() == true){
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra el mensaje de que se completo la lectura
                            archivo.aplicarLectura();
                            //se cambia el estado actual del proceso a Terminado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la lectura por completo de la lista de bloques del archivo
                            stop = true;
                            
                        //el contador de bloques leidos no es igual al tamaño total de bloques en los que esta divido el archivo 
                        } else {
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                            System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.BLOQUEADO;
                            //se detiene el PCB ya que no se completo la lectura por completo de la lista de bloques del archivo
                            stop = true;
                        }
                        
                        
                    } else if (operacionCompletada == false) {
                        System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                        System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se cambia el estado actual del proceso a Bloqueado
                        estadoActual = EstadoProceso.BLOQUEADO;
                        //se detiene el PCB ya que no se completo la lectura por completo de la lista de bloques del archivo
                        stop = true;
                    }
                    
                } else if (FileExplorer.isProcesoBloqueado() == false ){
                    if(operacionCompletada == true){
                        System.out.println("Lectura de bloque completada");
                        
                        //se aumenta el contador de bloques leidos del archivo
                        archivo.setCountLectura(archivo.getCountLectura()+1);
                        //se elimina la cabeza de la lista copia de bloques
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por leer");
                        listaBloques.print();
                        
                        //se verifica si el contador de bloques leidos es igual al tamaño total de bloques en los que esta divido el archivo
                        if(archivo.completeLectura() == true){
                            System.out.println("El proceso ya termino por completo");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra un mensaje de que se completo la lectura
                            archivo.aplicarLectura();
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la lectura por completo de los archivo en el SD
                            stop = true;
                        }
                        
                    } else if (operacionCompletada == false){
                        System.out.println("a llorar 3");
                    }
                }
            }
                
        } else if (tipoProceso == TipoProceso.MODIFICAR){
            
            boolean stop = false;
            while(stop != true){
                System.out.println("MODIFICAR");
                //pediremos la cabeza del ListaBloques que es una copia de la lista del archivo
                int index = (int) listaBloques.getHead().getElement();
                System.out.println("Bloque index: " + index);
                
                long inicio = System.currentTimeMillis();
                //true = indica que el proceso ya realizo la operacion
                //false = indica que el proceso no realizo la operacion
                boolean operacionCompletada = false;
                //para C_SCAN
                if(FileExplorer.getPolitica() == TipoPolitica.C_SCAN){
                    operacionCompletada = cabezalNormal.modificarInfo(index, nombreArchivo);
                //para SCAN
                } else if (FileExplorer.getPolitica() == TipoPolitica.SCAN){
                    operacionCompletada = cabezalReversaCompleto.modificarInfo(index, nombreArchivo);
                //para FIFO,LIFO Y SSTF
                } else {
                    operacionCompletada = cabezalReversa.modificarInfo(index, nombreArchivo);
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
                        System.out.println("La modificacion del bloque de '" + archivo.getName() + "' a '" + nombreArchivo + "' se a completado");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se aumenta el contador de bloques leidos del archivo
                        archivo.setCountModificar(archivo.getCountModificar()+1);
                        
                        //se elimina la cabeza de la lista copia de bloques
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por modificar");
                        listaBloques.print();
                        
                        //se verifica si el contador de bloques modificados es igual al tamaño total de bloques en los que esta divido el archivo
                        if(archivo.completeModificar() == true){
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, pero el proceso ya termino");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra el mensaje de que se completo la modificacion
                            archivo.aplicarCambios(nombreArchivo);
                            
                            System.out.println("ahora el archivo se llama " + archivo.getName());
                            
                            //se cambia el estado actual del proceso a Terminado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la modificacion por completo de la lista de bloques del archivo
                            stop = true;
                            
                        //el contador de bloques modificados no es igual al tamaño total de bloques en los que esta divido el archivo
                        } else {
                            System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                            System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                            //se cambia el estado actual del proceso a Bloqueado
                            estadoActual = EstadoProceso.BLOQUEADO;
                            //se detiene el PCB ya que no se completo la modificacion por completo de la lista de bloques del archivo
                            stop = true;
                        }
                        
                                                
                    } else if (operacionCompletada == false) {
                        System.out.println("Ya pasaron " + FileExplorer.getIoExceptionCycle() + " lecturas, inicia bloqueo");
                        System.out.println("Proceso " + procesoNombre + " se a BLOQUEADO");
                        //se setea el ProcesoBloqeuado a false
                        FileExplorer.setProcesoBloqueado(false);
                        //se cambia el estado actual del proceso a Bloqueado
                        estadoActual = EstadoProceso.BLOQUEADO;
                        //se detiene el PCB ya que no se completo la modificacion por completo de la lista de bloques del archivo
                        stop = true;
                    }
                    
                } else if (FileExplorer.isProcesoBloqueado() == false ){
                    
                    if(operacionCompletada == true){
                        System.out.println("La modificacion del bloque de '" + archivo.getName() + "' a '" + nombreArchivo + "' se a completado");
                        
                        //se aumenta el contador de bloques leidos del archivo
                        archivo.setCountModificar(archivo.getCountModificar()+1);
                        
                        //se elimina la cabeza de la lista copia de bloques
                        listaBloques.deleteBegin();
                        System.out.println("Bloques que faltan por modificar");
                        listaBloques.print();
                        
                        //se verifica si el contador de bloques modificados es igual al tamaño total de bloques en los que esta divido el archivo
                        if(archivo.completeModificar() == true){
                            System.out.println("El proceso ya termino por completo");
                            System.out.println("Proceso " + procesoNombre + " a TERMINADO");
                            //se muestra el mensaje de que se completo la modificacion
                            archivo.aplicarCambios(nombreArchivo);
                            
                            System.out.println("ahora el archivo se llama " + archivo.getName());
                            
                            //se cambia el estado actual del proceso a Terminado
                            estadoActual = EstadoProceso.TERMINADO;
                            //se termina el PCB ya que se completo la modificacion por completo de la lista de bloques del archivo
                            stop = true;
                       
                        }
                        
                    } else if (operacionCompletada == false){
                        System.out.println("a llorar 4");
                    }
                }
            }
        }
    }  
    
    
    /*
    NECESARIO APLICAR ESTA FUNCION LUEGO DE CREAR EL PCB
    Esto es para crear la copia de la lista de bloques del archivo al que se le realizara la operacion CRUD
    nota: no se crea una lista copia para las operaciones CRUD de CREAR
    */
    public void creaListaCopia(){
                
        //Primero se verifica la lista de bloques del archivo fue copiada )solo para las operaciones que no sean de CREAR=
        //if(copiaRealizada == false && tipoProceso != TipoProceso.CREAR){
        if(tipoProceso != TipoProceso.CREAR){
            ListaEnlazada copia = new ListaEnlazada();
            Nodo nodoOriginal = archivo.getBlockList().getHead();
            while (nodoOriginal != null){
                copia.insertFinal(nodoOriginal.getElement());
                nodoOriginal = nodoOriginal.getNext();
            }
            setListaBloques(copia);
        }
    }
    
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
    
    public int generarIDProceso(){
        int procesoID = ThreadLocalRandom.current().nextInt(111111, 999999);
        return procesoID;
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

    public void setEstadoActual(EstadoProceso estadoActual) {
        this.estadoActual = estadoActual;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public ListaEnlazada getListaBloques() {
        return listaBloques;
    }

    public void setListaBloques(ListaEnlazada listaBloques) {
        this.listaBloques = listaBloques;
    }

    
}
