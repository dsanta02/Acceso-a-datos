package gasolinera;

import gasolinera.console.AplicacionConsola;
import gasolinera.migration.MigraCSVToJson;
import gasolinera.model.Clientes;
import gasolinera.model.Pagos;
import gasolinera.persistence.ErrorPersistencia;
import gasolinera.persistence.csv.Ficheros;
import gasolinera.persistence.json.Jsons;
import gasolinera.repository.ClienteRepository;
import gasolinera.repository.PagoRepository;
import gasolinera.service.GestionClientes;
import gasolinera.service.GestionPagos;
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
    void csvConsoleOperationsGenerateIdsAndValidatePaymentCustomer() throws Exception {
        Ficheros csv = new Ficheros(directorio.resolve("csv/clientes.csv"),
                directorio.resolve("csv/pagos.csv"));
        comprobarFlujoConsola(csv, csv);

        assertEquals(1, csv.buscarClientes().get(0).getId());
        assertEquals(1, csv.buscarPagos().get(0).getIdentificador());
        assertThrows(IllegalArgumentException.class, () -> new GestionPagos(csv, csv)
                .procesarPago(999, LocalDate.now(), 2, 1, "diesel"));
        assertTrue(Files.readString(directorio.resolve("csv/clientes.csv"))
                .contains("\"Lopez; \"\"El Rápido\"\", S.L.\""));
    }

    @Test
    void jsonConsoleOperationsGenerateIdsAndKeepDateFormat() throws Exception {
        Jsons json = new Jsons(directorio.resolve("json/clientesJson.json"),
                directorio.resolve("json/PagosJson.json"));
        comprobarFlujoConsola(json, json);

        assertEquals(1, json.buscarClientes().get(0).getId());
        assertEquals(1, json.buscarPagos().get(0).getIdentificador());
        assertTrue(Files.readString(directorio.resolve("json/PagosJson.json"))
                .contains("\"fecha\":\"21/11/2026\""));
    }

    @Test
    void generatedIdsContinueAfterExistingIdsAndDuplicatePlatesAreRejected() {
        Ficheros csv = new Ficheros(directorio.resolve("seq/clientes.csv"),
                directorio.resolve("seq/pagos.csv"));
        verificarIdsConsecutivos(new Backend(csv, csv));
        Jsons json = new Jsons(directorio.resolve("seq-json/clientes.json"),
                directorio.resolve("seq-json/pagos.json"));
        verificarIdsConsecutivos(new Backend(json, json));
    }

    @Test
    void migrationPreservesIdsAndBacksUpExistingJson() throws Exception {
        Path csvClientes = directorio.resolve("origen/clientes.csv");
        Path csvPagos = directorio.resolve("origen/pagos.csv");
        Ficheros csv = new Ficheros(csvClientes, csvPagos);
        csv.crearArchivo();
        csv.reemplazarClientes(List.of(
                new Clientes(7, "Nombre; \"con comillas\", S.L.", "600123456", "1234ABC")
        ));
        csv.reemplazarPagos(List.of(
                new Pagos(4, 7, LocalDate.of(2026, 11, 21), 10.25, 4.5, "gasolina")
        ));

        Path jsonClientes = directorio.resolve("destino/clientesJson.json");
        Path jsonPagos = directorio.resolve("destino/PagosJson.json");
        Files.createDirectories(jsonClientes.getParent());
        Files.writeString(jsonClientes, "JSON anterior de clientes");
        Files.writeString(jsonPagos, "JSON anterior de pagos");

        Jsons json = new Jsons(jsonClientes, jsonPagos);
        assertTrue(new MigraCSVToJson(csvClientes, csvPagos, json).migrar());
        assertEquals(7, json.buscarClientes().get(0).getId());
        assertEquals(4, json.buscarPagos().get(0).getIdentificador());
        assertEquals(7, json.buscarPagos().get(0).getIdentificadorCliente());

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
        assertThrows(ErrorPersistencia.class, json::buscarPagos);
    }

    @Test
    void repositoriesRejectPaymentsForUnknownCustomers() {
        Ficheros csv = new Ficheros(directorio.resolve("fk/clientes.csv"),
                directorio.resolve("fk/pagos.csv"));
        Jsons json = new Jsons(directorio.resolve("fk-json/clientes.json"),
                directorio.resolve("fk-json/pagos.json"));
        Pagos pago = new Pagos(999, LocalDate.now(), 5, 2, "gasolina");

        assertThrows(IllegalArgumentException.class, () -> csv.guardar(pago));
        assertThrows(IllegalArgumentException.class, () -> json.guardar(pago));
    }

    private void comprobarFlujoConsola(ClienteRepository clientes, PagoRepository pagos) {
        String entradas = String.join("\n",
                "1", "Lopez; \"El Rápido\", S.L.", "600123456", "1234ABC",
                "2", "3", "1234", "4", "1", "21/11/2026", "10,25",
                "4,5", "gasolina", "5", "0", "");
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PrintStream anterior = System.out;
        try {
            System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
            new AplicacionConsola(new Scanner(entradas), clientes, pagos).ejecutar();
        } finally {
            System.setOut(anterior);
        }
        String texto = salida.toString(StandardCharsets.UTF_8);
        assertTrue(texto.contains("El cliente se ha registrado correctamente con el ID 1"));
        assertTrue(texto.contains("Identificador: 1"));
        assertTrue(texto.contains("Lopez; \"El Rápido\", S.L."));
    }

    private void verificarIdsConsecutivos(Backend backend) {
        backend.clientes().reemplazarClientes(List.of(
                new Clientes(7, "Cliente", "600000001", "1111AAA")
        ));
        GestionClientes gestionClientes = new GestionClientes(backend.clientes());
        Clientes nuevo = gestionClientes.darAlta("Nuevo", "600000002", "2222BBB");
        assertEquals(8, nuevo.getId());
        assertThrows(IllegalArgumentException.class,
                () -> gestionClientes.darAlta("Repetido", "600000003", "1111AAA"));

        GestionPagos gestionPagos = new GestionPagos(backend.clientes(), backend.pagos());
        Pagos nuevoPago = gestionPagos.procesarPago(7, LocalDate.of(2026, 11, 21),
                5, 2, "gasolina");
        assertEquals(1, nuevoPago.getIdentificador());
        assertThrows(IllegalArgumentException.class,
                () -> gestionPagos.procesarPago(999, LocalDate.now(), 5, 2, "gasolina"));
    }

    private record Backend(ClienteRepository clientes, PagoRepository pagos) {
    }
}
