/**
 * @file MySQLConnection.java
 * @brief Conexión a la base de datos coffeshop en MySQL
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @title Conexión a la base de datos coffeeshop en MySQL
 * @procedure La clase tiene el modelo general para la conexión a bases de datos con el manejador MySQL
 */
package db.conexiondb;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static Connection conexion;

    public static boolean conectarBD() {
        //Parametros de conexión a la base de datos
        String url = "jdbc:mysql://localhost:3306/" + "coffeeshop";
        String usuario = "root";
        String pass = "";
        try{
            //Driver para MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Establecemos la conexion
            conexion = DriverManager.getConnection(url, usuario, pass);
            if(conexion != null){
                return true; //Retornamos true para indicar que si se pudo hacer la conexión a la BD
            }
        } catch(ClassNotFoundException e){
            System.out.println("Error: No se encontro el driver de MySQL: " + e.toString());
        } catch(SQLException e){
            System.out.println("Error: Fallo en la conexion a la base de datos: " + e.toString());
        }
        return false; //Retornamos false para indicar que no se pudo hacer la conexión a la BD
    }
    
    //Para realizar las querys
    public static Connection getConexion(){
        return conexion;
    }
    
    public static void cerrarConexion(){
        try{
            if(conexion != null){
                conexion.close();
            }
        } catch(SQLException e){
            System.out.println("Error al cerrar la conexión: " + e.toString());
        }
    }
}
