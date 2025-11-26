package Main;
import java.util.Random;

import Clases.CargadorSistema;
import Clases.SistemaArchivos;
import Clases.Usuario;
import Clases.Archivo;
import Estructuras.ListaEnlazada;
import Estructuras.SD;
import Tipos_de_Datos.*;
import Interfaz.interfazPrincipal;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


/**
 *
 * @author pjroj
 */
public class main {
    static String rutaArchivo = "sistema_archivos.json"; //json
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        System.out.println("PRUEBA");
//        Usuario user = new Usuario("Andrea",TipoUsuario.USER);
//        ListaEnlazada list = new ListaEnlazada();
//        list.insertFinal(2);
//        list.insertFinal(4);
//        list.insertFinal(8);
//        int size = list.getSize();
//        Archivo prueba = new Archivo("Prueba.txt", size, list, user);
//        
//        SD sd = new SD();
//        sd.crearSD(10);
//        FileExplorer.setSD(sd);
//        
//        SD sd2 = FileExplorer.getSD();
//        sd2.insertDataInIndex(prueba);
//        System.out.println("Bloques disponibles");
//        System.out.println(sd2.getSizeAvailable());
//        sd2.print();
//        
//        System.out.println("FIN DE PRUEBA");

//        Thread t1 = new Thread(() -> funcionA());
//        Thread t2 = new Thread(() -> funcionB());
//            
//            t1.start();
//            t2.start();
            
            

        
        SistemaArchivos sistema = CargadorSistema.cargarSistema();
        if (sistema == null){
            sistema = CargadorSistema.cargarSistemaVacio();
        }
        
        interfazPrincipal v1 = new interfazPrincipal();
        v1.setResizable(false);
        v1.setLocationRelativeTo(null);
        v1.setVisible(true);
    }
    
//    static void funcionA(){
//        for (int i = 0; i < 5; i++) {
//            System.out.println("A ejecutando paso " + i);
//            try { Thread.sleep(5000); } catch (InterruptedException e) {}   
//        }
//    }
//    
//    static void funcionB(){
//        for (int i = 0; i < 5; i++) {
//            System.out.println("B ejecutando paso " + i);
//            try { Thread.sleep(7000);} catch (InterruptedException e) {}   
//        }
//    }
}
