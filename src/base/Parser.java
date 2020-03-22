package base;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Daniel
 */
public class Parser {

    public void listarUrl(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            System.out.println(link.attr("abs:href")); 
        }

    }
}
