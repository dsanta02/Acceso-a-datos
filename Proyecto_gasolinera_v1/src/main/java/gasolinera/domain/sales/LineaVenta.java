package gasolinera.domain.sales;

import gasolinera.domain.catalog.Producto;
import gasolinera.domain.shared.ValidacionDominio;
import gasolinera.domain.staff.Empleado;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineaVenta {

    private final int id;
    private final Venta venta;
    private final Producto producto;
    private final int numeroLinea;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal importe;
    private final List<Devolucion> devoluciones = new ArrayList<>();

    LineaVenta(Venta venta, int numeroLinea, Producto producto, BigDecimal cantidad) {
        this(0, venta, producto, numeroLinea, cantidad, producto.getPrecioVenta(),
                calcularImporte(producto, cantidad, producto.getPrecioVenta()));
    }

    public LineaVenta(int id, Venta venta, Producto producto, int numeroLinea,
                      BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal importe) {
        this.id = ValidacionDominio.idTecnico(id);
        this.venta = Objects.requireNonNull(venta, "La venta es obligatoria.");
        this.producto = Objects.requireNonNull(producto, "El producto es obligatorio.");
        this.numeroLinea = ValidacionDominio.positivo("El número de línea", numeroLinea);
        this.cantidad = ValidacionDominio.decimalPositivo("La cantidad", cantidad, 10, 3);
        producto.validarCantidad(this.cantidad);
        this.precioUnitario = ValidacionDominio.dinero("El precio unitario", precioUnitario, false);
        this.importe = ValidacionDominio.dinero("El importe de línea", importe, false);
        if (calcularImporte(producto, this.cantidad, this.precioUnitario)
                .compareTo(this.importe) != 0) {
            throw new IllegalArgumentException("El importe no coincide con cantidad por precio unitario.");
        }
    }

    private static BigDecimal calcularImporte(Producto producto, BigDecimal cantidad,
                                              BigDecimal precioUnitario) {
        Objects.requireNonNull(producto, "El producto es obligatorio.");
        producto.validarCantidad(cantidad);
        return cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
    }

    public Devolucion registrarDevolucion(Empleado empleado, BigDecimal cantidad,
                                          String motivo, LocalDateTime fechaHora,
                                          boolean aptaReventa) {
        if (venta.getEstado() != EstadoVenta.PAGADA) {
            throw new IllegalStateException("Solo se devuelve una línea de una venta pagada.");
        }
        BigDecimal cantidadNueva = ValidacionDominio.decimalPositivo(
                "La cantidad devuelta", cantidad, 10, 3);
        producto.validarCantidad(cantidadNueva);
        BigDecimal cantidadAcumulada = devoluciones.stream().map(Devolucion::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(cantidadNueva);
        if (cantidadAcumulada.compareTo(this.cantidad) > 0) {
            throw new IllegalArgumentException("La cantidad devuelta supera la cantidad comprada.");
        }

        BigDecimal yaReintegrado = devoluciones.stream()
                .map(Devolucion::getImporteReintegrado)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY), BigDecimal::add);
        BigDecimal importeAcumulado = cantidadAcumulada.compareTo(this.cantidad) == 0
                ? importe
                : importe.multiply(cantidadAcumulada)
                .divide(this.cantidad, 2, RoundingMode.HALF_UP);
        BigDecimal reintegro = importeAcumulado.subtract(yaReintegrado);
        Devolucion devolucion = new Devolucion(empleado, this, cantidadNueva,
                motivo, fechaHora, reintegro, aptaReventa);
        devoluciones.add(devolucion);
        return devolucion;
    }

    public int getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public List<Devolucion> getDevoluciones() {
        return List.copyOf(devoluciones);
    }
}
