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
//Clase para la verificacion del correo
import acceso.Registro;
//Clase para entrada por teclado
import java.util.Scanner;

public abstract class Users {
    //Datos generales para clientes y empleados
    protected int id;
    protected String nombre;
    protected String apellidoPaterno;
    protected String apellidoMaterno;
    protected String correo;
    protected String password;
    protected boolean sesion;
    protected boolean sesionPedidos;
    protected Scanner scanner;
    protected boolean tipoUser; //false para clientes, true para empleados
    
    public Users(){
        scanner = new Scanner(System.in);
    }
    
    //Metodos abstractos
    protected abstract void consultarID(); //Consulta el ID del usuario mediante su correo
    public abstract void insertarUser(); //Inserta el usuario a la BD
    protected abstract void modificarUser(int opcionMod); //Modifica el usuario en la BD
    protected abstract void darBajaUser(); //Cambia el estado a 0 en la BD
    public abstract String darBienvenidaUser(); //Se imprime: Bienvenido <NOM_COMPLETO_USER>, se devuelve un String
    //Para este método, para los clientes se visualizarán los productos con Estado = true
    //Para los empleados, se visualizarán todos los productos, sin importar su Estado
    protected abstract void verProductos();
    public abstract void menuUser();
    public abstract void menuPedidos();
    
    protected void modificarDatosMenu(){
        System.out.println("--------- MODIFICAR DATOS ---------");
        System.out.print("\nQue desea modificar?");
        System.out.print("\n\n1. Nombre");
        System.out.print("\n2. Apellido Paterno");
        System.out.print("\n3. Apellido Materno");
        System.out.print("\n4. Correo");
        System.out.print("\n5. Password");
        System.out.println("\n6. Salir");
        System.out.print("\n\nTeclee una opcion: ");
        int opcionMod = scanner.nextInt();
        switch(opcionMod){
            case 1 -> {
                System.out.print("\n\nTeclee el nuevo nombre: ");
                scanner.nextLine();
                nombre = scanner.nextLine();
                modificarUser(opcionMod);
            }
                    
            case 2 -> {
                System.out.print("\n\nTeclee el nuevo apellido paterno: ");
                scanner.nextLine();
                apellidoPaterno = scanner.nextLine();
                modificarUser(opcionMod);
            }
                    
            case 3 -> {
                System.out.print("\n\nTeclee el nuevo apellido materno: ");
                scanner.nextLine();
                apellidoMaterno = scanner.nextLine();
                modificarUser(opcionMod);
            }
                    
            case 4 -> {
                System.out.print("\n\nTeclee el nuevo correo: ");
                scanner.nextLine();
                correo = scanner.nextLine();
                Registro registro = new Registro(correo);
                if(registro.validarExistencia()){
                    System.out.println("\nEse correo ya esta en uso");
                } else if(!registro.verificarCorreoE() && tipoUser){ //Si es empleado y su correo no es de empleado
                    System.out.println("\nNo se puede ingresar ese correo debido a que eres empleado");
                } else if(registro.verificarCorreoE() && !tipoUser) //Si es cliente y su correo es de empleado
                    System.out.println("\nCorreo invalido...");
                else{
                    modificarUser(opcionMod);
                }
            }
                    
            case 5 -> {
                System.out.print("\n\nTeclee el nuevo password: ");
                scanner.nextLine();
                password = scanner.nextLine();
                modificarUser(opcionMod);
            }
            
            case 6 -> {
                System.out.println("Operacion cancelada");
            }
            
            default -> {
                System.out.println("Opcion invalida...");
            }
        }
    }
    
    protected void darBajaMenu(){
        System.out.println("--------- DAR DE BAJA LA CUENTA ---------");
        System.out.print("\nRealmente quieres darte de baja en el sistema? (s/n)");
        String confirmacionStr = scanner.next();
        confirmacionStr = confirmacionStr.toUpperCase();
        char confirmacion = confirmacionStr.charAt(0);
        if(confirmacion == 'S'){
            darBajaUser();
            setSesion(false);
        } else{
            System.out.println("Operacion cancelada...");
        }
    }
    
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

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }

    public boolean isTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(boolean tipoUser) {
        this.tipoUser = tipoUser;
    }
    
    
}
