/**
 * @file PedidosClientes.java
 * @brief Clase para el control de los pedidos de los clientes
 * @version 1.0
 * @date 2024-01-06
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase contiene los métodos necesarios para la lógica
 * de los pedidos de los clientes
 */
package compras.pedidos;

//Carrito del cliente
import compras.Carrito;
//Clases necesarias para la base de datos
import db.conexiondb.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//Para el control de Fecha y Hora
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class PedidosClientes extends Pedidos{
    
    public PedidosClientes(){
        tipoUser = false; //Establecemos que es cliente
    }
    
    @Override
    public void menuPedidos(){
        System.out.println("===== MENU DE PEDIDOS =====");
        System.out.print("\n1. Realizar pedido");
        System.out.print("\n2. Revisar estado de los pedidos");
        System.out.print("\n3. Ver pedidos realizados");
        System.out.print("\n4. Ver detalles de un pedido");
        System.out.print("\n5. Cancelar pedido");
        System.out.print("\n6. Salir");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                Carrito carrito = new Carrito(idCliente);
                if(carrito.isEmpty()){
                    System.out.println("No se puede realizar el pedido debido a que tu carrito esta vacio");
                } else{            
                    registrarPedido();
                }
            }
            
            case 2 -> {
                revisarEstadoPedidos();
            }
            
            case 3 -> {
                verPedidos();
            }
            
            case 4 -> {
                System.out.print("\n\nTeclee el codigo del pedido: ");
                setCodigoPedido(scanner.nextInt());
                verDetallesPedido();
            }
            
            case 5 -> {
                System.out.println("\n\nTeclee el codigo del pedido: ");
                setCodigoPedido(scanner.nextInt());
                if(exist()) cancelarPedido();
                else System.out.println("Codigo invalido...");
            }
            
            case 6 -> {
                sesion = false; //Cerramos la sesion de los pedidos
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    private void registrarPedido() {
        Connection conexion = null;
        try {
            if (MySQLConnection.conectarBD()) {
                conexion = MySQLConnection.getConexion();
                // Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);

                // Primero registramos el pedido
                String query = "INSERT INTO pedidos VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                st.setDate(1, Date.valueOf(LocalDate.now()));
                st.setTime(2, Time.valueOf(LocalTime.now()));
                Carrito carrito = new Carrito(idCliente);
                st.setFloat(3, carrito.calcularTotalCarrito());
                st.setBoolean(4, true); // Pedido pendiente para los empleados
                st.setBoolean(5, false); // No se debe cancelar el pedido recién insertado
                st.setInt(6, idCliente);
                st.executeUpdate();

                // Obtener el ID del pedido recién insertado
                ResultSet rs = st.getGeneratedKeys();
                codigoPedido = 0;
                if (rs.next()) {
                    codigoPedido = rs.getInt(1);
                }

                // Después registramos los detalles del pedido usando el ID del pedido insertado
                String queryDP = """
                                 INSERT INTO detallepedidos (CodigoPedido, IdProducto, Cantidad, Importe, Detalles)
                                 SELECT ?, IdProducto, Cantidad, Total, Detalles
                                 FROM carrito
                                 WHERE IdCliente = ?""";
                PreparedStatement stDP = conexion.prepareStatement(queryDP);
                stDP.setInt(1, codigoPedido);
                stDP.setInt(2, idCliente);
                stDP.executeUpdate();

                // Por último, vaciamos el carrito del cliente
                String queryCarrito = "DELETE FROM carrito WHERE IdCliente = ?";
                PreparedStatement stCarrito = conexion.prepareStatement(queryCarrito);
                stCarrito.setInt(1, idCliente);
                stCarrito.executeUpdate();

                // Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                System.out.println("Se ha registrado el pedido con éxito");
            } else {
                System.out.println("Error para establecer conexión con la base de datos");
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar el pedido: " + e.toString());
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
    
    //Verifica si el pedido existe en la base de datos
    private boolean exist(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM pedidos WHERE CodigoPedido = " + codigoPedido + " AND IdCliente = " + idCliente;
                //query += tipoUser ? "" : " AND IdCliente = " + idCliente;
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
        return false; //Para evitar errores
    }
    
    @Override
    protected void revisarEstadoPedidos(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                               SELECT estadopedidos.CodigoPedido, estadopedidos.IdEmpleado, 
                               concat(empleados.NombreE, ' ', empleados.ApellidoPaternoE, ' ', empleados.ApellidoMaternoE) 
                               AS 'Nombre completo del empleado',
                               estadopedidos.Estado
                               FROM estadopedidos
                               INNER JOIN empleados ON estadopedidos.IdEmpleado = empleados.IdEmpleado
                               INNER JOIN pedidos ON estadopedidos.CodigoPedido = pedidos.CodigoPedido
                               WHERE IdCliente = """ + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- ESTADO DE LOS PEDIDOS ---------");
                System.out.println("Codigo\tIDEmp\tNombre Empleado\t\t\t\t\tEstado");
                while(rs.next()){
                    int codPedido = rs.getInt(1);
                    int idEmp = rs.getInt(2);
                    String nomCompletoEmp = rs.getString(3);
                    String estadoPedido = rs.getString(4);
                    
                    //Ajustar la longitud máxima del nombre del empleado
                    int maxLength = 40;
                    if(nomCompletoEmp.length() > maxLength){
                        nomCompletoEmp = nomCompletoEmp.substring(0, maxLength);
                    }
                    
                    System.out.println(String.format("%d\t%d\t%-45s%s", 
                            codPedido,
                            idEmp,
                            nomCompletoEmp,
                            estadoPedido));
                    
                }
                System.out.println("\n\nLos pedidos que no aparecen es porque aun no cuentan con un empleado asignado");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al revisar el estado de los pedidos: " + e.toString());
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
    protected void verPedidos(){
        //Solo es una consulta a la tabla pedidos
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                               SELECT CodigoPedido, 
                               Fecha, 
                               Hora, 
                               Total, 
                               Pendiente, 
                               Cancelado 
                               FROM pedidos WHERE IdCliente = 
                               """ + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- PEDIDOS REALIZADOS ---------");
                System.out.println("Codigo\tFecha\t\tHora\t\tTotal\t\tPendiente\tCancelado");
                while(rs.next()){
                    int codPedido = rs.getInt(1);
                    Date fechaPedido = rs.getDate(2);
                    Time horaPedido = rs.getTime(3);
                    float totalPedido = rs.getFloat(4);
                    boolean pendiente = rs.getBoolean(5);
                    boolean cancelado = rs.getBoolean(6);
                    //Ajustamos los datos a String para poderlos imprimir
                    String fechaStr = String.valueOf(fechaPedido);
                    String horaStr = String.valueOf(horaPedido);
                    String pendienteStr = pendiente ? "SI" : "NO";
                    String canceladoStr = cancelado ? "SI" : "NO";
                    
                    //Imprimir los datos con alineación
                    System.out.println(String.format("%d\t%s\t%s\t%.2f\t\t%s\t\t%s", 
                            codPedido, 
                            fechaStr, 
                            horaStr, 
                            totalPedido,
                            pendienteStr, 
                            canceladoStr));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo consultar los pedidos realizados: " + e.toString());
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
    
    private void cancelarPedido(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                //Si cancelamos el pedido, tenemos que marcar a pendiente como false
                //Y marcar a cancelado como true
                //Y después verificamos si el empleado ya tomo el pedido
                //Si es así, cambiamos el estado a "CANCELADO POR EL CLIENTE"
                
                //Query1 -> Actualizamos pendiente y cancelado
                String query1 = "UPDATE pedidos SET Pendiente = false, Cancelado = true "
                        + "WHERE CodigoPedido = " + codigoPedido;
                Statement st1 = conexion.createStatement();
                st1.executeUpdate(query1);
                
                //Query2 -> Verificamos si hay un registro en la tabla estadopedidos
                String query2 = "SELECT * FROM estadopedidos WHERE CodigoPedido = " + codigoPedido;
                Statement st2 = conexion.createStatement();
                ResultSet rs = st2.executeQuery(query2);
                
                //Query3 -> Si hay un registro, quiere decir que el empleado aceptó el pedido
                //Por lo que cambiamos su estado a "CANCELADO POR EL CLIENTE"
                if(rs.next()){
                    String query3 = "UPDATE estadopedidos SET Estado = ? WHERE CodigoPedido = ?";
                    PreparedStatement st3 = conexion.prepareStatement(query3);
                    st3.setString(1, "CANCELADO POR EL CLIENTE");
                    st3.setInt(2, codigoPedido);
                    st3.executeUpdate();
                }
                System.out.println("Se ha cancelado el pedido con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al tratar de conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al cancelar el pedido: " + e.toString());
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
