package algoritmos;

/**
 * @author Daniel Migales Puertas
 *
 */
public class Links {

    int numero;
    String href;
    String titulo;

    public Links(int numero, String href) {
        this.numero = numero;
        this.href = href;
    }

    public Links(int numero, String href, String titulo) {
        this.numero = numero;
        this.href = href;
        this.titulo = titulo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
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

    @Override
    public String toString() {

        return "Numero: " + numero + "\tEnlace:" + href + "\n\t\tDescripcion: " + titulo;
    }

}
