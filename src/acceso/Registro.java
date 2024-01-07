/**
 * @file Registro.java
 * @brief Clase para el registro de usuarios
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase cuenta con validaciones para proceder con el registro de usuarios
 */
package acceso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.conexiondb.MySQLConnection;

public class Registro extends Acceso {
    public Registro(String correo){
        super(correo);
    }
    
    @Override
    //Este método verifica la existencia del usuario, esto es para evitar duplicados
    public boolean validarExistencia(){
        try{
            if(MySQLConnection.conectarBD()){
                Connection conexion = MySQLConnection.getConexion();
                //Si se hacen varias transacciones y en una hay error, ninguna se ejecuta
                conexion.setAutoCommit(false);
                /*String query = (verificarCorreoE()) ? "SELECT * FROM empleados WHERE CorreoE = ?" : 
                        "SELECT * FROM clientes WHERE CorreoE = ?";*/
                String query;
                if(verificarCorreoE()) query = "SELECT * FROM empleados WHERE CorreoE = ?";
                else if (verificarCorreoA()) query = "SELECT * FROM admins WHERE CorreoE = ?";
                else query = "SELECT * FROM clientes WHERE CorreoE = ?";
                PreparedStatement st = conexion.prepareStatement(query);
                st.setString(1, correo);
                ResultSet rs = st.executeQuery();
                //Confirmamos los cambios como una única transacción en la BD
                conexion.commit();
                conexion.setAutoCommit(true);
                return rs.next(); //Si hay un registro, quiere decir que hay un valor duplicado
            } else{
                System.out.println("Error para establecer conexion con la BD");
            }
        } catch(SQLException e){
            System.out.println("Error para validar duplicado: " + e.toString());
        }
        return true; //Para que no continue el registro
    }
}
