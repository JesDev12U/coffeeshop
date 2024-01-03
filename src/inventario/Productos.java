/**
 * @file Productos.java
 * @brief Clase para la lógica de los productos
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase contendrá los métodos para los productos del inventario
 * La mayoría de métodos los usa el empleado y algunos otros el carrito
 */

package inventario;
//Librerias para la base de datos
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import conexiondb.MySQLConnection;
//Clase para la entrada y salida de datos
import java.util.Scanner;

public class Productos {
    private int id;
    private String nombre;
    private String descripcion;
    private float precio;
    private boolean sesion;
    private final Scanner scanner;
    
    public Productos(){
        scanner = new Scanner(System.in);
    }

    public void menuInventario(){
        System.out.println("===== MENU DE INVENTARIO =====");
        System.out.print("\n1. Agregar productos");
        System.out.print("\n2. Modificar productos");
        System.out.print("\n3. Eliminar productos");
        System.out.print("\n4. Visualizar productos eliminados (dados de baja)");
        System.out.print("\n5. Dar de alta productos");
        System.out.print("\n6. Salir");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                System.out.println("--------- AGREGAR PRODUCTOS ---------");
                System.out.print("\nTeclee el nombre: ");
                scanner.nextLine();
                nombre = scanner.nextLine();
                //Buscamos por nombre
                if(verificarExistenciaProd(true)){
                    System.out.println("El producto ya existe en la base de datos!");
                } else{
                    System.out.print("\nTeclee la descripcion: ");
                    descripcion = scanner.nextLine();
                    System.out.print("\nTeclee el precio: ");
                    precio = scanner.nextFloat();
                    addProd();
                }
            }
            
            case 2 -> {
                System.out.println("--------- MODIFICAR PRODUCTOS ---------");
                System.out.print("\nTeclee el ID del producto a modificar: ");
                id = scanner.nextInt();
                //Buscamos por ID
                if(!verificarExistenciaProd(false)){
                    System.out.println("No existe algun producto con ese ID");
                } else{
                    System.out.println("Que desea modificar de ese producto?");
                    System.out.print("\n1. Nombre");
                    System.out.print("\n2. Descripcion");
                    System.out.print("\n3. Precio");
                    System.out.println("\n\nTeclee una opcion: ");
                    int opcionMod = scanner.nextInt();
                    switch(opcionMod){
                        case 1 -> {
                            System.out.print("\n\nTeclee el nuevo nombre: ");
                            scanner.nextLine();
                            nombre = scanner.nextLine();
                            //Verificamos por nombre
                            while(verificarExistenciaProd(true)){
                                System.out.println("\nEse nombre ya esta ocupado, teclee otro: ");
                                nombre = scanner.nextLine();
                            }
                            modProd(opcionMod);
                        }
                        
                        case 2 -> {
                            System.out.println("\n\nTeclee la nueva descripcion: ");
                            scanner.nextLine();
                            descripcion = scanner.nextLine();
                            modProd(opcionMod);
                        }
                        
                        case 3 -> {
                            System.out.println("\n\nTeclee el nuevo precio: ");
                            precio = scanner.nextFloat();
                            modProd(opcionMod);
                        }
                    }
                }
            }
            
            case 3 -> {
                System.out.println("--------- ELIMINAR PRODUCTOS ---------");
                System.out.print("\n\nTeclee el ID del producto a eliminar: ");
                id = scanner.nextInt();
                //Verificamos por ID
                if(!verificarExistenciaProd(false)) System.out.println("El ID tecleado no existe"); 
                else delProd();
            }
            
            case 4 -> {
                System.out.println("--------- PRODUCTOS DADOS DE BAJA---------\n");
                visualizarBajas();
            }
            
