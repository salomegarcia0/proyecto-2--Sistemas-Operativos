/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lector;
import Clases.*;
import Estructuras.*;
import Main.FileExplorer;
/**
 * La lectura con la clase CabezalNormal es desde el inicio hasta el final del SD y una vez que llegue al final el cabezal empieza a ir
 * en reversa hasta llegar al inicio y volver de regreso y asi sucesivamente.
 * @author pjroj
 */
public class CabezalReversaCompleto {

    public CabezalReversaCompleto() {
    }
    
    //NECESITO DOS VARIABLES BOOLEANAS, 1 PARA SABER SI SE INSCRIBIO EL EN BLOQUE O SIMPLEMENTE NO SE REALIZO NINGUN AVANCE DEBIDO A UN BLOQUEO
    
    public boolean modificarInfo(int index, String nameArchivo){
        System.out.println("CRUD: Modificar (reversa completo)");        
        //se pide el SD del FileExplorer (que es la clase Global), donde permanence los datos
        SD SD = FileExplorer.getSD();
        
        NodoBloque cabezal = SD.getLector();
        //para obtener el valor que dura cada lectura
        int tiempoSimulado = FileExplorer.getCiclo_reloj();
        //Booleano para detener la lectura
        boolean stopRead = false;
        //Booleano para saber si se realizo la operacion o no (el caso de no es cuando se bloqueo)
        boolean operacionRealizada = false;
        
        while (stopRead != false){
            //se pone en marca el hilo para simular el movimiento del cabezal en el disco SD
            Thread thread = new Thread(new Hilo(tiempoSimulado));
            thread.start();
            try {
                thread.join(); // espera a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //condicion para saber si encontro el bloque en base el index
            Bloque bloque = cabezal.getElement();
            
            //al contador de lecturas se le suma una lectura 
            FileExplorer.setCountLecturas(FileExplorer.getCountLecturas()+1);
            System.out.println("Lecturas realizadas: " + FileExplorer.getCountLecturas());
                
            //Se modifica el nombre del archivo por el nuevo
            if (bloque.getIndex() == index){
                System.out.println("Bloque " + index + " modificado");
                //se le setea el nombre del archivo
                bloque.setNameArchivo(nameArchivo);
                //Como ya se realizo la respectiva operacion CRUD en el bloque se cambia a true
                operacionRealizada = true; 
                //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
                // que proceso se debe bloquear
                if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                    //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                    FileExplorer.setProcesoBloqueado(true);
                    //se resetea el contador de Lecturas a 0
                    FileExplorer.setCountLecturas(0);
                }   
                //como si se encontro se cambia el estado de stopRead a true 
                stopRead = true;
            }
            
            //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
            // que proceso se debe bloquear
            if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                FileExplorer.setProcesoBloqueado(true);
                //se resetea el contador de Lecturas a 0
                FileExplorer.setCountLecturas(0);
                //como se bloquea el proceso (sin importa si llego a inscribir o no) estado de stopRead a true 
                stopRead = true;
            }
            
            //dependiendo del sentido del lector se selecciona como el nuevo cabezal el siguiente o el anterior 
            if(SD.isReversa()){
                //se toma el nodo previo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getPrevious();
            } else {
                //se toma el siguiente nodo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getNext();
            }
            
            //si el cabezal es null
            if (cabezal == null){
                if(SD.isReversa()){
                    System.out.println("El cabezal llego al inicio, invirtiendo orden de lectura");
                    SD.setReversa(false);
                    //como estaba en reversa el ultimo era la cabeza
                    SD.setLector(SD.getHead());
                } else {
                    System.out.println("El cabezal llego al final, invirtiendo orden de lectura");
                    //como estaba en sentido normal el ultimo era la cola
                    SD.setReversa(true);
                    SD.setLector(SD.getTail());
                }
            } else {
                SD.setLector(cabezal);
            }
            
        }
        
        //retorna un booleano 
        //true = indica que el proceso  ya realizo la operacion
        //false =  indica que el proceso no realizo la operacion
        return operacionRealizada;
        
