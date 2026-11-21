package gasolinera.domain.station;

import gasolinera.domain.shared.Combustible;
import gasolinera.domain.shared.ValidacionDominio;
import java.util.Objects;

public class Surtidor {

    private final int id;
    private final Estacion estacion;
    private final String codigoEquipo;
    private final int numeroVisible;
    private final Combustible combustible;
    private final EstadoSurtidor estado;

    public Surtidor(int id, Estacion estacion, String codigoEquipo, int numeroVisible,
                    Combustible combustible, EstadoSurtidor estado) {
        this.id = ValidacionDominio.idTecnico(id);
        this.estacion = Objects.requireNonNull(estacion, "La estación es obligatoria.");
        this.codigoEquipo = ValidacionDominio.obligatorio("El código del equipo", codigoEquipo, 30);
        this.numeroVisible = ValidacionDominio.positivo("El número visible", numeroVisible);
        this.combustible = Objects.requireNonNull(combustible, "El combustible es obligatorio.");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio.");
    }

    public Surtidor(Estacion estacion, String codigoEquipo, int numeroVisible,
                    Combustible combustible, EstadoSurtidor estado) {
        this(0, estacion, codigoEquipo, numeroVisible, combustible, estado);
    }

    public int getId() {
        return id;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public Combustible getCombustible() {
        return combustible;
    }

    public EstadoSurtidor getEstado() {
        return estado;
    }
}
