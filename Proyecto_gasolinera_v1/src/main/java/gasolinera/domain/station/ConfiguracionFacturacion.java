package gasolinera.domain.station;

import gasolinera.domain.shared.ValidacionDominio;
import java.util.Objects;

public class ConfiguracionFacturacion {

    private final int id;
    private final Estacion estacion;
    private final String codigo;
    private final String serieTicket;
    private final int siguienteNumero;
    private final String pieJustificante;

    public ConfiguracionFacturacion(int id, Estacion estacion, String codigo,
                                    String serieTicket, int siguienteNumero,
                                    String pieJustificante) {
        this.id = ValidacionDominio.idTecnico(id);
        this.estacion = Objects.requireNonNull(estacion, "La estación es obligatoria.");
        this.codigo = ValidacionDominio.obligatorio("El código de configuración", codigo, 30);
        this.serieTicket = ValidacionDominio.obligatorio("La serie de ticket", serieTicket, 20);
        this.siguienteNumero = ValidacionDominio.positivo("El siguiente número", siguienteNumero);
        this.pieJustificante = ValidacionDominio.obligatorio("El pie del justificante",
                pieJustificante, 255);
    }

    public ConfiguracionFacturacion(Estacion estacion, String codigo, String serieTicket,
                                    int siguienteNumero, String pieJustificante) {
        this(0, estacion, codigo, serieTicket, siguienteNumero, pieJustificante);
    }

    public int getId() {
        return id;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSerieTicket() {
        return serieTicket;
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    public String getPieJustificante() {
        return pieJustificante;
    }
}
