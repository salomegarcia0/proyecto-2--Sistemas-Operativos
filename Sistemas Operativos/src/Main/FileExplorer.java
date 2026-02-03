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
 * MUERE
 * @author pjroj
 */
public class FileExplorer {
    private static SD SD;
    private static int bloquesDisponibles = 0;
    private static int sizeBloque = 100; //el valor del "tamaño de los bloques, es base a esto que es simularemos la "memoria disponible" en el SD
    private static Cola listo;
    private static Cola colaListos;
    private static PCB procesoEnEjecucion = null;
    private static Cola colaBloqueados;
    private static Cola colaTerminado;
    /*
    Variables booleanas para saber la politica que esta activa;
    */
    private static TipoPolitica politica = TipoPolitica.SSTF ; //inicialmente sera fifo
    
    //para definir el tiempo que dura una lectura en ms (queremos inicialmente 1000ms = 1seg)
    private static int ciclo_reloj = 1000;
    //para saber cuantas lecturas se han completado hasta el momento
    //de modo que cuando llegue a a ser igual que el ioExceptionCycle, el proceso se pase a bloqueado
    private static int countLecturas;
    //booleano para saber si el proceso en ejecucion se bloqueo
    private static boolean procesoBloqueado;
    //ioExceptionCycle: cada cuantas lecturas ocurre una interrupcion de E/S
    //Cada 15 lecturas (asi lo definimos inicialmente)
    private static int ioExceptionCycle =30;
    //cuanto tiempo estara bloqueado el proceso cuando ocurre una operacion de E/S
    //Bloqueado durante 5 lecturas (asi lo definimos inicialmente)
    private static int ioCompletionTime = 5;
    //Reloj global del sistema
    private static long reloj_global;
    
    
    public static void Simulacion(){
        Thread t1 = new Thread(() -> funcionEjecucion());
        Thread t2 = new Thread(() -> funcionBloqueados());
        //inicia ambos en paralelo   
        t1.start();
        t2.start();    
    }
    
    
    static void funcionEjecucion(){
        System.out.println("funcionEjecucion");
        while (colaListos.isEmpty() == false){
            seleccionarProceso();
            System.out.println("selecciono");
            ejecutarProceso();
            System.out.println("ejecuto");
        }
    }
    
    static void funcionBloqueados(){
        System.out.println("funcionBloqueados");
        while (colaBloqueados.isEmpty() == false){
            moverBloqueadoAListo();
            System.out.println("movio a listo");
        }
    }
    
    
    //----------------------------------------------------------------
    /*
    Una vez creado un proceso se encola a la cola de listos y se le coloca el estado LISTO
    */
    public static void agregarProcesoListo(PCB proceso){
        //Primero se cambia el estado del proceso a 
        proceso.setEstadoActual(EstadoProceso.LISTO);
        proceso.creaListaCopia();
        colaListos.enColar(proceso);
        
    }
    
