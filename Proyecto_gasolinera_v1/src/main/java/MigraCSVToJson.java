import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MigraCSVToJson {

    private static final DateTimeFormatter SUFIJO_BACKUP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final Path csvClientes;
    private final Path csvPagos;
    private final Jsons destino;

    public MigraCSVToJson() {
        this(Paths.get("src/src/clientes.csv"), Paths.get("src/src/pagos.csv"),
                new Jsons());
    }

    public MigraCSVToJson(Path csvClientes, Path csvPagos, Jsons destino) {
        this.csvClientes = csvClientes;
        this.csvPagos = csvPagos;
        this.destino = destino;
    }

    public boolean migrar() {
        return migrar(csvClientes.toString(), csvPagos.toString());
    }

    public boolean migrar(String rutaClientes, String rutaPagos) {
        Path origenClientes = Paths.get(rutaClientes);
        Path origenPagos = Paths.get(rutaPagos);
        if (!Files.isRegularFile(origenClientes) || !Files.isRegularFile(origenPagos)) {
            System.out.println("Error: no se encuentran los archivos CSV.");
            return false;
        }

        try {
            Ficheros csv = new Ficheros(origenClientes, origenPagos);
            List<Clientes> clientes = csv.buscarClientes();
            List<Pagos> pagos = csv.buscarPagos();
            validarDatos(clientes, pagos);

            Path backup = crearCopiasDeSeguridad();
            try {
                destino.guardarClientes(clientes);
                destino.guardarPago(pagos);
            } catch (RuntimeException e) {
                restaurarDesdeBackup(backup);
                throw e;
            }

            System.out.println("Clientes migrados: " + clientes.size());
            System.out.println("Pagos migrados: " + pagos.size());
            System.out.println("Destino: " + destino.getArchivoClientes()
                    + " y " + destino.getArchivoPagos());
            if (backup != null) {
                System.out.println("Copia de seguridad anterior: " + backup);
            }
            return true;
        } catch (RuntimeException | IOException e) {
            System.out.println("Error en la migración: " + e.getMessage());
            return false;
        }
    }

    private void validarDatos(List<Clientes> clientes, List<Pagos> pagos) {
        Set<Integer> idsClientes = new HashSet<>();
        for (Clientes cliente : clientes) {
            if (cliente.getId() <= 0 || cliente.getNombre().isBlank()
                    || cliente.getTel().isBlank() || cliente.getMatricula().isBlank()
                    || !idsClientes.add(cliente.getId())) {
                throw new ErrorPersistencia("Los datos CSV contienen un cliente inválido o duplicado.");
            }
        }

        Set<Integer> idsPagos = new HashSet<>();
        for (Pagos pago : pagos) {
            if (pago.getIdentificador() <= 0 || pago.getIdentificadorCliente() <= 0
                    || pago.getImporte() <= 0 || pago.getLitros() <= 0
                    || !idsClientes.contains(pago.getIdentificadorCliente())
                    || !idsPagos.add(pago.getIdentificador())) {
                throw new ErrorPersistencia("Los datos CSV contienen un pago inválido o duplicado.");
            }
        }
    }

    private Path crearCopiasDeSeguridad() throws IOException {
        boolean existeClientes = Files.exists(destino.getArchivoClientes());
        boolean existePagos = Files.exists(destino.getArchivoPagos());
        if (!existeClientes && !existePagos) {
            return null;
        }

        Path directorioBase = destino.getArchivoClientes().toAbsolutePath().getParent();
        Path backup = directorioBase.resolve(
                "backup-json-" + LocalDateTime.now().format(SUFIJO_BACKUP)
        );
        Files.createDirectories(backup);
        if (existeClientes) {
            Files.copy(destino.getArchivoClientes(), backup.resolve("clientesJson.json"),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        if (existePagos) {
            Files.copy(destino.getArchivoPagos(), backup.resolve("PagosJson.json"),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return backup;
    }

    private void restaurarDesdeBackup(Path backup) throws IOException {
        if (backup == null) {
            Files.deleteIfExists(destino.getArchivoClientes());
            Files.deleteIfExists(destino.getArchivoPagos());
            return;
        }
        Path copiaClientes = backup.resolve("clientesJson.json");
        Path copiaPagos = backup.resolve("PagosJson.json");
        if (Files.exists(copiaClientes)) {
            Files.copy(copiaClientes, destino.getArchivoClientes(),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(destino.getArchivoClientes());
        }
        if (Files.exists(copiaPagos)) {
            Files.copy(copiaPagos, destino.getArchivoPagos(),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.deleteIfExists(destino.getArchivoPagos());
        }
    }
}
