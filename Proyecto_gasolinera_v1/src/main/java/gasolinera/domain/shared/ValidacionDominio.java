package gasolinera.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class ValidacionDominio {

    private ValidacionDominio() {
    }

    public static int idTecnico(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador técnico no puede ser negativo.");
        }
        return id;
    }

    public static String obligatorio(String nombre, String valor, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nombre + " no puede estar vacío.");
        }
        String normalizado = valor.trim();
        if (normalizado.length() > longitudMaxima) {
            throw new IllegalArgumentException(nombre + " supera " + longitudMaxima + " caracteres.");
        }
        return normalizado;
    }

    public static String opcional(String nombre, String valor, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = valor.trim();
        if (normalizado.length() > longitudMaxima) {
            throw new IllegalArgumentException(nombre + " supera " + longitudMaxima + " caracteres.");
        }
        return normalizado;
    }

    public static BigDecimal dinero(String nombre, BigDecimal valor, boolean permitirCero) {
        Objects.requireNonNull(valor, nombre + " no puede ser nulo.");
        BigDecimal normalizado;
        try {
            normalizado = valor.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(nombre + " admite como máximo dos decimales.", e);
        }
        if (permitirCero ? normalizado.signum() < 0 : normalizado.signum() <= 0) {
            throw new IllegalArgumentException(nombre + (permitirCero
                    ? " no puede ser negativo." : " debe ser mayor que cero."));
        }
        if (normalizado.precision() > 10) {
            throw new IllegalArgumentException(nombre + " excede el rango DECIMAL(10,2).");
        }
        return normalizado;
    }

    public static BigDecimal decimalPositivo(String nombre, BigDecimal valor, int precision,
                                             int escala) {
        Objects.requireNonNull(valor, nombre + " no puede ser nulo.");
        BigDecimal normalizado;
        try {
            normalizado = valor.setScale(escala, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(nombre + " excede la precisión decimal permitida.", e);
        }
        if (normalizado.signum() <= 0 || normalizado.precision() > precision) {
            throw new IllegalArgumentException(nombre + " no es válido para DECIMAL("
                    + precision + "," + escala + ").");
        }
        return normalizado;
    }

    public static int positivo(String nombre, int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(nombre + " debe ser mayor que cero.");
        }
        return valor;
    }
}
