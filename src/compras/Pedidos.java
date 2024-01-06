/**
 * @file Pedidos.java
 * @brief Clase para el control de los pedidos de los clientes
 * @version 1.0
 * @date 2024-01-03
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase contiene los métodos necesarios para la lógica
 * de los pedidos de los clientes
 */
package compras;
//Clases para la base de datos
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import db.conexiondb.MySQLConnection;
//Para obtener la fecha y hora actual
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;
import java.sql.Time;

public class Pedidos {
    private int idCliente;
    private int codigoPedido;
    private boolean sesion;
    
    public void registrarPedido() {
        try {
            if (MySQLConnection.conectarBD()) {
                Connection conexion = MySQLConnection.getConexion();
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
        }
    }

    public void revisarEstadoPedidos(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                               SELECT estadopedidos.CodigoPedido, estadopedidos.IdEmpleado, 
                               concat(empleados.NombreE, ' ', empleados.ApellidoPaternoE, ' ', empleados.ApellidoMaternoE) 
                               AS 'Nombre completo del empleado',
                               estadopedidos.Estado
                               FROM estadopedidos
                               INNER JOIN empleados ON estadopedidos.IdEmpleado = empleados.IdEmpleado;""";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- ESTADO DE LOS PEDIDOS ---------");
                System.out.println("Codigo\tIDEmp\tNombre Empleado\t\t\t\tEstado");
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
                    
                    System.out.println(String.format("%d\t%d\t%-45s\t-20s", 
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
        }
    }
    
    public void verPedidosRealizados(){
        //Solo es una consulta a la tabla pedidos
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
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
            } else{
                System.out.println("No se pudo establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo consultar los pedidos realizados: " + e.toString());
        }
    }
    
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
}