    /*
    Se seleccionaria un proceso de la cola de listos segun la politica aplicada
    */
    public static void seleccionarProceso(){
        //si la cola de listos no esta vacio se selecciona un proceso en base a la politica
        if(!colaListos.isEmpty()){
            
            /*
            El primer proceso en entrar es el primero en ser atendido
            */
            if (politica == TipoPolitica.FIFO){
                System.out.println("Politica FIFO");
                PCB proceso = colaListos.desColarInicio();
                
                System.out.println("Proceso seleccionado:" + proceso.getProcesoNombre());
                /*
                se verifica condiciones extra:
                1.ELIMINAR -> si se esta eliminando el archivo no se pueden hacer el resto de operaciones CRUD: LEER y MODIFICAR
                2.MODIFICAR -> si se esta modificando el archivo no se pueden hacer operaciones CRUD: LEER (bloquear).
                3.LEER -> si se esta leyendo el archivo no se puede hacer operaciones CRUD: MODIFICAR (bloquear)
                */
                //CASO 1 - para LEER y MODIFICAR
                if(proceso.getTipoProceso() != TipoProceso.CREAR && proceso.getTipoProceso() != TipoProceso.ELIMINAR){
                    if(proceso.getArchivo().isProcesoEliminizacion() == true || proceso.getArchivo() == null){
                        if (proceso.getArchivo() == null){
                            System.out.println("Este proceso no se puede realizar ya que el archivo no existe");
                        } else {
                            System.out.println("Este proceso no se puede realizar ya que hay una operacion de Eliminacion del archivo en proceso");
                        }
                        proceso.setEstadoActual(EstadoProceso.ERROR);
                        System.out.println("Moviendo a la cola de Terminado con el estado ERROR");
                        colaTerminado.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso(); 
                    }
                    
                //CASO 2 - para LEER
                } else if (proceso.getTipoProceso() == TipoProceso.MODIFICAR){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountLectura() > 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Lectura de archivo en proceso");
                         proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                //CASO 3 - para MODIFICAR
                } else if (proceso.getTipoProceso() == TipoProceso.LEER){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountModificar()> 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Modificacion de archivo en proceso");
                        proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                }
                
                FileExplorer.setProcesoEnEjecucion(proceso);
                
            /*
            El ultimo proceso en entrar es el primero en ser atendido    
            */    
            } else if (politica == TipoPolitica.LIFO){
                System.out.println("Politica LIFO");
                PCB proceso = colaListos.desColarFinal();
                
                System.out.println("Proceso seleccionado:" + proceso.getProcesoNombre());
                /*
                se verifica condiciones extra:
                1.ELIMINAR -> si se esta eliminando el archivo no se pueden hacer el resto de operaciones CRUD: LEER y MODIFICAR
                2.MODIFICAR -> si se esta modificando el archivo no se pueden hacer operaciones CRUD: LEER (bloquear).
                3.LEER -> si se esta leyendo el archivo no se puede hacer operaciones CRUD: MODIFICAR (bloquear)
                */
                //CASO 1 - para LEER y MODIFICAR
                if(proceso.getTipoProceso() != TipoProceso.CREAR && proceso.getTipoProceso() != TipoProceso.ELIMINAR){
                    if(proceso.getArchivo().isProcesoEliminizacion() == true || proceso.getArchivo() == null){
                        if (proceso.getArchivo() == null){
                            System.out.println("Este proceso no se puede realizar ya que el archivo no existe");
                        } else {
                            System.out.println("Este proceso no se puede realizar ya que hay una operacion de Eliminacion del archivo en proceso");
                        }
                        proceso.setEstadoActual(EstadoProceso.ERROR);
                        System.out.println("Moviendo a la cola de Terminado con el estado ERROR");
                        colaTerminado.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso(); 
                    }
                    
                //CASO 2 - para LEER
                } else if (proceso.getTipoProceso() == TipoProceso.MODIFICAR){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountLectura() > 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Lectura de archivo en proceso");
                         proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                //CASO 3 - para MODIFICAR
                } else if (proceso.getTipoProceso() == TipoProceso.LEER){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountModificar()> 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Modificacion de archivo en proceso");
                        proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                }
                //se desencola el ultimo proceso que llego
                FileExplorer.setProcesoEnEjecucion(proceso);
                
            /*
            Esta politica elige un proceso de la cola de Listos aleatoriamente  
            */   
            } else if (politica == TipoPolitica.PA){
                System.out.println("Politica PA");
                Random rand = new Random();
                //genera un valor random entre 0 y el tamaño de la lista menos 1
                int numero = rand.nextInt(colaListos.getSize());
                
                //se busca por index del numero que obtuvimos anteriormente y desencolamos de la cola de listos
                //el proceso que este en esa posicion para ser el nuevo proceso en ejecutar
                PCB proceso = colaListos.desColarIntermedio(numero);
                
                System.out.println("Proceso seleccionado:" + proceso.getProcesoNombre());
                /*
                se verifica condiciones extra:
                1.ELIMINAR -> si se esta eliminando el archivo no se pueden hacer el resto de operaciones CRUD: LEER y MODIFICAR
                2.MODIFICAR -> si se esta modificando el archivo no se pueden hacer operaciones CRUD: LEER (bloquear).
                3.LEER -> si se esta leyendo el archivo no se puede hacer operaciones CRUD: MODIFICAR (bloquear)
                */
                //CASO 1 - para LEER y MODIFICAR
                if(proceso.getTipoProceso() != TipoProceso.CREAR && proceso.getTipoProceso() != TipoProceso.ELIMINAR){
                    if(proceso.getArchivo().isProcesoEliminizacion() == true || proceso.getArchivo() == null){
                        if (proceso.getArchivo() == null){
                            System.out.println("Este proceso no se puede realizar ya que el archivo no existe");
                        } else {
                            System.out.println("Este proceso no se puede realizar ya que hay una operacion de Eliminacion del archivo en proceso");
                        }
                        proceso.setEstadoActual(EstadoProceso.ERROR);
                        System.out.println("Moviendo a la cola de Terminado con el estado ERROR");
                        colaTerminado.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso(); 
                    }
                    
                //CASO 2 - para LEER
                } else if (proceso.getTipoProceso() == TipoProceso.MODIFICAR){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountLectura() > 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Lectura de archivo en proceso");
                         proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                //CASO 3 - para MODIFICAR
                } else if (proceso.getTipoProceso() == TipoProceso.LEER){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountModificar()> 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Modificacion de archivo en proceso");
                        proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                }
                
                //el proceso que este en esa posicion para ser el nuevo proceso en ejecutar
                FileExplorer.setProcesoEnEjecucion(proceso);
                
            /*
               este busca el bloque mas cercano al cabezal 
            */ 
            } else if (politica == TipoPolitica.SSTF){
                System.out.println("Politica SSTF");
                //obtengo el la ubicacion del bloque en el que se encuentra el cabezal
                int ubicacion = SD.getLector().getElement().getIndex();
                
                //para saber el index actual en el recorrido
                int index = 0;
                //Es para guardar el index de la distancia mas corta
                int indexDistancia = 0;
                
                
                // para el calculos de las distancias
                int distanciaCalculada = 0;
                //para definir la distancia mas corta que se a encontrado hasta el momento, esta sera una variables de comparacion
                //inicialmente tendra el valor más alto que sera el tamaño de SD menos 1
                int distancia = SD.getSize() - 1;
                
                //obtengo la cabeza de la cola de listos
                NodoProceso nodoProceso = colaListos.getHead();
                
                //recorreremos la lista en busqueda del index mas cercano al cabezal 
                while (nodoProceso != null ){
                    PCB proceso = nodoProceso.getProceso();
                    //para los procesos CRUD LEER, MODIFICAR y ELIMINAR (ya que tienen la lista copia de la lista de bloques del archivo)
                    if(proceso.getTipoProceso() != TipoProceso.CREAR){
                        /*
                        la distancia va a ser igual a el index de la ubicacion del lector (ubicacion) menos el index
                        del primer valor en la lista de bloques del PCB (que es una copia de la lista de bloques del archivo).
                        RETORNA: esto retornara la distancia entre el cabezal y el bloque al que necesitaria acceder
                        */
                        distanciaCalculada = Math.abs(ubicacion - (int) proceso.getListaBloques().getHead().getElement());
                        
                                            
                    //para los procesos CRUD CREAR (ya que no hay una lista de bloques del archivo)
                    } else {
                        /*
                        la distancia va a ser igual a el index de la ubicacion del lector (ubicacion) menos el index
                        del bloque libre disponible.
                        RETORNA: esto retornara la distancia entre el cabezal y el bloque al que necesitaria acceder
                        */
                        distanciaCalculada = Math.abs(ubicacion - bloqueDespejadoCercano());
                    }
                    
                    //Se verifica si esta distancia calculada es menor a la distancia actual, si lo es la setea como el nuevo valor de distancia
                    //si la distancia no es menor no tiene sentido que sea el valor a tomar
                    
                    if (distanciaCalculada < distancia){
                        distancia = distanciaCalculada;
                        indexDistancia = index;
                    }        
                    
                    //si la distancia es cero, ese es el index mas optimo para el lector.
                    if(distancia == 0){
                        
                        break;
                    }
                    
                    //se para la siguiente comparacion
                    nodoProceso = nodoProceso.getNext();
                    index = index + 1;
                }
                
                System.out.println("Index del proceso seleccionado para SSTF: " + indexDistancia);
                System.out.println("Distancia del cabezal: " + distancia);
                
                //una vez se haya encontrado el indexDistancia de proceso mas cercano al cabezal desencolamos de la cola de listos
                //el proceso PCB que este en esa posicion para ser el nuevo proceso en ejecutar
                PCB proceso = colaListos.desColarIntermedio(indexDistancia);
                
                System.out.println("Proceso seleccionado:" + proceso.getProcesoNombre());
                /*
                se verifica condiciones extra:
                1.ELIMINAR -> si se esta eliminando el archivo no se pueden hacer el resto de operaciones CRUD: LEER y MODIFICAR
                2.MODIFICAR -> si se esta modificando el archivo no se pueden hacer operaciones CRUD: LEER (bloquear).
                3.LEER -> si se esta leyendo el archivo no se puede hacer operaciones CRUD: MODIFICAR (bloquear)
                */
                //CASO 1 - para LEER y MODIFICAR
                if(proceso.getTipoProceso() != TipoProceso.CREAR && proceso.getTipoProceso() != TipoProceso.ELIMINAR){
                    if(proceso.getArchivo().isProcesoEliminizacion() == true || proceso.getArchivo() == null){
                        if (proceso.getArchivo() == null){
                            System.out.println("Este proceso no se puede realizar ya que el archivo no existe");
                        } else {
                            System.out.println("Este proceso no se puede realizar ya que hay una operacion de Eliminacion del archivo en proceso");
                        }
                        proceso.setEstadoActual(EstadoProceso.ERROR);
                        System.out.println("Moviendo a la cola de Terminado con el estado ERROR");
                        colaTerminado.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso(); 
                    }
                    
                //CASO 2 - para LEER
                } else if (proceso.getTipoProceso() == TipoProceso.MODIFICAR){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountLectura() > 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Lectura de archivo en proceso");
                         proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                //CASO 3 - para MODIFICAR
                } else if (proceso.getTipoProceso() == TipoProceso.LEER){
                    //se verifica si se han hecho lecturas antes se manda el proceso de modificar a Bloquear para darle chance a Lectura de terminar
                    if (proceso.getArchivo().getCountModificar()> 0){
                        System.out.println("Este proceso no se puede realizar ya que hay una operacion de Modificacion de archivo en proceso");
                        proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
                        System.out.println("Moviendo a la cola de bloqueado");
                        colaBloqueados.enColar(proceso);
                        //se llama a esta operacion de  nuevo para conseguir el siguiente PCB
                        seleccionarProceso();
                    } 
                    
                }
                
                
                //el proceso PCB que este en esa posicion para ser el nuevo proceso en ejecutar
                FileExplorer.setProcesoEnEjecucion(proceso);
            
            /*
            
            */    
            } else if (politica == TipoPolitica.SCAN){
                System.out.println("Politica SCAN");
                System.out.println("SCAN aun no hecho");
            /*
                
            */
            } else if (politica == TipoPolitica.C_SCAN){
                System.out.println("Politica C-SCAN");
                System.out.println("C-SCAN aun no hecho");
                
            }
            
            procesoEnEjecucion.setEstadoActual(EstadoProceso.EJECUTANDO);

        } else {
            FileExplorer.setProcesoEnEjecucion(null);
        }
    }
    
