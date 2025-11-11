/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author pjroj
 */
public class Bloque {
    private int size;
    private boolean available; //inicialmente sera true, es decir, el bloque esta disponible para guardar informacion
    private String nameArchivo; //el nombre del archivo, si no hay uno, es decir, esta disponible será "vacio"

    public Bloque(int size, String nameArchivo) {
        this.size = size;
        this.available = true;
        this.nameArchivo = nameArchivo;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getNameArchivo() {
        return nameArchivo;
    }

    public void setNameArchivo(String nameArchivo) {
        this.nameArchivo = nameArchivo;
    }
    
    
}