            case 5 -> {
                System.out.println("--------- DAR DE ALTA PRODUCTOS ---------");
                System.out.print("\n\nTeclee el ID del producto a dar de alta: ");
                id = scanner.nextInt();
                //Si verificarBajProd es true, quiere decir que el producto ya esta dado de alta
                if(!verificarExistenciaProd(false) || verificarBajaProd()){
                    System.out.println("ID invalido...");
                } else{
                    darAltaProd();
                }
            }
            
            case 6 -> {
                setSesion(false); //Cerramos la sesión
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    private void addProd(){
        //Se añade un producto a la base de datos
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "INSERT INTO productos VALUES (DEFAULT, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, nombre);
                st.setString(2, descripcion);
                st.setFloat(3, precio);
                st.setBoolean(4, true); //Estara dado de alta el producto Estado = true
                st.executeUpdate();
                System.out.println("Se ha agregado el producto con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error con la conexion a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para agregar el producto: " + e.toString());
        }
    }
    
    private void modProd(int opcionMod){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                switch(opcionMod){
                    case 1 -> {
                        String query = "UPDATE productos SET NombreP = ? WHERE IdProducto = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, nombre);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el nombre del producto satisfactoriamente");
                    }
                    
                    case 2 -> {
                        String query = "UPDATE productos SET Descripcion = ? WHERE IdProducto = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, descripcion);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado la descripcion del producto satisfactoriamente");
                    }
                    
                    case 3 -> {
                        String query = "UPDATE productos SET Precio = ? WHERE IdProducto = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setFloat(1, precio);
                        st.setInt(2, id);
                        st.executeUpdate();
                        System.out.println("Se ha modificado el precio del producto satisfactoriamente");
                    }
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error para conectarse a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para modificar los datos del producto: " + e.toString());
        }
    }
    
    //En este metodo solo cambiamos el estado del producto a false
    //No se ocupa el DELETE
    private void delProd(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE productos SET Estado = false WHERE IdProducto = " + id;
                Statement st = conexion.createStatement();
                st.executeUpdate(query);
                System.out.println("Producto dado de baja de forma exitosa");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error para conectarse a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de baja el producto: " + e.toString());
        }
    }
    
    //Para evitar duplicados en el registro de productos
    //Y para verificar la existencia como tal
    //Si bln es true, quiere decir que buscara por nombre,
    //en caso contrario, buscara por ID
    public boolean verificarExistenciaProd(boolean bln){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = bln ? "SELECT * FROM productos WHERE NombreP = '" + nombre + "'" : 
                        "SELECT * FROM productos WHERE IdProducto = " + id;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si hay registros, quiere decir que el producto ya existe
            } else{
                System.out.println("Error para conectarse a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para verificar la existencia del producto: " + e.toString());
        }
        return true; //Para que no proceda el registro si es que hay error
    }
    
    //Este metodo verificara si el producto esta dado de baja
    private boolean verificarBajaProd(){
        try{
            if(MySQLConnection.conectarBD()){
                boolean estado = true;
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Estado FROM productos WHERE IdProducto = " + id;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    estado = rs.getBoolean(1);
                    //Tenemos la certeza de que solo habra un registro
                }
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.commit();
                conexion.setAutoCommit(true);
                return estado;
            }
        } catch(SQLException e){
            System.out.println("Error al verificar la baja del producto: " + e.toString());
        }
        return true; //Para evitar errores
    }
    
    //Se mostraran los productos con Estado = false
    private void visualizarBajas(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT IdProducto, NombreP, Descripcion, Precio FROM productos WHERE Estado = false";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  \t  " + 
                            rs.getString(3) + "  \t  " + rs.getFloat(4));
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo realizar la conexion a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al mostrar los productos dados de baja: " + e.toString());
        }
    }
    
    //Cambia el estado de los productos a true
    private void darAltaProd(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "UPDATE productos SET Estado = true WHERE IdProducto = " + id;
                Statement st = conexion.createStatement();
                st.executeUpdate(query);
                System.out.println("Se ha dado de alta el producto con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo realizar la conexion a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo dar de alta el producto: " + e.toString());
        }
    }
    
    //Setters y Getters

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }
}