import java.time.LocalDate;

public class Pagos {

    private int identificador;
    private int identificadorCliente;
    private LocalDate fecha;
    private double importe;
    private double litros;
    private String combustible;


    public Pagos(int identificador, int identificadorCliente, LocalDate fecha,
                 double importe, double litros, String combustible) {

        validarDatos(identificador, identificadorCliente, fecha, importe, litros, combustible);
        this.identificador = identificador;
        this.identificadorCliente = identificadorCliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = validarCombustible(combustible);
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
        if (identificadorCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser positivo.");
        }
        this.identificadorCliente = identificadorCliente;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        this.fecha = fecha;
    }

    public void setImporte(double importe) {
        validarPositivo(importe, "El importe");
        this.importe = importe;
    }

    public void setLitros(double litros) {
        validarPositivo(litros, "Los litros");
        this.litros = litros;
    }

    public void setCombustible(String combustible) {
        this.combustible = validarCombustible(combustible);
    }

    public void validar() {
        validarDatos(identificador, identificadorCliente, fecha,
                importe, litros, combustible);
    }

    private static void validarDatos(int identificador, int identificadorCliente,
                                     LocalDate fecha, double importe, double litros,
                                     String combustible) {
        if (identificador <= 0 || identificadorCliente <= 0) {
            throw new IllegalArgumentException("Los identificadores deben ser positivos.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede estar vacía.");
        }
        validarPositivo(importe, "El importe");
        validarPositivo(litros, "Los litros");
        validarCombustible(combustible);
    }

    private static void validarPositivo(double valor, String nombre) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(nombre + " debe ser positivo.");
        }
    }

    private static String validarCombustible(String combustible) {
        if (combustible == null || combustible.isBlank()) {
            throw new IllegalArgumentException("El combustible no puede estar vacío.");
        }
        String normalizado = combustible.trim().toLowerCase(java.util.Locale.ROOT);
        if (!normalizado.equals("gasolina") && !normalizado.equals("diesel")) {
            throw new IllegalArgumentException("El combustible debe ser gasolina o diesel.");
        }
        return normalizado;
    }
}
