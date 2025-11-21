/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Clases.*;
/**
 *
 * @author pjroj
 * El SD es una lista doblemente enlazada
 */
public class SD {
    private NodoBloque head,tail;
    private NodoBloque lector; //es como un apuntador hacia un bloque para indicar la ultima posicion del cabezal
    private int size; //el tamaño maximo del SD será 10 por el momento.
    private int sizeAvailable; //el tamaño de bloques disponibles, será todos aquellos bloques con available = true que es como se definieron inicialmente por defecto 
    private boolean reversa ; //
    
    public SD() {
        this.head = null;
        this.tail = null;
        this.lector = null;
        this.size = 0;
        this.sizeAvailable = 0;
        this.reversa = false;
    }

    public NodoBloque getHead() {
        return head;
    }

    public void setHead(NodoBloque head) {
        this.head = head;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public NodoBloque getTail() {
        return tail;
    }

    public void setTail(NodoBloque tail) {
        this.tail = tail;
    }

    public NodoBloque getLector() {
        return lector;
    }

    public void setLector(NodoBloque lector) {
        this.lector = lector;
    }

    public int getSizeDisponible() {
        return sizeAvailable;
    }

    public void setSizeDisponible(int sizeAvailable) {
        this.sizeAvailable = sizeAvailable;
    }

    public int getSizeAvailable() {
        return sizeAvailable;
    }

    public void setSizeAvailable(int sizeAvailable) {
        this.sizeAvailable = sizeAvailable;
    }

    public boolean isReversa() {
        return reversa;
    }

    public void setReversa(boolean reversa) {
        this.reversa = reversa;
    }
    
    /*
    Insercion de nodos de tipo Bloque
    */
    
    public void insertBegin(Bloque element) {
        NodoBloque nodo = new NodoBloque(element);
        if (isEmpty()) {
            setHead(nodo);
            setTail(nodo);
        } else {
            getHead().setPrevious(nodo);
            nodo.setNext(getHead());
            setHead(nodo);
        }
        //Para colocar el cabezal al inicio    
        setLector(getHead());
        size++;
        sizeAvailable++;
    }

    public void insertFinal(Bloque element) {
        NodoBloque nodo = new NodoBloque(element);
        if (isEmpty()) {
            setHead(nodo);
            setTail(nodo);
            setLector(getHead());
        } else {
            nodo.setPrevious(getTail());
            getTail().setNext(nodo);
            setTail(nodo);
        }
        size++;
        sizeAvailable++;
    }
    
    //se le pasaria el parametro sizeList que será la cantidad de bloque que tendra el SD, inicialmente lo definiremos como 10
    //todo los bloques se crearan con el 
    public void crearSD(int sizeList){
        String archivo = "vacio";
        for (int i = 0; i < sizeList; i++) {
            Bloque bloque = new Bloque(archivo,i);
            insertFinal(bloque);
        }
    }

    /*
    Todas esta funciones de insertData son funciones cuando ya al lista fue creada, es decir ya tiene la cantidad de bloques
    que definimos que tendría el SD, solo estamos accediendo a ellas para modificar la información que estos guardan
    necesita el nombre del archivo que se quiere guardar en el bloque
    */
    public void insertDataBegin(String nombreArchivo) {
        NodoBloque nodo = getHead();
        if (nodo.getElement().isAvailable() == false) {
            System.out.println("El primer bloque ya esta ocupado");
        } else if (nodo.getElement().isAvailable() == true){
            //para ubicar al cabezal en esta posicion
            setLector(nodo);
            //se setea el nombre del archivo que se esta guardando en partes
            nodo.getElement().setNameArchivo(nombreArchivo);
            //se cambia el estado del bloque a desocupado
            nodo.getElement().setAvailable(false);
            //se le resta uno a sizeAvailable ya que ahora un bloque estará ocupado
            sizeAvailable--;
        }
    }
    
    public void insertDataFinal(String nombreArchivo) {
        NodoBloque nodo = getTail();
        if (nodo.getElement().isAvailable() == false) {
            System.out.println("El último bloque ya esta ocupado");
        } else if (nodo.getElement().isAvailable() == true){
            //para ubicar al cabezal en esta posicion
            setLector(nodo);
            //se setea el nombre del archivo que se esta guardando en partes
            nodo.getElement().setNameArchivo(nombreArchivo);
            //se cambia el estado del bloque a desocupado
            nodo.getElement().setAvailable(false);
            //se le resta uno a sizeAvailable ya que ahora un bloque estará ocupado
            sizeAvailable--;
        }
    }
    
    /*
     Esta funcion de insertDataInIndex es solo para cargar los datos iniciales del sistema,ya que los archivos iniciales tienen guardado una lista
    con los apuntadores a los bloque donde se guardo la informacion
     */
     
    public void insertDataInIndex(Archivo archivo) {
        ListaEnlazada lista = archivo.getBlockList();
        Nodo nlista = lista.getHead();
        while(nlista != null){
            //index 0
            if ((int)nlista.getElement() == 0) {
                insertDataBegin(archivo.getName());
            
            //index = size - 1
            } else if ((int)nlista.getElement() == (size-1)){
                insertDataFinal(archivo.getName());
            } else {
                if (((int)nlista.getElement() > (int)((size-1)/2))) {
                    NodoBloque pointer = getTail();
                    int end = size - (int)nlista.getElement();
                    int aux = 0;    
                    while (aux < end) {
                        pointer = pointer.getPrevious();
                        //para ubicar al cabezal en esta posicion
                        setLector(pointer);
                        aux++;
                    }
                    //se inserta la informacion en el bloque y se cambia su estado a false (que esta ocupado)
                    pointer.getElement().setNameArchivo(archivo.getName());
                    pointer.getElement().setAvailable(false);
                    //se le resta uno a sizeAvailable ya que ahora un bloque estará ocupado
                    sizeAvailable--;
                } else {
                    int aux = 1; 
                    NodoBloque pointer = getHead();
                    while (aux < (int)nlista.getElement()) {
                        pointer = pointer.getNext();
                        //para ubicar al cabezal en esta posicion
                        setLector(pointer);
                        aux++;
                    }
                    //se inserta la informacion en el bloque y se cambia su estado a false (que esta ocupado)
                    pointer.getElement().setNameArchivo(archivo.getName());
                    pointer.getElement().setAvailable(false);
                    //se le resta uno a sizeAvailable ya que ahora un bloque estará ocupado
                    sizeAvailable--;
                }
            }
        nlista = nlista.getNext();
        }
    }
    
    public Bloque deleteBegin(){
        if (isEmpty()) {
            System.out.println("El SD esta vacio");
        } else {
            NodoBloque pointer = getHead();
            setHead(pointer.getNext());
            pointer.setNext(null);
            size--;
            return pointer.getElement();
        }
        return null;
    }
    
    public Bloque deleteFinal(){
        if (isEmpty()) {
            System.out.println("El SD esta vacio");
        } else {
            NodoBloque pointer = getHead();
            while (pointer.getNext().getNext() != null) {
                pointer = pointer.getNext();
            }
            pointer.setNext(null);
            size--;
            return pointer.getElement();
        }
        return null;
    }
    
    public Bloque deleteInIndex(int index) {
        if (isEmpty()) {
            System.out.println("El SD esta vacio");
        } else {
            if (index < 0) {
                System.out.println("Index Error");
            } else if (index >= size) {
                System.out.println("Index Error");
            } else if (index == 0) {
                return deleteBegin();
            } else if (index == size-1) {
                return deleteFinal();
            } else {
                NodoBloque pointer = getHead();
                int aux = 0; 
                while (pointer.getNext() != null && aux < index - 1) {
                    pointer = pointer.getNext();
                    aux++;
                }
                NodoBloque pointer2 = pointer.getNext();
                pointer.setNext(pointer2.getNext());
                pointer2.setNext(null);
                size--;
                return pointer.getElement();
            }
        }
        return null;
    }
    
    public void print() {
        NodoBloque pointer = getHead();
        int count = 1; //simplemente para la posicion del bloque
        while (pointer != null) {
            System.out.println("[ " + count + " - " + pointer.getElement().getNameArchivo() + " ]");
            pointer = pointer.getNext();
            count = count + 1;
        }
    }
    
    public boolean isEmpty() {
        return getHead() == null;
    }
    
}

