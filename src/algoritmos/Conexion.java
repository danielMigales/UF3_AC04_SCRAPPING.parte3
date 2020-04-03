package algoritmos;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Migales Puertas
 * 
 */
public class Conexion {

    //datos de la base de datos 
    private static Connection conexion;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String BD = "scrapping";
    private static final String TABLE = "datosWeb";

    //constructor de la conexion
    public Conexion() {
        conexion = null;
        try {
            Class.forName(DRIVER);
            conexion = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void desconectar() {
        conexion = null;
    }

    //crear una nueva base de datos y su tabla o sobreescribirla
    public void crearDB() throws SQLException, ClassNotFoundException {

        try {
            Class.forName(DRIVER);
            conexion = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            try (PreparedStatement ps = (PreparedStatement) conexion.prepareStatement("CREATE DATABASE IF NOT EXISTS " + BD)) {
                ps.executeUpdate();
                System.out.println("Base de datos " + BD + " creada o actualizada.");
                System.out.println("\n**************************************************\n");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //insertar los datos en la base de datos a partir del arraylist
    public void insertarDatos(ArrayList<Links> listado) throws SQLException, ClassNotFoundException {

        Class.forName(DRIVER);
        conexion = (Connection) DriverManager.getConnection(URL + BD, USER, PASSWORD);
        PreparedStatement ps = (PreparedStatement) conexion.prepareStatement("CREATE TABLE IF NOT EXISTS "
                + TABLE + "(id SERIAL PRIMARY KEY, url VARCHAR (400) NULL, descripcion VARCHAR (150) NULL, contador int (11) )");
        ps.executeUpdate();
        System.out.println("Tabla " + TABLE + " creada o actualizada.");
        System.out.println("\n**************************************************\n");

        for (Links link : listado) {
            String sql = "INSERT INTO " + TABLE + "(url, descripcion, contador) values ('" + link.getHref() + "', '"
                    + link.getTitulo() + "', '" + link.getNumero() + "')";
            System.out.println(sql);
            try (Statement st = conexion.createStatement()) {
                st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                System.out.println("Datos añadidos a la tabla.");
                try (ResultSet rs = st.getGeneratedKeys()) {
                    rs.next();
                }
            }
        }
        System.out.println("\n**************************************************\n");
    }

    //elimina los datos de la tabla 
    public void eliminarTabla(String nombreTabla) throws SQLException {

        try (Statement st = conexion.createStatement()) {
            String sql = "DROP TABLE IF EXISTS " + nombreTabla + ";";
            st.executeUpdate(sql);
            System.out.println("Eliminada tabla " + nombreTabla);
        }
    }

    //lee los datos en la base de datos y los muestra por pantalla
    public void consultaDatos() throws SQLException {

        Statement st = null;
        String sql = "SELECT * FROM " + TABLE + ";";
        conexion = (Connection) DriverManager.getConnection(URL + BD, USER, PASSWORD);
        try {
            st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int resultados = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                String campo1 = rs.getString("url");
                String campo2 = rs.getString("descripcion");
                String campo3 = rs.getString("contador");
                System.out.println("ID: " + id + "\nEnlace: " + campo1 + "\nDescripcion: " + campo2 + "\nContador: " + campo3 + "\n");
                resultados++;
            }
            if (resultados == 0) {
                System.out.println("No se ha encontrado ningun resultado.");
                System.out.println("\n**************************************************\n");
            }
            rs.close();
            System.out.println("\n**************************************************\n");
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }
}
