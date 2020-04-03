package algoritmos;

import base.Main;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Daniel Migales Puertas
 * 
 */
public class Parser {

    //Arraylist para guardar temporalmente los links
    ArrayList<Links> listaLinks = new ArrayList<>();
    ArrayList<Links> listaLinksCortos = new ArrayList<>();
    ArrayList<Links> listaLinksDuplicados = new ArrayList<>();

    //comprueba que la url introducida este disponible
    public int comprobarUrl(String url) {
        Connection.Response response = null;
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
    public void analizarUrl() throws IOException {

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
            try {
                Conexion conexion = new Conexion();
                conexion.crearDB();
                conexion.insertarDatos(listaLinksDuplicados);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        for (Map.Entry<String, Links> lista : mapLinks.entrySet()) {
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

}
