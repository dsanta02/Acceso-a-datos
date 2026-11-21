import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class GestionPagos {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Scanner sc;
    private final ClienteRepository clientes;
    private final PagoRepository pagos;

    public GestionPagos(Scanner sc, ClienteRepository clientes, PagoRepository pagos) {
        this.sc = sc;
        this.clientes = clientes;
        this.pagos = pagos;
    }

    public void procesarPago() {
        List<Clientes> listaClientes = clientes.buscarClientes();
        if (listaClientes.isEmpty()) {
            System.out.println("Tiene que haber clientes dados de alta.");
            return;
        }

        for (Clientes cliente : listaClientes) {
            System.out.println("ID: " + cliente.getId()
                    + " | Nombre: " + cliente.getNombre()
                    + " | Matrícula: " + cliente.getMatricula());
        }

        Clientes cliente = null;
        while (cliente == null) {
            int idCliente = leerEntero("Introduce el ID del cliente:");
            if (idCliente <= 0) {
                System.out.println("El ID tiene que ser positivo.");
                continue;
            }
            for (Clientes candidato : listaClientes) {
                if (candidato.getId() == idCliente) {
                    cliente = candidato;
                    break;
                }
            }
            if (cliente == null) {
                System.out.println("El ID del cliente no existe.");
            }
        }

        LocalDate fecha = leerFecha();
        double importe = leerDecimalPositivo("Introduce el importe:");
        double litros = leerDecimalPositivo("Introduce los litros:");
        String combustible = leerCombustible();

        int idPago = pagos.buscarPagos().stream()
                .mapToInt(Pagos::getIdentificador)
                .max()
                .orElse(0) + 1;
        Pagos pago = new Pagos(idPago, cliente.getId(), fecha, importe, litros, combustible);
        pagos.guardar(pago);

        System.out.println("Pago registrado correctamente.");
        System.out.println("Identificador: " + idPago);
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Importe: " + importe + " €");
    }

    public void consultarPagos() {
        List<Pagos> listaPagos = new ArrayList<>(pagos.buscarPagos());
        if (listaPagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }

        List<Clientes> listaClientes = clientes.buscarClientes();
        listaPagos.sort(Comparator.comparing(Pagos::getFecha).reversed()
                .thenComparing(Comparator.comparingInt(Pagos::getIdentificador).reversed()));

        for (Pagos pago : listaPagos) {
            String nombreCliente = listaClientes.stream()
                    .filter(c -> c.getId() == pago.getIdentificadorCliente())
                    .map(Clientes::getNombre)
                    .findFirst()
                    .orElse("Cliente no encontrado");
            System.out.println("Identificador: " + pago.getIdentificador());
            System.out.println("Cliente: " + nombreCliente);
            System.out.println("Fecha: " + pago.getFecha());
            System.out.println("Importe: " + pago.getImporte());
            System.out.println("Litros: " + pago.getLitros());
            System.out.println("Combustible: " + pago.getCombustible());
            System.out.println("--------------------------");
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

    private String leerCombustible() {
        while (true) {
            System.out.println("Introduce el tipo de combustible:");
            String combustible = sc.nextLine().trim().toLowerCase(Locale.ROOT);
            if (combustible.equals("gasolina") || combustible.equals("diesel")) {
                return combustible;
            }
            System.out.println("Combustible no válido.");
        }
    }
}
