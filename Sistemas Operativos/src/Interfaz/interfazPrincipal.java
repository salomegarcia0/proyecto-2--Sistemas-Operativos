/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaz;

import Clases.Archivo;
import Clases.Bloque;
import Clases.CargadorSistema;
import Clases.Directorio;
import Clases.SistemaArchivos;
import Clases.Usuario;
import Estructuras.Nodo;
import Estructuras.NodoBloque;
import Estructuras.SD;
import Main.FileExplorer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author salom
 */
public class interfazPrincipal extends javax.swing.JFrame {
    private SistemaArchivos sistema;
    private DefaultTableModel modeloTablaArchivos;
    private Usuario usuarioActual;
    
    /**
     * Creates new form interfazPrincipal
     */
    public interfazPrincipal(){
        
        if(FileExplorer.getSD() == null){
            SD disco = new SD();
            disco.crearSD(15);
            FileExplorer.setSD(disco);
        }
        
        initComponents();
        cargarArbol();
        
        debugSDCompleto();
        cargarDatosEnSD();
        debugSDCompleto();
        
        cargarTablaArchivos();
        actualizarComboUsuarios();
        actualizarComboPoliticas();
        
        
    }
    
    private void actualizarComboUsuarios(){
        String seleccionAnterior = (String) comboUsuarios.getSelectedItem();
        comboUsuarios.removeAllItems();
        
        if (sistema != null && sistema.getUsuarios() != null){
            for (Usuario usuario : sistema.getUsuarios()){
                String item = usuario.getName() + " (" + (usuario.getType().name().equals("ADMIN")? "Administrador" : "Usuario") + ")";
                comboUsuarios.addItem(item);
            }
        }
        
        if (seleccionAnterior != null){
            comboUsuarios.setSelectedItem(seleccionAnterior);
        } else if (comboUsuarios.getItemCount() > 0){
            comboUsuarios.setSelectedIndex(0);
        }
   
    }
    
    private void actualizarComboPoliticas(){
        comboPoliticas.removeAllItems();
        comboPoliticas.addItem("FIFO");
        comboPoliticas.addItem("LIFO");
        comboPoliticas.addItem("C-SCAN");
        comboPoliticas.addItem("SCAN");
        comboPoliticas.addItem("SSTF");
        
        comboPoliticas.setSelectedItem("FIFO");
    }
    
    private void cargarArbol(){
        sistema = CargadorSistema.cargarSistema(); //ojo aca que solo acepta el archivo con este nombre, revisar solo prueba
        
        if (sistema == null){
            sistema = CargadorSistema.cargarSistemaVacio();
        }
        
        DefaultMutableTreeNode nodoRoot = construirNodoArbol(sistema.getRoot());
        
        arbolSistema.setModel(new javax.swing.tree.DefaultTreeModel(nodoRoot));
    }
    
    private DefaultMutableTreeNode construirNodoArbol(Directorio dir){
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(dir.getName());
        
        Nodo aux = dir.getElementos().getHead();
        
        while (aux != null){
            Object elemento = aux.getElement();

            if (elemento instanceof Directorio){
                nodo.add(construirNodoArbol((Directorio) elemento));
            } else if (elemento instanceof Archivo ){
                Archivo archivo = (Archivo) elemento;
                nodo.add(new DefaultMutableTreeNode(archivo.getName()));
            }
            
            aux = aux.getNext();
        }
        
        return nodo;
    }
    
    //PRIMERA PARTE DEL TABBED PANE DONDE ESTÁ LA TABLA
    private void cargarTablaArchivos(){
        modeloTablaArchivos = new DefaultTableModel(new Object[]{"Nombre", "Tamaño", "Primer Bloque", "Bloques", "Usuario"}, 0);
        if (tablaArchivos == null){
            return;
        }
        
        tablaArchivos.setModel(modeloTablaArchivos);
        if (sistema != null && sistema.getRoot() != null){
            llenarTablaJson(sistema.getRoot(), modeloTablaArchivos);
        } else {
            System.out.println("error llenando tabla");
        }
    }
    
