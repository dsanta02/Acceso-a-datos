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

        if (!c.crearArchivo()) {

            System.out.println(
                    "No se ha podido preparar el almacenamiento."
            );

            return;
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
            System.out.println("6. Migrar datos de CSV a JSON");
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

            } else if (opcion == 6) {

                // Aquí iría la migración de CSV a JSON

            } else if (opcion != 0) {

                System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        System.out.println("Hasta pronto.");
    }


}
