/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import Tipos_de_Datos.TipoUsuario;
/**
 *
 * @author pjroj
 */
public class Usuario {
    private String name;
    private TipoUsuario type;

    public Usuario(String name, TipoUsuario type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TipoUsuario getType() {
        return type;
    }

    public void setType(TipoUsuario type) {
        this.type = type;
    }
    
}