    private void llenarTablaJson(Directorio dir, DefaultTableModel modelo){
        if (dir == null || dir.getElementos() == null){
            return;
        }
        
        Nodo aux = dir.getElementos().getHead();
        
        while(aux != null){
            Object elemento = aux.getElement();
            
            if (elemento instanceof Directorio){
                llenarTablaJson((Directorio) elemento, modelo);
            } else if (elemento instanceof Archivo){
                Archivo archivo = (Archivo) elemento;
                modelo.addRow(new Object[]{
                    archivo.getName(),
                    archivo.getSize(),
                    obtenerPrimerBloque(archivo),
                    obtenerListaBloques(archivo),
                    archivo.getUsuario().getName(),
                });
                System.out.println("Archivo "+archivo.getName()+" agregado");
            }
            
            aux = aux.getNext();
        }
    }
    
    
    private String obtenerListaBloques(Archivo archivo){
        if (archivo.getBlockList() == null || archivo.getBlockList().getHead() == null){
            return "[]";
        }
        StringBuilder listaBloques = new StringBuilder();
        Nodo nodoActual = archivo.getBlockList().getHead();
        
        listaBloques.append("[");
        
        while (nodoActual != null){
            if (nodoActual.getElement() instanceof Integer){
                int bloque = (Integer) nodoActual.getElement();
                listaBloques.append(bloque);
                
                if(nodoActual.getNext() != null){
                    listaBloques.append(" , ");
                }
            }
            nodoActual = nodoActual.getNext();
        }
        
        listaBloques.append("]");
        String resultado = listaBloques.toString();
        return resultado;
    }
    
    private int obtenerPrimerBloque(Archivo archivo){
        if (archivo.getBlockList() == null || archivo.getBlockList().getHead() == null){
            return -1;
        }
        
        Nodo primerNodo = archivo.getBlockList().getHead();
        if (primerNodo.getElement() instanceof Integer){
            return (Integer) primerNodo.getElement();
        }
        
        return -1;
    }
    
    // Las siguientes dos funciones verifican que es lo que cada usuario puede ver
    //admin puede ver todo
    //otro usuarios solo pueden ver archivos publicos, admin y archivos propios
    private boolean puedeVerDirectorio(Directorio directorio){
        if (usuarioActual == null ) return false;
        
        if (usuarioActual.getType().name().equals("ADMIN")){
           return true; 
        }
        
        return directorio.isEsPublico() || directorio.getUsuario().getName().equals(usuarioActual.getName()) 
                || directorio.getUsuario().getName().equals("admin");  
    }
    
    private boolean puedeVerArchivo(Archivo archivo){
        if (usuarioActual == null ) return false;
        
        if (usuarioActual.getType().name().equals("ADMIN")){
            return true;
        }
        
        return archivo.getUsuario().getName().equals(usuarioActual.getName()) 
                || archivo.getUsuario().getName().equals("admin"); 
        
    }
    
    private DefaultMutableTreeNode construirNodoArbolFiltrado(Directorio dir){
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(dir.getName());
        
        Nodo aux = dir.getElementos().getHead();
        
        while (aux != null){
            Object elemento = aux.getElement();
            
            if (elemento instanceof Directorio){
                Directorio subDir = (Directorio) elemento;
                
                if (puedeVerDirectorio(subDir)){
                    DefaultMutableTreeNode subNodo = construirNodoArbolFiltrado(subDir);
                    
                    if (subNodo.getChildCount() > 0 || subDir.getElementos().getSize() == 0){
                        nodo.add(subNodo);
                    }
                }
            } else if (elemento instanceof Archivo){
                Archivo archivo = (Archivo) elemento;
                
                if (puedeVerArchivo(archivo)){
                    DefaultMutableTreeNode nodoArchivo = new DefaultMutableTreeNode(archivo.getName()+ " " + archivo.getSize());
                    nodoArchivo.setUserObject(archivo);
                    nodo.add(nodoArchivo);
                }
            }
            aux = aux.getNext();
        }
        
        return nodo;
    }
 
    
    private void cargarArbolFiltrado(){
        DefaultMutableTreeNode nodoRoot = construirNodoArbolFiltrado(sistema.getRoot());
        arbolSistema.setModel(new javax.swing.tree.DefaultTreeModel(nodoRoot));
    }
    
