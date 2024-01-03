/**
 * @file Login.java
 * @brief Proceso de login para usuarios
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase proporciona métodos que son útiles para el proceso de Login de usuarios
 */
package acceso;
//Librerías para la base de datos
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import conexiondb.MySQLConnection;

public class Login extends Acceso{
    private final String password;
    
    public Login(String correo, String password){
        super(correo);
        this.password = password;
    }
    
    @Override
    public boolean validarExistencia(){ //Validamos correo y password
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = (verificarCorreoE()) ? "SELECT * FROM empleados WHERE CorreoE = ? AND PasswordE = ?" : 
                        "SELECT * FROM clientes WHERE CorreoE = ? AND PasswordC = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, correo);
                st.setString(2, password);
                ResultSet rs = st.executeQuery();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si hay un registro, entonces las credenciales son correctas
            } else{
                System.out.println("Se perdio la conexion a la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para validar credenciales: " + e.toString());
        }
        return false;
    }
    
    //Este método servirá para verificar si el usuario está dado de baja o no
    public boolean consultarEstado(){
        try{
            if(MySQLConnection.conectarBD()){
                boolean estado = false;
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = (verificarCorreoE()) ? "SELECT Estado FROM empleados WHERE CorreoE = '" + correo + "'" : "SELECT Estado FROM clientes WHERE CorreoE = '" + correo + "'";
                Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    estado = rs.getBoolean(1);
                    //Tenemos la certeza de que solo devolverá un resultado la consulta
                }
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return estado;
            } else{
                System.out.println("Error para establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("No se pudo verificar el estado");
        }
        return false;
    }
    
    //Este método servirá para dar de alta a los usuarios
    //Solo es cambiar su estado a True
    public void darAltaUser(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                String query = (verificarCorreoE()) ? "UPDATE empleados SET Estado = true WHERE CorreoE = '" + correo + "'" : "UPDATE clientes SET Estado = true WHERE CorreoE = '" + correo + "'";
                PreparedStatement st = conexion.prepareStatement(query);
                st.executeUpdate();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
            } else{
                System.out.println("Error para establecer conexion con la base de datos");
            }
        } catch(SQLException e){
            System.out.println("Error para dar de alta al usuario: " + e.toString());
        }
    }
}
