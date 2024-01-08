/**
 * @file Clientes.java
 * @brief Clase para la lógica de los clientes
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase contendrá los métodos que puede realizar un cliente
 */
package usuarios;
//Importamos las clases para la base de datos
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import db.conexiondb.MySQLConnection;
//Clase para el carrito
import compras.Carrito;
//Clase para los pedidos
import compras.pedidos.PedidosClientes;
//Clase para la visualizacion de productos
import inventario.ProductosClientes;

public class Clientes extends Users {
    
    @Override
    public void menuUser() {
        System.out.println("===== MENU DE CLIENTES =====");
        System.out.print("\n1. Carrito");
        System.out.print("\n2. Ver productos");
        System.out.print("\n3. Pedidos");
        System.out.print("\n4. Modificar datos");
        System.out.print("\n5. Dar de baja la cuenta");
        System.out.print("\n6. Cerrar sesion");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                consultarID();
                Carrito carrito = new Carrito(id);
                carrito.setSesion(true); //Activamos la sesion
                while(carrito.isSesion()){
                    carrito.menu();
                }
            }
            
            case 2 -> {
                ProductosClientes prod = new ProductosClientes();
                prod.verProductos();
            }
            
            case 3 -> {
                PedidosClientes pedCli = new PedidosClientes();
                pedCli.setSesion(true); //Activamos la sesion de los pedidos
                pedCli.setIdCliente(id);
                while(pedCli.isSesion()){
                    pedCli.menuPedidos();
                }
            }
            
            case 4 -> {
                tipoUser = "CLIENTE";
                modificarDatosMenu();
            }
            
            case 5 -> {
                darBajaMenu();
            }
            
            case 6 -> {
                sesion = false;
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    @Override
    public void consultarID(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT IdCliente FROM clientes WHERE CorreoE = '" + correo + "'";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    id = rs.getInt(1); //Se obtiene el ID del cliente
                    //Se tiene la certeza de que siempre se devolverá un registro
                    //Esto es porque el correo es único para cada cliente
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            }
        } catch(SQLException e){
            System.out.println("Error al consultar el ID: " + e.toString());
            id = -1;
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
    
    //Implementación de los métodos abstractos
    @Override
    public void insertarUser(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Hacemos el control de errores con las transacciones
                //Si falla una transacción, no se realiza niguna otra
                conexion.setAutoCommit(false);
                String query = "INSERT INTO clientes VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, apellidoPaterno);
                st.setString(3, apellidoMaterno);
                st.setString(4, correo);
                st.setString(5, password);
                st.setBoolean(6, true); //Indicamos que está el cliente activo Estado = true
                st.executeUpdate();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                System.out.println("Registro del cliente completado");
            } else{
                System.out.println("No hubo conexion con la BD");
            }
        } catch(SQLException e){
            System.out.println("Error para realizar la inserción del cliente: " + e.toString());
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
    protected void modificarUser(int opcionMod){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Hacemos el control de errores con las transacciones
                //Si falla una transacción, no se realiza niguna otra
                conexion.setAutoCommit(false);
                consultarID();
                switch(opcionMod){
                    case 1 -> {
                        String query = "UPDATE clientes SET NombreC = ? WHERE IdCliente = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, nombre);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el nombre con exito");
                    }
                    
                    case 2 -> {
                        String query = "UPDATE clientes SET ApellidoPaternoC = ? WHERE IdCliente = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoPaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido paterno con exito");
                    }
                    
                    case 3 -> {
                        String query = "UPDATE clientes SET ApellidoMaternoC = ? WHERE IdCliente = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoMaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido materno con exito");
                    }
                    
                    case 4 -> {
                        String query = "UPDATE clientes SET CorreoE = ? WHERE IdCliente = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, correo);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el correo con exito");
                    }
                    
                    case 5 -> {
                        String query = "UPDATE clientes SET PasswordC = ? WHERE IdCliente = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, password);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el password con exito");
                    }
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else {
                System.out.println("Error con la conexion de la BD");
            }
        } catch(SQLException e){
            System.out.println("Error para realizar la actualización de datos del cliente: " + e.toString());
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
    protected void darBajaUser(){
        //En este método no se realiza un DELETE, solo se cambia el estado a false
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Hacemos el control de errores con las transacciones
                //Si falla una transacción, no se realiza niguna otra
                conexion.setAutoCommit(false);
                consultarID();
                String query = "UPDATE clientes SET Estado = ? WHERE IdCliente = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setBoolean(1, false);
                st.setInt(2, id);
                st.executeUpdate();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                System.out.println("La baja del cliente se ha realizado con exito");
            } else{
                System.out.println("Error en la conexión con la BD");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de baja al cliente: " + e.toString());
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
    public String darBienvenidaUser(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                String nom = "";
                conexion = MySQLConnection.getConexion();
                //Hacemos el control de errores con las transacciones
                //Si falla una transacción, no se realiza niguna otra
                conexion.setAutoCommit(false);
                consultarID();
                String query = "select concat(NombreC, ' ', ApellidoPaternoC, ' ', "
                        + "ApellidoMaternoC) as 'Nombre completo' from clientes "
                        + "where IdCliente = " + id;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    nom = rs.getString(1);
                    //Tenemos la certeza de que nos devolverá solo un nombre
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return nom;
            }
        } catch(SQLException e){
            System.out.println(e.toString());
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
        return "ERROR";
    }
}
