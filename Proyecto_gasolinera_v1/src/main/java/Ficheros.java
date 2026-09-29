import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ficheros implements ProcesosLeerEscribir {

    private Path archivosClientes;
    private Path archivosPagos;

    private DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Ficheros() {

        archivosClientes = Paths.get("src/clientes.csv");
        archivosPagos = Paths.get("src/pagos.csv");
    }

    @Override
    public boolean guardarClientes(List<Clientes> clientes) {

        ArrayList<String> datos = new ArrayList<>();

        try {

            for (int i = 0; i < clientes.size(); i++) {

                Clientes c = clientes.get(i);

                String linea = c.getId() + ";" +
                        escapar(c.getNombre()) + ";" +
                        escapar(c.getTel()) + ";" +
                        escapar(c.getMatricula());

                datos.add(linea);
            }

            Files.write(
                    archivosClientes,
                    datos,
                    StandardCharsets.UTF_8
            );

            return true;

        } catch (IOException e) {

            System.out.println("Error al guardar los clientes: "
                    + e.getMessage());

            return false;
        }
    }

    @Override
    public boolean guardarPago(List<Pagos> pagos) {

        ArrayList<String> datos = new ArrayList<>();

        try {

            for (int i = 0; i < pagos.size(); i++) {

                Pagos p = pagos.get(i);

                String linea = p.getIdentificador() + ";" +
                        p.getIdentificadorCliente() + ";" +
                        p.getFecha().format(formatoFecha) + ";" +
                        String.format("%.2f", p.getImporte()) + ";" +
                        String.format("%.2f", p.getLitros()) + ";" +
                        escapar(p.getCombustible());

                datos.add(linea);
            }

            Files.write(
                    archivosPagos,
                    datos,
                    StandardCharsets.UTF_8
            );

            return true;

        } catch (IOException e) {

            System.out.println("Error al guardar los pagos: "
                    + e.getMessage());

            return false;
        }
    }

    @Override
    public List<Clientes> leerClientes() {

        List<Clientes> lista = new ArrayList<>();

        try {

            List<String> lineas = Files.readAllLines(
                    archivosClientes,
                    StandardCharsets.UTF_8
            );

            for (int i = 0; i < lineas.size(); i++) {

                String linea = lineas.get(i);

                String[] datos = separar(linea); //usamos array para ir metido datos 1 por 1

            if (datos.length != 4) { //aqui controlamos el numero de datos
                System.out.println(
                        "Error: el registro de clientes de la línea "
                                + (i + 1) + " no tiene 4 campos."
                );

                return null;
            }

            Clientes cliente = new Clientes(
                    Integer.parseInt(datos[0]),
                    datos[1],
                    datos[2],
                    datos[3]
            );

            lista.add(cliente);
        }

        } catch (IOException e) {

            System.out.println(
                    "Error al leer los clientes: " + e.getMessage()
            );

            return null;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: existe un registro de cliente que no se puede interpretar."
            );

            return null;

        } catch (Exception e) {

            System.out.println(
                    "Error: existe un registro de cliente no válido."
            );

            return null;
        }

        return lista;
    }

    @Override
    public List<Pagos> leerPagos() {

        List<Pagos> lista = new ArrayList<>();

        try {

            List<String> lineas = Files.readAllLines(
                    archivosPagos,
                    StandardCharsets.UTF_8
            );

            for (int i = 0; i < lineas.size(); i++) {

                String linea = lineas.get(i);

                String[] datos = separar(linea); //usamos array para ir metido datos 1 por 1

                if (datos.length != 6) { //aqui controlamos el numero de datos

                    System.out.println(
                            "Error: el registro de pagos de la línea "
                                    + (i + 1) + " no tiene 6 campos."
                    );

                    return null;
                }

                Pagos pago = new Pagos(
                        Integer.parseInt(datos[0]),
                        Integer.parseInt(datos[1]),
                        LocalDate.parse(datos[2], formatoFecha),
                        Double.parseDouble(datos[3].replace(",", ".")),
                        Double.parseDouble(datos[4].replace(",", ".")),
                        datos[5]
                );

                lista.add(pago);
            }

        } catch (IOException e) {

            System.out.println(
                    "Error al leer los pagos: " + e.getMessage()
            );

            return null;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: existe un registro de pago que no se puede interpretar."
            );

            return null;

        } catch (Exception e) {

            System.out.println(
                    "Error: existe un registro de pago no válido."
            );

            return null;
        }

        return lista;
    }

    @Override
    public boolean crearArchivo() {

        try {

            Path carpeta = archivosClientes.getParent();

            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }

            if (!Files.exists(archivosClientes)) {
                Files.createFile(archivosClientes);
            }

            if (!Files.exists(archivosPagos)) {
                Files.createFile(archivosPagos);
            }

            return true;

        } catch (IOException e) {

            System.out.println(
                    "Error al crear los archivos: " + e.getMessage()
            );

            return false;
        }
    }
    // Añade comillas al texto y duplica las comillas internas para guardarlo correctamente en el CSV.
    public String escapar(String texto) {

        texto = texto.replace("\"", "\"\"");

        return "\"" + texto + "\"";
    }
    // Separa los campos del CSV respetando los textos que están entre comillas.
    public String[] separar(String linea) {

        ArrayList<String> campos = new ArrayList<>();

        String campo = "";
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {

            char caracter = linea.charAt(i);

            if (caracter == '"') {

                if (dentroComillas
                        && i + 1 < linea.length()
                        && linea.charAt(i + 1) == '"') {

                    campo = campo + '"';
                    i++;

                } else {

                    dentroComillas = !dentroComillas;
                }

            } else if (caracter == ';' && !dentroComillas) {

                campos.add(campo);
                campo = "";

            } else {

                campo = campo + caracter;
            }
        }

        campos.add(campo);

        if (dentroComillas) {
            throw new IllegalArgumentException(
                    "Comillas sin cerrar en el registro."
            );
        }

        return campos.toArray(new String[0]);
    }
}


