package gasolinera.demo;

import gasolinera.domain.catalog.ProductoEnvasado;
import gasolinera.domain.catalog.ProductoGranel;
import gasolinera.domain.sales.Devolucion;
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
        System.out.println("Estación creada: " + estacion.getNombre());

        Empleado empleado = new Empleado("EMP-001", "12345678A", "Ana López",
                "600000001", null, LocalDate.of(2022, 5, 1), estacion, null);
        System.out.println("Empleado creado: " + empleado.getNombre()
                + " en " + empleado.getEstacion().getNombre());

        ProductoEnvasado agua = new ProductoEnvasado("ENV-001", "Agua 1,5 L",
                new BigDecimal("1.25"), "Botella 1,5 L");
        ProductoGranel frutosSecos = new ProductoGranel("GR-001", "Frutos secos",
                new BigDecimal("12.00"), 3);

        BigDecimal cantidadAgua = new BigDecimal("2");
        BigDecimal cantidadFrutosSecos = new BigDecimal("0.375");
        System.out.println("Importe de " + cantidadAgua + " unidades de "
                + agua.getDescripcion() + ": " + agua.calcularImporte(cantidadAgua));
        System.out.println("Importe de " + cantidadFrutosSecos + " kg de "
                + frutosSecos.getDescripcion() + ": "
                + frutosSecos.calcularImporte(cantidadFrutosSecos));

        mostrarRechazo("1,5 unidades de " + agua.getDescripcion(),
                () -> agua.validarCantidad(new BigDecimal("1.5")));
        mostrarRechazo("0,1256 kg de " + frutosSecos.getDescripcion(),
                () -> frutosSecos.validarCantidad(new BigDecimal("0.1256")));

        Venta venta = new Venta("T-2026-0001", estacion, empleado, null,
                LocalDateTime.now());
        venta.agregarLinea(1, agua, new BigDecimal("2"));
        var lineaFrutosSecos = venta.agregarLinea(2, frutosSecos, cantidadFrutosSecos);
        venta.confirmar();
        System.out.println("Venta confirmada con " + venta.getLineas().size()
                + " líneas. Total: " + venta.getImporteTotal());

        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("3.00"), MedioPago.EFECTIVO));
        venta.agregarPago(new PagoVenta(venta, LocalDateTime.now(),
                new BigDecimal("4.00"), MedioPago.TARJETA));
        System.out.println("Pagos registrados: " + venta.getPagos().size()
                + ", importe cobrado: " + venta.getPagos().stream()
                .map(PagoVenta::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        venta.marcarPagada();
        System.out.println("Estado de la venta: " + venta.getEstado());

        Devolucion primeraDevolucion = lineaFrutosSecos.registrarDevolucion(empleado,
                new BigDecimal("0.125"), "Devolución parcial", LocalDateTime.now(), false);
        Devolucion segundaDevolucion = lineaFrutosSecos.registrarDevolucion(empleado,
                new BigDecimal("0.125"), "Devolución parcial", LocalDateTime.now(), false);
        System.out.println("Primera devolución: " + primeraDevolucion.getCantidad()
                + " kg, reintegro " + primeraDevolucion.getImporteReintegrado());
        System.out.println("Segunda devolución: " + segundaDevolucion.getCantidad()
                + " kg, reintegro " + segundaDevolucion.getImporteReintegrado());
    }

    private static void mostrarRechazo(String descripcion, Runnable validacion) {
        try {
            validacion.run();
            System.out.println("ERROR: se esperaba rechazo para " + descripcion);
        } catch (IllegalArgumentException e) {
            System.out.println("Rechazado: " + descripcion + " -> " + e.getMessage());
        }
    }
}
