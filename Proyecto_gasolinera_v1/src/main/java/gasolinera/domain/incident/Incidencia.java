package gasolinera.domain.incident;

import gasolinera.domain.shared.ValidacionDominio;
import gasolinera.domain.staff.Empleado;
import gasolinera.domain.station.Estacion;
import java.time.LocalDateTime;
import java.util.Objects;

public class Incidencia {

    private final int id;
    private final String referencia;
    private final Estacion estacion;
    private final Empleado empleadoResponsable;
    private final String descripcion;
    private final LocalDateTime fechaHora;
    private final EstadoIncidencia estado;

    public Incidencia(int id, String referencia, Estacion estacion,
                      Empleado empleadoResponsable, String descripcion,
                      LocalDateTime fechaHora, EstadoIncidencia estado) {
        this.id = ValidacionDominio.idTecnico(id);
        this.referencia = ValidacionDominio.obligatorio("La referencia de incidencia",
                referencia, 30);
        this.estacion = Objects.requireNonNull(estacion, "La estación es obligatoria.");
        this.empleadoResponsable = empleadoResponsable;
        this.descripcion = ValidacionDominio.obligatorio("La descripción", descripcion, 255);
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora son obligatorias.");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio.");
    }

    public Incidencia(String referencia, Estacion estacion, Empleado empleadoResponsable,
                      String descripcion, LocalDateTime fechaHora, EstadoIncidencia estado) {
        this(0, referencia, estacion, empleadoResponsable, descripcion, fechaHora, estado);
    }

    public int getId() {
        return id;
    }

    public String getReferencia() {
        return referencia;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getEmpleadoResponsable() {
        return empleadoResponsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }
}
