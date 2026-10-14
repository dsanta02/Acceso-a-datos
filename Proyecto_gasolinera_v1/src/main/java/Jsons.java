
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Jsons implements ProcesosLeerEscribir {

    private final Path archivoClientes = Paths.get("src/clientesJson.json");
    private final Path archivoPagos = Paths.get("src/PagosJson.json");

    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public boolean crearArchivo() {
        try {
            if (!Files.exists(archivoClientes)) {
                Files.writeString(archivoClientes, "[]");
            }

            if (!Files.exists(archivoPagos)) {
                Files.writeString(archivoPagos, "[]");
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al crear los archivos JSON: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarClientes(List<Clientes> clientes) {
        String json = "[";

        for (int i = 0; i < clientes.size(); i++) {
            Clientes c = clientes.get(i);

            json += "{\"id\":" + c.getId()
                    + ",\"nombre\":\"" + c.getNombre()
                    + "\",\"tel\":\"" + c.getTel()
                    + "\",\"matricula\":\"" + c.getMatricula() + "\"}";

            if (i < clientes.size() - 1) {
                json += ",";
            }
        }

        json += "]";

        try {
            Files.writeString(archivoClientes, json);
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar clientes: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarPago(List<Pagos> pagos) {
        String json = "[";

        for (int i = 0; i < pagos.size(); i++) {
            Pagos p = pagos.get(i);

            json += "{\"identificador\":" + p.getIdentificador()
                    + ",\"identificadorCliente\":" + p.getIdentificadorCliente()
                    + ",\"fecha\":\"" + p.getFecha().format(formato)
                    + "\",\"importe\":" + p.getImporte()
                    + ",\"litros\":" + p.getLitros()
                    + ",\"combustible\":\"" + p.getCombustible() + "\"}";

            if (i < pagos.size() - 1) {
                json += ",";
            }
        }

        json += "]";

        try {
            Files.writeString(archivoPagos, json);
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar pagos: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Clientes> leerClientes() {
        List<Clientes> clientes = new ArrayList<>();

        try {
            String json = Files.readString(archivoClientes).trim();

            if (json.isEmpty() || json.equals("[]")) {
                return clientes;
            }

            json = json.substring(1, json.length() - 1);
            String[] registros = json.split("\\},\\{");

            for (String registro : registros) {
                registro = registro.replace("{", "").replace("}", "");
                String[] campos = registro.split(",");

                int id = Integer.parseInt(campos[0].split(":", 2)[1]);
                String nombre = campos[1].split(":", 2)[1].replace("\"", "");
                String tel = campos[2].split(":", 2)[1].replace("\"", "");
                String matricula = campos[3].split(":", 2)[1].replace("\"", "");

                clientes.add(new Clientes(id, nombre, tel, matricula));
            }

        } catch (IOException e) {
            System.out.println("Error al leer clientes: " + e.getMessage());
        }

        return clientes;
    }

    @Override
    public List<Pagos> leerPagos() {
        List<Pagos> pagos = new ArrayList<>();

        try {
            String json = Files.readString(archivoPagos).trim();

            if (json.isEmpty() || json.equals("[]")) {
                return pagos;
            }

            json = json.substring(1, json.length() - 1);
            String[] registros = json.split("\\},\\{");

            for (String registro : registros) {
                registro = registro.replace("{", "").replace("}", "");
                String[] campos = registro.split(",");

                int id = Integer.parseInt(campos[0].split(":", 2)[1]);
                int idCliente = Integer.parseInt(campos[1].split(":", 2)[1]);
                String fechaTexto = campos[2].split(":", 2)[1].replace("\"", "");
                double importe = Double.parseDouble(campos[3].split(":", 2)[1]);
                double litros = Double.parseDouble(campos[4].split(":", 2)[1]);
                String combustible = campos[5].split(":", 2)[1].replace("\"", "");

                LocalDate fecha = LocalDate.parse(fechaTexto, formato);

                pagos.add(new Pagos(
                        id, idCliente, fecha, importe, litros, combustible
                ));
            }

        } catch (IOException e) {
            System.out.println("Error al leer pagos: " + e.getMessage());
        }

        return pagos;
    }
}





