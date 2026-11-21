package gasolinera.domain.catalog;

import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract sealed class Producto permits ProductoEnvasado, ProductoGranel {

    private final int id;
    private final String referencia;
    private final String descripcion;
    private final BigDecimal precioVenta;
    private final EstadoProducto estado;

    protected Producto(int id, String referencia, String descripcion,
                       BigDecimal precioVenta, EstadoProducto estado) {
        this.id = ValidacionDominio.idTecnico(id);
        this.referencia = ValidacionDominio.obligatorio("La referencia del producto",
                referencia, 30);
        this.descripcion = ValidacionDominio.obligatorio("La descripción", descripcion, 150);
        this.precioVenta = ValidacionDominio.dinero("El precio de venta", precioVenta, false);
        this.estado = java.util.Objects.requireNonNull(estado, "El estado del producto es obligatorio.");
    }

    public final BigDecimal calcularImporte(BigDecimal cantidad) {
        validarCantidad(cantidad);
        return cantidad.multiply(precioVenta).setScale(2, RoundingMode.HALF_UP);
    }

    public abstract void validarCantidad(BigDecimal cantidad);

    public abstract String getModalidad();

    public abstract String getFormatoEnvase();

    public abstract Integer getPrecisionPesada();

    protected final BigDecimal validarCantidadDecimal(BigDecimal cantidad) {
        return ValidacionDominio.decimalPositivo("La cantidad", cantidad, 10, 3);
    }

    public int getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public EstadoProducto getEstado() {
        return estado;
    }
}
