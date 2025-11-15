/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *
 * @author salom
 */
public class SistemaArchivos {
    private Directorio root;
    private Usuario[] usuarios;
    private int totalUsuarios;
    
    

    public SistemaArchivos(Directorio root, Usuario[] usuarios, int totalUsuarios) {
        this.root = root;
        this.usuarios = usuarios;
        this.totalUsuarios = totalUsuarios;
    }

    private static Usuario buscarUsuario(Usuario[] usuarios, String name) {
        for (Usuario usuario : usuarios) {
            if (usuario.getName().equals(name)) {
                return usuario;
            }
        }
        return usuarios[0];
    }
    
    
    
    public Directorio getRoot() {
        return root;
    }

    public void setRoot(Directorio root) {
        this.root = root;
    }

    public Usuario[] getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuario[] usuarios) {
        this.usuarios = usuarios;
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }
    
    
}
