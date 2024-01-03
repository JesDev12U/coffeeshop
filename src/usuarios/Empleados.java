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
import java.util.Scanner;
import conexiondb.MySQLConnection;
//Clase para el inventario
import inventario.*;

public class Empleados extends Users{
    Scanner scanner;
    private boolean sesion;
    
    public Empleados() {
        scanner = new Scanner(System.in);
    }
    
    public void menuEmpleados() {
        System.out.println("===== MENU DE EMPLEADOS =====");
        System.out.print("\n1. Inventario");
        System.out.print("\n2. Ver productos");
        System.out.print("\n3. Pedidos");
        System.out.print("\n4. Cerrar sesion");
        System.out.print("\n\nSeleccione una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion) {
            case 1 -> {
                Productos productos = new Productos();
                productos.setSesion(true); //Activamos la sesion
                while(productos.isSesion()){
                    productos.menuInventario();
                }
            }
            
            case 2 -> {
                verProductos();
            }
            
            case 3 -> {
                System.out.println("In dev...");
            }
            
            case 4 -> {
                setSesion(false); //Cerramos la sesion
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    //Metodos abstractos
    @Override
    protected void consultarID(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
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
        }
    }
    
    @Override
    protected void insertarUser(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
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
        }
    }
    
    @Override
    protected void modificarUser(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                consultarID();
                String query = "UPDATE empleados SET NombreE = ?, "
                        + "ApellidoPaternoE = ?, "
                        + "ApellidoMaternoE = ?, "
                        + "CorreoE = ?,"
                        + "PasswordE = ? "
                        + "WHERE IdEmpleado = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, apellidoPaterno);
                st.setString(3, apellidoMaterno);
                st.setString(4, correo);
                st.setString(5, password);
                st.setInt(6, id);
                System.out.println("Se han modificado los datos del empleado con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al modificar los datos del empleado: " + e.toString());
        }
    }
    
    @Override
    //Este metodo solo cambia el Estado = false
    protected void darBajaUser(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                consultarID();
                conexion.setAutoCommit(false);
                String query = "UPDATE empleados SET Estado = false WHERE IdEmpleado = " + id;
                Statement st = conexion.createStatement();
                st.executeQuery(query);
                System.out.println("Se ha dado de baja al empleado exitosamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error al conectarse con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al dar de baja al empleado: " + e.toString());
        }
    }
    
    @Override
    public String darBienvenidaUser(){
        try{
            if(MySQLConnection.conectarBD()){
                String nom = "";
                Connection conexion = MySQLConnection.getConexion();
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
        }
        return "ERROR";
    }
    
    @Override
    protected void visualizarPedidos(){
        
    }
    
    @Override
    //Se visualizaran los productos sin importar su estado
    protected void verProductos(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM productos";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  \t  " + 
                            rs.getString(3) + "  \t  " + rs.getFloat(4) + "  \t  " + rs.getBoolean(5));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo conectar a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para mostrar los productos: " + e.toString());
        }
    }
    
    //Getters and Setters

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }
    
}
