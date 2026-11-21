package gasolinera.demo;

import gasolinera.domain.catalog.ProductoEnvasado;
import gasolinera.domain.catalog.ProductoGranel;
import gasolinera.domain.sales.MedioPago;
import gasolinera.domain.sales.PagoVenta;
import gasolinera.domain.sales.Venta;
import gasolinera.domain.staff.Empleado;
import gasolinera.domain.station.Estacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DominioDemo {

    private DominioDemo() {
    }

    public static void main(String[] args) {
        Estacion estacion = new Estacion("EST-001", "Estación Centro",
                "Calle Mayor 10", "910000000", LocalDate.of(2020, 1, 1));
        Empleado empleado = new Empleado("EMP-001", "12345678A", "Ana López",
                "600000001", null, LocalDate.of(2022, 5, 1), estacion, null);
        ProductoEnvasado agua = new ProductoEnvasado("ENV-001", "Agua 1,5 L",
                new BigDecimal("1.25"), "Botella 1,5 L");
        ProductoGranel frutosSecos = new ProductoGranel("GR-001", "Frutos secos",
                new BigDecimal("12.00"), 3);

        System.out.println("Importe de dos unidades: "
                + agua.calcularImporte(new BigDecimal("2")));
        System.out.println("Importe de 0,375 kg: "
                + frutosSecos.calcularImporte(new BigDecimal("0.375")));

        Venta venta = new Venta("T-2026-0001", estacion, empleado, null,
                LocalDateTime.now());
        venta.agregarLinea(1, agua, new BigDecimal("2"));
        venta.agregarLinea(2, frutosSecos, new BigDecimal("0.375"));
        venta.confirmar();
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("2.50"), MedioPago.EFECTIVO));
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("4.50"), MedioPago.TARJETA));
        venta.marcarPagada();
        System.out.println("Total del ticket: " + venta.getImporteTotal()
                + " (" + venta.getEstado() + ")");
    }
}
