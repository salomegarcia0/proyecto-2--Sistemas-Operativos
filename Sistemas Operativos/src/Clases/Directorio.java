/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Estructuras.ListaEnlazada;

/**
 *
 * @author salom
 */
public class Directorio {
    private String name;
    private Usuario usuario;
    private ListaEnlazada elementos;
    private int size;//el tamaño total contenido, esto puede cambiar
    private int cantidadArchivos;
    private int cantidadSubDir;
    private boolean esPublico;

    /**
     * constructor JSON
    **/
    
    public Directorio(String name, Usuario usuario, int size, int cantidadArchivos, int cantidadSubDir, boolean esPublico) {
        this.name = name;
        this.usuario = usuario;
        this.size = size;//peso de los archivos, en nuestro caso son la cantidad de bloques que ocupan los archivos pero con esto simulamos los mb
        this.cantidadArchivos = cantidadArchivos;
        this.cantidadSubDir = cantidadSubDir;
        this.esPublico = esPublico;
        this.elementos = new ListaEnlazada();
    }
    
    /**
     * los directorios privados son solo para un ADMIN?
     * @param name
     * @param usuario
     * @param esPublico 
     */
    public Directorio(String name, Usuario usuario, boolean esPublico) {
        this.name = name;
        this.usuario = usuario;
        this.size = 0;
        this.cantidadArchivos = 0;
        this.cantidadSubDir = 0;
        this.esPublico = esPublico;
        this.elementos = new ListaEnlazada();
    }
    
    public void agregarElemento(Object elemento){
        if (elemento != null){
            elementos.insertFinal(elemento);
            
            if (elemento instanceof Archivo archivo){
                cantidadArchivos++;
                size += archivo.getSize();
            } else if(elemento instanceof Directorio) {
                Directorio subDir = (Directorio) elemento;
                cantidadSubDir ++;
                size += subDir.getSize();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
        
    public boolean estaVacio(){
        return elementos.getSize() == 0;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ListaEnlazada getElementos() {
        return elementos;
    }

    public void setElementos(ListaEnlazada elementos) {
        this.elementos = elementos;
    }

    public int getCantidadArchivos() {
        return cantidadArchivos;
    }

    public void setCantidadArchivos(int cantidadArchivos) {
        this.cantidadArchivos = cantidadArchivos;
    }

    public int getCantidadSubDir() {
        return cantidadSubDir;
    }

    public void setCantidadSubDir(int cantidadSubDir) {
        this.cantidadSubDir = cantidadSubDir;
    }

    public boolean isEsPublico() {
        return esPublico;
    }

    public void setEsPublico(boolean esPublico) {
        this.esPublico = esPublico;
    }
    
}
