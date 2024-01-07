/**
 * @file Acceso.java
 * @brief Clase abstracta para el Login y el Registro de usuarios
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure Esta clase proporciona las validaciones para poder continuar con el Login y el Registro de usuarios
 */
package acceso;

//Librerías para la verificación del correo del empleado
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Acceso {
    protected String correo;
    
    public Acceso(String correo){
        this.correo = correo;
    }
    
    //Este método servirá para que no haya duplicados en el registro
    //Y en el Login servirá para validar las credenciales del usuario
    public abstract boolean validarExistencia();
    
    //Este método validará que el correo ingresado sea de un empleado
    //Estructura de un correo de un empleado: xxxx@coffeeshop.mx
    public boolean verificarCorreoE(){
        // Expresión regular para validar el correo
        String regex = "^[a-zA-Z0-9._%+-]+@coffeeshop\\.mx$";

        // Compilar la expresión regular
        Pattern pattern = Pattern.compile(regex);

        // Crear un matcher para el correo proporcionado
        Matcher matcher = pattern.matcher(correo);

        // Verificar si coincide con la expresión regular
        return matcher.matches();
    }
    
    //Este método validará que el correo ingresado sea de un administrador
    //Estructura de un correo de un empleado: xxxx@coffeeshop.admin.mx
    public boolean verificarCorreoA(){
        // Expresión regular para validar el correo
        String regex = "^[a-zA-Z0-9._%+-]+@coffeeshop.admin\\.mx$";

        // Compilar la expresión regular
        Pattern pattern = Pattern.compile(regex);

        // Crear un matcher para el correo proporcionado
        Matcher matcher = pattern.matcher(correo);

        // Verificar si coincide con la expresión regular
        return matcher.matches();
    }
}
