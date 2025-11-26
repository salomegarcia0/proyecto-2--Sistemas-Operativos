/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

/**
 *AHHHH
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
   
    public boolean agregarUsuario(Usuario nuevo){
        if (nuevo == null){
            System.out.println("Error, usuario null");
            return false;
        }
        
        for (Usuario usuario : usuarios){
            if (usuario.getName().equals(nuevo.getName())){
                System.out.println("Error, el usuario ya exite");
                return false;
            }
        }
        
        try{
            Usuario[] nuevosUsuarios = new Usuario[usuarios.length + 1];
            System.arraycopy(usuarios, 0, nuevosUsuarios, 0, usuarios.length);
            nuevosUsuarios[usuarios.length] = nuevo;
            
            this.usuarios = nuevosUsuarios;
            this.totalUsuarios = usuarios.length;
            
            System.out.println("Usuario Agregado: " + nuevo.getName());
            
            return true;
            
        } catch (Exception e){
            System.out.print(e.getMessage());
            return false;
        }
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
