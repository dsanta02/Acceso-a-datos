package gasolinera.domain.staff;

import gasolinera.domain.shared.ValidacionDominio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class AsignacionFuncion {

    private final int id;
    private final Empleado empleado;
    private final Funcion funcion;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final BigDecimal limiteDevoluciones;
    private final String numeroAcreditacion;
    private final LocalDate fechaCaducidadAcreditacion;
    private final BigDecimal presupuestoCompras;
    private final Integer nivelAutorizacion;

    public AsignacionFuncion(int id, Empleado empleado, Funcion funcion,
                             LocalDate fechaInicio, LocalDate fechaFin,
                             BigDecimal limiteDevoluciones, String numeroAcreditacion,
                             LocalDate fechaCaducidadAcreditacion,
                             BigDecimal presupuestoCompras, Integer nivelAutorizacion) {
        this.id = ValidacionDominio.idTecnico(id);
        this.empleado = Objects.requireNonNull(empleado, "El empleado es obligatorio.");
        this.funcion = Objects.requireNonNull(funcion, "La función es obligatoria.");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria.");
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede preceder a la inicial.");
        }
        this.fechaFin = fechaFin;
        this.limiteDevoluciones = limiteDevoluciones == null ? null
                : ValidacionDominio.dinero("El límite de devoluciones", limiteDevoluciones, false);
        this.numeroAcreditacion = ValidacionDominio.opcional("El número de acreditación",
                numeroAcreditacion, 30);
        this.fechaCaducidadAcreditacion = fechaCaducidadAcreditacion;
        this.presupuestoCompras = presupuestoCompras == null ? null
                : ValidacionDominio.dinero("El presupuesto de compras", presupuestoCompras, false);
        if (nivelAutorizacion != null && nivelAutorizacion <= 0) {
            throw new IllegalArgumentException("El nivel de autorización debe ser positivo.");
        }
        this.nivelAutorizacion = nivelAutorizacion;
        validarDatosEspecificos();
    }

    public AsignacionFuncion(Empleado empleado, Funcion funcion, LocalDate fechaInicio,
                             LocalDate fechaFin, BigDecimal limiteDevoluciones,
                             String numeroAcreditacion, LocalDate fechaCaducidadAcreditacion,
                             BigDecimal presupuestoCompras, Integer nivelAutorizacion) {
        this(0, empleado, funcion, fechaInicio, fechaFin, limiteDevoluciones,
                numeroAcreditacion, fechaCaducidadAcreditacion, presupuestoCompras,
                nivelAutorizacion);
    }

    private void validarDatosEspecificos() {
        boolean cajero = "CAJERO".equals(funcion.getCodigo());
        boolean expendedor = "EXPENDEDOR".equals(funcion.getCodigo());
        boolean responsable = "RESPONSABLE_TIENDA".equals(funcion.getCodigo());
        boolean jefe = "JEFE_ESTACION".equals(funcion.getCodigo());
        boolean camposCajero = limiteDevoluciones != null && numeroAcreditacion == null
                && fechaCaducidadAcreditacion == null && presupuestoCompras == null
                && nivelAutorizacion == null;
        boolean camposExpendedor = limiteDevoluciones == null && numeroAcreditacion != null
                && fechaCaducidadAcreditacion != null && presupuestoCompras == null
                && nivelAutorizacion == null;
        boolean camposResponsable = limiteDevoluciones == null && numeroAcreditacion == null
                && fechaCaducidadAcreditacion == null && presupuestoCompras != null
                && nivelAutorizacion == null;
        boolean camposJefe = limiteDevoluciones == null && numeroAcreditacion == null
                && fechaCaducidadAcreditacion == null && presupuestoCompras == null
                && nivelAutorizacion != null;

        if ((cajero && !camposCajero) || (expendedor && !camposExpendedor)
                || (responsable && !camposResponsable) || (jefe && !camposJefe)
                || !(cajero || expendedor || responsable || jefe)) {
            throw new IllegalArgumentException(
                    "Los datos específicos no coinciden con la función asignada.");
        }
    }

    public int getId() {
        return id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getLimiteDevoluciones() {
        return limiteDevoluciones;
    }

    public String getNumeroAcreditacion() {
        return numeroAcreditacion;
    }

    public LocalDate getFechaCaducidadAcreditacion() {
        return fechaCaducidadAcreditacion;
    }

    public BigDecimal getPresupuestoCompras() {
        return presupuestoCompras;
    }

    public Integer getNivelAutorizacion() {
        return nivelAutorizacion;
    }
}
