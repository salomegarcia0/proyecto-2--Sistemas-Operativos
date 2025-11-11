/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Clases.*;
/**
 *
 * @author pjroj
 */
public class SD {
    private NodoBloque head;
    private NodoBloque lector; //es como un apuntador hacia un bloque para indicar la ultima posicion del cabezal
    private int size; //el tamaño maximo del SD será 10 por el momento.
    
    
    public SD() {
        this.head = null;
        this.lector = null;
        this.size = 0;
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
    
    public void insertBegin(Bloque element) {
        NodoBloque nodo = new NodoBloque(element);
        if (isEmpty()) {
            setHead(nodo);
        } else {
           nodo.setNext(getHead());
           setHead(nodo);
        }
        size++;
    }
    
    public void insertFinal(Bloque element) {
        NodoBloque nodo = new NodoBloque(element);
        if (isEmpty()) {
            setHead(nodo);
        } else {
           NodoBloque pointer = getHead();
           while (pointer.getNext() != null) {
               pointer = pointer.getNext();
           }
           pointer.setNext(nodo);
        }
        size++;
    }
    
    public void insertInIndex(Bloque element, int index) {
        NodoBloque nodo = new NodoBloque(element);
        if (isEmpty()) {
            setHead(nodo);
        } else {
            if (index < 0) {
                System.out.println("Index Error");
            } else if (index > size) {
                System.out.println("Index Error");
            } else if (index == size) {
                insertFinal(element);
            } else if (index == 0) {
                insertBegin(element);
            } else {
                NodoBloque pointer = getHead();
                int aux = 0; 
                while (pointer.getNext() != null && aux < index - 1) {
                    pointer = pointer.getNext();
                    aux++;
                }
                nodo.setNext(pointer.getNext());
                pointer.setNext(nodo); 
            }
        }
        size++;
    }
    //
    public void insertInicialData(Archivo archivo){
        Nodo pointer = archivo.getBlockList().getHead();
        NodoBloque bloque = getHead();
        System.out.println("EN PROCESO");
//        while (pointer != null){
//            if (pointer.getElement() < 0) {
//                    System.out.println("Index Error");
//                } else if (pointer.getElement() > size) {
//                    System.out.println("Index Error");
//                } else if (pointer.getElement() == size) {
//                    //insertFinal(element);
//                } else if (pointer.getElement() == 0) {
//                    //insertBegin(element);
//                } else {
//                    //NodoBloque pointer = getHead();
//                    int aux = 0; 
//                    while (pointer.getNext() != null && aux < pointer.getElement() - 1) {
//                        pointer = pointer.getNext();
//                        aux++;
//                    }
//                    //nodo.setNext(pointer.getNext());
//                    //pointer.setNext(nodo); 
//                }
//            }
//            //pointer = pointer.getNext();
//        
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
