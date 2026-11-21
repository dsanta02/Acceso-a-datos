import java.io.IOException;
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
import java.util.Locale;

public class Ficheros implements ProcesosLeerEscribir, ClienteRepository, PagoRepository {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Path archivosClientes;
    private final Path archivosPagos;

    public Ficheros() {
        this(Paths.get("src/src/clientes.csv"), Paths.get("src/src/pagos.csv"));
    }

    public Ficheros(Path archivosClientes, Path archivosPagos) {
        this.archivosClientes = archivosClientes;
        this.archivosPagos = archivosPagos;
    }

    @Override
    public List<Clientes> buscarClientes() {
        List<Clientes> clientes = new ArrayList<>();
        List<String> lineas = leerLineas(archivosClientes, "clientes");

        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = separar(lineas.get(i));
            if (datos.length != 4) {
                throw errorRegistro("clientes", i + 1, "deben existir 4 campos");
            }
            try {
                clientes.add(new Clientes(
                        Integer.parseInt(datos[0].trim()),
                        datos[1],
                        datos[2],
                        datos[3]
                ));
            } catch (RuntimeException e) {
                throw new ErrorPersistencia(
                        "Registro de cliente no válido en la línea " + (i + 1), e
                );
            }
        }
        return clientes;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        String normalizada = matricula.trim();
        return buscarClientes().stream()
                .anyMatch(cliente -> cliente.getMatricula().equalsIgnoreCase(normalizada));
    }

    @Override
    public void guardar(Clientes cliente) {
        List<Clientes> clientes = buscarClientes();
        clientes.add(cliente);
        guardarClientes(clientes);
    }

    @Override
    public List<Pagos> buscarPagos() {
        List<Pagos> pagos = new ArrayList<>();
        List<String> lineas = leerLineas(archivosPagos, "pagos");

        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = separar(lineas.get(i));
            if (datos.length != 6) {
                throw errorRegistro("pagos", i + 1, "deben existir 6 campos");
            }
            try {
                pagos.add(new Pagos(
                        Integer.parseInt(datos[0].trim()),
                        Integer.parseInt(datos[1].trim()),
                        LocalDate.parse(datos[2].trim(), FORMATO_FECHA),
                        Double.parseDouble(datos[3].trim().replace(",", ".")),
                        Double.parseDouble(datos[4].trim().replace(",", ".")),
                        datos[5]
                ));
            } catch (RuntimeException e) {
                throw new ErrorPersistencia(
                        "Registro de pago no válido en la línea " + (i + 1), e
                );
            }
        }
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
        List<String> lineas = new ArrayList<>();
        for (Clientes cliente : clientes) {
            if (cliente == null) {
                throw new ErrorPersistencia("No se puede guardar un cliente nulo.");
            }
            cliente.validar();
            lineas.add(cliente.getId() + ";"
                    + escapar(cliente.getNombre()) + ";"
                    + escapar(cliente.getTel()) + ";"
                    + escapar(cliente.getMatricula()));
        }
        escribir(archivosClientes, lineas, "clientes");
        return true;
    }

    @Override
    public boolean guardarPago(List<Pagos> pagos) {
        List<String> lineas = new ArrayList<>();
        for (Pagos pago : pagos) {
            if (pago == null) {
                throw new ErrorPersistencia("No se puede guardar un pago nulo.");
            }
            pago.validar();
            lineas.add(pago.getIdentificador() + ";"
                    + pago.getIdentificadorCliente() + ";"
                    + pago.getFecha().format(FORMATO_FECHA) + ";"
                    + String.format(Locale.ROOT, "%.2f", pago.getImporte()) + ";"
                    + String.format(Locale.ROOT, "%.2f", pago.getLitros()) + ";"
                    + escapar(pago.getCombustible()));
        }
        escribir(archivosPagos, lineas, "pagos");
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
            crearSiNoExiste(archivosClientes);
            crearSiNoExiste(archivosPagos);
            return true;
        } catch (IOException e) {
            throw new ErrorPersistencia("No se pudieron preparar los CSV.", e);
        }
    }

    private List<String> leerLineas(Path archivo, String tipo) {
        try {
            if (!Files.exists(archivo)) {
                return new ArrayList<>();
            }
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            lineas.removeIf(String::isBlank);
            return lineas;
        } catch (IOException e) {
            throw new ErrorPersistencia("No se pudieron leer " + tipo + " desde " + archivo, e);
        }
    }

    private void escribir(Path archivo, List<String> lineas, String tipo) {
        Path temporal = null;
        try {
            crearPadre(archivo);
            temporal = Files.createTempFile(archivo.toAbsolutePath().getParent(),
                    archivo.getFileName().toString(), ".tmp");
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ErrorPersistencia("No se pudieron guardar " + tipo + " en " + archivo, e);
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
            Files.createFile(archivo);
        }
    }

    private void crearPadre(Path archivo) throws IOException {
        Path padre = archivo.toAbsolutePath().getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
    }

    private ErrorPersistencia errorRegistro(String tipo, int linea, String detalle) {
        return new ErrorPersistencia(
                "Registro de " + tipo + " inválido en la línea " + linea + ": " + detalle
        );
    }

    public String escapar(String texto) {
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    public String[] separar(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (caracter == '"') {
                if (dentroComillas && i + 1 < linea.length()
                        && linea.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    dentroComillas = !dentroComillas;
                }
            } else if (caracter == ';' && !dentroComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }

        if (dentroComillas) {
            throw new ErrorPersistencia("Comillas sin cerrar en registro CSV.");
        }
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }
}
