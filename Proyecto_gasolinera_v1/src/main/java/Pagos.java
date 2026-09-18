import java.time.LocalDate;

public class Pagos {

    private int identificador;
    private int identificadorCliente;
    private LocalDate fecha;
    private double importe;
    private double litros;
    private String combustible;

    // Constructor
    public Pagos(int identificador, int identificadorCliente, LocalDate fecha,
                double importe, double litros, String combustible) {

        this.identificador = identificador;
        this.identificadorCliente = identificadorCliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible.trim();
    }

    public int getIdentificador() {
        return identificador;
    }

    public int getIdentificadorCliente() {
        return identificadorCliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public double getLitros() {
        return litros;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setIdentificadorCliente(int identificadorCliente) {
        this.identificadorCliente = identificadorCliente;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible.trim();
    }
}
