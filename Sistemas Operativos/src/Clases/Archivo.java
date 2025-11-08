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
    private int size;
    private ListaEnlazada blockList;
    private Usuario usuario;   

    public Archivo(String name, int size, ListaEnlazada blockList, Usuario usuario) {
        this.name = name;
        this.size = size;
        this.blockList = blockList;
        this.usuario = usuario;
    }
        
}
