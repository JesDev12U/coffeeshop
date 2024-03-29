/**
 * @file Empleados.java
 * @brief Clase para la lógica de los empleados
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase contendrá los métodos que puede realizar un empleado
 */
package usuarios;
//Librerias para la base de datos
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import db.conexiondb.MySQLConnection;
//Clases para el inventario
import inventario.ProductosEmpleados;
//Clase para los pedidos
import compras.pedidos.PedidosEmpleados;

public class Empleados extends Users{
    
    @Override
    public void menuUser() {
        System.out.println("===== MENU DE EMPLEADOS =====");
        System.out.print("\n1. Inventario");
        System.out.print("\n2. Ver productos");
        System.out.print("\n3. Pedidos");
        System.out.print("\n4. Modificar datos");
        System.out.print("\n5. Dar de baja la cuenta");
        System.out.print("\n6. Cerrar sesion");
        System.out.print("\n\nSeleccione una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion) {
            case 1 -> {
                ProductosEmpleados productos = new ProductosEmpleados();
                productos.setSesion(true); //Activamos la sesion
                while(productos.isSesion()){
                    productos.menu();
                }
            }
            
            case 2 -> {
                ProductosEmpleados prod = new ProductosEmpleados();
                prod.verProductos();
            }
            
            case 3 -> {
                PedidosEmpleados pedEmp = new PedidosEmpleados();
                pedEmp.setSesion(true); //Habilitamos la sesion de pedidos
                pedEmp.setIdEmpleado(id);
                while(pedEmp.isSesion()){
                    pedEmp.menuPedidos();
                }
            }
            
            case 4 -> {
                tipoUser = "EMPLEADO";
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
    
    //Metodos abstractos
    @Override
    public void consultarID(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT IdEmpleado FROM empleados WHERE CorreoE = '" + correo + "'";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    id = rs.getInt(1);
                    //Asumimos que solo se tendra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al conectarse a la base de datos");
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
    
    @Override
    public void insertarUser(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "INSERT INTO empleados VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, apellidoPaterno);
                st.setString(3, apellidoMaterno);
                st.setString(4, correo);
                st.setString(5, password);
                st.setBoolean(6, true); //Establecemos el estado en true ya que el empleado esta activo
                st.executeUpdate();
                System.out.println("Se ha realizado el registro del empleado con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al insertar al empleado: " + e.toString());
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
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                consultarID();
                switch(opcionMod){
                    case 1 -> {
                        String query = "UPDATE empleados SET NombreE = ? WHERE IdEmpleado = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, nombre);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el nombre con exito");
                    }
                    
                    case 2 -> {
                        String query = "UPDATE empleados SET ApellidoPaternoE = ? WHERE IdEmpleado = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoPaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido paterno con exito");
                    }
                    
                    case 3 -> {
                        String query = "UPDATE empleados SET ApellidoMaternoE = ? WHERE IdEmpleado = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoMaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido materno con exito");
                    }
                    
                    case 4 -> {
                        String query = "UPDATE empleados SET CorreoE = ? WHERE IdEmpleado = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, correo);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el correo con exito");
                    }
                    
                    case 5 -> {
                        String query = "UPDATE empleados SET PasswordE = ? WHERE IdEmpleado = ?";
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
            } else{
                System.out.println("Error al conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al modificar los datos del empleado: " + e.toString());
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
    //Este metodo solo cambia el Estado = false
    protected void darBajaUser(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                consultarID();
                conexion.setAutoCommit(false);
                String query = "UPDATE empleados SET Estado = false WHERE IdEmpleado = " + id;
                Statement st = conexion.createStatement();
                st.executeUpdate(query);
                System.out.println("Se ha dado de baja al empleado exitosamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al dar de baja al empleado: " + e.toString());
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
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                consultarID();
                String query = "SELECT CONCAT(NombreE, ' ', ApellidoPaternoE, ' ', "
                        + "ApellidoMaternoE) FROM empleados WHERE IdEmpleado = " + id;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    nom = rs.getString(1);
                    //Tenemos la certeza que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return nom;
            }
        } catch(SQLException e){
            System.out.println("Error al consultar el nombre del empleado: " + e.toString());
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
