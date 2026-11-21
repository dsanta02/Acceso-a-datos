import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class GestionClientes {

    private final Scanner sc;
    private final ClienteRepository clientes;

    public GestionClientes(Scanner sc, ClienteRepository clientes) {
        this.sc = sc;
        this.clientes = clientes;
    }

    public void darAlta() {
        String nombre = leerTextoObligatorio("Introduzca su nombre:");
        String tel = leerTextoObligatorio("Introduzca su número de teléfono:");
        String matricula = leerTextoObligatorio("Introduzca la matrícula:")
                .toUpperCase(Locale.ROOT);

        if (clientes.existeMatricula(matricula)) {
            System.out.println("La matrícula ya está registrada.");
            return;
        }

        int id = clientes.buscarClientes().stream()
                .mapToInt(Clientes::getId)
                .max()
                .orElse(0) + 1;
        clientes.guardar(new Clientes(id, nombre, tel, matricula));
        System.out.println("El cliente se ha registrado correctamente con el ID " + id);
    }

    public void listarCliente() {
        List<Clientes> lista = ordenar(clientes.buscarClientes());
        if (lista.isEmpty()) {
            System.out.println("No hay clientes en la lista.");
            return;
        }
        for (Clientes cliente : lista) {
            mostrar(cliente);
        }
    }

    public void buscarCliente() {
        System.out.println("Introduce un texto para buscar:");
        String texto = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        while (texto.isEmpty()) {
            System.out.println("El texto no puede estar vacío.");
            System.out.println("Introduce un texto para buscar:");
            texto = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        }

        List<Clientes> lista = ordenar(clientes.buscarClientes());
        boolean encontrado = false;
        for (Clientes cliente : lista) {
            if (cliente.getNombre().toLowerCase(Locale.ROOT).contains(texto)
                    || cliente.getMatricula().toLowerCase(Locale.ROOT).contains(texto)
                    || cliente.getTel().contains(texto)) {
                mostrar(cliente);
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("No se ha encontrado cliente.");
        }
    }

    private String leerTextoObligatorio(String mensaje) {
        System.out.println(mensaje);
        String texto = sc.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.println("El valor no puede estar vacío.");
            System.out.println(mensaje);
            texto = sc.nextLine().trim();
        }
        return texto;
    }

    private List<Clientes> ordenar(List<Clientes> lista) {
        List<Clientes> ordenada = new ArrayList<>(lista);
        ordenada.sort(Comparator.comparing(Clientes::getNombre,
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Clientes::getId));
        return ordenada;
    }

    private void mostrar(Clientes cliente) {
        System.out.println("Identificador: " + cliente.getId());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Teléfono: " + cliente.getTel());
        System.out.println("Matrícula: " + cliente.getMatricula());
        System.out.println("----------------------");
    }
}
