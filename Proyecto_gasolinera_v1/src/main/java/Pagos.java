import java.util.Date;

public class Pagos {

    private String id;
    private String id_cli;
    private Date fecha;
    private double importe;
    private double litros;
    Combustible combustible;

    public String getId() {
        return id;
    }

    public String getId_cli() {
        return id_cli;
    }

    public Date getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public double getLitros() {
        return litros;
    }

    public Combustible getCombustible() {
        return combustible;
    }
}
