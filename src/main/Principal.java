/**
 * @file Principal.java
 * @brief Clase que tendrá el método main
 * @version 1.0
 * @date 2024-01-02
 * @author Jesus Antonio Lopez Bandala
 * @title Clase principal
 * @procedure Esta clase servirá para la lógica principal de la aplicación
 */
package main;
//Clase para la conexión a la BD de MySQL
import db.conexiondb.MySQLConnection;
//Clase para la entrada de datos por teclado
import java.util.Scanner;
//Importamos las clases para el acceso de usuarios
import acceso.*;
//Importamos las clases de los usuarios
import usuarios.*;

public class Principal {
    
    public static void main(String[] args) {
        if(MySQLConnection.conectarBD()){
            boolean seguir = true;
            do{
                Scanner scanner = new Scanner(System.in);
                System.out.println("======== COFFEE SHOP ========");
                System.out.println("\nQue desea hacer?");
                System.out.print("\n1. Login");
                System.out.print("\n2. Registro");
                System.out.print("\n3. Salir");
                System.out.println("\n\nIngrese su opcion: ");
                int opcion = scanner.nextInt();
                switch(opcion){
                    case 1 ->{
                        System.out.print("\n\n***** LOGIN *****");
                        System.out.print("\n\nIngrese su correo electronico: ");
                        scanner.nextLine();
                        String correo = scanner.nextLine();
                        System.out.print("\nIngrese su password: ");
                        String password = scanner.nextLine();
                        Login login = new Login(correo, password);
                        //Verificamos credenciales
                        if(login.validarExistencia()){
                            //Una vez verificadas las credenciales, asignamos el menu correspondiente
                            //Es decir, determinamos el tipo de usuario
                            if(login.verificarCorreoE()){
                                //Para los empleados
                                Empleados empleados = new Empleados();
                                empleados.setCorreo(correo); //Asignamos el correo para después dar la bienvenida
                                //Activamos la sesion
                                empleados.setSesion(true);
                                
                                while(empleados.isSesion()){
                                    System.out.println("********* SESION DE EMPLEADOS *********");
                                    System.out.println("Bienvenido " + empleados.darBienvenidaUser() + "\n");
                                    empleados.menuUser();
                                }
                            } else{
                                //Para los clientes
                                Clientes clientes = new Clientes();
                                clientes.setCorreo(correo); //Asignamos el correo para después dar la bienvenida
                                //Activamos la sesion
                                clientes.setSesion(true);
                                while(clientes.isSesion()){
                                    System.out.println("********* SESION DE CLIENTES *********");
                                    System.out.println("Bienvenido " + clientes.darBienvenidaUser());
                                    clientes.menuUser();
                                }
                            }
                        } else{
                            System.out.println("Usuario y/o contraseña incorrectos");
                        }
                    }
                    
                    case 2 -> {
                        System.out.print("\n\n***** REGISTRO *****");
                        System.out.print("\n\nTeclee su correo: ");
                        scanner.nextLine();
                        String correo = scanner.nextLine();
                        Registro registro = new Registro(correo);
                        if(registro.validarExistencia()){
                            System.out.println("Error! Ese correo ya esta asociado a una cuenta...");
                        } else{
                            System.out.print("\nTeclee su password: ");
                            String password = scanner.nextLine();
                            System.out.print("\nTeclee su nombre: ");
                            String nombre = scanner.nextLine();
                            System.out.println("\nTeclee su apellido paterno: ");
                            String apellidoPaterno = scanner.nextLine();
                            System.out.println("\nTeclee su apellido materno: ");
                            String apellidoMaterno = scanner.nextLine();
                            if(registro.verificarCorreoE()){
                                //Si el correo es de un empleado
                                Empleados empleado = new Empleados();
                                empleado.setNombre(nombre);
                                empleado.setApellidoPaterno(apellidoPaterno);
                                empleado.setApellidoMaterno(apellidoMaterno);
                                empleado.setCorreo(correo);
                                empleado.setPassword(password);
                                empleado.insertarUser();
                            } else{
                                //Si el correo es de un cliente
                                Clientes cliente = new Clientes();
                                cliente.setNombre(nombre);
                                cliente.setApellidoPaterno(apellidoPaterno);
                                cliente.setApellidoMaterno(apellidoMaterno);
                                cliente.setCorreo(correo);
                                cliente.setPassword(password);
                                cliente.insertarUser();
                            }
                        }
                    }
                    
                    case 3 -> {
                        seguir = false;
                    }
                    
                    default -> {
                        System.out.println("Opcion invalida...");
                    }
                }
            } while(seguir);
        } else{
            System.out.println("Fallo en la conexion con la BD");
        }
    }
}
