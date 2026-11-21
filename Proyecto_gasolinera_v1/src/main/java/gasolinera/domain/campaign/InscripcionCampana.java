package gasolinera.domain.campaign;

import gasolinera.domain.customer.Cliente;
import gasolinera.domain.shared.ValidacionDominio;
import java.time.LocalDate;
import java.util.Objects;

public class InscripcionCampana {

    private final int id;
    private final Cliente cliente;
    private final Campana campana;
    private final LocalDate fechaInscripcion;

    public InscripcionCampana(int id, Cliente cliente, Campana campana,
                              LocalDate fechaInscripcion) {
        this.id = ValidacionDominio.idTecnico(id);
        this.cliente = Objects.requireNonNull(cliente, "El cliente es obligatorio.");
        this.campana = Objects.requireNonNull(campana, "La campaña es obligatoria.");
        this.fechaInscripcion = Objects.requireNonNull(fechaInscripcion,
                "La fecha de inscripción es obligatoria.");
    }

    public InscripcionCampana(Cliente cliente, Campana campana, LocalDate fechaInscripcion) {
        this(0, cliente, campana, fechaInscripcion);
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Campana getCampana() {
        return campana;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }
}
