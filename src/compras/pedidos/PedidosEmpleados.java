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
    
    @Override
    public void menuPedidos(){
        System.out.println("===== MENU DE PEDIDOS =====");
        System.out.print("\n1. Ver pedidos");
        System.out.print("\n2. Ver detalles de un pedido");
        System.out.print("\n3. Aceptar pedido");
        System.out.print("\n4. Ver estados de los pedidos");
        System.out.print("\n5. Cambiar estado de un pedido");
        System.out.print("\n6. Salir");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                verPedidos();
            }
            
            case 2 -> {
                System.out.print("\n\nTeclee el codigo del pedido: ");
                setCodigoPedido(scanner.nextInt());
                verDetallesPedido();
            }
            
            case 3 -> {
                System.out.println("\n\nTeclee el codigo del pedido: ");
                codigoPedido = scanner.nextInt();
                if(isPendiente()) aceptarPedido();
                else System.out.println("Codigo invalido...");
            }
            
            case 4 -> {
                revisarEstadoPedidos();
            }
            
            case 5 -> {
                System.out.println("\n\nTeclee el codigo del pedido: ");
                codigoPedido = scanner.nextInt();
                if(isCancelado()){
                    System.out.println("Codigo invalido...");
                } else if(isAceptado()) System.out.println("Codigo invalido...");
                else if(isPendiente()) System.out.println("Codigo invalido...");
                else{
                    System.out.println("\n\nTeclee el nuevo estado del pedido: ");
                    scanner.nextLine();
                    modificarEstado(scanner.nextLine());
                }
            }
            
            case 6 -> {
                sesion = false; //Cerramos la sesion de pedidos
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    @Override
    protected void verPedidos(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
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
    
    private boolean isPendiente(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM pedidos WHERE CodigoPedido = " + codigoPedido + " AND Pendiente = true";
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
        return false; //Marcamos que el pedido no esta pendiente, para evitar errores
    }
    
    private void aceptarPedido(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                //Primero cambiamos a Pendiente = false
                String query1 = "UPDATE pedidos SET Pendiente = false WHERE CodigoPedido = " + codigoPedido;
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
    
    //Este método verifica si el pedido ha sido cancelado
    //Esto es útil para poder proceder con el cambio de estado del pedido
    private boolean isCancelado(){
        boolean cancelado = true;
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Cancelado FROM pedidos WHERE CodigoPedido = " + codigoPedido;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    cancelado = rs.getBoolean(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para comprobar si el pedido fue cancelado: " + e.toString());
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
        return cancelado;
    }
    
    //Verifica si el pedido ya habia sido aceptado
    private boolean isAceptado() {
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM estadopedidos WHERE CodigoPedido = " + codigoPedido + " AND IdEmpleado <> " + idEmpleado;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si hay un registro, entonces el pedido ya habia sido aceptado
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo verificar si el pedido ya habia sido aceptado: " + e.toString());
        } finally {
            if(conexion != null){
                try{
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch(SQLException e){
                    System.out.println("No se pudo cerrar la conexion: " + e.toString());
                }
            }
        }
        return true; //Para evitar errores
    }
    
    private void modificarEstado(String estado){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE estadopedidos SET Estado = ? WHERE CodigoPedido = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, estado);
                st.setInt(2, codigoPedido);
                st.executeUpdate();
                System.out.println("Se ha modificado el estado del pedido con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo modificar el estado del pedido: " + e.toString());
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
    
    @Override
    protected void revisarEstadoPedidos(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT CodigoPedido, Estado FROM estadopedidos WHERE IdEmpleado = " + idEmpleado;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- ESTADO DE LOS PEDIDOS ---------");
                System.out.println("Codigo\tEstado");
                while(rs.next()){
                    int codPedido = rs.getInt(1);
                    String estadoPedido = rs.getString(2);
                    
                    //Imprimimos los datos
                    System.out.println(String.format("%d\t%s",
                            codPedido,
                            estadoPedido));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo revisar el estado de los pedidos: " + e.toString());
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