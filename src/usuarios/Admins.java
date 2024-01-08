/**
 * @file Admins.java
 * @brief Clase para interactuar con el usuario administrador
 * @version 1.0
 * @date 2024-01-07
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase representa las acciones que puede hacer el usuario Admin
 */
package usuarios;

//Para la base de datos
import db.conexiondb.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admins extends Users{
    private int idCliente;
    private int idEmpleado;
    private int idAdmin;
    
    @Override
    public void menuUser(){
        System.out.println("===== MENU DE ADMINISTRADORES =====");
        System.out.print("\n1. Dar baja a clientes");
        System.out.print("\n2. Dar baja a empleados");
        System.out.print("\n3. Dar alta a clientes");
        System.out.print("\n4. Dar alta a empleados");
        System.out.print("\n5. Dar alta a administrador");
        System.out.print("\n6. Ver todos los empleados");
        System.out.print("\n7. Ver todos los clientes");
        System.out.print("\n8. Ver todos los administradores");
        System.out.print("\n9. Modificar datos");
        System.out.print("\n10. Dar de baja la cuenta");
        System.out.print("\n11. Cerrar sesion");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                System.out.print("\n\nTeclee el ID del cliente: ");
                idCliente = scanner.nextInt();
                if(!userExists("CLIENTE", idCliente)) System.out.println("El cliente no existe en la base de datos");
                else if(!isClienteActivo()) System.out.println("El cliente ya estaba dado de baja");
                else altaBajaCliente(false);
            }
            
            case 2 -> {
                System.out.print("\n\nTeclee el ID del empleado: ");
                idEmpleado = scanner.nextInt();
                if(!userExists("EMPLEADO", idEmpleado)) System.out.println("El empleado no existe en la base de datos");
                else if(!isEmpleadoActivo()) System.out.println("El empleado ya estaba dado de baja");
                else altaBajaEmpleado(false);
            }
            
            case 3 -> {
                System.out.print("\n\nTeclee el ID del cliente: ");
                idCliente = scanner.nextInt();
                if(!userExists("CLIENTE", idCliente)) System.out.println("El cliente no existe en la base de datos");
                else if(isClienteActivo()) System.out.println("El cliente ya estaba dado de alta");
                else altaBajaCliente(true);
            }
            
            case 4 -> {
                System.out.print("\n\nTeclee el ID del empleado: ");
                idEmpleado = scanner.nextInt();
                if(!userExists("EMPLEADO", idEmpleado)) System.out.println("El empleado no existe en la base de datos");
                else if(isEmpleadoActivo()) System.out.println("El empleado ya estaba dado de alta");
                else altaBajaEmpleado(true);
            }
            
            case 5 -> {
                System.out.print("\n\nTeclee el ID del administrador: ");
                idAdmin = scanner.nextInt();
                if(!userExists("ADMINISTRADOR", idAdmin)) System.out.println("El administrador no existe en la base de datos");
                else if(isAdminActivo()) System.out.println("El administrador ya estaba dado de alta");
                else altaAdmin();
            }
            
            case 6 -> {
                verUser("EMPLEADO");
            }
            
            case 7 -> {
                verUser("CLIENTE");
            }
            
            case 8 -> {
                verUser("ADMINISTRADOR");
            }
            
            case 9 -> {
                tipoUser = "ADMINISTRADOR";
                modificarDatosMenu();
            }
            
            case 10 -> {
                darBajaMenu();
            }
            
            case 11 -> {
                sesion = false;
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    private boolean isClienteActivo(){
        boolean activo = false;
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Estado FROM clientes WHERE IdCliente = " + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    activo = rs.getBoolean(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo comprobar si el cliente esta activo: " + e.toString());
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
        return activo;
    }
    
    private boolean isEmpleadoActivo(){
        boolean activo = false;
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Estado FROM empleados WHERE IdEmpleado = " + idEmpleado;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    activo = rs.getBoolean(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo comprobar si el empleado esta activo: " + e.toString());
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
        return activo;
    }
    
    private boolean isAdminActivo(){
        boolean activo = false;
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Estado FROM admins WHERE IdAdmin = " + idAdmin;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    activo = rs.getBoolean(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error para establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo comprobar si el administrador esta activo: " + e.toString());
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
        return activo;
    }
    
    private boolean userExists(String user, int idUser){ //false para clientes, true para empleados
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM ";
                switch (user) {
                    case "ADMINISTRADOR" -> query += "admins WHERE IdAdmin = ";
                    case "EMPLEADO" -> query += "empleados WHERE IdEmpleado = ";
                    case "CLIENTE" -> query += "clientes WHERE IdCliente = ";
                    default -> System.out.println("No se detecto ningun usuario");   
                }
                query += idUser;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next();
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo verificar si el usuario existe");
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
    
    private void altaBajaCliente(boolean bln){ //false para baja, true para alta
        String operacion = bln ? "alta" : "baja"; //Para la impresion en pantalla
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE clientes SET Estado = ? WHERE IdCliente = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setBoolean(1, bln);
                st.setInt(2, idCliente);
                st.executeUpdate();
                System.out.println("Se ha dado de " + operacion + " al cliente correctamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de " + operacion + " al cliente: " + e.toString());
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
    
    private void altaBajaEmpleado(boolean bln){ //false para baja, true para alta
        String operacion = bln ? "alta" : "baja"; //Para la impresion en pantalla
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE empleados SET Estado = ? WHERE IdEmpleado = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setBoolean(1, bln);
                st.setInt(2, idEmpleado);
                st.executeUpdate();
                System.out.println("Se ha dado de " + operacion + " al empleado correctamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de " + operacion + " al empleado: " + e.toString());
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
    
    private void altaAdmin(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE admins SET Estado = true WHERE IdAdmin = " + idAdmin;
                Statement st = conexion.createStatement();
                st.executeUpdate(query);
                System.out.println("Se ha dado de alta al administrador correctamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de alta al administrador: " + e.toString());
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
    
    private void verUser(String user){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query;
                switch (user) {
                    case "EMPLEADO" -> query = """
                                SELECT IdEmpleado, NombreE, ApellidoPaternoE,
                                ApellidoMaternoE, CorreoE, Estado
                                FROM empleados
                                """;
                    case "ADMINISTRADOR" -> query = """
                                                    SELECT IdAdmin, NombreA, ApellidoPaternoA,
                                                    ApellidoMaternoA, CorreoE, Estado 
                                                    FROM admins
                                                    """;
                    case "CLIENTE" -> query = """
                                              SELECT IdCliente, NombreC, ApellidoPaternoC,
                                              ApellidoMaternoC, CorreoE, Estado
                                              FROM clientes
                                              """;
                    default -> query = "ERROR";
                }
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println(" --------- TABLA DE " + user + " ---------");
                System.out.println("ID\tNombre\t\t\tApellidoPat\t\t\tApellidoMat\t\t\tCorreo\t\t\t\t\t\tEstado");
                while(rs.next()){
                    int idUser = rs.getInt(1);
                    String nomUser = rs.getString(2);
                    String apPatUser = rs.getString(3);
                    String apMatUser = rs.getString(4);
                    String correoUser = rs.getString(5);
                    boolean estadoUser = rs.getBoolean(6);
                    
                    String estadoUserStr = estadoUser ? "Activo" : "Inactivo";
                    
                    //Imprimir los datos
                    System.out.println(String.format("%d\t%-20s\t%-20s\t\t%-20s\t\t%-30s\t\t\t%s",
                            idUser,
                            nomUser,
                            apPatUser,
                            apMatUser,
                            correoUser,
                            estadoUserStr));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo mostrar a los " + user + ": " + e.toString());
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
    public void consultarID(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT IdAdmin FROM admins WHERE CorreoE = '" + correo + "'";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    id = rs.getInt(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo buscar el ID del admin: " + e.toString());
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
                String query = "INSERT INTO admins VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, apellidoPaterno);
                st.setString(3, apellidoMaterno);
                st.setString(4, correo);
                st.setString(5, password);
                st.setBoolean(6, true); //Va a estar dado de alta al insertarse
                st.executeUpdate();
                System.out.println("Se ha registrado el admin correctamente");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo registrar al admin: " + e.toString());
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
                        String query = "UPDATE admins SET NombreA = ? WHERE IdAdmin = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, nombre);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el nombre con exito");
                    }
                    
                    case 2 -> {
                        String query = "UPDATE admins SET ApellidoPaternoA = ? WHERE IdAdmin = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoPaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido paterno con exito");
                    }
                    
                    case 3 -> {
                        String query = "UPDATE admins SET ApellidoMaternoA = ? WHERE IdAdmin = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, apellidoMaterno);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el apellido materno con exito");
                    }
                    
                    case 4 -> {
                        String query = "UPDATE admins SET CorreoE = ? WHERE IdAdmin = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, correo);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el correo con exito");
                    }
                    
                    case 5 -> {
                        String query = "UPDATE admins SET PasswordA = ? WHERE IdAdmin = ?";
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
            System.out.println("Error para realizar la actualización de datos del admin: " + e.toString());
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
                String query = "UPDATE admins SET Estado = ? WHERE IdAdmin= ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setBoolean(1, false);
                st.setInt(2, id);
                st.executeUpdate();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                System.out.println("La baja del administrador se ha realizado con exito");
            } else{
                System.out.println("Error en la conexión con la BD");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de baja al administrador: " + e.toString());
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
                String query = "select concat(NombreA, ' ', ApellidoPaternoA, ' ', "
                        + "ApellidoMaternoA) as 'Nombre completo' from admins "
                        + "where IdAdmin = " + id;
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
