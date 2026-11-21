import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public class Jsons implements ProcesosLeerEscribir, ClienteRepository, PagoRepository {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Path archivoClientes;
    private final Path archivoPagos;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    public Jsons() {
        this(Paths.get("src/src/clientesJson.json"), Paths.get("src/src/PagosJson.json"));
    }

    public Jsons(Path archivoClientes, Path archivoPagos) {
        this.archivoClientes = archivoClientes;
        this.archivoPagos = archivoPagos;
    }

    @Override
    public List<Clientes> buscarClientes() {
        Type tipo = new TypeToken<List<Clientes>>() {
        }.getType();
        List<Clientes> clientes = leerLista(archivoClientes, tipo, "clientes");
        validarClientes(clientes);
        return clientes;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        return buscarClientes().stream()
                .anyMatch(cliente ->
                        cliente.getMatricula().equalsIgnoreCase(matricula.trim()));
    }

    @Override
    public void guardar(Clientes cliente) {
        List<Clientes> clientes = buscarClientes();
        clientes.add(cliente);
        guardarClientes(clientes);
    }

    @Override
    public List<Pagos> buscarPagos() {
        Type tipo = new TypeToken<List<Pagos>>() {
        }.getType();
        List<Pagos> pagos = leerLista(archivoPagos, tipo, "pagos");
        validarPagos(pagos);
        return pagos;
    }

    @Override
    public void guardar(Pagos pago) {
        List<Pagos> pagos = buscarPagos();
        pagos.add(pago);
        guardarPago(pagos);
    }

    @Override
    public boolean guardarClientes(List<Clientes> clientes) {
        validarClientes(clientes);
        escribir(archivoClientes, gson.toJson(clientes), "clientes");
        return true;
    }

    @Override
    public boolean guardarPago(List<Pagos> pagos) {
        validarPagos(pagos);
        escribir(archivoPagos, gson.toJson(pagos), "pagos");
        return true;
    }

    @Override
    public List<Clientes> leerClientes() {
        return buscarClientes();
    }

    @Override
    public List<Pagos> leerPagos() {
        return buscarPagos();
    }

    @Override
    public boolean crearArchivo() {
        try {
            crearSiNoExiste(archivoClientes);
            crearSiNoExiste(archivoPagos);
            return true;
        } catch (IOException e) {
            throw new ErrorPersistencia("No se pudieron preparar los JSON.", e);
        }
    }

    public Path getArchivoClientes() {
        return archivoClientes;
    }

    public Path getArchivoPagos() {
        return archivoPagos;
    }

    private <T> List<T> leerLista(Path archivo, Type tipo, String tipoDatos) {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(archivo, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            List<T> resultado = gson.fromJson(json, tipo);
            if (resultado == null) {
                throw new ErrorPersistencia("El archivo JSON de " + tipoDatos
                        + " contiene null en lugar de una lista.");
            }
            return resultado;
        } catch (IOException e) {
            throw new ErrorPersistencia(
                    "No se pudieron leer los datos JSON de " + tipoDatos
                            + " desde " + archivo, e
            );
        } catch (RuntimeException e) {
            throw new ErrorPersistencia(
                    "El archivo JSON de " + tipoDatos + " no tiene datos válidos: " + archivo,
                    e
            );
        }
    }

    private void validarClientes(List<Clientes> clientes) {
        for (Clientes cliente : clientes) {
            if (cliente == null) {
                throw new ErrorPersistencia("El JSON contiene un cliente incompleto.");
            }
            try {
                cliente.validar();
            } catch (IllegalArgumentException e) {
                throw new ErrorPersistencia("El JSON contiene un cliente inválido.", e);
            }
        }
    }

    private void validarPagos(List<Pagos> pagos) {
        for (Pagos pago : pagos) {
            if (pago == null) {
                throw new ErrorPersistencia("El JSON contiene un pago incompleto.");
            }
            try {
                pago.validar();
            } catch (IllegalArgumentException e) {
                throw new ErrorPersistencia("El JSON contiene un pago inválido.", e);
            }
        }
    }

    private void escribir(Path archivo, String contenido, String tipoDatos) {
        Path temporal = null;
        try {
            crearPadre(archivo);
            temporal = Files.createTempFile(archivo.toAbsolutePath().getParent(),
                    archivo.getFileName().toString(), ".tmp");
            Files.writeString(temporal, contenido, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ErrorPersistencia(
                    "No se pudieron guardar los datos JSON de " + tipoDatos
                            + " en " + archivo, e
            );
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ignored) {
                    // El temporal no afecta al archivo de datos original.
                }
            }
        }
    }

    private void crearSiNoExiste(Path archivo) throws IOException {
        crearPadre(archivo);
        if (!Files.exists(archivo)) {
            Files.writeString(archivo, "[]", StandardCharsets.UTF_8);
        }
    }

    private void crearPadre(Path archivo) throws IOException {
        Path padre = archivo.toAbsolutePath().getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
    }

    private static class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate fecha, Type type,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(fecha.format(FORMATO_FECHA));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type type,
                                     JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString(), FORMATO_FECHA);
        }
    }
}
