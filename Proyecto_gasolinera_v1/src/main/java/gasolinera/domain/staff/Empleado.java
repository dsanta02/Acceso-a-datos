package gasolinera.domain.staff;

import gasolinera.domain.shared.ValidacionDominio;
import gasolinera.domain.station.Estacion;
import java.time.LocalDate;
import java.util.Objects;

public class Empleado {

    private final int id;
    private final String numeroEmpleado;
    private final String documento;
    private final String nombre;
    private final String telefono;
    private final String email;
    private final LocalDate fechaIncorporacion;
    private final Estacion estacion;
    private final Empleado supervisor;

    public Empleado(int id, String numeroEmpleado, String documento, String nombre,
                    String telefono, String email, LocalDate fechaIncorporacion,
                    Estacion estacion, Empleado supervisor) {
        this.id = ValidacionDominio.idTecnico(id);
        this.numeroEmpleado = ValidacionDominio.obligatorio("El número de empleado",
                numeroEmpleado, 20);
        this.documento = ValidacionDominio.obligatorio("El documento", documento, 20);
        this.nombre = ValidacionDominio.obligatorio("El nombre del empleado", nombre, 100);
        this.telefono = ValidacionDominio.opcional("El teléfono", telefono, 20);
        this.email = ValidacionDominio.opcional("El email", email, 254);
        if (this.telefono == null && this.email == null) {
            throw new IllegalArgumentException("El empleado debe tener teléfono o email.");
        }
        this.fechaIncorporacion = Objects.requireNonNull(fechaIncorporacion,
                "La fecha de incorporación es obligatoria.");
        this.estacion = Objects.requireNonNull(estacion, "La estación es obligatoria.");
        validarSupervisor(supervisor);
        this.supervisor = supervisor;
    }

    public Empleado(String numeroEmpleado, String documento, String nombre,
                    String telefono, String email, LocalDate fechaIncorporacion,
                    Estacion estacion, Empleado supervisor) {
        this(0, numeroEmpleado, documento, nombre, telefono, email, fechaIncorporacion,
                estacion, supervisor);
    }

    private void validarSupervisor(Empleado candidato) {
        for (Empleado actual = candidato; actual != null; actual = actual.supervisor) {
            if (actual == this || (id > 0 && actual.id == id)) {
                throw new IllegalArgumentException("Un empleado no puede supervisarse a sí mismo "
                        + "ni depender de alguien bajo su supervisión.");
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }
}
