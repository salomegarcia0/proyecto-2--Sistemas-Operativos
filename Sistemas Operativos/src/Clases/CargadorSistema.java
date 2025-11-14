/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;

import Estructuras.ListaEnlazada;
import Tipos_de_Datos.TipoUsuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Clase para leer el json y poder cargar el sistema
 * @author salom
 */
public class CargadorSistema {
    
    public static SistemaArchivos cargarSistema(String rutaArchivo){
        try{
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new FileReader(rutaArchivo), JsonObject.class); //esto va a abrir el archivo
            
            //leer usuarios
            JsonArray arrayUsuarios = json.getAsJsonArray("usuarios");
            Usuario[] usuarios = cargarUsuarios(arrayUsuarios);
            
            JsonObject rootJson = json.getAsJsonObject("sistemaArchivos");
            Directorio root = construirDirectorio(rootJson, usuarios);
            
            return new SistemaArchivos(root, usuarios, usuarios.length);
            
        }catch (Exception e){
            return null;
        }
    }
    
    //este metodo es para cuando no carga el archivo o json o en su defecto no existe ninguno, se inicializa el sistema vacio
    public static SistemaArchivos cargarSistemaVacio(){
        Usuario admin = new Usuario("admin", TipoUsuario.ADMIN);
        Usuario user1 = new Usuario("user1", TipoUsuario.USER);
        Usuario user2 = new Usuario("user2", TipoUsuario.USER);
        
        Usuario[] usuarios = {admin, user1, user2};
        
        Directorio root = new Directorio("root", admin, 0, 0, 0, true);
        
        return new SistemaArchivos(root, usuarios, usuarios.length);
    }
    
    private static Usuario[] cargarUsuarios(JsonArray arrayUsuarios){
        Usuario [] usuarios = new Usuario[arrayUsuarios.size()];
        
        for (int i = 0; i < arrayUsuarios.size(); i++){
            JsonObject u = arrayUsuarios.get(i).getAsJsonObject();
            String nombre = u.get("nombre").getAsString();
            TipoUsuario tipo = u.get("tipo").getAsString().equals("ADMIN") ? TipoUsuario.ADMIN : TipoUsuario.USER;
            
            usuarios[i] = new Usuario(nombre, tipo);
        }
        return usuarios;
    }
    
    private static Usuario buscarUsuario(Usuario[] usuarios, String name) {
        for (Usuario usuario : usuarios) {
            if (usuario.getName().equals(name)) {
                return usuario;
            }
        }
        return usuarios[0];
    }
    
    private static Directorio construirDirectorio(JsonObject dirJson, Usuario[] usuarios){
        
        Directorio directorio;
        String name = dirJson.get("nombre").getAsString();
        Usuario owner = buscarUsuario(usuarios, dirJson.get("usuario").getAsString());
        
        if (dirJson.has("size") && dirJson.has("cantidadArchivos")){
            int size = dirJson.get("size").getAsInt();
            int cantidadArchivos = dirJson.get("cantidadArchivos").getAsInt();
            int cantidadSubDir = dirJson.get("cantidadSubDir").getAsInt();
            boolean esPublico = dirJson.has("esPublico") && dirJson.get("esPublico").getAsBoolean();
            
            directorio = new Directorio(name, owner, size, cantidadArchivos, cantidadSubDir, esPublico);
        } else {
            boolean esPublico = dirJson.has("esPublico") && dirJson.get("esPublico").getAsBoolean();
            directorio = new Directorio(name, owner, esPublico);
        }
        
        JsonArray contenido = dirJson.getAsJsonArray("contenido");
        if (contenido != null){
            for (int  i = 0; i < contenido.size(); i++ ){
                JsonObject elemento = contenido.get(i).getAsJsonObject();
                String tipo = elemento.get("tipo").getAsString();
                
                if("DIRECTORIO".equals(tipo)){
                    Directorio subDir = construirDirectorio(elemento, usuarios);
                    directorio.agregarElemento(subDir);
                } else if ("ARCHIVO".equals(tipo)){
                    Archivo archivo = construirArchivo(elemento, usuarios);
                    directorio.agregarElemento(archivo);
                }
            }
        }
        
        return directorio;
    }
    
    public static Archivo construirArchivo(JsonObject archivoJson, Usuario[] usuarios){
        
        String name = archivoJson.get("name").getAsString();
        int size = archivoJson.get("size").getAsInt();
        String usuarioStr = archivoJson.get("usuario").getAsString();
        
        Usuario owner = buscarUsuario(usuarios, usuarioStr);
        
        ListaEnlazada blockList = new ListaEnlazada();
        
        if(archivoJson.has("blockList") && !archivoJson.get("blockList").isJsonNull()){
            JsonArray bloquesJson = archivoJson.getAsJsonArray("blockList");
            for (int i = 0; i < bloquesJson.size(); i++){
                int bloque = bloquesJson.get(i).getAsInt();
                blockList.insertFinal(bloque);
            }
        }
        
        Archivo archivo = new Archivo(name, size, blockList, owner);
        
        return archivo;
    }
    
}
