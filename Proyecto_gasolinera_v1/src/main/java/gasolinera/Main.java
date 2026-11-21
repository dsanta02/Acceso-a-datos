package gasolinera;

import gasolinera.console.AplicacionConsola;
import gasolinera.migration.MigraCSVToJson;
import gasolinera.persistence.ErrorPersistencia;
import gasolinera.persistence.csv.Ficheros;
import gasolinera.persistence.json.Jsons;
import gasolinera.repository.ClienteRepository;
import gasolinera.repository.PagoRepository;

import java.nio.file.Files;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Selecciona el almacenamiento:");
        System.out.println("1. CSV");
        System.out.println("2. JSON");

        int opcion = leerOpcionAlmacenamiento(sc);
        ClienteRepository clientes;
        PagoRepository pagos;

        if (opcion == 1) {
            Ficheros csv = new Ficheros();
            try {
                csv.crearArchivo();
            } catch (ErrorPersistencia e) {
                System.out.println("No se pudo preparar el almacenamiento CSV: "
                        + e.getMessage());
                sc.close();
                return;
            }
            clientes = csv;
            pagos = csv;
        } else {
            Jsons json = new Jsons();
            if (!prepararJson(sc, json)) {
                sc.close();
                return;
            }
            clientes = json;
            pagos = json;
        }

        try {
            new AplicacionConsola(sc, clientes, pagos).ejecutar();
        } catch (ErrorPersistencia e) {
            System.out.println("No se puede iniciar la aplicación: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static int leerOpcionAlmacenamiento(Scanner sc) {
        while (true) {
            String entrada = sc.nextLine().trim();
            if (entrada.equals("1") || entrada.equals("2")) {
                return Integer.parseInt(entrada);
            }
            System.out.println("Selecciona 1 para CSV o 2 para JSON:");
        }
    }

    private static boolean prepararJson(Scanner sc, Jsons json) {
        boolean noExisteNinguno = !Files.exists(json.getArchivoClientes())
                && !Files.exists(json.getArchivoPagos());

        if (noExisteNinguno && confirmar(sc,
                "¿Quieres migrar los datos CSV a JSON? (si/no)")) {
            if (!new MigraCSVToJson().migrar()) {
                System.out.println("No se ha podido completar la migración.");
                return false;
            }
        } else if (!noExisteNinguno) {
            try {
                json.buscarClientes();
                json.buscarPagos();
            } catch (ErrorPersistencia e) {
                System.out.println("Los archivos JSON existentes no se pueden leer: "
                        + e.getMessage());
                if (!confirmar(sc,
                        "¿Quieres reemplazarlos importando los CSV? Se guardará una copia de seguridad. (si/no)")) {
                    return false;
                }
                if (!new MigraCSVToJson().migrar()) {
                    System.out.println("No se ha podido recuperar el almacenamiento JSON.");
                    return false;
                }
            }
        }

        try {
            json.crearArchivo();
            return true;
        } catch (ErrorPersistencia e) {
            System.out.println("No se pudo preparar el almacenamiento JSON: "
                    + e.getMessage());
            return false;
        }
    }

    private static boolean confirmar(Scanner sc, String mensaje) {
        while (true) {
            System.out.println(mensaje);
            String respuesta = sc.nextLine().trim();
            if (respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("s")) {
                return true;
            }
            if (respuesta.equalsIgnoreCase("no") || respuesta.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Respuesta no válida. Escribe si o no.");
        }
    }
}
