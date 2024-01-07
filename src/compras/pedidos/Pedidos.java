/**
 * @file Pedidos.java
 * @brief Clase abstracta para el control de los pedidos
 * @version 1.0
 * @date 2024-01-06
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase contiene los métodos necesarios para la lógica
 * de los pedidos, contiene métodos que son aplicables para los clientes y los empleados
 */
package compras.pedidos;

import db.conexiondb.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Pedidos {
    protected int idCliente;
    protected int idEmpleado;
    protected int codigoPedido;
    protected boolean sesion;
    protected boolean tipoUser; //false para los clientes, true para los empleados
    //Se define el boolean tipoUser para no tener que hacer una implementación en la clase
    //PedidosClientes y PedidosEmpleados ya que lo unico que cambia en estos métodos es un WHERE
    
    public void verDetallesPedido(){ //false para los clientes, true para los empleados
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                           SELECT dp.CodigoPedido, 
                           dp.IdProducto,
                           p.NombreP,
                           p.Precio,
                           dp.Cantidad, 
                           dp.Importe, 
                           dp.Detalles AS Detalles_Producto
                           FROM detallepedidos dp
                           JOIN productos p ON dp.IdProducto = p.IdProducto
                           JOIN pedidos pe ON dp.CodigoPedido = pe.CodigoPedido
                           """;
                query += tipoUser ? "WHERE dp.CodigoPedido = ? AND pe.Cancelado = false;" 
                        : "WHERE dp.CodigoPedido = ? AND pe.IdCliente = ?;"; //Clientes
                PreparedStatement st = conexion.prepareStatement(query);
                st.setInt(1, codigoPedido);
                if(!tipoUser) st.setInt(2, idCliente);
                ResultSet rs = st.executeQuery();
                
                System.out.println("--------- DETALLES DEL PEDIDO " + codigoPedido + " ---------");
                System.out.println("Codigo\tIdProd\tNombreProd\tPrecio\t\tCantidad\tImporte\t\tDetalles");
                
                while(rs.next()){
                    int codPedido = rs.getInt(1);
                    int idProd = rs.getInt(2);
                    String nombreProd = rs.getString(3);
                    float precioProd = rs.getFloat(4);
                    int cantidadProd = rs.getInt(5);
                    float importe = rs.getFloat(6);
                    String detalles = rs.getString(7);
                    
                    //Imprimir los datos con alineación
                    System.out.println(String.format("%d\t%d\t%s\t\t%.2f\t\t%d\t\t%.2f\t\t%s",
                            codPedido,
                            idProd,
                            nombreProd,
                            precioProd,
                            cantidadProd,
                            importe,
                            detalles));
                }
                System.out.println("\nSi no hay ningun dato, el codigo que fue tecleado no fue encontrado en la base de datos");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo realizar la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo consultar del detalle del pedido: " + e.toString());
        }
    }
    
    //Verifica si el pedido existe en la base de datos
    public boolean exist(){ //false para clientes, true para empleados
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM pedidos WHERE CodigoPedido = " + codigoPedido;
                query += tipoUser ? "" : " AND IdCliente = " + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si encuentra un registro, entonces si existe el pedido
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo comprobar si el pedido existe: " + e.toString());
        }
        return false; //Para evitar errores
    }
    
    public abstract void revisarEstadoPedidos();
    
    //Setters y Getters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(int codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }   

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