    public static void ejecutarProceso(){
        if(procesoEnEjecucion != null){
            System.out.println("Proceso " + procesoEnEjecucion.getProcesoNombre() + "se esta EJECUTANDO");
            procesoEnEjecucion.ejecutar();
                
            if(procesoEnEjecucion.getEstadoActual() == EstadoProceso.BLOQUEADO){
                /*Mueve el proceso que estaba en ejecucion y que no se completo 
                (proceso IO_BOUND a la cola de Bloqueados;
                */
                    System.out.println("me bloquee");
                    moverEjecutandoABloqueado(procesoEnEjecucion);
                    //ejecuta el mover de bloqueado a listo
                    Thread t2 = new Thread(() -> funcionBloqueados());
                    //inicia ambos en paralelo   
                    t2.start();
            }else if(procesoEnEjecucion.getEstadoActual() == EstadoProceso.TERMINADO){
                /*Mueve el proceso que estaba en ejecucion y que se completo a la cola
                de procesos completados
                */
                    System.out.println("pase");
                    moverEjecutadoACompletado(procesoEnEjecucion);
                    
                    
                }
            }
            System.out.println("Cola Listos");
            colaListos.print();
            System.out.println("Cola Bloqueas");
            colaBloqueados.print();
            System.out.println("Cola Terminado");
            colaTerminado.print();
        
    }
    
    
    //para mover el proceso que termino de ejecutarse. Proceso con el estado TERMINADO
    public static void moverEjecutadoACompletado(PCB proceso){
        proceso.setEstadoActual(EstadoProceso.TERMINADO);
        colaTerminado.enColar(proceso);
        FileExplorer.setProcesoEnEjecucion(null); 
    }
    
