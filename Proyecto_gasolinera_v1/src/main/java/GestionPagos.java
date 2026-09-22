import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

        if (id_cli <= 0) {
            System.out.println("Tiene que ser un número positivo.");
            return;
        }

        boolean encontrado = false;

        for (int i = 0; i < clientes.size(); i++) {

            if (id_cli == clientes.get(i).getId()) {
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("El ID del cliente no existe.");
            return;
        }


    }

    public void consultarPagos() {




    }

}
