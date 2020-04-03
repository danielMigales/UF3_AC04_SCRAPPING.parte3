package base;

import algoritmos.Conexion;
import algoritmos.Parser;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniel Migales Puertas
 *
 */
public class Menu {

    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";
    String ANSI_RESET = "\u001B[0m";

    //Menu y llamada a metodos para las funciones del programa
    public void menuPrincipal() {
        Scanner entrada = new Scanner(System.in);
        int seleccion;
        boolean salir = true;

        do {
            System.out.println("\n*******************************************************MENU PRINCIPAL************************************************\n");
            System.out.println(ANSI_BLUE + "1. SCRAPPING WEB:" + ANSI_RESET);
            System.out.println("\tExtrae todas las url de una web, las recorta (hasta el .com/) y las compara entre si separando las repetidas.");
            System.out.println("\tFinalmente analiza las url resultantes y extrae las que corresponden a la url introducida, quedando solo las url externas");
            System.out.println("\tLos datos son almacenados en la base de datos llamada scrapping, en la tabla datosWeb (creacion automatica).");
            System.out.println("\tSe imprimira en pantalla: ");
            System.out.println("\t\tLISTADO DE TODOS LOS ENLACES ENCONTRADOS EN LA WEB");
            System.out.println("\t\tLISTADO DE TODAS LAS URL'S ENCONTRADAS EN FORMATO CORTO");
            System.out.println("\t\tLISTADO SIN LAS URL'S REPETIDAS\n");
            System.out.println(ANSI_BLUE + "2. CONSULTAR LOS REGISTROS INTRODUCIDOS EN LA BASE DE DATOS:" + ANSI_RESET);
            System.out.println("\tVisualiza en consola todos los registros existentes en la base de datos scrapping.\n");
            System.out.println(ANSI_BLUE + "3 SALIR DEL PROGRAMA.\n" + ANSI_RESET);
            System.out.println("Seleccione una opcion:");
            seleccion = entrada.nextInt();
            System.out.println("\n***************************************************************************************************************************\n");

            switch (seleccion) {
                case 1: {
                    try {
                        //Llamada al metodo que analiza una url con jsoup
                        Parser jsoup = new Parser();
                        jsoup.analizarUrl();
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                case 2:
                    try {
                        //Llamada al metodo que realiza la consulta a la base de datos
                    Conexion conexion = new Conexion();
                    conexion.consultaDatos();
                } catch (SQLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case 3:
                    salir = false;
                    break;
            }
        } while (salir);
    }

}
