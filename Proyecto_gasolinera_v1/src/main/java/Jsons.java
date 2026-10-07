import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

            String json = clientes.stream()
                    .map(c -> "{\"id\":" + c.getId()
                            + ",\"nombre\":\"" + c.getNombre()
                            + "\",\"tel\":\"" + c.getTel()
                            + "\",\"matricula\":\"" + c.getMatricula()
                            + "\"}")
                    .reduce("[",
                            (resultado, cliente) -> resultado + cliente + ",");

            json = json.substring(0, json.length() - 1) + "]";

            Files.writeString(clienteJson, json);

            return true;

        } catch (IOException e) {

            System.out.println("Error al guardar los clientes: "
                    + e.getMessage());

            return false;
        }
    }

    @Override
    public boolean guardarPago(List<Pagos> pago) {
        return false;
    }

    @Override
    public List<Clientes> leerClientes() {



        return List.of();
    }

    @Override
    public List<Pagos> leerPagos() {
        return List.of();
    }

    @Override
    public boolean crearArchivo() {
        return false;
    }
}
