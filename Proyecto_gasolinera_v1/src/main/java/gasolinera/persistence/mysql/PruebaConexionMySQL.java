package gasolinera.persistence.mysql;

import java.sql.Connection;
import java.sql.SQLException;

public final class PruebaConexionMySQL {

    private PruebaConexionMySQL() {
    }

    public static void main(String[] args) {
        try (Connection conexion = ConexionMySQL.obtenerConexion()) {
            if (conexion.isClosed()) {
                System.err.println("Error: la conexión con MySQL se abrió cerrada.");
                return;
            }
            System.out.println("Conexión con MySQL correcta");
        } catch (IllegalStateException e) {
            System.err.println("Error de configuración: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar o comprobar la conexión con MySQL."
                    + (e.getSQLState() == null ? "" : " Código SQL: " + e.getSQLState()));
        }
    }
}