    private void llenarTablaFiltrada(Directorio dir, DefaultTableModel modelo){
        if (dir == null || dir.getElementos() == null) return;
        
        Nodo aux = dir.getElementos().getHead();
        
        while(aux != null){
            Object elemento = aux.getElement();
            
            if (elemento instanceof Directorio){
                if(puedeVerDirectorio((Directorio) elemento)){
                    llenarTablaFiltrada((Directorio) elemento, modelo);
                }
            } else if (elemento instanceof Archivo){
                Archivo archivo = (Archivo) elemento;
                
                if (puedeVerArchivo(archivo)){
                    modelo.addRow(new Object[]{
                        archivo.getName(),
                        archivo.getSize(),
                        obtenerPrimerBloque(archivo),
                        obtenerListaBloques(archivo),
                        archivo.getUsuario().getName()
                    });
                }
            }
            aux = aux.getNext();
        }
    }
    
    private void cargarTablaArchivosFiltrada(){
        modeloTablaArchivos = new DefaultTableModel(new Object[]{"Nombre", "Tamaño", "Primer Bloque", "Bloques", "Usuario"}, 0);
        
        tablaArchivos.setModel(modeloTablaArchivos);
        
        if (sistema != null && sistema.getRoot() != null){
            llenarTablaFiltrada(sistema.getRoot(), modeloTablaArchivos);
        }
    }
    
    private void actualizarLabelUsuario(){
        if (usuarioActual == null){
            usuarioEnUso.setText("No seleccionado");
            return;
        }
        
        String textoUsuario = usuarioActual.getName();
        
        if (usuarioActual.getType().name().equals("ADMIN")){
            textoUsuario += (" (Administrador)");
            usuarioEnUso.setForeground(Color.RED);
        } else {
            textoUsuario += (" (Usuario)");
            usuarioEnUso.setForeground(Color.BLUE);
        }
        
        usuarioEnUso.setText(textoUsuario);
    }
    
    private void actualizarInterfazCompleta(){
        cargarArbolFiltrado();
        
        cargarTablaArchivosFiltrada();
        
        generarBloquesSD();
        
    }
    
    //==========================================================================
    
    //POLITICASSSS
    private void aplicarPoliticaEnSistema(String politica){
        // aca es dodnde voy a escribir el codigo para las politicas
        // hablar con andrea para hacerlo juntas !!!!!!
        
        System.out.println("Aplicando Politicaaaaaaaaaaaaaa");
    }
    
    private void aplicarPoliticaSeleccionada(){
        String politicaSeleccionada = (String) comboPoliticas.getSelectedItem();
        
        if (politicaSeleccionada == null){
            System.out.println("No se selecciono ninguna politica");
            return;
        }
        
        aplicarPoliticaEnSistema(politicaSeleccionada);
    }
    
    private void cargarDatosEnSD(){
        SD disco = FileExplorer.getSD();
        if (disco == null){
            System.out.println("Fallo cargando SD, SD null, VERIFICR CARGAR DATOS SD");
            return;
        }
        
        if (sistema != null && sistema.getRoot() != null){
            asignarBloquesArchivos(sistema.getRoot());
        }
        
        actualizarEstadisticasSD();
        
        generarBloquesSD();
    }
     //esta funcion recorre los archivos y asigna recursivamente los bloques
    private void asignarBloquesArchivos(Directorio dir){
        if (dir == null || dir.getElementos() == null) return;
        
        Nodo aux = dir.getElementos().getHead();
        
        while (aux != null){
            Object elemento = aux.getElement();
            
            if (elemento instanceof Directorio){
                asignarBloquesArchivos((Directorio) elemento);
            } else if (elemento instanceof Archivo) {
                Archivo archivo = (Archivo) elemento;
                asignarBloquesArchivo(archivo);
            }
            aux = aux.getNext();
        }
    }
    
    //asigna bloques para un solo archivo especiffico
    private void asignarBloquesArchivo(Archivo archivo){
        SD disco = FileExplorer.getSD();
        if(disco == null || archivo.getBlockList() == null) return;
        
        Nodo nodoBloque = archivo.getBlockList().getHead();
        
        while(nodoBloque != null){
            if (nodoBloque.getElement() instanceof Integer){
                int numeroBloque = (Integer) nodoBloque.getElement();
                
                NodoBloque nodoSD = disco.getHead();
                
                int contador = 0;
                
                while (nodoSD != null && contador <= numeroBloque){
                    if (contador == numeroBloque){
                        Bloque bloque = nodoSD.getElement();
                        bloque.setAvailable(false);
                        bloque.setNameArchivo(archivo.getName());
                        bloque.setNameArchivo(archivo.getName());
                        
                        System.out.println("Bloque: " + numeroBloque + " asignado a: " + archivo.getName());
                        break;
                    }
                    
                    nodoSD = nodoSD.getNext();
                    contador++;
                }
            }
            
            nodoBloque = nodoBloque.getNext();
        }
    }
    
