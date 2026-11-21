import java.util.Scanner;

public class aplicacionConsola {

    private final Scanner sc;
    private final ClienteRepository clientes;
    private final PagoRepository pagos;

    public aplicacionConsola(Scanner sc, ClienteRepository clientes, PagoRepository pagos) {
        this.sc = sc;
        this.clientes = clientes;
        this.pagos = pagos;
    }

    public void ejecutar() {
        GestionClientes gestionClientes = new GestionClientes(sc, clientes);
        GestionPagos gestionPagos = new GestionPagos(sc, clientes, pagos);
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            String entrada = sc.nextLine().trim();
            int opcion;
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Introduce una opción numérica válida.");
                continue;
            }

            try {
                switch (opcion) {
                    case 1 -> gestionClientes.darAlta();
                    case 2 -> gestionClientes.listarCliente();
                    case 3 -> gestionClientes.buscarCliente();
                    case 4 -> gestionPagos.procesarPago();
                    case 5 -> gestionPagos.consultarPagos();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (ErrorPersistencia e) {
                System.out.println("No se pudo completar la operación: " + e.getMessage());
            }
        }

        System.out.println("Hasta pronto.");
    }

    private void mostrarMenu() {
        System.out.println("=== GESTIÓN DE GASOLINERA ===");
        System.out.println("1. Dar de alta un cliente");
        System.out.println("2. Listar clientes");
        System.out.println("3. Buscar clientes");
        System.out.println("4. Procesar un pago de repostaje");
        System.out.println("5. Consultar pagos");
        System.out.println("0. Salir");
        System.out.println("Opción:");
    }
}