        /*
        -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
        acontinuacion
        -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
        -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
        -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
        */
        
        
    }
    
    /*
    el tema de eliminar un arhivo es que dicho archivo se elimina al final de un proceso aunque hay asegurar de alguna forma que si el archivo
    pues tiene una peticion de eliminacion, no se muestre en lainterzfaz por seguridad ya que si el usuario realiza peticiones de CRUD de lectura
    o modificacion para dicho archivo dará erro porque el archivo ya no existira.
    
    */
    public boolean eliminarInfo(int index, Archivo archivo){
        System.out.println("CRUD: Eliminar (reversa completo)");
        //se pide el SD del FileExplorer (que es la clase Global), donde permanence los datos
        SD SD = FileExplorer.getSD();
        NodoBloque cabezal = SD.getLector();
        //para obtener el valor que dura cada lectura
        int tiempoSimulado = FileExplorer.getCiclo_reloj();
        //Booleano para detener la lectura
        boolean stopRead = false;
        //Booleano para saber si se realizo la operacion o no (el caso de no es cuando se bloqueo)
        boolean operacionRealizada = false;
        
        while (stopRead != false){
            //se pone en marca el hilo para simular el movimiento del cabezal en el disco SD
            Thread thread = new Thread(new Hilo(tiempoSimulado));
            thread.start();
            try {
                thread.join(); // espera a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //condicion para saber si encontro el bloque en base el index
            Bloque bloque = cabezal.getElement();
            
             //al contador de lecturas se le suma una lectura 
            FileExplorer.setCountLecturas(FileExplorer.getCountLecturas()+1);
            System.out.println("Lecturas realizadas: " + FileExplorer.getCountLecturas());
                
            
            //Se modifica el nombre del archivo por el nuevo
            if (bloque.getIndex() == index){
                System.out.println("Eliminando parte del archivo en el bloque " + index + " eliminado");
                //se le setea el nombre del archivo a "vacio", lo que indica que se elimino el archivo
                bloque.setNameArchivo("vacio");
                
                //se elimina la ubicacion del bloque de la lista de bloques del archivo
                ListaEnlazada list = archivo.getBlockList();
                //se insertaria siempre el nuevo bloque al final de la lsita
                list.deleteInIndex(bloque.getIndex());
                archivo.setBlockList(list);
                
                //se reduce la cantidad de bloques en las que esta dividido el archivo
                //archivo.setSize(archivo.getSize()-1);
                
                //se le cambia el estado del bloque de ocupado a desocupado para que se puedan guardar en el futuro 
                bloque.setAvailable(true);
                
                System.out.println("Bloque " + bloque.getIndex() + " desocupado");
                //se aumenta la cantidad de bloques disponibles en SD
                SD.setSizeDisponible(SD.getSizeDisponible()+1);
                //Como ya se realizo la respectiva operacion CRUD en el bloque se cambia a true
                operacionRealizada = true; 
                //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
                // que proceso se debe bloquear
                if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                    //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                    FileExplorer.setProcesoBloqueado(true);
                    //se resetea el contador de Lecturas a 0
                    FileExplorer.setCountLecturas(0);
                }   
                //como si se encontro se cambia el estado de stopRead a true 
                stopRead = true;
            }
            
            //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
            // que proceso se debe bloquear
            if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                FileExplorer.setProcesoBloqueado(true);
                //se resetea el contador de Lecturas a 0
                FileExplorer.setCountLecturas(0);
                //como se bloquea el proceso (sin importa si llego a inscribir o no) estado de stopRead a true 
                stopRead = true;
            }
            
            //dependiendo del sentido del lector se selecciona como el nuevo cabezal el siguiente o el anterior 
            if(SD.isReversa()){
                //se toma el nodo previo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getPrevious();
            } else {
                //se toma el siguiente nodo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getNext();
            }
            
            //si el cabezal es null
            if (cabezal == null){
                if(SD.isReversa()){
                    System.out.println("El cabezal llego al inicio, invirtiendo orden de lectura");
                    SD.setReversa(false);
                    //como estaba en reversa el ultimo era la cabeza
                    SD.setLector(SD.getHead());
                } else {
                    System.out.println("El cabezal llego al final, invirtiendo orden de lectura");
                    //como estaba en sentido normal el ultimo era la cola
                    SD.setReversa(true);
                    SD.setLector(SD.getTail());
                }
            } else {
                SD.setLector(cabezal);
            }
            
        }
        
        //retorna un booleano 
        //true = indica que el proceso  ya realizo la operacion
        //false =  indica que el proceso no realizo la operacion
        return operacionRealizada;
        
        /*
        -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
        acontinuacion
        -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
        -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
        -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
        */
    }
    
    public boolean leerInfo(int index){
        System.out.println("CRUD: Leer (reversa completo)");
        //se pide el SD del FileExplorer (que es la clase Global), donde permanence los datos
        SD SD = FileExplorer.getSD();
        NodoBloque cabezal = SD.getLector();
        //para obtener el valor que dura cada lectura
        int tiempoSimulado = FileExplorer.getCiclo_reloj();
        //Booleano para detener la lectura
        boolean stopRead = false;
        //Booleano para saber si se realizo la operacion o no (el caso de no es cuando se bloqueo)
        boolean operacionRealizada = false;
        
        while (stopRead != false){
            //se pone en marca el hilo para simular el movimiento del cabezal en el disco SD
            Thread thread = new Thread(new Hilo(tiempoSimulado));
            thread.start();
            try {
                thread.join(); // espera a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //condicion para saber si encontro el bloque en base el index
            Bloque bloque = cabezal.getElement();
            
            
            //al contador de lecturas se le suma una lectura 
            FileExplorer.setCountLecturas(FileExplorer.getCountLecturas()+1);
            System.out.println("Lecturas realizadas: " + FileExplorer.getCountLecturas());
            //Se modifica el nombre del archivo por el nuevo
            if (bloque.getIndex() == index){
                System.out.println("Bloque " + index + " leido");
                //Como ya se realizo la respectiva operacion CRUD en el bloque se cambia a true
                operacionRealizada = true; 
                //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
                // que proceso se debe bloquear
                if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                    //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                    FileExplorer.setProcesoBloqueado(true);
                    //se resetea el contador de Lecturas a 0
                    FileExplorer.setCountLecturas(0);
                }
                //como si se encontro se cambia el estado de stopRead a true 
                stopRead = true;
            }
            
            //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
            // que proceso se debe bloquear
            if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                FileExplorer.setProcesoBloqueado(true);
                //se resetea el contador de Lecturas a 0
                FileExplorer.setCountLecturas(0);
                //como se bloquea el proceso (sin importa si llego a inscribir o no) estado de stopRead a true 
                stopRead = true;
            }
            
            //dependiendo del sentido del lector se selecciona como el nuevo cabezal el siguiente o el anterior 
            if(SD.isReversa()){
                //se toma el nodo previo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getPrevious();
            } else {
                //se toma el siguiente nodo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getNext();
            }
            
            //si el cabezal es null
            if (cabezal == null){
                if(SD.isReversa()){
                    System.out.println("El cabezal llego al inicio, invirtiendo orden de lectura");
                    SD.setReversa(false);
                    //como estaba en reversa el ultimo era la cabeza
                    SD.setLector(SD.getHead());
                } else {
                    System.out.println("El cabezal llego al final, invirtiendo orden de lectura");
                    //como estaba en sentido normal el ultimo era la cola
                    SD.setReversa(true);
                    SD.setLector(SD.getTail());
                }
            } else {
                SD.setLector(cabezal);
            }
            
        }
        
        //retorna un booleano 
        //true = indica que el proceso  ya realizo la operacion
        //false =  indica que el proceso no realizo la operacion
        return operacionRealizada;
        
        /*
        -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
        acontinuacion
        -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
        -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
        -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
        */
    }
    
    public boolean insertInfo(Archivo archivo){
    System.out.println("CRUD: Insertar (reversa completo)");        
        //se pide el SD del FileExplorer (que es la clase Global), donde permanence los datos
        SD SD = FileExplorer.getSD();
        NodoBloque cabezal = SD.getLector();
        //para obtener el valor que dura cada lectura
        int tiempoSimulado = FileExplorer.getCiclo_reloj();
        //Booleano para detener la lectura
        boolean stopRead = false;
        //Booleano para saber si se realizo la operacion o no (el caso de no es cuando se bloqueo)
        boolean operacionRealizada = false;
        
        while (stopRead != false){
            //se pone en marca el hilo para simular el movimiento del cabezal en el disco SD
            Thread thread = new Thread(new Hilo(tiempoSimulado));
            thread.start();
            try {
                thread.join(); // espera a que el hilo termine antes de continuar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            
            Bloque bloque = cabezal.getElement();
            
            //al contador de lecturas se le suma una lectura 
            FileExplorer.setCountLecturas(FileExplorer.getCountLecturas()+1);
            System.out.println("Lecturas realizadas: " + FileExplorer.getCountLecturas());
            //Condicion para saber si hay un espacio disponible, si encuentra un espacio disponible se realiza la insercion de la info
            if (bloque.isAvailable() == true){
                System.out.println("Insertando parte del archivo en el bloque " + bloque.getIndex() );
                //se agrega la ubicacion del bloque a la lista de bloques del archivo
                ListaEnlazada list = archivo.getBlockList();
                //se insertaria siempre el nuevo bloque al final de la lsita
                list.insertFinal(bloque.getIndex());
                archivo.setBlockList(list);
                
                //se le setea el nombre del archivo
                bloque.setNameArchivo(archivo.getName());
                //se le cambia el estado del bloque de desocupado a ocupado para que no se pueda usar en el futuro 
                bloque.setAvailable(false);
                System.out.println("Bloque " + bloque.getIndex() + " ocupado");
                //se disminuye la cantidad de bloques disponibles en SD
                SD.setSizeDisponible(SD.getSizeDisponible()-1);
                //Como ya se realizo la respectiva operacion CRUD en el bloque se cambia a true
                operacionRealizada = true; 
                //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
                // que proceso se debe bloquear
                if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                    //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                    FileExplorer.setProcesoBloqueado(true);
                    //se resetea el contador de Lecturas a 0
                    FileExplorer.setCountLecturas(0);
                }
                //como si se encontro y se inserto la informacion se cambia el estado de stopRead a true 
                stopRead = true;
            }
            
            
            //si el contador de lecturas es igual a valor de nuestro IoCompletionTime, se cambia el valor del booleano a true, que indica
            // que proceso se debe bloquear
            if (FileExplorer.getCountLecturas() == FileExplorer.getIoExceptionCycle()){
                //como el proceso a sido bloqueado se cambia el estado de true en el FileExplorer para indicar que el proceso se bloqueara
                FileExplorer.setProcesoBloqueado(true);
                //se resetea el contador de Lecturas a 0
                FileExplorer.setCountLecturas(0);
                //como se bloquea el proceso (sin importa si llego a inscribir o no) estado de stopRead a true 
                stopRead = true;
            }
            
            //dependiendo del sentido del lector se selecciona como el nuevo cabezal el siguiente o el anterior 
            if(SD.isReversa()){
                //se toma el nodo previo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getPrevious();
            } else {
                //se toma el siguiente nodo para luego setearlo como el nuevo cabezal
                cabezal = cabezal.getNext();
            }
            
            //si el cabezal es null
            if (cabezal == null){
                if(SD.isReversa()){
                    System.out.println("El cabezal llego al inicio, invirtiendo orden de lectura");
                    SD.setReversa(false);
                    //como estaba en reversa el ultimo era la cabeza
                    SD.setLector(SD.getHead());
                } else {
                    System.out.println("El cabezal llego al final, invirtiendo orden de lectura");
                    //como estaba en sentido normal el ultimo era la cola
                    SD.setReversa(true);
                    SD.setLector(SD.getTail());
                }
            } else {
                SD.setLector(cabezal);
            }
            
        }
        
        //retorna un booleano 
        //true = indica que el proceso  ya realizo la operacion
        //false =  indica que el proceso no realizo la operacion
        return operacionRealizada;
        
        /*
        -si el operacionRealiza = true y procesoBloqueado = true (de FileExplorer) el proceso realizo su operacion por completo y pero se debe bloquear
        acontinuacion
        -si el operacionRealiza = false y procesoBloqueado = true (de FileExplorer) el proceso no se realizo su operacion y se bloqueo
        -si el operacionRealiza = true y procesoBloqueado = false (de FileExplorer) el proceso realizo su operacion por completo y no se realizo bloqueo
        -(CASO IMPOSIBLE) si el operacionRealiza = false y procesoBloqueado = false (de FileExplorer)
        */   
    }
}
