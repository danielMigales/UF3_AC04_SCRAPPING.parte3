package base;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Daniel
 */
public class Conexion {

    private static Connection conexion;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://localhost:3306/scrapping";

    public Conexion() {

        conexion = null;
        try {
            Class.forName(DRIVER);
            conexion = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
            if (conexion != null) {
                System.out.println("Conexion a la base de datos correcta.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar a la base de datos." + e);
        }
    }

    public void desconectar() {

        conexion = null;
    }

    public void insertarDatos(Links listado) throws SQLException {

        String sql = "INSERT INTO enlaces values ('" + listado.getNumero() + "', '" +
                listado.getHref() + "', '"+listado.getTitulo() + "')";

        Statement st = null;
        try {
            st = conexion.createStatement();
            st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Datos añadidos.");
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

}
