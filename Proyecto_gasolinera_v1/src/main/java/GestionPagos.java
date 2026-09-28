import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestionPagos {
    Scanner sc;
    GestionClientes c;
    public GestionPagos(Scanner sc, GestionClientes c) {
        this.sc = sc;
        this.c = c;
    }

    public void procesarPago(List<Pagos> pagos, List<Clientes> clientes) {

        if (clientes.isEmpty()) {
            System.out.println("Tiene que haber clientes dados de alta.");
            return;
        }

        c.listarCliente(clientes);

        int id_cli;

        do {
            System.out.println("Introduce el ID del cliente:");
            id_cli = sc.nextInt();

            if (id_cli <= 0) {
                System.out.println("El ID tiene que ser positivo.");
            }

        } while (id_cli <= 0);

        boolean b = false;
        String nombreCliente = "";

        for (int i = 0; i < clientes.size(); i++) {

            if (id_cli == clientes.get(i).getId()) {
                b = true;
                nombreCliente = clientes.get(i).getNombre();
                break;
            }
        }

        if (b == false) {
            System.out.println("El ID del cliente no existe.");
            return;
        }

        sc.nextLine();

        System.out.println("Introduce la fecha (dd/MM/yyyy):");
        String fechaTexto = sc.nextLine();

        LocalDate fecha;

        if (fechaTexto.isEmpty()) {
            fecha = LocalDate.now();
        } else {
            fecha = LocalDate.parse(
                    fechaTexto,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
        }

        double importe;

        do {
            System.out.println("Introduce el importe:");
            importe = sc.nextDouble();

            if (importe <= 0) {
                System.out.println("El importe debe ser positivo.");
            }

        } while (importe <= 0);

        double litros;

        do {
            System.out.println("Introduce los litros:");
            litros = sc.nextDouble();

            if (litros <= 0) {
                System.out.println("Los litros deben ser positivos.");
            }

        } while (litros <= 0);

        sc.nextLine();

        String combustible;

        do {
            System.out.println("Introduce el tipo de combustible:");
            combustible = sc.nextLine().trim().toLowerCase();

            if (!combustible.equals("gasolina") &&
                    !combustible.equals("diesel")) {

                System.out.println("Combustible no válido.");
            }

        } while (!combustible.equals("gasolina") &&
                !combustible.equals("diesel"));


        int id = 1;

        for (int i = 0; i < pagos.size(); i++) {

            if (id <= pagos.get(i).getIdentificador()) {
                id = pagos.get(i).getIdentificador() + 1;
            }
        }


        Pagos p = new Pagos(
                id,
                id_cli,
                fecha,
                importe,
                litros,
                combustible
        );

        pagos.add(p);

        boolean guardado = c.gestionFicheros.guardarPago(pagos);

        if (guardado) {

            System.out.println("Pago registrado correctamente.");
            System.out.println("Identificador: " + id);
            System.out.println("Cliente: " + nombreCliente);
            System.out.println("Importe: " + importe + " €");

        } else {

            pagos.remove(p);
            System.out.println("No se ha podido guardar el pago.");

        }
    }

    public void consultarPagos(List<Pagos> pagos, List<Clientes> clientes) {

        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }

        pagos.sort(new Comparator<Pagos>() {
            @Override
            public int compare(Pagos p1, Pagos p2) {

                int resultado = p2.getFecha().compareTo(p1.getFecha());

                if (resultado == 0) {
                    return Integer.compare(
                            p2.getIdentificador(),
                            p1.getIdentificador()
                    );
                }

                return resultado;
            }
        });

        for (int i = 0; i < pagos.size(); i++) {

            Pagos p = pagos.get(i);

            String nombreCliente = "";

            for (int j = 0; j < clientes.size(); j++) {

                if (p.getIdentificadorCliente() == clientes.get(j).getId()) {
                    nombreCliente = clientes.get(j).getNombre();
                    break;
                }
            }

            System.out.println("Identificador: " + p.getIdentificador());
            System.out.println("Cliente: " + nombreCliente);
            System.out.println("Fecha: " + p.getFecha());
            System.out.println("Importe: " + p.getImporte());
            System.out.println("Litros: " + p.getLitros());
            System.out.println("Combustible: " + p.getCombustible());
            System.out.println("--------------------------");
        }
    }

}
