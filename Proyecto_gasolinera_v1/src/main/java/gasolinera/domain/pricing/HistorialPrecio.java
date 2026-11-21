package gasolinera.domain.pricing;

import gasolinera.domain.catalog.Producto;
import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class HistorialPrecio {

    private final int id;
    private final Producto producto;
    private final BigDecimal precioAnterior;
    private final BigDecimal precioNuevo;
    private final LocalDateTime fechaHora;

    public HistorialPrecio(int id, Producto producto, BigDecimal precioAnterior,
                           BigDecimal precioNuevo, LocalDateTime fechaHora) {
        this.id = ValidacionDominio.idTecnico(id);
        this.producto = Objects.requireNonNull(producto, "El producto es obligatorio.");
        this.precioAnterior = ValidacionDominio.dinero("El precio anterior",
                precioAnterior, false);
        this.precioNuevo = ValidacionDominio.dinero("El precio nuevo", precioNuevo, false);
        if (this.precioAnterior.compareTo(this.precioNuevo) == 0) {
            throw new IllegalArgumentException("El historial solo registra cambios reales de precio.");
        }
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias.");
    }

    public HistorialPrecio(Producto producto, BigDecimal precioAnterior,
                           BigDecimal precioNuevo, LocalDateTime fechaHora) {
        this(0, producto, precioAnterior, precioNuevo, fechaHora);
    }

    public int getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public BigDecimal getPrecioNuevo() {
        return precioNuevo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
