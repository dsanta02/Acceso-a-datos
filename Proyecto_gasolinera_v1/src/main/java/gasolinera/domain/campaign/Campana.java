package gasolinera.domain.campaign;

import gasolinera.domain.shared.ValidacionDominio;
import java.time.LocalDate;
import java.util.Objects;

public class Campana {

    private final int id;
    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    public Campana(int id, String codigo, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = ValidacionDominio.idTecnico(id);
        this.codigo = ValidacionDominio.obligatorio("El código de campaña", codigo, 20);
        this.nombre = ValidacionDominio.obligatorio("El nombre de campaña", nombre, 100);
        this.descripcion = ValidacionDominio.opcional("La descripción", descripcion, 255);
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFin = Objects.requireNonNull(fechaFin, "La fecha de fin es obligatoria.");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede preceder a la inicial.");
        }
    }

    public Campana(String codigo, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        this(0, codigo, nombre, descripcion, fechaInicio, fechaFin);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
