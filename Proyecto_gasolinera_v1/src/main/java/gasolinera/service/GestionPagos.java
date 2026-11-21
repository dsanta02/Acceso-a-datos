package gasolinera.service;

import gasolinera.model.Pagos;
import gasolinera.repository.ClienteRepository;
import gasolinera.repository.PagoRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GestionPagos {

    private final ClienteRepository clientes;
    private final PagoRepository pagos;

    public GestionPagos(ClienteRepository clientes, PagoRepository pagos) {
        this.clientes = clientes;
        this.pagos = pagos;
    }

    public Pagos procesarPago(int idCliente, LocalDate fecha, double importe,
                              double litros, String combustible) {
        if (clientes.buscarPorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException("El ID del cliente no existe.");
        }
        return pagos.guardar(new Pagos(idCliente, fecha, importe, litros, combustible));
    }

    public List<Pagos> consultarPagos() {
        List<Pagos> lista = new ArrayList<>(pagos.buscarPagos());
        lista.sort(Comparator.comparing(Pagos::getFecha).reversed()
                .thenComparing(Comparator.comparingInt(Pagos::getIdentificador).reversed()));
        return lista;
    }

    public String normalizarCombustible(String combustible) {
        if (combustible == null) {
            throw new IllegalArgumentException("El combustible no puede estar vacío.");
        }
        String normalizado = combustible.trim().toLowerCase(Locale.ROOT);
        if (!normalizado.equals("gasolina") && !normalizado.equals("diesel")) {
            throw new IllegalArgumentException("Combustible no válido.");
        }
        return normalizado;
    }
}
