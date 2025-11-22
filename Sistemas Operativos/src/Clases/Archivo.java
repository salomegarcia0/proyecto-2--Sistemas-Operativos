/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import Estructuras.ListaEnlazada;

/**
 *
 * @author pjroj
 */
public class Archivo {
    private String name;
    private int size; //cantidad de bloques es la que esta dividio o se dividira
    private ListaEnlazada blockList;
    private Usuario usuario;   

    public Archivo(String name, int size, ListaEnlazada blockList, Usuario usuario) {
        this.name = name;
        this.size = size;
        this.blockList = blockList;
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

    public ListaEnlazada getBlockList() {
        return blockList;
    }

    public void setBlockList(ListaEnlazada blockList) {
        this.blockList = blockList;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
        
    
}
