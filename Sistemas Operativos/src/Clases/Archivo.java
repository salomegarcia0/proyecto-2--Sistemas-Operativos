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
    
    //
    
    /*
    Son variables para saber si una operacion CRUD se realizo por completo y se pueden aplicar los cambios visualmente, es decir que si por
    ejemplo si se realiza una operacion de Modificacion sobre el archivo "A" (que se dividio en 3 bloques que es size) para cambiarle el nombre a "B" para
    cambiarle el nombre se generan 3 procesos de modificacion para cada uno de los bloques por lo que el contador de countModificar ira
    aumentando cada vez que completen c/u de estos procesos, al llegar el countModificar = size, significa que ya se pueden aplicar los cambios
    */
    private int countLectura;
    private int countModificar;
    
    /*
    
    */
    private boolean eliminacion;
    private boolean crear;

    public Archivo(String name, int size, ListaEnlazada blockList, Usuario usuario) {
        this.name = name;
        this.size = size;
        this.blockList = blockList;
        this.usuario = usuario;
        this.countLectura = this.countModificar = 0;
        this.eliminacion = this.crear =  false;
    }
    
    public void aplicarCambios(String nombreArchivo){
        if(countModificar == size){
            String nombreViejo = name;
            name = nombreArchivo;
            System.out.println("Se completo el cambio del nombre del archivo '" + nombreViejo + "' a " + name + "'");
            countModificar = 0;
        }
        
    }
    
    public void aplicarLectura(){
        if(countLectura == size){
            System.out.println("Se completo la solicitud de lectura del archivo " + name);
            System.out.println("ME LEISTE FELICIDADES :D");
            countLectura = 0;
        }
        
    }
    
    public void aplicarCreacion(){
        //se verifica si el tamaño de la lista de bloques coincide con el tamaño en el que debe esatr dividido el archivo
        if(blockList.getSize() == size){
            System.out.println("Se completo la creacion del archivo " + name);
            blockList.print();
            crear =  true;
        }
    }
    
    public void aplicarEliminar(){
        if(blockList.isEmpty() == true){
            System.out.println("Se completo la eliminacion del archivo " + name);
            System.out.println("FALTA ELIMINARLO DEL ARBOL PRIMERO Y LUEGO LA CLASE");
            //AQUI FALTARIA VER COMO HACER PARA ELIMANAR ESTE ARCHIVO, NO COMO TAL LA CLASE SINO DEL ARBOL ANTES DE ELIMAR LA CLASE
        }
    }
    
    public boolean isProcesoEliminizacion(){
        return size > blockList.getSize();
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

    public int getCountLectura() {
        return countLectura;
    }

    public void setCountLectura(int countLectura) {
        this.countLectura = countLectura;
    }
    
    public boolean completeLectura(){
        return countLectura == size;
    }

    public int getCountModificar() {
        return countModificar;
    }

    public void setCountModificar(int countModificar) {
        this.countModificar = countModificar;
    }
    
    public boolean completeModificar(){
        return countModificar == size;
    }

    public boolean isCrear() {
        return crear;
    }

    public void setCrear(boolean crear) {
        this.crear = crear;
    }
        
    public String toString(){
        return this.name;
    }
}
