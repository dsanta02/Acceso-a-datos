import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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

        System.out.println("Introduce el ID del cliente:");
        int id_cli = sc.nextInt();

        do {
            System.out.println("Introduce el ID del cliente:");
            id_cli = sc.nextInt();

            if (id_cli <= 0) {
                System.out.println("El ID tiene que ser positivo.");
            }

        } while (id_cli <= 0);


        boolean b = false;

        for(int i = 0; i < clientes.size(); i ++) {

            if(id_cli == clientes.get(i).getId()) {
                b = true;
                break;
            }

        }

        if(b == false) {
            System.out.println("el id del clinte no existe");
            return;
        }

        int id = 1;

        for(int i = 0; i < pagos.size(); i ++) {

            if(id <= pagos.get(i).getIdentificador()) {
                id = pagos.get(i).getIdentificador() + 1;
            }

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
