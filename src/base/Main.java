package base;

import algoritmos.Conexion;
import algoritmos.Links;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
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
//Dada la pagina web anterior, nuestra base de datos resultante será:
//www.marca.com     Deportes 	+1
//www.as.com        Deportes	+1
//www.elpais.es     Noticias 	+2
//Nuestro programa mostrarà de manera ordenada las webs mas entrelazadas.
//
public class Main {
    
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";
    String ANSI_RESET = "\u001B[0m";

    //Arraylist para guardar temporalmente los links
    ArrayList<Links> listaLinks = new ArrayList<>();
    ArrayList<Links> listaLinksCortos = new ArrayList<>();
    ArrayList<Links> listaLinksDuplicados = new ArrayList<>();

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
            System.out.println(ANSI_BLUE + "1. SCRAPPING WEB:" + ANSI_RESET);
            System.out.println("\tExtrae todas las url de una web, las recorta (hasta el .com/) y las compara entre si separando las repetidas.");
            System.out.println("\tLos datos son almacenados en la base de datos llamada scrapping, en la tabla datosWeb (creacion automatica).\n");
            System.out.println(ANSI_BLUE + "2. CONSULTAR LOS REGISTROS INTRODUCIDOS EN LA BASE DE DATOS:" + ANSI_RESET);
            System.out.println("\tVisualiza en pantalla los registros existentes en la base de datos preestablecida.\n");
            System.out.println(ANSI_BLUE + "3 SALIR DEL PROGRAMA.\n" + ANSI_RESET);
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
            //obtengo solo los enlaces
            Elements links = doc.select("a[href]");
            int contador = 1;

            System.out.println("LISTADO DE TODOS LOS ENLACES ENCONTRADOS: \n");
            //recorro los elementos que sean un link href
            for (Element link : links) {
                //obtengo los dos elementos
                String href = link.attr("abs:href");
                String titulo = link.text();
                Links objetoLink = new Links(contador, href, titulo);
                listaLinks.add(objetoLink);
                System.out.println(objetoLink);
                contador++;
            }
            System.out.println("\n**************************************************\n");

            cortarUrl(listaLinks);
            buscarDuplicados(listaLinksCortos);

            //GUARDAR LOS DATOS DIRECTAMENTE EN LA BASE DE DATOS 
            gestionarBD(listaLinksDuplicados);
        } else {
            System.out.println("El Status Code es: " + comprobarUrl(url) + " Ha sido bajo.");
        }

    }

    public void cortarUrl(ArrayList<Links> listaLinks) {

        System.out.println("LISTADO DE LAS URL'S RECORTADAS: \n");
        int contador = 1;
        String href = null;
        //el elemento href lo recorto usando como separacion la barra, quedando separado en tres partes: https , www.xxxx.com , resto de datos    
        for (Links listaLink : listaLinks) {
            String[] hrefsplit = listaLink.getHref().split(Pattern.quote("/"));
            for (int i = 1; i < hrefsplit.length; i++) {
                href = hrefsplit[2];
            }
            String titulo = listaLink.getTitulo();
            Links enlaceCorto = new Links(contador, href, titulo);
            System.out.println(enlaceCorto);
            listaLinksCortos.add(enlaceCorto);
            contador++;
        }
        System.out.println("\n**************************************************\n");
    }

    public void buscarDuplicados(ArrayList<Links> listaLinks) {

        System.out.println("LISTADO FINAL DE URL'S: \n");

        //ahora recorro el arraylist obtenido y extraigo las url que estan repetidas          
        Map<String, Links> mapLinks = new HashMap<String, Links>(listaLinks.size());

        int contador = 1;
        for (Links lista : listaLinks) {
            mapLinks.put(lista.getHref(), lista);
        }
        //Agrego cada elemento del map a una nueva lista y muestro cada elemento.        
        for (Entry<String, Links> lista : mapLinks.entrySet()) {
            String href = lista.getKey();
            String titulo = lista.getValue().getTitulo();
            Links enlaceDuplicado = new Links(contador, href, titulo);
            System.out.println(enlaceDuplicado);
            listaLinksDuplicados.add(enlaceDuplicado);
            contador++;
        }
        System.out.println("\n**************************************************\n");

        //FALTA QUE EL CONTADOR CUENTE LAS VECES QUE SE REPITE CADA URL....NO TENGO NI IDEA DE COMO HACERLO
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
