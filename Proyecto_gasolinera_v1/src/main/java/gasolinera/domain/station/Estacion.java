package gasolinera.domain.station;

import gasolinera.domain.shared.ValidacionDominio;
import java.time.LocalDate;
import java.util.Objects;

public class Estacion {

    private final int id;
    private final String codigo;
    private final String nombre;
    private final String direccion;
    private final String telefono;
    private final LocalDate fechaApertura;

    public Estacion(int id, String codigo, String nombre, String direccion,
                    String telefono, LocalDate fechaApertura) {
        this.id = ValidacionDominio.idTecnico(id);
        this.codigo = ValidacionDominio.obligatorio("El código de estación", codigo, 20);
        this.nombre = ValidacionDominio.obligatorio("El nombre", nombre, 100);
        this.direccion = ValidacionDominio.obligatorio("La dirección", direccion, 200);
        this.telefono = ValidacionDominio.obligatorio("El teléfono", telefono, 20);
        this.fechaApertura = Objects.requireNonNull(fechaApertura, "La fecha de apertura es obligatoria.");
    }

    public Estacion(String codigo, String nombre, String direccion, String telefono,
                    LocalDate fechaApertura) {
        this(0, codigo, nombre, direccion, telefono, fechaApertura);
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

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }
}
