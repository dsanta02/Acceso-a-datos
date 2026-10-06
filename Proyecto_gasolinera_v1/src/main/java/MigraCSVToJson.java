import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MigraCSVToJson {

    public static void migar(Path archivosClientes, Path archivosPagos ,
                        Path ClienteJson, Path PagosJson ) throws IOException {

         archivosClientes = Path.of("src/clientes.csv");
         archivosPagos = Path.of("src/pagos.csv");

         ClienteJson = Path.of("src/clientesJson.json");
         PagosJson = Path.of("src/PagosJson.json");

        if(!Files.exists(archivosClientes)) {
            throw new IOException("NO EXISTE EL ARCHIVO");
        }

        if(!Files.exists(archivosPagos)) {
            throw new IOException("NO EXISTE EL ARCHIVO");
        }

        if(!Files.exists(ClienteJson)) {
            throw new IOException("NO EXISTE EL ARCHIVO");
        }

        if(!Files.exists(PagosJson)) {
            throw new IOException("NO EXISTE EL ARCHIVO");
        }

        List<String>


    }

    public static void migrarClientes(Path archivosClientes, Path ClienteJson) throws IOException {

        List<String> lista = Files.readAllLines(archivosClientes);




    }


}
