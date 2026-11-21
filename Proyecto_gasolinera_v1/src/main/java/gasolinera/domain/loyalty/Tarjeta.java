package gasolinera.domain.loyalty;

import gasolinera.domain.customer.Cliente;
import gasolinera.domain.shared.ValidacionDominio;

import java.time.LocalDate;
import java.util.Objects;

public class Tarjeta {

    private final int id;
    private final String numeroTarjeta;
    private final LoteTarjeta lote;
    private final Cliente cliente;
    private final LocalDate fechaEmision;
    private final LocalDate fechaAsignacion;

    public Tarjeta(int id, String numeroTarjeta, LoteTarjeta lote, Cliente cliente,
                   LocalDate fechaEmision, LocalDate fechaAsignacion) {
        this.id = ValidacionDominio.idTecnico(id);
        this.numeroTarjeta = ValidacionDominio.obligatorio("El número de tarjeta",
                numeroTarjeta, 30);
        this.lote = Objects.requireNonNull(lote, "El lote de origen es obligatorio.");
        this.fechaEmision = Objects.requireNonNull(fechaEmision, "La fecha de emisión es obligatoria.");
        if ((cliente == null) != (fechaAsignacion == null)) {
            throw new IllegalArgumentException(
                    "La fecha de asignación debe existir exactamente cuando hay titular.");
        }
        if (fechaAsignacion != null && fechaAsignacion.isBefore(fechaEmision)) {
            throw new IllegalArgumentException(
                    "La fecha de asignación no puede ser anterior a la fecha de emisión.");
        }
        this.cliente = cliente;
        this.fechaAsignacion = fechaAsignacion;
    }

    public Tarjeta(String numeroTarjeta, LoteTarjeta lote, LocalDate fechaEmision) {
        this(0, numeroTarjeta, lote, null, fechaEmision, null);
    }

    public int getId() {
        return id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public LoteTarjeta getLote() {
        return lote;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }
}
