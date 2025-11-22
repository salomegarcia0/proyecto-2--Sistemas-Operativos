/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Estructuras;
import Clases.Bloque;

/**
 *
 * @author pjroj
 */
public class NodoBloque {
    private Bloque element;
    private NodoBloque next,previous;

    public NodoBloque(Bloque element) {
        this.element = element;
        this.next = null;
        this.previous = null;
    }

    public Bloque getElement() {
        return element;
    }

    public void setElement(Bloque element) {
        this.element = element;
    }

    public NodoBloque getNext() {
        return next;
    }

    public void setNext(NodoBloque next) {
        this.next = next;
    }

    public NodoBloque getPrevious() {
        return previous;
    }

    public void setPrevious(NodoBloque previous) {
        this.previous = previous;
    }
    
}
