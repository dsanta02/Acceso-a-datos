


import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Jsons implements ProcesosLeerEscribir {

    Path archivoClientes;
    Path archivoPagos;
    Path clienteJson;
    Path pagosJson;

    public Jsons() {
        this.archivoClientes = Paths.get("src/clientes.csv");
        this.archivoPagos = Paths.get("src/pagos.csv");
        this.clienteJson = Paths.get("src/clientesJson.json");
        this.pagosJson = Paths.get("src/PagosJson.json");
    }

    @Override
    public boolean guardarClientes(List<Clientes> clientes) {
        try {
            String json = clientes.stream() //cada cliente en texto
                    .map(c -> "{\"id\":" + c.getId()
                            + ",\"nombre\":\"" + c.getNombre()
                            + "\",\"tel\":\"" + c.getTel()
                            + "\",\"matricula\":\"" + c.getMatricula()
                            + "\"}")
                    .reduce("[",//junta los textos en 1 solo
                            (resultado, cliente) -> resultado + cliente + ",");

            if (!clientes.isEmpty()) {
                json = json.substring(0, json.length() - 1); //fuera ultima coma
            }

            json += "]"; //cerramos el json con el ] al final

            Files.writeString(clienteJson, json);
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar los clientes: "
                    + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean guardarPago(List<Pagos> pagos) {
        try {
            String json = pagos.stream()
                    .map(p -> "{\"identificador\":" + p.getIdentificador()
                            + ",\"identificadorCliente\":" + p.getIdentificadorCliente()
                            + ",\"fecha\":\"" + p.getFecha()
                            + "\",\"importe\":" + p.getImporte()
                            + ",\"litros\":" + p.getLitros()
                            + ",\"combustible\":\"" + p.getCombustible()
                            + "\"}")
                    .reduce("[",
                            (resultado, pago) -> resultado + pago + ",");

            if (!pagos.isEmpty()) {
                json = json.substring(0, json.length() - 1);
            }

            json += "]";

            Files.writeString(pagosJson, json);
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar los pagos: "
                    + e.getMessage());
            return false;
        }
    }


    @Override
    public List<Clientes> leerClientes() {

        List<Clientes> clientes = new ArrayList<>();

        try {
            String json = Files.readString(clienteJson);

            // comprobamos si el archivo esdta vacio
            if (json.isBlank() || json.equals("[]")) {
                return clientes;
            }

            // Separar los objetos de clientes del JSON
            String[] objetos = json.substring(1, json.length() - 1)
                    .split("\\},\\{");

            // Recorrer cada objeto de cliente
            for (String objeto : objetos) {

                // Quitar las [ y las comilla
                objeto = objeto.replace("{", "")
                        .replace("}", "")
                        .replace("\"", "");

                // Separar los campos del cliente
                String[] datos = objeto.split(",");

                // Obtener los datos y convertir el ID a número
                int id = Integer.parseInt(datos[0].split(":")[1]);
                String nombre = datos[1].split(":")[1];
                String tel = datos[2].split(":")[1];
                String matricula = datos[3].split(":")[1];

                // Crear el objeto cliente con sus datos
                Clientes cliente = new Clientes(id, nombre, tel, matricula);

                // Añadir el cliente a la lista
                clientes.add(cliente);
            }

        } catch (IOException e) {
            // Mostrar un mensaje si ocurre un error al leer el archivo
            System.out.println("Error al leer los clientes: " + e.getMessage());
        }

        // Devolver la lista de clientes
        return clientes;
    }



    @Override
    public List<Pagos> leerPagos() {
        List<Pagos> pagos = new ArrayList<>();

        try {
            String json = Files.readString(pagosJson);

            if (json.isBlank() || json.equals("[]")) {
                return pagos;
            }

            String[] objetos = json.substring(1, json.length() - 1)
                    .split("\\},\\{");

            for (String objeto : objetos) {
                objeto = objeto.replace("{", "")
                        .replace("}", "")
                        .replace("\"", "");

                String[] datos = objeto.split(",");

                int identificador = Integer.parseInt(datos[0].split(":")[1]);
                int identificadorCliente = Integer.parseInt(datos[1].split(":")[1]);
                LocalDate fecha = LocalDate.parse(datos[2].split(":", 2)[1]);
                double importe = Double.parseDouble(datos[3].split(":")[1]);
                double litros = Double.parseDouble(datos[4].split(":")[1]);
                String combustible = datos[5].split(":")[1];

                Pagos pago = new Pagos(
                        identificador,
                        identificadorCliente,
                        fecha,
                        importe,
                        litros,
                        combustible
                );

                pagos.add(pago);
            }

        } catch (IOException e) {
            System.out.println("Error al leer los pagos: " + e.getMessage());
        }

        return pagos;
    }


    @Override
    public boolean crearArchivo() {
        try {
            if (!Files.exists(clienteJson)) {
                Files.writeString(clienteJson, "[]");
            }

            if (!Files.exists(pagosJson)) {
                Files.writeString(pagosJson, "[]");
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al crear los archivos: "
                    + e.getMessage());
            return false;
        }
    }
}


