/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trafacmo.cliente;

import java.awt.Component;
import java.awt.Container;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import trafacmo.ConexionBD;

/**
 *
 * @author USUARIO
 */
public class DaCliente {
    static Connection conn = ConexionBD.getConnection();
    
    public static void limpiarCampos(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JTextField) {
                ((JTextField) componente).setText("");
            } else if (componente instanceof Container) {
                limpiarCampos((Container) componente);
            }
        }
    }
    
    
    public static void editar(JTable tabla, JTextField[] cajas) {
        // Obtener el indice de la fila seleccionada
        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            for (int i = 0; i < cajas.length; i++) {
                String dato = tabla.getModel().getValueAt(fila, i).toString();
                cajas[i].setText(dato);
            }

            cajas[0].setEditable(false);
            cajas[1].requestFocus();
        } else {
            JOptionPane.showMessageDialog(null, "Debes seleccionar una fila para editar.");
        }

    }
    
    public static void guardarCliente(cliente cliente, JTable tabla) {
    try {
        if (cliente.getId().equals("")) {
            JOptionPane.showMessageDialog(null, "Ingrese un Id", "Sistema", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llamada al procedimiento almacenado
        CallableStatement cstmt = conn.prepareCall("{ CALL insertar_cliente(?, ?, ?, ?, ?, ?, ?) }");
        cstmt.setString(1, cliente.getId());
        cstmt.setString(2, cliente.getNombre());
        cstmt.setString(3, cliente.getRuc());
        cstmt.setString(4, cliente.getDni());
        cstmt.setString(5, cliente.getTelefono());
        cstmt.setString(6, cliente.getEmail());
        cstmt.setString(7, cliente.getDireccion());

        cstmt.execute(); // ejecuta insert o update

        // Refrescar JTable
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // limpia
        DaCliente.mostrarDatos(modelo); // vuelve a llenar con la BD

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    public static void mostrarDatos(DefaultTableModel modelo) {
    try {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Cliente");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[]{
                rs.getString("id"),
                rs.getString("nombre"),
                rs.getString("ruc"),
                rs.getString("dni"),
                rs.getInt("telefono"),
                rs.getString("email"),
                rs.getString("direccion")
            };
            modelo.addRow(row);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

}
