/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Estructuras.ListaEnlazada;
import Clases.Usuario;

/**
 *
 * @author pjroj
 */
public class Directorio {
    private String name; 
    private Usuario usuario; 
    private int size;//el tamaño total contenido, esto puede cambiar

    public Directorio(String name, int size, Usuario usuario) {
        this.name = name;
        this.size = size;
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
        
    
}
