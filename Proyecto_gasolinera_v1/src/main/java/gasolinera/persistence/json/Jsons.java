package gasolinera.persistence.json;

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
import gasolinera.model.Clientes;
import gasolinera.model.Pagos;
import gasolinera.persistence.ErrorPersistencia;
import gasolinera.repository.ClienteRepository;
import gasolinera.repository.PagoRepository;

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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Jsons implements ClienteRepository, PagoRepository {

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
        if (matricula == null || matricula.isBlank()) {
            return false;
        }
        return buscarClientes().stream()
                .anyMatch(cliente ->
                        cliente.getMatricula().equalsIgnoreCase(matricula.trim()));
    }

    @Override
    public Optional<Clientes> buscarPorId(int id) {
        return buscarClientes().stream().filter(cliente -> cliente.getId() == id).findFirst();
    }

    @Override
    public Clientes guardar(Clientes cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        cliente.validarNuevo();
        if (existeMatricula(cliente.getMatricula())) {
            throw new IllegalArgumentException("La matrícula ya está registrada.");
        }
        List<Clientes> clientes = buscarClientes();
        Clientes guardado = new Clientes(siguienteId(
                clientes.stream().mapToInt(Clientes::getId).max().orElse(0)),
                cliente.getNombre(), cliente.getTel(), cliente.getMatricula());
        clientes.add(guardado);
        reemplazarClientes(clientes);
        return guardado;
    }

    @Override
    public void reemplazarClientes(List<Clientes> clientes) {
        validarClientes(clientes);
        escribir(archivoClientes, gson.toJson(clientes), "clientes");
    }

    @Override
    public List<Pagos> buscarPagos() {
        Type tipo = new TypeToken<List<Pagos>>() {
        }.getType();
        List<Pagos> pagos = leerLista(archivoPagos, tipo, "pagos");
        validarPagos(pagos);
        validarRelaciones(pagos);
        return pagos;
    }

    @Override
    public Pagos guardar(Pagos pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        pago.validarNuevo();
        if (buscarPorId(pago.getIdentificadorCliente()).isEmpty()) {
            throw new IllegalArgumentException("El cliente del pago no existe.");
        }
        List<Pagos> pagos = buscarPagos();
        Pagos guardado = new Pagos(siguienteId(
                pagos.stream().mapToInt(Pagos::getIdentificador).max().orElse(0)),
                pago.getIdentificadorCliente(), pago.getFecha(), pago.getImporte(),
                pago.getLitros(), pago.getCombustible());
        pagos.add(guardado);
        reemplazarPagos(pagos);
        return guardado;
    }

    @Override
    public void reemplazarPagos(List<Pagos> pagos) {
        validarPagos(pagos);
        validarRelaciones(pagos);
        escribir(archivoPagos, gson.toJson(pagos), "pagos");
    }

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

    private void validarRelaciones(List<Pagos> pagos) {
        Set<Integer> ids = new HashSet<>();
        for (Clientes cliente : buscarClientes()) {
            ids.add(cliente.getId());
        }
        for (Pagos pago : pagos) {
            if (!ids.contains(pago.getIdentificadorCliente())) {
                throw new ErrorPersistencia("Un pago referencia a un cliente inexistente.");
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

    private int siguienteId(int maximo) {
        try {
            return Math.addExact(maximo, 1);
        } catch (ArithmeticException e) {
            throw new ErrorPersistencia("No quedan IDs disponibles.", e);
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
