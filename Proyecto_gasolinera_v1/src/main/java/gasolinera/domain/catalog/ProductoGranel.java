package gasolinera.domain.catalog;

import java.math.BigDecimal;

public final class ProductoGranel extends Producto {

    private final int precisionPesada;

    public ProductoGranel(int id, String referencia, String descripcion,
                          BigDecimal precioVenta, EstadoProducto estado,
                          int precisionPesada) {
        super(id, referencia, descripcion, precioVenta, estado);
        if (precisionPesada < 0 || precisionPesada > 3) {
            throw new IllegalArgumentException("La precisión de pesada debe estar entre 0 y 3.");
        }
        this.precisionPesada = precisionPesada;
    }

    public ProductoGranel(String referencia, String descripcion, BigDecimal precioVenta,
                          int precisionPesada) {
        this(0, referencia, descripcion, precioVenta, EstadoProducto.ACTIVO, precisionPesada);
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        BigDecimal normalizada = validarCantidadDecimal(cantidad);
        if (normalizada.stripTrailingZeros().scale() > precisionPesada) {
            throw new IllegalArgumentException("La cantidad supera la precisión de pesada del producto.");
        }
    }

    @Override
    public String getModalidad() {
        return "GRANEL";
    }

    @Override
    public String getFormatoEnvase() {
        return null;
    }

    @Override
    public Integer getPrecisionPesada() {
        return precisionPesada;
    }
}
