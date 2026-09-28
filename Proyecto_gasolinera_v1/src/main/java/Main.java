import java.util.List;
import java.util.Scanner;
public class Main {


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int opcion = 0;
        Ficheros c = new Ficheros();

        boolean existe = c.crearArchivo();

        if(existe == false) {
            System.out.println("no se ha podido crear el archivo debido a la ruta");
            return;
        }

        List<Clientes> clientes = c.leerClientes();
        List<Pagos> pagos = c.leerPagos();
        GestionClientes g = new GestionClientes(sc, c);


        do{


            c.crearArchivo();

            System.out.println("===GESTION DE GASOLINERA===");
            System.out.println("1. Dar de alta un cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar clientes");
            System.out.println("4. Procesar un pago de repostaje");
            System.out.println("5. Consultar pagos");
            System.out.println("0. Salir");
            System.out.println("Opción:");

            opcion = sc.nextInt();

            if (opcion == 1) {
                System.out.println("OPCION 1");

                g.darAlta(clientes);

            } else if (opcion == 2) {
                System.out.println("OPCION 2");
                g.listarCliente(clientes);

            } else if(opcion == 3) {
                System.out.println("OPCION 3");
                GestionClientes cliente = new GestionClientes(sc, c);
                cliente.buscarCliente(clientes);

            } else if (opcion == 4) {
                System.out.println("OPCION 4");
                GestionPagos p = new GestionPagos(sc,g);
                p.procesarPago(pagos, clientes);

            } else if (opcion == 5) {
                System.out.println("OPCION 5");
                GestionPagos p = new GestionPagos(sc, g);
                p.consultarPagos(pagos, clientes);

            }


        }while(opcion != 0);



    }

}
