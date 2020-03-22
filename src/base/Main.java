package base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Migales Puertas
 */
//Realiza una araña web que a partir de una url obtenga de una pagina web los diferentes enlaces.
//Descargaremos la información que se encuentre en la web, url+texto, y la almacenaremos en una base de datos. 
//Cuando nos encontremos con un url+texto incrementaremos un contador.
public class Main {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);
        int seleccion;
        boolean salir = true;
        Parser parser = new Parser();

        do {
            System.out.println("\n******************MENU PRINCIPAL******************\n");
            System.out.println("1. Scrapping de pagina web: Ver en pantalla los enlaces encontrados.Descargar y almacenar los datos en la base de datos.");
            System.out.println("2. Salir del programa.\n");
            seleccion = entrada.nextInt();

            Conexion baseDatos = new Conexion();
            
            switch (seleccion) {
                case 1: 
                    try {
                    parser.listarUrl();

                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

                case 2:
                    salir = false;
                    break;
            }
        } while (salir);

    }

}
