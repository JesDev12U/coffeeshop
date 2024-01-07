/**
 * @file PedidosEmpleados.java
 * @brief Clase para la gestión de los pedidos por parte de los empleados
 * @version 1.0
 * @date 2024-01-06
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase contiene los métodos necesarios para la lógica
 * de la gestión de los pedidos por parte de los empleados
 */
package compras.pedidos;
//Clases para la base de datos
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import db.conexiondb.MySQLConnection;
//Para las fechas y horas
import java.sql.Date;
import java.sql.Time;

public class PedidosEmpleados extends Pedidos{
    
    public PedidosEmpleados(){
        tipoUser = true; //Establecemos que es un empleado
    }
    
    public void verPedidos(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                               SELECT CodigoPedido, IdCliente, Fecha, Hora, Total, Pendiente
                               FROM pedidos
                               WHERE Cancelado = false
                               ORDER BY Pendiente DESC
                               """;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- PEDIDOS PENDIENTES ---------");
                System.out.println("Codigo\tIdCliente\tFecha\t\tHora\t\tTotal\t\tPendiente");
                while(rs.next()){
                    int codPedido = rs.getInt(1);
                    int idCli = rs.getInt(2);
                    Date fecPedido = rs.getDate(3);
                    Time horPedido = rs.getTime(4);
                    float totalPedido = rs.getFloat(5);
                    boolean pendiente = rs.getBoolean(6);
                    //Convertimos la fecha y hora en Strings para poderlos imprimir
                    String fecPedidoStr = String.valueOf(fecPedido);
                    String horPedidoStr = String.valueOf(horPedido);
                    String pendienteStr = pendiente ? "SI" : "NO";
                    //Imprimimos los datos
                    System.out.println(String.format("%d\t%d\t\t%s\t%s\t%.2f\t\t%s",
                            codPedido,
                            idCli,
                            fecPedidoStr,
                            horPedidoStr,
                            totalPedido,
                            pendienteStr));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para visualizar los pedidos: " + e.toString());
        }
    }
    
    public boolean isPendiente(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM pedidos WHERE CodigoPedido = " + codigoPedido + "AND Pendiente = true";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si hay un registro, quiere decir que el pedido esta pendiente
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo verificar si el pedido es pendiente: " + e.toString());
        }
        return false; //Marcamos que el pedido no esta pendiente, para evitar errores
    }
    
    public void aceptarPedido(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                //Primero cambiamos a Pendiente = false
                String query1 = "UPDATE pedidos SET Pendiente = true WHERE CodigoPedido = " + codigoPedido;
                Statement st1 = conexion.createStatement();
                st1.executeUpdate(query1);
                
                //Despues hacemos el registro en la tabla estadopedidos
                String query2 = "INSERT INTO estadopedidos VALUES (?, ?, ?)";
                PreparedStatement st2 = conexion.prepareStatement(query2);
                st2.setInt(1, codigoPedido);
                st2.setInt(2, idEmpleado);
                st2.setString(3, "RECIEN ACEPTADO");
                st2.executeUpdate();
                System.out.println("Se ha aceptado el pedido correctamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo aceptar el pedido: " + e.toString());
        }
    }
}