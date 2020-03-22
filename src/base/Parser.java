package base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Daniel
 */
public class Parser {

    ArrayList<Links> listaLinks = new ArrayList<>();
    Links enlace;

    public void listarUrl() throws IOException {

        System.out.println("Introduzca la url: ");
        Scanner entradaString = new Scanner(System.in);
        String url = entradaString.nextLine();

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        int contador = 1;

        for (Element link : links) {
            String href = link.attr("abs:href");
            String titulo = link.text();
            enlace = new Links(contador, href, titulo);
            listaLinks.add(enlace);
            contador++;
            System.out.println(enlace);
            Conexion conexion = new Conexion();
            try {
                conexion.insertarDatos(enlace);
            } catch (SQLException ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    }
