/**
 * @file Carrito.java
 * @brief Clase para la lógica del carrito de los clientes
 * @version 1.0
 * @date 2024-01-03
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase contendrá los métodos de toda la lógica necesaria
 * para añadir los productos que desea comprar el cliente
 */
package compras;
//Clases para la base de datos
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.conexiondb.MySQLConnection;
//Para verificar la existencia de productos en la base de datos
import inventario.ProductosEmpleados;

public class Carrito extends Compras {
    private final int idCliente;
    private String detalles;
    private int cantidad;
    private float total;
    
    public Carrito(int idCliente){
        this.idCliente = idCliente;
    }
    
    @Override
    public void menu(){
        System.out.println("===== MENU DE CARRITO =====");
        System.out.print("\n1. Agregar producto");
        System.out.print("\n2. Modificar producto");
        System.out.print("\n3. Eliminar producto");
        System.out.print("\n4. Ver carrito");
        System.out.print("\n5. Salir");
        System.out.print("\n\nTeclee una opcion: ");
        int opcion = scanner.nextInt();
        switch(opcion){
            case 1 -> {
                System.out.println("--------- AGREGAR PRODUCTOS AL CARRITO ---------");
                System.out.print("\nTeclee el ID del producto: ");
                scanner.nextLine();
                idProducto = scanner.nextInt();
                //Verificamos si el producto existe en la base de datos
                ProductosEmpleados producto = new ProductosEmpleados();
                producto.idProducto = idProducto;
                if(!producto.verificarExistenciaProd(false)){ //Buscamos por ID
                    System.out.println("ID invalido...");
                } else if(verificarExistenciaProd(false)){ //No importa el valor booleano
                    System.out.println("El producto ya habia sido agregado a tu carrito...");
                } else if(!producto.verificarBajaProd()) System.out.println("ID invalido..."); 
                else{
                    System.out.print("\nTeclee los detalles: ");
                    scanner.nextLine();
                    detalles = scanner.nextLine();
                    System.out.println("\nTeclee la cantidad: ");
                    cantidad = scanner.nextInt();
                    addProd();
                }
            }
            
            case 2 -> {
                System.out.println("--------- MODIFICAR PRODUCTO DEL CARRITO ---------");
                System.out.print("\nTeclee el ID del producto: ");
                scanner.nextLine();
                idProducto = scanner.nextInt();
                if(verificarExistenciaProd(false)){ //No importa el valor booleano
                    System.out.println("Que deseas modificar?");
                    System.out.print("\n1. Detalles");
                    System.out.print("\n2. Cantidad");
                    System.out.print("\n\nTeclee una opcion: ");
                    int opcionMod = scanner.nextInt();
                    switch(opcionMod){
                        case 1 -> {
                            System.out.print("\nTeclee los nuevos detalles: ");
                            scanner.nextLine();
                            detalles = scanner.nextLine();
                            modProd(opcionMod);
                        }
                        
                        case 2 -> {
                            System.out.print("\nTeclee la nueva cantidad: ");
                            scanner.nextLine();
                            cantidad = scanner.nextInt();
                            modProd(opcionMod);
                        }
                        
                        default -> {
                            System.out.println("Opcion invalida...");
                        }
                    }
                } else{
                    System.out.println("No se encuentra ese producto en tu carrito");
                }
            }
            
            case 3 -> {
                System.out.println("--------- ELIMINAR PRODUCTO DEL CARRITO ---------");
                System.out.print("\nTeclee el ID del producto: ");
                scanner.nextLine();
                idProducto = scanner.nextInt();
                if(verificarExistenciaProd(false)){ //No importa el valor booleano
                    delProd();
                } else{
                    System.out.println("El producto no se encuentra en tu carrito");
                }
            }
            
            case 4 -> {
                verCarrito();
            }
            
            case 5 -> {
                setSesion(false); //Cerramos la sesion del carrito
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    @Override
    protected void addProd(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "INSERT INTO carrito VALUES (?, ?, ?, ?, ?)";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setInt(1, idCliente);
                st.setInt(2, idProducto);
                st.setString(3, detalles);
                st.setInt(4, cantidad);
                calcularTotal();
                st.setFloat(5, total);
                st.executeUpdate();
                System.out.println("Se ha agregado el producto al carrito con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo agregar el producto al carrito: " + e.toString());
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
    protected void modProd(int opcionMod){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                switch(opcionMod){
                    case 1 -> { //Modificacion de detalles
                        String query = "UPDATE carrito SET Detalles = ? WHERE IdCliente = ? AND IdProducto = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        st.setString(1, detalles);
                        st.setInt(2, idCliente);
                        st.setInt(3, idProducto);
                        st.executeUpdate();
                        System.out.println("Se ha modificado los detalles del producto con exito");                         
                    }
                    
                    case 2 -> { //Modificacion de la cantidad
                        String query = "UPDATE carrito SET Cantidad = ?, Total = ? WHERE IdCliente = ? AND IdProducto = ?";
                        PreparedStatement st = conexion.prepareStatement(query);
                        calcularTotal();
                        st.setInt(1, cantidad);
                        st.setFloat(2, total);
                        st.setInt(3, idCliente);
                        st.setInt(4, idProducto);
                        
                        st.executeUpdate();
                        System.out.println("Se ha modificado la cantidad de productos con exito");
                    }
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo conectar a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error al actualizar el producto del carrito: " + e.toString());
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
    protected void delProd() { //Aqui si se implementa el DELETE
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "DELETE FROM carrito WHERE IdCliente = ? AND IdProducto = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setInt(1, idCliente);
                st.setInt(2, idProducto);
                st.executeUpdate();
                System.out.println("Se ha borrado el producto del carrito con exito");
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            }
        } catch(SQLException e){
            System.out.println("Error al eliminar el producto del carrito: " + e.toString());
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
    protected boolean verificarExistenciaProd(boolean bln){ //Se buscara siempre por el ID del producto en el carrito
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM carrito WHERE IdCliente = ? AND IdProducto = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setInt(1, idCliente);
                st.setInt(2, idProducto);
                ResultSet rs = st.executeQuery();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si devuelve true, entonces ya esta el registro del producto en el carrito
            } else{
                System.out.println("No se pudo realizar la conexion a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo verificar la existencia del producto en el carrito: " + e.toString());
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
        return true; //Para evitar errores
    }
    
    private void calcularTotal(){
        buscarPrecioProd();
        total = cantidad * precio;
    }
    
    public float calcularTotalCarrito(){
        float totalPagar = 0;
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                //Calculamos el total a pagar
                String queryTotal = """
                                    SELECT SUM(carrito.Total) AS SumaTotal
                                    FROM carrito
                                    INNER JOIN productos ON carrito.IdProducto = productos.IdProducto
                                    WHERE carrito.IdCliente = """ + idCliente;
                Statement stTotal = conexion.createStatement();
                ResultSet rsTotal = stTotal.executeQuery(queryTotal);
                
                while(rsTotal.next()) {
                    totalPagar = rsTotal.getFloat(1);
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error para establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo consultar el total del carrito: " + e.toString());
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
        return totalPagar;
    }
    
    private void buscarPrecioProd(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT Precio FROM productos WHERE IdProducto = " + idProducto;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    precio = rs.getFloat(1);
                    //Tenemos la certeza de que solo habrá un registro
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            }
        } catch(SQLException e){
            System.out.println("Error al consultar el precio del producto: " + e.toString());
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
    
    private void verCarrito(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = """
                               SELECT carrito.IdProducto, productos.NombreP, carrito.Detalles, productos.Precio, carrito.Cantidad, carrito.Total
                               FROM carrito
                               INNER JOIN productos on carrito.IdProducto = productos.IdProducto
                               WHERE carrito.IdCliente = """ + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                System.out.println("--------- CARRITO ---------");
                System.out.println("ID\tNombre\t\t\t\tDetalles\t\t\t\t\t\tPrecio\t\tCantidad\tTotal");
                while(rs.next()){
                    int idProd = rs.getInt(1);
                    String nomProd = rs.getString(2);
                    String detallesProd = rs.getString(3);
                    float precioProd = rs.getFloat(4);
                    int cantidadProd = rs.getInt(5);
                    float totalProd = rs.getFloat(6);
                    
                    //Ajustar la longitud máxima de los detalles
                    int maxLength = 50;
                    if(detallesProd.length() > maxLength){
                        detallesProd = detallesProd.substring(0, maxLength);
                    }
                    //Imprimir los datos con alineación y columnas más anchas para nombre y detalles
                    System.out.println(String.format("%d\t%-25s\t%-55s\t%.2f\t\t%d\t\t%.2f", idProd, 
                            nomProd, detallesProd, precioProd, cantidadProd, totalProd));
                }
                
                System.out.println("Total a pagar: $" + String.format("%.2f", calcularTotalCarrito()));
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("No se pudo realizar la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para mostrar el carrito: " + e.toString());
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
    
    //Se hace la verificacion si el carrito esta vacio o no
    public boolean isEmpty(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = "SELECT * FROM carrito WHERE IdCliente = " + idCliente;
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return !rs.next(); //Si el carrito tiene registros, decimos que no esta vacio 
            } else{
                System.out.println("No se pudo realizar la conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para consultar el carrito: " + e.toString());
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
        return true; //Para evitar errores
    }
}
