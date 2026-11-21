package gasolinera.service;

import gasolinera.model.Clientes;
import gasolinera.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GestionClientes {

    private final ClienteRepository clientes;

    public GestionClientes(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public Clientes darAlta(String nombre, String telefono, String matricula) {
        if (clientes.existeMatricula(matricula)) {
            throw new IllegalArgumentException("La matrícula ya está registrada.");
        }
        return clientes.guardar(new Clientes(nombre, telefono, matricula));
    }

    public List<Clientes> listarCliente() {
        return ordenar(clientes.buscarClientes());
    }

    public List<Clientes> buscarCliente(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de búsqueda no puede estar vacío.");
        }
        String consulta = texto.trim().toLowerCase(Locale.ROOT);
        return ordenar(clientes.buscarClientes()).stream()
                .filter(cliente -> cliente.getNombre().toLowerCase(Locale.ROOT).contains(consulta)
                        || cliente.getMatricula().toLowerCase(Locale.ROOT).contains(consulta)
                        || cliente.getTel().contains(consulta))
                .toList();
    }

    private List<Clientes> ordenar(List<Clientes> lista) {
        List<Clientes> ordenada = new ArrayList<>(lista);
        ordenada.sort(Comparator.comparing(Clientes::getNombre,
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Clientes::getId));
        return ordenada;
    }
}
