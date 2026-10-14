

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MigraCSVToJson {

    private final Path clienteJson = Paths.get("src/clientesJson.json");
    private final Path pagosJson = Paths.get("src/PagosJson.json");

    public boolean migrar(String rutaClientes, String rutaPagos) {

        Path archivoClientes = Paths.get(rutaClientes);
        Path archivoPagos = Paths.get(rutaPagos);


        if (!Files.exists(archivoClientes) || !Files.exists(archivoPagos)) {
            System.out.println("Error: no se encuentran los archivos CSV.");
            return false;
        }


        if (Files.exists(clienteJson) || Files.exists(pagosJson)) {
            System.out.println("Error: ya existe algún archivo JSON de destino.");
            return false;
        }

        List<Clientes> clientes = new ArrayList<>();
        List<Pagos> pagos = new ArrayList<>();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {

            List<String> lineasClientes = Files.readAllLines(archivoClientes);

            for (int i = 0; i < lineasClientes.size(); i++) {
                String[] datos = lineasClientes.get(i).split(";", -1);

                if (datos.length != 4) {
                    System.out.println("Error en clientes, línea " + (i + 1)
                            + ": deben existir 4 campos.");
                    return false;
                }

                int id = Integer.parseInt(datos[0].trim());
                String nombre = datos[1].trim();
                String tel = datos[2].trim();
                String matricula = datos[3].trim();

                clientes.add(new Clientes(id, nombre, tel, matricula));
            }


            List<String> lineasPagos = Files.readAllLines(archivoPagos);

            for (int i = 0; i < lineasPagos.size(); i++) {
                String[] datos = lineasPagos.get(i).split(";", -1);

                if (datos.length != 6) {
                    System.out.println("Error en pagos, línea " + (i + 1)
                            + ": deben existir 6 campos.");
                    return false;
                }

                int id = Integer.parseInt(datos[0].trim());
                int idCliente = Integer.parseInt(datos[1].trim());
                LocalDate fecha = LocalDate.parse(datos[2].trim(), formato);
                double importe = Double.parseDouble(datos[3].trim().replace(",", "."));
                double litros = Double.parseDouble(datos[4].trim().replace(",", "."));
                String combustible = datos[5].trim();

                pagos.add(new Pagos(
                        id, idCliente, fecha, importe, litros, combustible
                ));
            }


            Jsons jsons = new Jsons();

            jsons.guardarClientes(clientes);
            jsons.guardarPago(pagos);

            System.out.println("Clientes migrados: " + clientes.size());
            System.out.println("Pagos migrados: " + pagos.size());
            System.out.println("Destino: " + clienteJson + " y " + pagosJson);

            return true;

        } catch (IOException | NumberFormatException
                 | java.time.DateTimeException e) {

            System.out.println("Error en la migración: " + e.getMessage());
            return false;
        }
    }
}

