import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;


public class RegistroFicheros {

    ArrayList<Clientes> Clientes;
    ArrayList<Pagos> pagos;

    Scanner teclado;

    private Path archivosClientes;
    private Path archivosPagos;

    public RegistroFicheros(Scanner teclado) {

        this.teclado = teclado;

        Clientes = new ArrayList<>();
        pagos = new ArrayList<>();

        archivosClientes = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivoClientes.csv");
        archivosPagos = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivosPagos.csv");

    }

    public void crearArchivo() {

        try {
            if(!Files.exists(archivosClientes)) {
                Files.createFile(archivosClientes);
            }

            if(!Files.exists(archivosPagos)) {
                Files.createFile(archivosPagos);
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void darAlta () {

        System.out.println("introduce un nombre: ");
        String nombre = teclado.nextLine();
        while(nombre.isEmpty()) {
            System.out.println("el nombre es obligatorio");
            System.out.println("nombre: ");
            nombre = teclado.nextLine();;
        }

        System.out.println("introduce el telefono: ");
        String telefono = teclado.nextLine();
        while(telefono.isEmpty()) {
            System.out.println("la matricula debe de ser obligatoria");
            System.out.println("matricula: ");
            telefono = teclado.nextLine();
        }

        System.out.println("introduce lña matricula: ");
        String matricula = teclado.nextLine();

        for(int i = 0; i < Clientes.size(); i ++) {

            if (Clientes.get(i).getMatricula().equals(matricula)){

                System.out.printf("no puede haber matriculas iguales");
                System.out.println("matricula: ");
                matricula = teclado.nextLine();

            }
        }

        int id = 1;

        for (int i = 0; i < Clientes.size(); i ++) {

            if(Clientes.get(i).getId() <= id) {

                id = Clientes.get(i).getId() + 1;

            }

            System.out.println("se ha asignado su i automaticamente: " + id);

        }

        Clientes cli1 = new Clientes(id,nombre,telefono,matricula);

        Clientes.add(cli1);

        guardarClientes();

        System.out.println("se ha registrado el cliente con el id " + id);


    }

    public void guardarClientes() {

        try {

            ArrayList<String> lineas = new ArrayList<>();

            for (int i = 0; i < Clientes.size(); i++) {

                Clientes cliente = Clientes.get(i);

                String linea =
                        cliente.getId() + ";"
                                + cliente.getNombre() + ";"
                                + cliente.getTel() + ";"
                                + cliente.getMatricula();

                lineas.add(linea);
            }


            Files.write(archivosClientes, lineas);

        } catch (IOException e) {

            System.out.println(
                    "Error al guardar los clientes."
            );
        }
    }

    public void listarClientes () {

        if (Clientes.isEmpty()) {

            System.out.println("no hay ningun cliente...");

            return;
        }


        Clientes.sort(new Comparator<Clientes>() {
            @Override
            public int compare(Clientes o1, Clientes o2) {

                int resultado = o1.getNombre().compareToIgnoreCase(o2.getNombre());

                if (resultado == 0) {
                    return Integer.compare(o1.getId(), o2.getId());
                }

                return resultado;
            }

            // 0 → los nombres son iguales ignorando mayúsculas/minúsculas
            // Número negativo → o1 debe ir antes que o2.
            // Número positivo → o1 debe ir después que o2.
        });

        for(int i = 0; i < Clientes.size(); i ++) {


            System.out.println("Identificador: " + Clientes.get(i).getId());
            System.out.println("Nombre: " + Clientes.get(i).getNombre());
            System.out.println("Teléfono: " + Clientes.get(i).getTel());
            System.out.println("Matrícula: " + Clientes.get(i).getMatricula());
            System.out.println("----------------------");

        }

    }

    public void buscarClientes() {

        System.out.println("Introduce el texto que quieres buscar:");
        String texto = teclado.nextLine().trim();

        while (texto.isEmpty()) {
            System.out.println("El texto no puede estar vacío.");
            System.out.println("Introduce el texto que quieres buscar:");
            texto = teclado.nextLine().trim();
        }

        texto = texto.toLowerCase();

        boolean encontrado = false;

        for (int i = 0; i < Clientes.size(); i++) {

            if (Clientes.get(i).getNombre().toLowerCase().contains(texto)
                    || Clientes.get(i).getTel().toLowerCase().contains(texto)
                    || Clientes.get(i).getMatricula().toLowerCase().contains(texto)) {

                encontrado = true;

                System.out.println("Identificador: " + Clientes.get(i).getId());
                System.out.println("Nombre: " + Clientes.get(i).getNombre());
                System.out.println("Teléfono: " + Clientes.get(i).getTel());
                System.out.println("Matrícula: " + Clientes.get(i).getMatricula());
                System.out.println("----------------------");
            }
        }

        if (!encontrado) {
            System.out.println("No se han encontrado clientes.");
        }

        Clientes.sort(new Comparator<Clientes>() {
            @Override
            public int compare(Clientes o1, Clientes o2) {

                int resultado = o1.getNombre().compareToIgnoreCase(o2.getNombre()); //primera comparacion

                if (resultado == 0) {
                    return Integer.compare(o1.getId(), o2.getId()); //segunda comparacion por id
                }

                return resultado;
            }

            // 0 → los nombres son iguales ignorando mayúsculas/minúsculas
            // Número negativo → o1 debe ir antes que o2.
            // Número positivo → o1 debe ir después que o2.
        });

    }

    public void procesarPago(Clientes c) {

        if (Clientes.isEmpty()) {

            System.out.println("para procesar un pago debe de darse de alta: ");

        } else {

            listarClientes();

            System.out.println("introduce el id del cliente: ");

            int id = teclado.nextInt();

            if(id <= 0) {
                System.out.println("el id debe de ser positivo");
            } else if (id != c.getId()) {

                System.out.println("el id no coincide");

            } else {



            }

        }




    }






}
