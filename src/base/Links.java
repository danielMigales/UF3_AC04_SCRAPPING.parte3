package base;

/**
 * @author Daniel
 */

public class Links {
    
    int numero;
    String href;
    String titulo;

    public Links(int numero, String href, String titulo) {
        this.numero = numero;
        this.href = href;
        this.titulo = titulo;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        String link = "\nLink " + numero + ":" + "\nHref: " + href + "\nTitulo: " + titulo;
        return link;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
