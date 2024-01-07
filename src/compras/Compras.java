/**
 * @file Compras.java
 * @brief Clase abstracta para las compras
 * @version 1.0
 * @date 2024-01-03
 * @author Jesus Antonio Lopez Bandala
 * @procedure La clase contendrá los métodos necesarios para la lógica
 * de las compras del cliente
 */
package compras;

//Clase para la entrada de datos por teclado
import java.util.Scanner;

public abstract class Compras {
    protected int idProducto;
    protected float precio;
    protected boolean sesion;
    protected Scanner scanner;
    
    public Compras(){
        scanner = new Scanner(System.in);
    }
    
    protected abstract void addProd();
    protected abstract void modProd(int opcionMod);
    protected abstract void delProd();
    protected abstract boolean verificarExistenciaProd(boolean bln);
    public abstract void menu();
    
    //Setters y Getters

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }
    
    
}