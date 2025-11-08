/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;

/**
 *
 * @author salom
 */
public class NodoBloque {
    private Object element;
    private NodoBloque next;

    public NodoBloque(Object element) {
        this.element = element;
        this.next = null;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    public NodoBloque getNext() {
        return next;
    }

    public void setNext(NodoBloque next) {
        this.next = next;
    }
    
}
