package gasolinera.domain.sales;

import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PagoVenta {

    private final int id;
    private final Venta venta;
    private final LocalDateTime fechaHora;
    private final BigDecimal importe;
    private final MedioPago medioPago;

    public PagoVenta(int id, Venta venta, LocalDateTime fechaHora,
                     BigDecimal importe, MedioPago medioPago) {
        this.id = ValidacionDominio.idTecnico(id);
        this.venta = Objects.requireNonNull(venta, "La venta es obligatoria.");
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias.");
        this.importe = ValidacionDominio.dinero("El importe del pago", importe, false);
        this.medioPago = Objects.requireNonNull(medioPago, "El medio de pago es obligatorio.");
    }

    public PagoVenta(Venta venta, LocalDateTime fechaHora,
                     BigDecimal importe, MedioPago medioPago) {
        this(0, venta, fechaHora, importe, medioPago);
    }

    public int getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }
}
