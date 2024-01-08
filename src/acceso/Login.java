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
import db.conexiondb.MySQLConnection;

public class Login extends Acceso{
    private final String password;
    
    public Login(String correo, String password){
        super(correo);
        this.password = password;
    }
    
    @Override
    public boolean validarExistencia(){ //Validamos correo y password
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                /*String query = (verificarCorreoE()) ? "SELECT * FROM empleados WHERE CorreoE = ? AND PasswordE = ?" : 
                        "SELECT * FROM clientes WHERE CorreoE = ? AND PasswordC = ?";*/
                String query;
                //Verificamos si es admin
                if(verificarCorreoA()) query = "SELECT * FROM admins WHERE CorreoE = ? AND PasswordA = ?;";
                //Si no, verificamos si es empleado
                else if(verificarCorreoE()) query = "SELECT * FROM empleados WHERE CorreoE = ? AND PasswordE = ?";
                //Si no, entonces es cliente
                else query = "SELECT * FROM clientes WHERE CorreoE = ? AND PasswordC = ?";
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
        return false;
    }
    
    //Este método servirá para verificar si el usuario está dado de baja o no
    public boolean consultarEstado(){
        Connection conexion = null;
        try{
            if(MySQLConnection.conectarBD()){
                boolean estado = false;
                conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                //String query = (verificarCorreoE()) ? "SELECT Estado FROM empleados WHERE CorreoE = '" + correo + "'" : "SELECT Estado FROM clientes WHERE CorreoE = '" + correo + "'";
                String query;
                //Verificamos si es Admin
                if(verificarCorreoA()) query = "SELECT Estado FROM admins WHERE CorreoE = '" + correo + "'";
                //Si no, verificamos si es empleado
                else if(verificarCorreoE()) query = "SELECT Estado FROM empleados WHERE CorreoE = '" + correo + "'";
                //Si no, entonces es cliente
                else query = "SELECT Estado FROM clientes WHERE CorreoE = '" + correo + "'";
                
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
        return false;
    }
}
