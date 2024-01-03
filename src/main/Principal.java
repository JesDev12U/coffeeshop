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
import conexiondb.MySQLConnection;
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
                System.out.print("\n\n1. Login");
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
                            Clientes clientes = new Clientes();
                            clientes.setCorreo(correo); //Asignamos el correo para después dar la bienvenida
                            System.out.println("Bienvenido " + clientes.darBienvenidaUser());
                        }
                    }
                }
            } while(seguir);
        } else{
            System.out.println("Fallo en la conexion con la BD");
        }
    }
}
