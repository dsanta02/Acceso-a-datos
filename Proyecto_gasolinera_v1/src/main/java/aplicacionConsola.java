
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class aplicacionConsola {

    Scanner sc;
    Ficheros c;

    public aplicacionConsola(Scanner sc, Ficheros c) {
        this.sc = sc;
        this.c = c;
    }

    public void ejecutar() {

        Path jsonClientes = Paths.get("src/clientesJson.json");
        Path jsonPagos = Paths.get("src/PagosJson.json");

        // Comprobar si ya existe el almacenamiento JSON
        if (!Files.exists(jsonClientes) && !Files.exists(jsonPagos)) {

            System.out.println("¿Quieres migrar los datos CSV a JSON? (si/no)");
            String respuesta = sc.nextLine();

            if (respuesta.equalsIgnoreCase("si")) {

                System.out.println("Ruta del CSV de clientes:");
                String rutaClientes = sc.nextLine();

                System.out.println("Ruta del CSV de pagos:");
                String rutaPagos = sc.nextLine();

                MigraCSVToJson migracion = new MigraCSVToJson();

                if (!migracion.migrar(rutaClientes, rutaPagos)) {
                    System.out.println("No se ha podido completar la migración.");
                    return;
                }

            } else if (respuesta.equalsIgnoreCase("n")) {

                if (!c.crearArchivo()) {
                    System.out.println("No se ha podido preparar el almacenamiento.");
                    return;
                }

            } else {
                System.out.println("Respuesta no válida. Escribe s o n.");
                return;
            }

        } else {


            if (!c.crearArchivo()) {
                System.out.println("No se ha podido preparar el almacenamiento.");
                return;
            }
        }

        List<Clientes> clientes = c.leerClientes();
        List<Pagos> pagos = c.leerPagos();

        if (clientes == null || pagos == null) {
            System.out.println(
                    "No se puede continuar porque existe un problema "
                            + "con los datos almacenados."
            );
            return;
        }

        GestionClientes g = new GestionClientes(sc, c);

        int opcion;

        do {

            System.out.println("=== GESTION DE GASOLINERA ===");
            System.out.println("1. Dar de alta un cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar clientes");
            System.out.println("4. Procesar un pago de repostaje");
            System.out.println("5. Consultar pagos");
            System.out.println("0. Salir");
            System.out.println("Opción:");

            opcion = sc.nextInt();

            if (opcion == 1) {

                g.darAlta(clientes);

            } else if (opcion == 2) {

                g.listarCliente(clientes);

            } else if (opcion == 3) {

                g.buscarCliente(clientes);

            } else if (opcion == 4) {

                GestionPagos p = new GestionPagos(sc, g);
                p.procesarPago(pagos, clientes);

            } else if (opcion == 5) {

                GestionPagos p = new GestionPagos(sc, g);
                p.consultarPagos(pagos, clientes);

            } else if (opcion != 0) {

                System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        System.out.println("Hasta pronto.");
    }
}