    //==========================================================================
    //2DA PESTAÑA DEL TABBED PANE, SD, ACÁ SERÁN VISIBLES LOS BLOQUES
    private void generarBloquesSD(){
        panelDisco.removeAll();
        panelDisco.setLayout(null);
        
        int width = 60;
        int height = 60;
        int separacion = 0;
        
        int x = 20;
        int y = 20;
        
        SD disco = FileExplorer.getSD();
        if (disco == null){
            System.out.println("error, no se inicio SD");
            return;
        }
        
        NodoBloque nodoActual = disco.getHead();
        int contador = 0;
        int maxBloques = disco.getSize();
        
        while (nodoActual != null && contador < maxBloques){
            Bloque bloqueReal = nodoActual.getElement();
            JPanel panelBloque = new JPanel();
            panelBloque.setLayout(new BorderLayout());
            
            if (bloqueReal.isAvailable()){
                panelBloque.setBackground(Color.GREEN);
            } else {
                panelBloque.setBackground(Color.RED);
            }
            
            panelBloque.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            panelBloque.setBounds(x, y, width, height);
            
            JLabel labelNum = new JLabel(String.valueOf(contador), SwingConstants.CENTER);
            labelNum.setFont(new Font("Arial", Font.PLAIN, 16));
            panelBloque.add(labelNum, BorderLayout.CENTER);
            
            //mostrar el nombre del archivo en el bloque pero esto capaz lo quite pq pa q    
            if (!bloqueReal.isAvailable()){
                String nombreArchivo = bloqueReal.getNameArchivo();
                
                if (nombreArchivo.length() > 8){
                    nombreArchivo = nombreArchivo.substring(0,6)+"...";
                }
                
                JLabel lblArchivo = new JLabel(nombreArchivo, SwingConstants.CENTER);
                lblArchivo.setFont(new Font("Arial", Font.PLAIN, 9));
                lblArchivo.setForeground(Color.WHITE);
                panelBloque.add(lblArchivo, BorderLayout.SOUTH);
            }
            
            String tooltip = "Bloque" + contador + " - " + (bloqueReal.isAvailable() ? "LIBRE" : "OCUPADO POR: " + bloqueReal.getNameArchivo());
            panelBloque.setToolTipText(tooltip);
            
            panelDisco.add(panelBloque);
            
            if (contador == 7){
                
                x = 20;
                y += height + separacion + 0;
            } else {
                x += width + separacion;
            }
            
            nodoActual = nodoActual.getNext();
            contador++;
        }
        
        panelDisco.repaint();
        panelDisco.revalidate();
        
        actualizarEstadisticasSD();
    }
    
    private void actualizarEstadisticasSD(){
        SD disco = FileExplorer.getSD();
        if (disco == null){
            return;
        }

        int totalBloques = disco.getSize();
        int bloquesOcupados = contarBloquesOcupadosManual(disco);
        int bloquesLibres = totalBloques - bloquesOcupados;
        
        if(lblBloquesTotales != null){
           lblBloquesTotales.setText(String.valueOf(totalBloques));
        }
        
        if(lblBloquesOcupados != null){
           lblBloquesOcupados.setText(String.valueOf(bloquesOcupados));
        }
        
        if(lblbloquesLibres != null){
           lblbloquesLibres.setText(String.valueOf(bloquesLibres));
        }
    }
    
    // con este metodo voy a tratar de arreglar el problema con el contador en SD para no tocar alla
    private int contarBloquesOcupadosManual(SD disco){
        int ocupados = 0;
        NodoBloque actual = disco.getHead();
        
        while(actual != null){
            if (!actual.getElement().isAvailable()){
                ocupados++;
            }
            
            actual = actual.getNext();
        }
        return ocupados;
    }
    
