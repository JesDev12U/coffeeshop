/**
 * @file ProductosClientes.java
 * @brief Clase para la gestión de productos por parte de los clientes
 * @version 1.0
 * @date 2024-01-06
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase representa la composición de los productos por parte de los clientes
 * Es decir, contiene métodos para interactuar con los productos que solo el cliente tiene acceso a ellos
 */
package inventario;

import db.conexiondb.MySQLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductosClientes {

    //Se visualizarán únicamente los productos activos Estado = true
    public void verProductos(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT IdProducto, NombreP, Descripcion, Precio FROM productos WHERE Estado = true";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- PRODUCTOS ---------");
                System.out.println("ID\tNombre\t\t\t\tDescripcion\t\t\t\t\t\tPrecio");
                while(rs.next()){
                    int idProd = rs.getInt(1);
                    String nomProd = rs.getString(2);
                    String descripcion = rs.getString(3);
                    float precio = rs.getFloat(4);
                    
                    //Ajustar la longitud máxima de la descripción
                    int maxLength = 50;
                    if(descripcion.length() > maxLength){
                        descripcion = descripcion.substring(0, maxLength);
                    }
                    
                    System.out.println(String.format("%d\t%-25s\t%-55s\t%.2f", idProd, 
                            nomProd, descripcion, precio));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else {
                System.out.println("No se pudo conectar a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al mostrar los productos activos: " + e.toString());
        } finally {
            if(conexion != null){
                try {
                conexion.setAutoCommit(true);
                conexion.close(); // Cerrar la conexión en el bloque finally
                } catch (SQLException closingException) {
                    System.out.println("Error al cerrar la conexión: " + closingException.toString());
                }
            }
        }
    }
}
