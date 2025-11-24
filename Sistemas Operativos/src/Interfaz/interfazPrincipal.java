/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaz;

import Clases.Archivo;
import Clases.CargadorSistema;
import Clases.Directorio;
import Clases.SistemaArchivos;
import Clases.Usuario;
import Estructuras.Nodo;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTable;
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
        initComponents();
        cargarArbol();
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
    
    private void cargarTablaArchivos(){
        modeloTablaArchivos = new DefaultTableModel(new Object[]{"Nombre", "Tamaño", "Bloques", "Usuario"}, 0);
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
                        obtenerListaBloques(archivo),
                        archivo.getUsuario().getName()
                    });
                }
            }
            aux = aux.getNext();
        }
    }
    
    private void cargarTablaArchivosFiltrada(){
        modeloTablaArchivos = new DefaultTableModel(new Object[]{"Nombre", "Tamaño", "Bloques", "Color"}, 0);
        
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
        
    }
    
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
        jPanel4 = new javax.swing.JPanel();
        Eliminar_btn = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        Leer_btn = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Modificar_btn = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaArchivos = new javax.swing.JTable();
        comboUsuarios = new javax.swing.JComboBox<>();
        comboPoliticas = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        Aplicar_btn = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usuarioEnUso = new javax.swing.JLabel();

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
        panel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, 790, 10));
        panel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 480, 710, 10));

        jPanel3.setBackground(new java.awt.Color(238, 238, 238));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        crear_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        crear_btn.setForeground(new java.awt.Color(51, 51, 51));
        crear_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        crear_btn.setText("Crear");
        crear_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(crear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 500, 110, 30));

        jPanel4.setBackground(new java.awt.Color(238, 238, 238));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Eliminar_btn.setBackground(new java.awt.Color(51, 51, 51));
        Eliminar_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Eliminar_btn.setForeground(new java.awt.Color(51, 51, 51));
        Eliminar_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Eliminar_btn.setText("Eliminar");
        Eliminar_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.add(Eliminar_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 500, 110, 30));

        jPanel5.setBackground(new java.awt.Color(238, 238, 238));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Leer_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Leer_btn.setForeground(new java.awt.Color(51, 51, 51));
        Leer_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Leer_btn.setText("Leer");
        Leer_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel5.add(Leer_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

        panel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 500, 110, 30));

        jPanel6.setBackground(new java.awt.Color(238, 238, 238));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Modificar_btn.setBackground(new java.awt.Color(51, 51, 51));
        Modificar_btn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Modificar_btn.setForeground(new java.awt.Color(51, 51, 51));
        Modificar_btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Modificar_btn.setText("Modificar");
        Modificar_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel6.add(Modificar_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 110, 30));

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

        jTabbedPane1.addTab("Tabla de Archivos", jScrollPane3);

        panel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 490, 390));

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
    private javax.swing.JLabel Leer_btn;
    private javax.swing.JLabel Modificar_btn;
    private javax.swing.JTree arbolSistema;
    private javax.swing.JComboBox<String> comboPoliticas;
    private javax.swing.JComboBox<String> comboUsuarios;
    private javax.swing.JLabel crear_btn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private java.awt.Panel panel1;
    private javax.swing.JTable tablaArchivos;
    private javax.swing.JLabel usuarioEnUso;
    // End of variables declaration//GEN-END:variables
}