    //==========================================================================
    
    //DEBUG BORRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAR
    
    private void debugSDCompleto() {
        System.out.println("\n===  DEBUG COMPLETO SD ===");

        // verificar SD
        SD disco = FileExplorer.getSD();
        if (disco == null) {
            System.out.println("SD es null en FileExplorer");
            return;
        }

        System.out.println("SD basico:");
        System.out.println("   - disco.getSize(): " + disco.getSize());
        System.out.println("   - disco.getSizeAvailable(): " + disco.getSizeAvailable());
        System.out.println("   - disco.getHead(): " + (disco.getHead() != null ? "EXISTE" : "NULL"));

        // verifica si los datos se cargaron del JSON
        System.out.println("\n VERIFICANDO CARGA DE DATOS:");
        if (sistema != null && sistema.getRoot() != null) {
            verificarArchivosCargados(sistema.getRoot());
        } else {
            System.out.println(" Sistema o root es null");
        }

        //estado real de cada bloque en el SD
        System.out.println("\nESTADO REAL DE BLOQUES:");
        NodoBloque actual = disco.getHead();
        int contador = 0;
        int ocupadosReales = 0;

        while (actual != null && contador < disco.getSize()) {
            Bloque bloque = actual.getElement();
            String estado = bloque.isAvailable() ? "LIBRE" : "OCUPADO";
            String archivo = bloque.isAvailable() ? "" : " por '" + bloque.getNameArchivo() + "'";

            System.out.println("   Bloque " + contador + ": " + estado + archivo);

            if (!bloque.isAvailable()) {
                ocupadosReales++;
            }

            actual = actual.getNext();
            contador++;
        }

        // compara con lo que deberia estar ocupado segun JSON
        System.out.println(" COMPARACIoN:");
        System.out.println("   - Bloques ocupados (reales): " + ocupadosReales);
        System.out.println("   - Bloques libres (reales): " + (disco.getSize() - ocupadosReales));
        System.out.println("   - SizeAvailable(): " + disco.getSizeAvailable());
        System.out.println("   - Diferencia: " + (ocupadosReales - (disco.getSize() - disco.getSizeAvailable())));

        System.out.println("=== FIN DEBUG ===\n");
    }

    private void verificarArchivosCargados(Directorio dir) {
        if (dir == null || dir.getElementos() == null) return;

        Nodo aux = dir.getElementos().getHead();
        int archivosConBloques = 0;
        int archivosSinBloques = 0;

        while (aux != null) {
            Object elemento = aux.getElement();

            if (elemento instanceof Directorio) {
                verificarArchivosCargados((Directorio) elemento);
            } else if (elemento instanceof Archivo) {
                Archivo archivo = (Archivo) elemento;
                if (archivo.getBlockList() != null && archivo.getBlockList().getHead() != null) {
                    archivosConBloques++;
                    System.out.println(archivo.getName() + " - Bloques: " + obtenerListaBloques(archivo));
                } else {
                    archivosSinBloques++;
                    System.out.println(archivo.getName() + " - SIN bloques asignados");
                }
            }

            aux = aux.getNext();
        }

        if (archivosConBloques + archivosSinBloques > 0) {
            System.out.println(" Resumen: " + archivosConBloques + " con bloques, " + archivosSinBloques + " sin bloques");
        }
    }

    //===================================================================
    
    //COLAS INTERFAZ
    
    
    
    //FIN COLAS INTERFAZ
    
    
    //===================================================================
    //CRUD ACCCIONES PARA LA INTERFAZ
    
