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

    // Funcion para eliminar un proceso de la cola
    public PCB desColar() {
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
            } 
            System.out.println("[ Id: "+ pointer.getProceso().getProcesoID() + " | Nombre: " + pointer.getProceso().getProcesoNombre()); 
            pointer = pointer.getNext();
        }
        if(isEmpty()){
            System.out.println("vacio");
        }
    }
    
    
}
