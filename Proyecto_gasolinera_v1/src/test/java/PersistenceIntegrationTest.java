import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceIntegrationTest {

    @TempDir
    Path directorio;

    @Test
    void csvSupportsCustomerAndPaymentOperations() throws Exception {
        Ficheros csv = new Ficheros(
                directorio.resolve("csv/clientes.csv"),
                directorio.resolve("csv/pagos.csv")
        );
        comprobarOperaciones(new Backend(csv, csv));

        String contenido = Files.readString(directorio.resolve("csv/clientes.csv"));
        assertTrue(contenido.contains("\"Lopez; \"\"El Rápido\"\", S.L.\""));
    }

    @Test
    void jsonSupportsCustomerAndPaymentOperationsAndDateFormat() throws Exception {
        Jsons json = new Jsons(
                directorio.resolve("json/clientesJson.json"),
                directorio.resolve("json/PagosJson.json")
        );
        comprobarOperaciones(new Backend(json, json));

        String contenido = Files.readString(directorio.resolve("json/PagosJson.json"));
        assertTrue(contenido.contains("\"fecha\":\"21/11/2026\""));
        assertTrue(contenido.startsWith("["));
    }

    @Test
    void migrationUsesCsvParserAndBacksUpExistingJson() throws Exception {
        Path csvClientes = directorio.resolve("origen/clientes.csv");
        Path csvPagos = directorio.resolve("origen/pagos.csv");
        Ficheros csv = new Ficheros(csvClientes, csvPagos);
        csv.crearArchivo();
        csv.guardarClientes(List.of(
                new Clientes(7, "Nombre; \"con comillas\", S.L.", "600123456", "1234ABC")
        ));
        csv.guardarPago(List.of(
                new Pagos(4, 7, LocalDate.of(2026, 11, 21), 10.25, 4.5, "gasolina")
        ));

        Path jsonClientes = directorio.resolve("destino/clientesJson.json");
        Path jsonPagos = directorio.resolve("destino/PagosJson.json");
        Files.createDirectories(jsonClientes.getParent());
        Files.writeString(jsonClientes, "JSON anterior de clientes");
        Files.writeString(jsonPagos, "JSON anterior de pagos");

        Jsons json = new Jsons(jsonClientes, jsonPagos);
        MigraCSVToJson migracion = new MigraCSVToJson(csvClientes, csvPagos, json);
        assertTrue(migracion.migrar());
        assertEquals("Nombre; \"con comillas\", S.L.",
                json.buscarClientes().get(0).getNombre());
        assertEquals(1, json.buscarPagos().size());

        try (var backups = Files.list(jsonClientes.getParent())) {
            Path backup = backups.filter(path -> path.getFileName().toString()
                    .startsWith("backup-json-")).findFirst().orElseThrow();
            assertEquals("JSON anterior de clientes",
                    Files.readString(backup.resolve("clientesJson.json")));
            assertEquals("JSON anterior de pagos",
                    Files.readString(backup.resolve("PagosJson.json")));
        }
    }

    @Test
    void invalidJsonRaisesPersistenceErrorInsteadOfReturningNull() throws Exception {
        Path archivoClientes = directorio.resolve("invalid/clientes.json");
        Path archivoPagos = directorio.resolve("invalid/pagos.json");
        Files.createDirectories(archivoClientes.getParent());
        Files.writeString(archivoClientes, "[{invalid");
        Jsons json = new Jsons(archivoClientes, archivoPagos);

        assertThrows(ErrorPersistencia.class, json::buscarClientes);
        assertTrue(json.buscarPagos().isEmpty());
    }

    private void comprobarOperaciones(Backend backend) {
        Scanner entradaCliente = new Scanner(
                "Lopez; \"El Rápido\", S.L.\n600123456\n1234ABC\n"
        );
        GestionClientes gestionClientes = new GestionClientes(
                entradaCliente, backend.clientes()
        );
        gestionarSinSalida(gestionClientes::darAlta);
        assertEquals(1, backend.clientes().buscarClientes().size());

        Scanner entradaPago = new Scanner(
                "1\n21/11/2026\n10,25\n4,5\ngasolina\n"
        );
        GestionPagos gestionPagos = new GestionPagos(
                entradaPago, backend.clientes(), backend.pagos()
        );
        gestionarSinSalida(gestionPagos::procesarPago);
        assertEquals(1, backend.pagos().buscarPagos().size());
        assertEquals(LocalDate.of(2026, 11, 21),
                backend.pagos().buscarPagos().get(0).getFecha());

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream anterior = System.out;
        try {
            System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
            gestionClientes.listarCliente();
            gestionPagos.consultarPagos();
        } finally {
            System.setOut(anterior);
        }
        assertTrue(salida.toString(StandardCharsets.UTF_8).contains("Identificador: 1"));
    }

    private void gestionarSinSalida(Runnable operacion) {
        PrintStream anterior = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            operacion.run();
        } finally {
            System.setOut(anterior);
        }
    }

    private record Backend(ClienteRepository clientes, PagoRepository pagos) {
    }
}
