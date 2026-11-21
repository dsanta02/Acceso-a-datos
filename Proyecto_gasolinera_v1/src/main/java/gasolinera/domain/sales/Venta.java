package gasolinera.domain.sales;

import gasolinera.domain.catalog.EstadoProducto;
import gasolinera.domain.catalog.Producto;
import gasolinera.domain.customer.Cliente;
import gasolinera.domain.shared.ValidacionDominio;
import gasolinera.domain.staff.Empleado;
import gasolinera.domain.station.Estacion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Venta {

    private final int id;
    private final String referenciaTicket;
    private final Estacion estacion;
    private final Empleado empleado;
    private final Cliente cliente;
    private final LocalDateTime fechaHora;
    private EstadoVenta estado;
    private BigDecimal importeTotal;
    private final List<LineaVenta> lineas = new ArrayList<>();
    private final List<PagoVenta> pagos = new ArrayList<>();

    public Venta(int id, String referenciaTicket, Estacion estacion, Empleado empleado,
                 Cliente cliente, LocalDateTime fechaHora) {
        this.id = ValidacionDominio.idTecnico(id);
        this.referenciaTicket = ValidacionDominio.obligatorio("La referencia del ticket",
                referenciaTicket, 40);
        this.estacion = Objects.requireNonNull(estacion, "La estación es obligatoria.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado es obligatorio.");
        this.cliente = cliente;
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias.");
        this.estado = EstadoVenta.PREPARACION;
        this.importeTotal = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
    }

    public Venta(String referenciaTicket, Estacion estacion, Empleado empleado,
                 Cliente cliente, LocalDateTime fechaHora) {
        this(0, referenciaTicket, estacion, empleado, cliente, fechaHora);
    }

    public LineaVenta agregarLinea(int numeroLinea, Producto producto, BigDecimal cantidad) {
        if (estado != EstadoVenta.PREPARACION) {
            throw new IllegalStateException("Solo se pueden añadir líneas durante la preparación.");
        }
        if (lineas.stream().anyMatch(linea -> linea.getNumeroLinea() == numeroLinea)) {
            throw new IllegalArgumentException("El número de línea ya existe en este ticket.");
        }
        if (producto == null || producto.getEstado() != EstadoProducto.ACTIVO) {
            throw new IllegalArgumentException("La línea requiere un producto activo.");
        }
        LineaVenta linea = new LineaVenta(this, numeroLinea, producto, cantidad);
        lineas.add(linea);
        return linea;
    }

    public void confirmar() {
        if (estado != EstadoVenta.PREPARACION) {
            throw new IllegalStateException("Solo se puede confirmar una venta en preparación.");
        }
        if (lineas.isEmpty()) {
            throw new IllegalStateException("Una venta confirmada debe contener al menos una línea.");
        }
        importeTotal = lineas.stream().map(LineaVenta::getImporte)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), BigDecimal::add);
        estado = EstadoVenta.CONFIRMADA;
    }

    public void agregarPago(PagoVenta pago) {
        if (estado != EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("Los pagos solo se añaden a una venta confirmada.");
        }
        if (pago == null || pago.getVenta() != this) {
            throw new IllegalArgumentException("El pago debe corresponder a esta venta.");
        }
        BigDecimal suma = pagos.stream().map(PagoVenta::getImporte)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), BigDecimal::add)
                .add(pago.getImporte());
        if (suma.compareTo(importeTotal) > 0) {
            throw new IllegalArgumentException("Los pagos no pueden superar el total de la venta.");
        }
        pagos.add(pago);
    }

    public void marcarPagada() {
        if (estado != EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede marcar pagada una venta confirmada.");
        }
        BigDecimal importePagado = pagos.stream().map(PagoVenta::getImporte)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), BigDecimal::add);
        if (importePagado.compareTo(importeTotal) != 0) {
            throw new IllegalStateException("La suma de los pagos debe coincidir con el total.");
        }
        estado = EstadoVenta.PAGADA;
    }

    public int getId() {
        return id;
    }

    public String getReferenciaTicket() {
        return referenciaTicket;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public List<LineaVenta> getLineas() {
        return List.copyOf(lineas);
    }

    public List<PagoVenta> getPagos() {
        return List.copyOf(pagos);
    }
}
