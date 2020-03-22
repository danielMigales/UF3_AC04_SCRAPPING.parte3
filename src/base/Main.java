package base;

import java.io.IOException;
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
        String url = null;

        System.out.println("******************MENU PRINCIPAL******************");
        System.out.println("");
        System.out.println("1. Scrapping de pagina web. Introduzca la url");
        System.out.println("2. Salir del programa.");
        seleccion = entrada.nextInt();

        

        try {
            Parser parser = new Parser();
            parser.listarUrl(url);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
