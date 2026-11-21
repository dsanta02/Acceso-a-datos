package gasolinera.console;

import gasolinera.model.Clientes;
import gasolinera.model.Pagos;
import gasolinera.persistence.ErrorPersistencia;
import gasolinera.repository.ClienteRepository;
import gasolinera.repository.PagoRepository;
import gasolinera.service.GestionClientes;
import gasolinera.service.GestionPagos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

public class AplicacionConsola {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private final Scanner sc;
    private final ClienteRepository clientes;
    private final PagoRepository pagos;

    public AplicacionConsola(Scanner sc, ClienteRepository clientes, PagoRepository pagos) {
        this.sc = sc;
        this.clientes = clientes;
        this.pagos = pagos;
    }

    public void ejecutar() {
        GestionClientes gestionClientes = new GestionClientes(clientes);
        GestionPagos gestionPagos = new GestionPagos(clientes, pagos);
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
                    case 1 -> darAlta(gestionClientes);
                    case 2 -> listarClientes(gestionClientes.listarCliente());
                    case 3 -> buscarClientes(gestionClientes);
                    case 4 -> procesarPago(gestionPagos, gestionClientes);
                    case 5 -> consultarPagos(gestionPagos);
                    case 0 -> continuar = false;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (ErrorPersistencia e) {
                System.out.println("No se pudo completar la operación: " + e.getMessage());
            } catch (IllegalArgumentException e) {
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

    private void darAlta(GestionClientes gestion) {
        Clientes cliente = gestion.darAlta(leerObligatorio("Introduzca su nombre:"),
                leerObligatorio("Introduzca su número de teléfono:"),
                leerObligatorio("Introduzca la matrícula:"));
        System.out.println("El cliente se ha registrado correctamente con el ID "
                + cliente.getId());
    }

    private void listarClientes(List<Clientes> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay clientes en la lista.");
            return;
        }
        lista.forEach(this::mostrarCliente);
    }

    private void buscarClientes(GestionClientes gestion) {
        String texto = leerObligatorio("Introduce un texto para buscar:");
        List<Clientes> encontrados = gestion.buscarCliente(texto);
        if (encontrados.isEmpty()) {
            System.out.println("No se ha encontrado cliente.");
            return;
        }
        encontrados.forEach(this::mostrarCliente);
    }

    private void procesarPago(GestionPagos gestion, GestionClientes gestionClientes) {
        List<Clientes> listaClientes = gestionClientes.listarCliente();
        if (listaClientes.isEmpty()) {
            System.out.println("Tiene que haber clientes dados de alta.");
            return;
        }
        for (Clientes cliente : listaClientes) {
            System.out.println("ID: " + cliente.getId() + " | Nombre: " + cliente.getNombre()
                    + " | Matrícula: " + cliente.getMatricula());
        }

        int idCliente = leerEntero("Introduce el ID del cliente:");
        while (idCliente <= 0 || clientes.buscarPorId(idCliente).isEmpty()) {
            System.out.println(idCliente <= 0 ? "El ID tiene que ser positivo."
                    : "El ID del cliente no existe.");
            idCliente = leerEntero("Introduce el ID del cliente:");
        }

        LocalDate fecha = leerFecha();
        double importe = leerDecimalPositivo("Introduce el importe:");
        double litros = leerDecimalPositivo("Introduce los litros:");
        String combustible;
        while (true) {
            try {
                combustible = gestion.normalizarCombustible(
                        leerObligatorio("Introduce el tipo de combustible:"));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        Pagos pago = gestion.procesarPago(idCliente, fecha, importe, litros, combustible);
        String nombreCliente = clientes.buscarPorId(idCliente).orElseThrow().getNombre();
        System.out.println("Pago registrado correctamente.");
        System.out.println("Identificador: " + pago.getIdentificador());
        System.out.println("Cliente: " + nombreCliente);
        System.out.println("Importe: " + pago.getImporte() + " €");
    }

    private void consultarPagos(GestionPagos gestion) {
        List<Pagos> lista = gestion.consultarPagos();
        if (lista.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        for (Pagos pago : lista) {
            String nombreCliente = clientes.buscarPorId(pago.getIdentificadorCliente())
                    .map(Clientes::getNombre).orElse("Cliente no encontrado");
            System.out.println("Identificador: " + pago.getIdentificador());
            System.out.println("Cliente: " + nombreCliente);
            System.out.println("Fecha: " + pago.getFecha());
            System.out.println("Importe: " + pago.getImporte());
            System.out.println("Litros: " + pago.getLitros());
            System.out.println("Combustible: " + pago.getCombustible());
            System.out.println("--------------------------");
        }
    }

    private String leerObligatorio(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El valor no puede estar vacío.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
    }

    private double leerDecimalPositivo(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            try {
                double valor = Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
                if (Double.isFinite(valor) && valor > 0) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
                continue;
            }
            System.out.println("El valor debe ser positivo.");
        }
    }

    private LocalDate leerFecha() {
        while (true) {
            System.out.println("Introduce la fecha (dd/MM/yyyy), o deja vacío para hoy:");
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha no válida. Usa el formato dd/MM/yyyy.");
            }
        }
    }

    private void mostrarCliente(Clientes cliente) {
        System.out.println("Identificador: " + cliente.getId());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Teléfono: " + cliente.getTel());
        System.out.println("Matrícula: " + cliente.getMatricula());
        System.out.println("----------------------");
    }
}
