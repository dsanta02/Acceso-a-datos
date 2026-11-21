package gasolinera;

import gasolinera.domain.catalog.ProductoEnvasado;
import gasolinera.domain.catalog.ProductoGranel;
import gasolinera.domain.sales.Devolucion;
import gasolinera.domain.sales.EstadoVenta;
import gasolinera.domain.sales.MedioPago;
import gasolinera.domain.sales.PagoVenta;
import gasolinera.domain.sales.Venta;
import gasolinera.domain.staff.Empleado;
import gasolinera.domain.station.Estacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DominioTest {

    @Test
    void productModalitiesValidateQuantitiesAndCalculateRoundedAmounts() {
        ProductoEnvasado envasado = new ProductoEnvasado("E-1", "Agua",
                new BigDecimal("1.25"), "Botella 1 L");
        ProductoGranel granel = new ProductoGranel("G-1", "Frutos secos",
                new BigDecimal("12.00"), 2);

        assertEquals(new BigDecimal("2.50"), envasado.calcularImporte(new BigDecimal("2")));
        assertEquals(new BigDecimal("3.00"), granel.calcularImporte(new BigDecimal("0.25")));
        assertThrows(IllegalArgumentException.class,
                () -> envasado.calcularImporte(new BigDecimal("1.5")));
        assertThrows(IllegalArgumentException.class,
                () -> granel.calcularImporte(new BigDecimal("0.001")));
    }

    @Test
    void saleRequiresLinesAndExactPaymentBeforeBeingMarkedPaid() {
        Estacion estacion = new Estacion("E-1", "Centro", "Calle 1", "910000000",
                LocalDate.of(2020, 1, 1));
        Empleado empleado = new Empleado("EMP-1", "12345678A", "Ana",
                null, "ana@example.test", LocalDate.of(2022, 1, 1), estacion, null);
        ProductoEnvasado producto = new ProductoEnvasado("P-1", "Agua",
                new BigDecimal("2.50"), "Botella");
        Venta venta = new Venta("T-1", estacion, empleado, null, LocalDateTime.now());

        assertThrows(IllegalStateException.class, venta::confirmar);
        venta.agregarLinea(1, producto, BigDecimal.ONE);
        venta.confirmar();
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("1.00"), MedioPago.EFECTIVO));
        assertThrows(IllegalStateException.class, venta::marcarPagada);
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("1.50"), MedioPago.TARJETA));
        venta.marcarPagada();

        assertEquals(EstadoVenta.PAGADA, venta.getEstado());
        assertEquals(new BigDecimal("2.50"), venta.getImporteTotal());
    }

    @Test
    void successiveReturnsUseOriginalPriceAndNeverExceedBoughtQuantityOrAmount() {
        Estacion estacion = new Estacion("E-1", "Centro", "Calle 1", "910000000",
                LocalDate.of(2020, 1, 1));
        Empleado empleado = new Empleado("EMP-1", "12345678A", "Ana",
                "600000001", null, LocalDate.of(2022, 1, 1), estacion, null);
        ProductoGranel producto = new ProductoGranel("G-1", "Frutos secos",
                new BigDecimal("12.00"), 3);
        Venta venta = new Venta("T-2", estacion, empleado, null, LocalDateTime.now());
        var linea = venta.agregarLinea(1, producto, new BigDecimal("0.375"));
        venta.confirmar();
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("4.50"), MedioPago.TARJETA));
        venta.marcarPagada();

        Devolucion primera = linea.registrarDevolucion(empleado,
                new BigDecimal("0.125"), "Devolución parcial", LocalDateTime.now(), false);
        Devolucion segunda = linea.registrarDevolucion(empleado,
                new BigDecimal("0.125"), "Devolución parcial", LocalDateTime.now(), false);
        Devolucion finalDevolucion = linea.registrarDevolucion(empleado,
                new BigDecimal("0.125"), "Resto de la línea", LocalDateTime.now(), false);

        assertEquals(new BigDecimal("1.50"), primera.getImporteReintegrado());
        assertEquals(new BigDecimal("1.50"), segunda.getImporteReintegrado());
        assertEquals(new BigDecimal("1.50"), finalDevolucion.getImporteReintegrado());
        assertThrows(IllegalArgumentException.class, () -> linea.registrarDevolucion(empleado,
                new BigDecimal("0.001"), "Exceso", LocalDateTime.now(), false));
    }
}
