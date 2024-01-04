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
import conexiondb.MySQLConnection;
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