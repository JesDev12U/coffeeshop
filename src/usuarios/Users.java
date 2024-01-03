/**
 * @file Users.java
 * @brief Superclase para los clientes y empleados
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase abstracta contendra diferentes metodos que comparten
 * tanto los cliente como los empleados
 */
package usuarios;

public abstract class Users {
    //Datos generales para clientes y empleados
    protected int id;
    protected String nombre;
    protected String apellidoPaterno;
    protected String apellidoMaterno;
    protected String correo;
    protected String password;
    
    //Metodos abstractos
    protected abstract void consultarID(); //Consulta el ID del usuario mediante su correo
    protected abstract void insertarUser(); //Inserta el usuario a la BD
    protected abstract void modificarUser(); //Modifica el usuario en la BD
    protected abstract void darBajaUser(); //Cambia el estado a 0 en la BD
    public abstract String darBienvenidaUser(); //Se imprime: Bienvenido <NOM_COMPLETO_USER>, se devuelve un String
    //Para este método, para los clientes se visualizarán los pedidos específicos de un cliente
    //Para los empleados, se visualizarán todos los pedidos de todos los clientes
    protected abstract void visualizarPedidos();
    //Para este método, para los clientes se visualizarán los productos con Estado = true
    //Para los empleados, se visualizarán todos los productos, sin importar su Estado
    protected abstract void verProductos();
    
    //Setters and getters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
