package gasolinera.domain.sales;

import gasolinera.domain.shared.ValidacionDominio;
import gasolinera.domain.staff.Empleado;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Devolucion {

    private final int id;
    private final LineaVenta linea;
    private final Empleado empleado;
    private final BigDecimal cantidad;
    private final String motivo;
    private final LocalDateTime fechaHora;
    private final BigDecimal importeReintegrado;
    private final boolean aptaReventa;

    Devolucion(Empleado empleado, LineaVenta linea, BigDecimal cantidad,
               String motivo, LocalDateTime fechaHora, BigDecimal importeReintegrado,
               boolean aptaReventa) {
        this(0, linea, empleado, cantidad, motivo, fechaHora, importeReintegrado, aptaReventa);
    }

    public Devolucion(int id, LineaVenta linea, Empleado empleado, BigDecimal cantidad,
                      String motivo, LocalDateTime fechaHora, BigDecimal importeReintegrado,
                      boolean aptaReventa) {
        this.id = ValidacionDominio.idTecnico(id);
        this.linea = Objects.requireNonNull(linea, "La línea de venta es obligatoria.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado es obligatorio.");
        if (linea.getVenta().getEstado() != EstadoVenta.PAGADA) {
            throw new IllegalArgumentException("La venta de la línea debe estar pagada.");
        }
        this.cantidad = ValidacionDominio.decimalPositivo("La cantidad devuelta", cantidad, 10, 3);
        linea.getProducto().validarCantidad(this.cantidad);
        this.motivo = ValidacionDominio.obligatorio("El motivo", motivo, 200);
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias.");
        this.importeReintegrado = ValidacionDominio.dinero(
                "El importe reintegrado", importeReintegrado, true);
        if (this.importeReintegrado.compareTo(linea.getImporte()) > 0) {
            throw new IllegalArgumentException("El reintegro supera el importe original de la línea.");
        }
        this.aptaReventa = aptaReventa;
    }

    public int getId() {
        return id;
    }

    public LineaVenta getLinea() {
        return linea;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getImporteReintegrado() {
        return importeReintegrado;
    }

    public boolean isAptaReventa() {
        return aptaReventa;
    }
}
