package base;

import base.algoritmos.Links;
import base.algoritmos.Conexion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Daniel Migales Puertas
 */
//Realiza una araña web que a partir de una url obtenga de una pagina web los diferentes enlaces.
//Descargaremos la información que se encuentre en la web, url+texto, y la almacenaremos en una base de datos. 
//Cuando nos encontremos con un url+texto incrementaremos un contador.
//
public class Main {

    //Arraylist para guardar temporalmente los links
    ArrayList<Links> listaLinks = new ArrayList<>();

    //Metodo principal
    public static void main(String[] args) throws ClassNotFoundException {
        Main main = new Main();
        main.menuPrincipal();
    }

    //Menu y llamada a metodos para las funciones del programa
    public void menuPrincipal() {
        Scanner entrada = new Scanner(System.in);
        int seleccion;
        boolean salir = true;

        do {
            System.out.println("\n******************MENU PRINCIPAL******************\n");
            System.out.println("1. SCRAPPING WEB:");
            System.out.println("\tExtrae todas las url de una web, las recorta (hasta el .com/) y las compara entre si separando las repetidas.");
            System.out.println("\tLos datos son almacenados en la base de datos llamada scrapping, en la tabla datosWeb (creacion automatica).\n");
            System.out.println("2. CONSULTAR LOS REGISTROS INTRODUCIDOS EN LA BASE DE DATOS:");
            System.out.println("\tVisualiza en pantalla los registros existentes en la base de datos preestablecida.\n");
            System.out.println("3 SALIR DEL PROGRAMA.\n");
            System.out.println("Seleccione una opcion:");
            seleccion = entrada.nextInt();
            System.out.println("\n**************************************************\n");

            switch (seleccion) {
                case 1: {
                    try {
                        AnalizarUrl(); //Llamada al metodo que analiza una url
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                case 2:
                    leerBD();
                    break;
                case 3:
                    salir = false;
                    break;
            }
        } while (salir);
    }

    //Metodo para leer una pagina web en busca de enlaces y su descripcion 
    public void AnalizarUrl() throws IOException {

        System.out.println("Introduzca la url (formato correcto: www.ejemplo.com) ");
        Scanner entradaString = new Scanner(System.in);
        String url = "http://" + entradaString.nextLine();
        System.out.println("\n**************************************************\n");

        //primero se comprueba que la web este disponible
        if (comprobarUrl(url) == 200) {
            //analisis con jsoup
            Document doc = Jsoup.connect(url).get();
            //
            Elements links = doc.select("a[href]");
            int contador = 1;
            int repeticiones = 0;

            //recorro los elementos que sean un link href
            for (Element link : links) {
                //obtengo los dos elementos
                String href = link.attr("abs:href");
                String titulo = link.text();
                //el elemento href lo recorto usando como separacion la barra, quedando separado en tres partes: https    www.xxxx.com   y el resto de datos
                String[] hrefsplit = href.split(Pattern.quote("/"));
                //contruyo objeto y almaceno en arraylist
                for (int i = 1; i < hrefsplit.length; i++) {
                    Links enlace = new Links(contador, hrefsplit[2], titulo);
                    listaLinks.add(enlace);
                    //System.out.println(enlace + "\n");
                    contador++;
                }
            }

            //ahora recorro el arraylist obtenido y extraigo las url que estan repetidas          
            Map<String, Links> mapLinks = new HashMap<String, Links>(listaLinks.size());

            for (Links lista : listaLinks) {
                mapLinks.put(lista.getHref(), lista);
            }
            //Agrego cada elemento del map a una nueva lista y muestro cada elemento.
            ArrayList<Links> nuevaLista = new ArrayList();
            for (Entry<String, Links> lista : mapLinks.entrySet()) {
                nuevaLista.add(lista.getValue());
                System.out.println(lista.getValue());
            }
            System.out.println("\n**************************************************\n");
            
            //GUARDAR LOS DATOS DIRECTAMENTE EN LA BASE DE DATOS 
            gestionarBD(nuevaLista);
        } else {
            System.out.println("El Status Code es: " + comprobarUrl(url) + " Ha sido bajo.");
        }

    }

//comprueba que la url introducida este disponible
    public int comprobarUrl(String url) {
        Response response = null;
        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
            System.out.println("\n**************************************************\n");
        }
        System.out.println("El status code ha sido de " + response.statusCode());
        System.out.println("\n**************************************************\n");
        return response.statusCode();
    }

    //crea la base de datos e inserta los datos
    public void gestionarBD(ArrayList<Links> listaLinks) {
        Conexion conexion = null;
        try {
            conexion = new Conexion();
            conexion.crearDB();
            conexion.insertarDatos(listaLinks);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leerBD() {
        Conexion conexion = null;
        try {
            conexion = new Conexion();
            conexion.consultaDatos();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