    /*para mover el proceso que no termino de ejecutarse, paso a Bloqueado
    Proceso con estado BLOQUEADO
    */
    public static void moverEjecutandoABloqueado(PCB proceso){ // esto es para mover el proceso a bloqueados pero hay que ver lo de las interrpciones
        proceso.setEstadoActual(EstadoProceso.BLOQUEADO);
        colaBloqueados.enColar(proceso);
        FileExplorer.setProcesoEnEjecucion(null);
    }
    
    /*para mover el proceso que no se puede ejecutar ya sea por cuestiones de que se esta eliminado un archivo, o porque se
    va a modificar un archivo y ya se esta leyendo o viceversa
    Se coloca al proceos una etiqueta ERROR.
    ---------------------------------------------------------------------
    1.	Eliminar -> si se esta eliminando el archivo no se pueden hacer el resto de operaciones CRUD: LEER y MODIFICAR
    2.  MODIFICAR -> si se esta modificando el archivo no se pueden hacer operaciones CRUD: LEER (bloquear).
    3.	LEER -> si se esta leyendo el archivo no se puede hacer operaciones CRUD: MODIFICAR (bloquear)
    
    */
    public static void moverEjecutandoACompletadoError(PCB proceso){ // esto es para mover el proceso a bloqueados pero hay que ver lo de las interrpciones
        proceso.setEstadoActual(EstadoProceso.ERROR);
        colaTerminado.enColar(proceso);
        FileExplorer.setProcesoEnEjecucion(null);
    }
    