    private void crear(){
        DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolSistema.getLastSelectedPathComponent();
        if (nodoSeleccionado == null){
            JOptionPane.showMessageDialog(this, "Seleccione un directorio donde crear el elemento", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String[] opciones = {"Archivos", "Directorio"};
        int choice = JOptionPane.showOptionDialog(this, "¿Qué desea crear?", "Crear Elemento", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        
        if (choice == 0){
            crearArchivo(nodoSeleccionado);
        } else if (choice == 1) {
            crearDirectorio(nodoSeleccionado);
        }
    }
    
    private void crearArchivo(DefaultMutableTreeNode parentNode){
        
    }
    
    private void crearDirectorio(DefaultMutableTreeNode parentNode){
        
    }
      
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        jScrollPane1 = new javax.swing.JScrollPane();
        arbolSistema = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        crear_btn = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        Modificar_btn = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Eliminar_btn = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        comboUsuarios = new javax.swing.JComboBox<>();
        comboPoliticas = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        Aplicar_btn = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usuarioEnUso = new javax.swing.JLabel();
        panelSD = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        panelSDStats = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lblBloquesTotales = new javax.swing.JLabel();
        lblBloquesOcupados = new javax.swing.JLabel();
        lblbloquesLibres = new javax.swing.JLabel();
        panelDisco = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        panelProcesos = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        leer_btn = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setViewportView(arbolSistema);

        panel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 270, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Controles");
        panel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("Usuario:");
        panel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 510, -1, 20));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("Política:");
        panel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 550, 60, 20));
        panel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, 810, 10));
        panel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 480, 730, 10));

        jPanel3.setBackground(new java.awt.Color(238, 238, 238));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        crear_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        crear_btn.setForeground(new java.awt.Color(51, 51, 51));
        crear_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        crear_btn.setText("Crear");
        crear_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        crear_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                crear_btnMouseClicked(evt);
            }
        });
        jPanel3.add(crear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 500, 110, 30));

        jPanel5.setBackground(new java.awt.Color(238, 238, 238));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Modificar_btn.setBackground(new java.awt.Color(51, 51, 51));
        Modificar_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Modificar_btn.setForeground(new java.awt.Color(51, 51, 51));
        Modificar_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Modificar_btn.setText("Modificar");
        Modificar_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel5.add(Modificar_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 500, 110, 30));

        jPanel6.setBackground(new java.awt.Color(238, 238, 238));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Eliminar_btn.setBackground(new java.awt.Color(51, 51, 51));
        Eliminar_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Eliminar_btn.setForeground(new java.awt.Color(51, 51, 51));
        Eliminar_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Eliminar_btn.setText("Eliminar");
        Eliminar_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel6.add(Eliminar_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 550, 110, 30));

        jPanel1.setBackground(new java.awt.Color(243, 243, 243));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Simulador de Sistema de Archivos");
        jPanel1.add(jLabel3);

        panel1.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 30));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Explorador de Archivos");
        panel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 150, 20));

        comboUsuarios.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        comboUsuarios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUsuariosActionPerformed(evt);
            }
        });
        panel1.add(comboUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 500, 180, -1));

        comboPoliticas.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        comboPoliticas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboPoliticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPoliticasActionPerformed(evt);
            }
        });
        panel1.add(comboPoliticas, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 550, 180, -1));

        jPanel8.setBackground(new java.awt.Color(238, 238, 238));
        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Aplicar_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Aplicar_btn.setForeground(new java.awt.Color(51, 51, 51));
        Aplicar_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Aplicar_btn.setText("Aplicar");
        Aplicar_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Aplicar_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Aplicar_btnMouseClicked(evt);
            }
        });
        jPanel8.add(Aplicar_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 540, 110, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Usuario:");
        panel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, -1, -1));

        usuarioEnUso.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        usuarioEnUso.setForeground(new java.awt.Color(51, 51, 51));
        usuarioEnUso.setText("jLabel6");
        panel1.add(usuarioEnUso, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 40, 180, -1));

        tablaArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaArchivos.setEnabled(false);
        tablaArchivos.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(tablaArchivos);

        panelSD.addTab("Tabla de Archivos", jScrollPane3);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelSDStats.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl1.setText("Bloques Totales:");
        panelSDStats.add(lbl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        lbl2.setText("Bloques Ocupados:");
        panelSDStats.add(lbl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        lbl3.setText("Bloques Libres:");
        panelSDStats.add(lbl3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        lblBloquesTotales.setText("jLabel6");
        panelSDStats.add(lblBloquesTotales, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, -1, -1));

        lblBloquesOcupados.setText("jLabel7");
        panelSDStats.add(lblBloquesOcupados, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, -1, -1));

        lblbloquesLibres.setText("jLabel9");
        panelSDStats.add(lblbloquesLibres, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, -1, -1));

        jPanel2.add(panelSDStats, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 520, 150));

        panelDisco.setBackground(new java.awt.Color(255, 255, 255));
        panelDisco.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel9.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel9.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel9.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("1");
        jPanel9.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 60, -1));

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel10.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel10.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel10.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("2");
        jPanel10.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 60, -1));

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel11.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel11.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel11.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("4");
        jPanel11.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 60, -1));

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel13.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel13.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel13.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("5");
        jPanel13.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 60, -1));

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel14.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel14.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel14.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("3");
        jPanel14.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 60, -1));

        jPanel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel21.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel21.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel21.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("0");
        jPanel21.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, -1));

        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel15.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel15.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel15.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("6");
        jPanel15.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 60, -1));

        jPanel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel20.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel20.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel20.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("7");
        jPanel20.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 60, -1));

        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel23.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel23.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel23.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("8");
        jPanel23.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 60, -1));

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel17.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel17.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel17.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("9");
        jPanel17.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, 60, -1));

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel16.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel16.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel16.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("10");
        jPanel16.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 60, -1));

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel18.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel18.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel18.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("11");
        jPanel18.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, 60, -1));

        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel22.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel22.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel22.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel22.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("12");
        jPanel22.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 70, 60, -1));

        jPanel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel19.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel19.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel19.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("13");
        jPanel19.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 60, -1));

        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 5));
        jPanel24.setMaximumSize(new java.awt.Dimension(40, 60));
        jPanel24.setMinimumSize(new java.awt.Dimension(40, 60));
        jPanel24.setPreferredSize(new java.awt.Dimension(40, 60));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 51, 51));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("14");
        jPanel24.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 60));

        panelDisco.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 70, 60, -1));

        jPanel2.add(panelDisco, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 210));

        panelSD.addTab("SD", jPanel2);
        panelSD.addTab("Procesos", panelProcesos);

        panel1.add(panelSD, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 520, 390));

        jPanel7.setBackground(new java.awt.Color(238, 238, 238));
        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        leer_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        leer_btn.setForeground(new java.awt.Color(51, 51, 51));
        leer_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        leer_btn.setText("Leer");
        leer_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        leer_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                leer_btnMouseClicked(evt);
            }
        });
        jPanel7.add(leer_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 500, 110, 30));

        getContentPane().add(panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboPoliticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPoliticasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboPoliticasActionPerformed

    private void comboUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUsuariosActionPerformed
        String seleccion = (String) comboUsuarios.getSelectedItem();
        if (seleccion != null && sistema != null && sistema.getUsuarios() != null){
            String nombreUsuario = seleccion.split(" ")[0];
            
            for (Usuario usuario : sistema.getUsuarios()){
                if (usuario.getName().equals(nombreUsuario)){
                    usuarioActual = usuario;
                    
                    System.out.println("Usuario " + usuarioActual.getName() + " agregado existosamente");
                    
                    actualizarLabelUsuario();
                    actualizarInterfazCompleta();
                    break;
                }
            }
        }
    }//GEN-LAST:event_comboUsuariosActionPerformed

    private void Aplicar_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Aplicar_btnMouseClicked
        aplicarPoliticaSeleccionada();
    }//GEN-LAST:event_Aplicar_btnMouseClicked

    private void crear_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_crear_btnMouseClicked
        crear();
    }//GEN-LAST:event_crear_btnMouseClicked

    private void leer_btnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leer_btnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_leer_btnMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(interfazPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(interfazPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(interfazPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(interfazPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new interfazPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Aplicar_btn;
    private javax.swing.JLabel Eliminar_btn;
    private javax.swing.JLabel Modificar_btn;
    private javax.swing.JTree arbolSistema;
    private javax.swing.JComboBox<String> comboPoliticas;
    private javax.swing.JComboBox<String> comboUsuarios;
    private javax.swing.JLabel crear_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lblBloquesOcupados;
    private javax.swing.JLabel lblBloquesTotales;
    private javax.swing.JLabel lblbloquesLibres;
    private javax.swing.JLabel leer_btn;
    private java.awt.Panel panel1;
    private javax.swing.JPanel panelDisco;
    private javax.swing.JPanel panelProcesos;
    private javax.swing.JTabbedPane panelSD;
    private javax.swing.JPanel panelSDStats;
    private javax.swing.JTable tablaArchivos;
    private javax.swing.JLabel usuarioEnUso;
    // End of variables declaration//GEN-END:variables
}
