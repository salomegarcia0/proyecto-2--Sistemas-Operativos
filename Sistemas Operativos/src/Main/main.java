package Main;

import Clases.CargadorSistema;
import Clases.SistemaArchivos;
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
        SistemaArchivos sistema = CargadorSistema.cargarSistema(rutaArchivo);
        if (sistema == null){
            sistema = CargadorSistema.cargarSistemaVacio();
        }
        
        interfazPrincipal v1 = new interfazPrincipal();
        v1.setVisible(true);
    }
    
}
