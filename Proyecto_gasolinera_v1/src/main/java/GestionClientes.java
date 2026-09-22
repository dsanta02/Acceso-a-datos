import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class GestionClientes {

    Scanner sc;
    Ficheros gestionFicheros;

    public GestionClientes(Scanner sc, Ficheros gestionFicheros) {
        this.sc = sc;
        this.gestionFicheros = gestionFicheros;
    }

    public void darAlta(List<Clientes> clientes) {

        String nombre;
        String tel;
        String matricula;

        do {
            System.out.println("Introduzca su nombre:");
            nombre = sc.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            }

        } while (nombre.isEmpty());


        do {
            System.out.println("Introduzca su número de teléfono:");
            tel = sc.nextLine().trim();

            if (tel.isEmpty()) {
                System.out.println("El teléfono no puede estar vacío.");
            }

        } while (tel.isEmpty());


        do {
            System.out.println("Introduzca la matrícula:");
            matricula = sc.nextLine().trim().toUpperCase();

            if (matricula.isEmpty()) {
                System.out.println("La matrícula no puede estar vacía.");
            }

        } while (matricula.isEmpty());


        boolean opcion = false;

        for (int i = 0; i < clientes.size(); i++) {

            if (matricula.equalsIgnoreCase(clientes.get(i).getMatricula())) {
                opcion = true;
                break;
            }
        }


        if (opcion == true) {
            System.out.println("La matrícula ya está registrada.");
            return;
        }

        int id = 1;

        for (int i = 0; i < clientes.size(); i ++) {

            if(id <= clientes.get(i).getId()) {
                id = clientes.get(i).getId() + 1;

            }

        }

        Clientes c = new Clientes(id, nombre,tel,matricula);

        clientes.add(c);

        gestionFicheros.guardarClientes(clientes);

        System.out.println("el cliente se ha regisrado correctamente con el id " + id );

    }

    public void listarCliente(List<Clientes> cliente) {

        if(cliente.isEmpty()) {

            System.out.println("no hay clientes en la lista");

        }

        cliente.sort(new Comparator<Clientes>() {
            @Override
            public int compare(Clientes o1, Clientes o2) {
                int resultado = o1.getNombre().compareToIgnoreCase(o2.getNombre());

                if(resultado == 0) {

                    return Integer.compare(o1.getId(), o2.getId());

                }
                return resultado;
            }
        });

        for(int i = 0; i < cliente.size(); i ++) {

            System.out.println("id: " + cliente.get(i).getId());
            System.out.println("nombre: " + cliente.get(i).getNombre());
            System.out.println("telefono: " + cliente.get(i).getTel());
            System.out.println("matricula: " + cliente.get(i).getMatricula());

        }

    }

    public void buscarCliente(List<Clientes> cliente) {

        System.out.println("introduce un texto para buscar: ");
        String txt = sc.nextLine();

        while(txt.isEmpty()) {
            System.out.println("el texto no puede estar vacio");
            System.out.println("introduce un texto para buscar: ");
            txt = sc.nextLine().trim();
        }

        cliente.sort(new Comparator<Clientes>() {
            @Override
            public int compare(Clientes o1, Clientes o2) {
                int resultado = o1.getNombre().compareToIgnoreCase(o2.getNombre());

                if (resultado == 0) {
                    return Integer.compare(o1.getId(), o2.getId());
                }

                return resultado;
            }
        });

        boolean encontrado = false;

        for(int i = 0; i < cliente.size(); i ++) {

            if(cliente.get(i).getNombre().toLowerCase().contains(txt) ||
                cliente.get(i).getMatricula().toLowerCase().contains(txt) ||
                cliente.get(i).getTel().contains(txt)){

                encontrado = true;

                System.out.println("Identificador: " + cliente.get(i).getId());
                System.out.println("Nombre: " + cliente.get(i).getNombre());
                System.out.println("Teléfono: " + cliente.get(i).getTel());
                System.out.println("Matrícula: " + cliente.get(i).getMatricula());
                System.out.println("----------------------");

            }

        }

        if(encontrado == false) {

            System.out.println("no se ha encontrado cliente.");

        }

    }

    public boolean existe() {


        return false;
    }

}