    /*
    para mover el proceso que esta en la cola de bloqueados a la cola de listos.
    */
    public static void moverBloqueadoAListo(){
        if (!colaBloqueados.isEmpty()){
            //se desencola el proceso de la cola de bloqueados.
            PCB procesoReanudando = colaBloqueados.desColarInicio();
            //simula el tiempo de bloqueado del proceso PCB
            
            System.out.println("DURACION BLOQUEO"+ioCompletionTime*ciclo_reloj);
            Thread thread = new Thread(new Hilo(ioCompletionTime*ciclo_reloj));
            thread.start();
            try {
                thread.join(); // espera a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("\nYa se cumplio el tiempo de bloqueo, devolviendo a la cola de listos\n");
            
            
            
            //se le cambia el estado del proceso a LISTO
            procesoReanudando.setEstadoActual(EstadoProceso.LISTO);
            //se encola el proceso de regreso a la cola de listos.
            colaListos.enColar(procesoReanudando);
            System.out.println(procesoReanudando.getProcesoNombre()+ " reanudado");
//            if(!colaListos.isEmpty()){
//                Thread t1 = new Thread(() -> Simulacion());
//                //inicia ambos en paralelo   
//                t1.start();
//            }
            
             
        }
        
    }
    
    
    /*
    Esto es solo para los caso de un PCB con operacion CREAR para la conseguir el bloque libre mas
    cercano al cabezal
    */
    public static int bloqueDespejadoCercano(){
         //el nodo cabezal
        NodoBloque cabezal = SD.getLector();
        //obtengo el la ubicacion del bloque en el que se encuentra el cabezal
        int ubicacion = cabezal.getElement().getIndex();
        //para saber la ubicacion del bloque disponible
        int index = 0;
        int distanciaDer;
        int distanciaIzq;
        //Nodos para conseguir el index mas cercano
        NodoBloque nodoDer = SD.getLector();
        NodoBloque nodoIzq = SD.getLector();
        
        //para IZQUIERDA
        while(nodoIzq != null && nodoIzq.getElement().isAvailable() == false){
            nodoIzq = nodoIzq.getPrevious();
        }
        if(nodoIzq != null){
            distanciaIzq = nodoIzq.getElement().getIndex();
        } else {
            distanciaIzq = -1;
        }
        
        //para DERECHA
        while(nodoDer != null && nodoDer.getElement().isAvailable() == false){
            nodoDer = nodoDer.getNext();
        }
        if(nodoDer != null){
            distanciaDer = nodoDer.getElement().getIndex();
        } else {
            distanciaDer = -1;
        }
        
        //solo hay espacio del lado derecho
        if(distanciaIzq == -1){
            index = distanciaDer;
        //solo hay espacio del lado izquierdo
        } else if (distanciaDer == -1){
            index = distanciaIzq;
        //la distancia es la misma, se hará de forma random la seleccion de la ubicacion entre estas dos index de distancias
        } else if(Math.abs(ubicacion - distanciaIzq) == Math.abs(ubicacion - distanciaDer)){
            Random rand = new Random();
            boolean valor = rand.nextBoolean();
            if (valor == true){
                index = distanciaDer;
            } else {
                index = distanciaIzq;
            }
        } else if(Math.abs(ubicacion - distanciaIzq) < Math.abs(ubicacion - distanciaDer)){
            index = distanciaIzq;
        } else if(Math.abs(ubicacion - distanciaIzq) > Math.abs(ubicacion - distanciaDer)){
            index = distanciaDer;
        }
        
        System.out.println("Bloque mas cercano ubicado en el index " + index );
        return index;
    }        

    public static void contarBloquesDisponiblesReales(){
               
        int disponibles = 0;
        NodoBloque actual = SD.getHead();
        
        while (actual != null){
            if (actual.getElement().isAvailable()){
                disponibles++;
            }
            actual = actual.getNext();
        }
        bloquesDisponibles = disponibles;
        setBloquesDisponibles(disponibles);
    }

    public static int getBloquesDisponibles() {
        return bloquesDisponibles;
    }

    public static void setBloquesDisponibles(int bloquesDisponibles) {
        FileExplorer.bloquesDisponibles = bloquesDisponibles;
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
    
    
     public static void inicializarColasProcesos() {
        System.out.println("=== INICIALIZANDO COLAS DE PROCESOS ===");
        
        if (colaListos == null) {
            colaListos = new Cola("Listos");
            System.out.println("Cola Listos inicializada");
        }
        
        if (colaBloqueados == null) {
            colaBloqueados = new Cola("Bloqueados");
            System.out.println("Cola Bloqueados inicializada");
        }
        
        if (colaTerminado == null) {
            colaTerminado = new Cola("Terminados");
            System.out.println("Cola Terminados inicializada");
        }
        
    }
    
    
}
