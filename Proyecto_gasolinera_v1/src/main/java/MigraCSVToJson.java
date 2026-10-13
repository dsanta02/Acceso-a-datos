
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MigraCSVToJson {

    public boolean migrar(String rutaClientes, String rutaPagos) {

        Path csvClientes = Paths.get(rutaClientes);
        Path csvPagos = Paths.get(rutaPagos);

        Path jsonClientes = Paths.get("src/clientesJson.json");
        Path jsonPagos = Paths.get("src/PagosJson.json");

        try {
            // Comprobar que existen los CSV
            if (!Files.exists(csvClientes) || !Files.exists(csvPagos)) {
                System.out.println("Error: falta alguno de los archivos CSV.");
                return false;
            }

            // Comprobar que los destinos están libres
            if (Files.exists(jsonClientes) || Files.exists(jsonPagos)) {
                System.out.println("Error: ya existe algún archivo JSON de destino.");
                return false;
            }

            List<Clientes> clientes = new ArrayList<>();
            List<Pagos> pagos = new ArrayList<>();

            // Leer todas las líneas del archivo CSV de clientes
            List<String> lineasClientes = Files.readAllLines(csvClientes);

// Recorrer las líneas de clientes
            for (int i = 0; i < lineasClientes.size(); i++) {

                // Separar los datos usando el punto y coma
                String[] datos = lineasClientes.get(i).split(";", -1);

                // Comprobar que cada cliente tenga 4 campos
                if (datos.length != 4) {
                    throw new IllegalArgumentException(
                            "Clientes CSV, línea " + (i + 1) + ": formato incorrecto.");
                }

                try {
                    // Convertir el ID a número
                    int id = Integer.parseInt(datos[0]);

                    // Obtener los datos del cliente
                    String nombre = datos[1];
                    String tel = datos[2];
                    String matricula = datos[3];

                    // Crear el cliente y añadirlo a la lista
                    clientes.add(new Clientes(id, nombre, tel, matricula));

                } catch (NumberFormatException e) {
                    // Informar si el ID no es un número válido
                    throw new IllegalArgumentException(
                            "Clientes CSV, línea " + (i + 1) + ": ID incorrecto.");
                }
            }


                // Leer todas las líneas del archivo CSV de pagos
            List<String> lineasPagos = Files.readAllLines(csvPagos);

                // Preparar el formato de fecha del CSV: día/mes/año
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // Recorrer las líneas de pagos
            for (int i = 0; i < lineasPagos.size(); i++) {

                // Separar los datos usando el punto y coma
                String[] datos = lineasPagos.get(i).split(";", -1);

                // Comprobar que cada pago tenga 6 campos
                if (datos.length != 6) {
                    throw new IllegalArgumentException(
                            "Pagos CSV, línea " + (i + 1) + ": formato incorrecto.");
                }

                try {
                    // Convertir los datos numéricos
                    int id = Integer.parseInt(datos[0]);
                    int idCliente = Integer.parseInt(datos[1]);

                    // Convertir la fecha al formato LocalDate
                    LocalDate fecha = LocalDate.parse(datos[2], formato);

                    // Obtener el importe y los litros
                    double importe = Double.parseDouble(datos[3]);
                    double litros = Double.parseDouble(datos[4]);

                    // Obtener el tipo de combustible
                    String combustible = datos[5];

                    // Crear el pago y añadirlo a la lista
                    pagos.add(new Pagos(
                            id, idCliente, fecha, importe, litros, combustible));

                } catch (NumberFormatException e) {
                    // Informar si algún número no es válido
                    throw new IllegalArgumentException(
                            "Pagos CSV, línea " + (i + 1) + ": número incorrecto.");

                } catch (java.time.format.DateTimeParseException e) {
                    // Informar si la fecha no tiene el formato correcto
                    throw new IllegalArgumentException(
                            "Pagos CSV, línea " + (i + 1) + ": fecha incorrecta.");
                }
            }

            // Guardar los datos en JSON
            Jsons jsons = new Jsons();
                //comprobamos si se han podido guardar los clientes
            if (!jsons.guardarClientes(clientes)) {
                throw new IOException("No se pudieron guardar los clientes.");
            }

            if (!jsons.guardarPago(pagos)) {
                throw new IOException("No se pudieron guardar los pagos.");
            }

            System.out.println("Migración completada.");
            System.out.println("Clientes migrados: " + clientes.size());
            System.out.println("Pagos migrados: " + pagos.size());
            System.out.println("Resultado: " + jsonClientes);
            System.out.println("Resultado: " + jsonPagos);

            return true;

        } catch (Exception e) {
        System.out.println("Error en la migración.");
        return false;
    }
    }
}

