package gasolinera.domain.loyalty;

import gasolinera.domain.shared.ValidacionDominio;
import java.time.LocalDate;
import java.util.Objects;

public class LoteTarjeta {

    private final int id;
    private final String codigoLote;
    private final LocalDate fechaRecepcion;
    private final int cantidad;
    private final EstadoLoteTarjeta estado;

    public LoteTarjeta(int id, String codigoLote, LocalDate fechaRecepcion,
                       int cantidad, EstadoLoteTarjeta estado) {
        this.id = ValidacionDominio.idTecnico(id);
        this.codigoLote = ValidacionDominio.obligatorio("El código de lote", codigoLote, 30);
        this.fechaRecepcion = Objects.requireNonNull(fechaRecepcion,
                "La fecha de recepción es obligatoria.");
        this.cantidad = ValidacionDominio.positivo("La cantidad de tarjetas", cantidad);
        this.estado = Objects.requireNonNull(estado, "El estado del lote es obligatorio.");
    }

    public LoteTarjeta(String codigoLote, LocalDate fechaRecepcion, int cantidad) {
        this(0, codigoLote, fechaRecepcion, cantidad, EstadoLoteTarjeta.ABIERTO);
    }

    public int getId() {
        return id;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public LocalDate getFechaRecepcion() {
        return fechaRecepcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public EstadoLoteTarjeta getEstado() {
        return estado;
    }
}
