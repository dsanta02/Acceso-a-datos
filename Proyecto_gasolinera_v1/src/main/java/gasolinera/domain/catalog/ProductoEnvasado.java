package gasolinera.domain.catalog;

import gasolinera.domain.shared.ValidacionDominio;

import java.math.BigDecimal;

public final class ProductoEnvasado extends Producto {

    private final String formatoEnvase;

    public ProductoEnvasado(int id, String referencia, String descripcion,
                            BigDecimal precioVenta, EstadoProducto estado,
                            String formatoEnvase) {
        super(id, referencia, descripcion, precioVenta, estado);
        this.formatoEnvase = ValidacionDominio.obligatorio("El formato del envase",
                formatoEnvase, 50);
    }

    public ProductoEnvasado(String referencia, String descripcion, BigDecimal precioVenta,
                            String formatoEnvase) {
        this(0, referencia, descripcion, precioVenta, EstadoProducto.ACTIVO, formatoEnvase);
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        BigDecimal normalizada = validarCantidadDecimal(cantidad);
        if (normalizada.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Un producto envasado se vende en unidades enteras.");
        }
    }

    @Override
    public String getModalidad() {
        return "ENVASADO";
    }

    @Override
    public String getFormatoEnvase() {
        return formatoEnvase;
    }

    @Override
    public Integer getPrecisionPesada() {
        return null;
    }
}
