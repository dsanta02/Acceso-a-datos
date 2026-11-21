package gasolinera.domain.refueling;

import gasolinera.domain.customer.Cliente;
import gasolinera.domain.shared.Combustible;
import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class PagoRepostaje {

    private final int id;
    private final Integer idPagoAntiguo;
    private final Cliente cliente;
    private final LocalDate fecha;
    private final BigDecimal importe;
    private final BigDecimal litros;
    private final Combustible combustible;

    public PagoRepostaje(int id, Integer idPagoAntiguo, Cliente cliente, LocalDate fecha,
                         BigDecimal importe, BigDecimal litros, Combustible combustible) {
        this.id = ValidacionDominio.idTecnico(id);
        if (idPagoAntiguo != null && idPagoAntiguo <= 0) {
            throw new IllegalArgumentException("El identificador antiguo debe ser positivo.");
        }
        this.idPagoAntiguo = idPagoAntiguo;
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria.");
        this.importe = ValidacionDominio.dinero("El importe", importe, false);
        this.litros = ValidacionDominio.decimalPositivo("Los litros", litros, 30, 15);
        this.combustible = Objects.requireNonNull(combustible, "El combustible es obligatorio.");
    }

    public PagoRepostaje(Integer idPagoAntiguo, Cliente cliente, LocalDate fecha,
                         BigDecimal importe, BigDecimal litros, Combustible combustible) {
        this(0, idPagoAntiguo, cliente, fecha, importe, litros, combustible);
    }

    public int getId() {
        return id;
    }

    public Integer getIdPagoAntiguo() {
        return idPagoAntiguo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public BigDecimal getLitros() {
        return litros;
    }

    public Combustible getCombustible() {
        return combustible;
    }
}
