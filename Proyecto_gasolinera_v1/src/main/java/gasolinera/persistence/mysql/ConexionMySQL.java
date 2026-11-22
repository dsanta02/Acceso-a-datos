package gasolinera.persistence.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ConexionMySQL {

    private static final String VARIABLE_URL = "GASOLINERA_DB_URL";
    private static final String VARIABLE_USUARIO = "GASOLINERA_DB_USER";
    private static final String VARIABLE_PASSWORD = "GASOLINERA_DB_PASSWORD";

    private ConexionMySQL() {
    }

    public static Connection obtenerConexion() throws SQLException {
        Map<String, String> variables = System.getenv();
        List<String> faltantes = new ArrayList<>();
        String url = obtenerVariable(variables, VARIABLE_URL, faltantes);
        String usuario = obtenerVariable(variables, VARIABLE_USUARIO, faltantes);
        String password = obtenerVariable(variables, VARIABLE_PASSWORD, faltantes);

        if (!faltantes.isEmpty()) {
            throw new IllegalStateException("Faltan variables de entorno requeridas: "
                    + String.join(", ", faltantes));
        }

        return DriverManager.getConnection(url, usuario, password);
    }

    private static String obtenerVariable(Map<String, String> variables, String nombre,
                                         List<String> faltantes) {
        String valor = variables.get(nombre);
        if (valor == null || valor.isBlank()) {
            faltantes.add(nombre);
            return null;
        }
        return valor;
    }
}
