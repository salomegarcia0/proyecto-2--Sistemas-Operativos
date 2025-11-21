/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Main.FileExplorer;
/**
 *
 * @author pjroj
 */
public class Bloque {
    private int size;
    private boolean available; //inicialmente sera true, es decir, el bloque esta disponible para guardar informacion
    private String nameArchivo; //el nombre del archivo, si no hay uno, es decir, esta disponible será "vacio"
    private int index; //para tener el index de la ubicacion del bloque en el SD, eso simplificaria el obtener el apuntador de los bloques para agregarlos a las listas de los archivos

    public Bloque(String nameArchivo, int index) {
        this.size = FileExplorer.getSizeBloque();
        this.available = true;
        this.nameArchivo = nameArchivo;
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    
}
