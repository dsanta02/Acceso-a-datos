package gasolinera.domain.catalog;

import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.util.Objects;

public class AcuerdoSuministro {

    private final int id;
    private final Proveedor proveedor;
    private final Producto producto;
    private final String referenciaProveedor;
    private final BigDecimal precioCompra;
    private final int plazoEntrega;

    public AcuerdoSuministro(int id, Proveedor proveedor, Producto producto,
                             String referenciaProveedor, BigDecimal precioCompra,
                             int plazoEntrega) {
        this.id = ValidacionDominio.idTecnico(id);
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor es obligatorio.");
        this.producto = Objects.requireNonNull(producto, "El producto es obligatorio.");
        this.referenciaProveedor = ValidacionDominio.obligatorio(
                "La referencia del proveedor", referenciaProveedor, 40);
        this.precioCompra = ValidacionDominio.dinero("El precio de compra", precioCompra, false);
        if (plazoEntrega < 0) {
            throw new IllegalArgumentException("El plazo de entrega no puede ser negativo.");
        }
        this.plazoEntrega = plazoEntrega;
    }

    public AcuerdoSuministro(Proveedor proveedor, Producto producto,
                             String referenciaProveedor, BigDecimal precioCompra,
                             int plazoEntrega) {
        this(0, proveedor, producto, referenciaProveedor, precioCompra, plazoEntrega);
    }

    public int getId() {
        return id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public int getPlazoEntrega() {
        return plazoEntrega;
    }
}
