/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Clases.PCB;
import Tipos_de_Datos.*;
/**
 * Clase para crear colas para organizar los procesos
 * @author salom
 */
public class Cola {
    private NodoProceso head, tail;
    private int size;
    private String nombre;
    
    public Cola(){
        this.head = this.tail = null;
        size = 0;
    }
    
    public Cola(String nombre) {
        this.head = this.tail = null;
        size = 0;
        this.nombre = nombre;
    }

    public NodoProceso getHead() {
        return head;
    }

    public void setHead(NodoProceso head) {
        this.head = head;
    }

    public NodoProceso getTail() {
        return tail;
    }

    public void setTail(NodoProceso tail) {
        this.tail = tail;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    // Funcion para agregar un proceso a la cola
    public void enColar(PCB proceso) {
        NodoProceso nodo = new NodoProceso (proceso);
        if (isEmpty()) {
            setHead(nodo);
            setTail(nodo);
        } else {
            getTail().setNext(nodo);
            setTail(nodo);
        }
        size++;
    }

    // Funcion para eliminar un proceso al inicio de la cola 
    public PCB desColarInicio() {
        if (isEmpty()) {
            System.out.println("La lista esta vacia");
            return null;
        } else {
            NodoProceso pointer = getHead();
            PCB proceso = pointer.getProceso();
            if(pointer != getTail()){
                setHead(pointer.getNext());
                pointer.setNext(null);
                size--;
            }else {
                setHead(null);
                pointer.setNext(null);
                setTail(null);
                size--;
            }
            
            return proceso;
        }
    }
    
    // Funcion para eliminar un proceso al final de la cola 
    public PCB desColarFinal() {
        if (isEmpty()) {
            System.out.println("La lista esta vacia");
            return null;
        } else {
            NodoProceso pointer = getHead();
            //me posiciono en el antepenultimo PCB de la cola
            while (pointer.getNext() != getTail()){
                pointer = pointer.getNext();
            }
            PCB proceso = getTail().getProceso();
            if(pointer != getHead()){
                setTail(pointer);
                pointer.setNext(null);
                size--;
            }else {
                setHead(null);
                pointer.setNext(null);
                setTail(null);
                size--;
            }
            
            return proceso;
        }
    }
    
    //index es ubicacion del proceso en la cola iniciado desde 0
    public PCB desColarIntermedio(int index){
        if (isEmpty()) {
            System.out.println("La cola esta vacia");
        } else {
            if (index < 0) {
                System.out.println("Index Error");
            } else if (index >= size) {
                System.out.println("Index Error");
            } else if (index == 0) {
                return desColarInicio();
            } else if (index == size-1) {
                return desColarFinal();
            } else {
                NodoProceso pointer = getHead();
                int aux = 0; 
                while (pointer.getNext() != null && aux < index - 1) {
                    pointer = pointer.getNext();
                    aux++;
                }
                NodoProceso pointer2 = pointer.getNext();
                pointer.setNext(pointer2.getNext());
                pointer2.setNext(null);
                size--;
                return pointer.getProceso();
            }
        }
        return null;
    }
        
    public boolean isEmpty() {
        return getHead() == null && getTail() == null;
    }
    
    public void print() {
        NodoProceso pointer = getHead();
        while (pointer != null) {
            String tproceso;
            TipoProceso tipo = pointer.getProceso().getTipoProceso();
            if (tipo == TipoProceso.CREAR){
                tproceso = "CREAR";
            }else if (tipo == TipoProceso.ELIMINAR){
                tproceso = "ELIMINAR";
            }else if (tipo == TipoProceso.MODIFICAR){
                tproceso = "MODIFICAR";
            }else if (tipo == TipoProceso.LEER){
                tproceso = "LEER";
            }
            System.out.println("[ Id: "+ pointer.getProceso().getProcesoID() + " | Nombre: " + pointer.getProceso().getProcesoNombre()); 
            pointer = pointer.getNext();
        }
        if(isEmpty()){
            System.out.println("vacio");
        }
    }
    
    
}
