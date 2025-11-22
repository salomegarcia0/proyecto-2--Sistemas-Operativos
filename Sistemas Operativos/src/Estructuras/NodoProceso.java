/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Clases.PCB;
/**
 * Clase para crear nodos de tipo PCB
 * @author salom
 */
public class NodoProceso {

    private NodoProceso next;
    private PCB proceso;

    public NodoProceso(PCB proceso) {
        this.next = null;
        this.proceso = proceso;
    }

    public NodoProceso getNext() {
        return next;
    }

    public void setNext(NodoProceso next) {
        this.next = next;
    }

    public PCB getProceso() {
        return proceso;
    }

    public void setProceso(PCB proceso) {
        this.proceso = proceso;
    }
    
}
