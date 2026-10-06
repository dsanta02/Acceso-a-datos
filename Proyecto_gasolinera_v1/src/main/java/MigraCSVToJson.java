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

        List<String> clientes = Files.readAllLines(archivosClientes);
        List<String> pagos = Files.readAllLines(archivosPagos);

        String json = migrarClientes(clientes);
        String json2 = migrarPagos(pagos);

    }

    public static String migrarClientes(List<String> clientes) throws IOException {

        String json = "[\n";

        for (int i = 0; i < clientes.size(); i ++) {

            String[] datos = clientes.get(i).split(";");
                json += "  {\n";
                json += "    \"id\":" + datos[0] + ",\n";
                json += "    \"nombre\":" + datos[1] + ",\n";
                json += "    \"telefono\":" + datos[2] + ", \n";
                json += "    \"matricula\":" + datos[3] + ", \n";
                json += "}";

            if(i < clientes.size() -1) {
                json += " , ";
            }
            json += "\n";
        }

        return json;


    }

    public static String migrarPagos(List<String> pagos){

        String json = "[\n";

        for(int i = 0; i < pagos.size(); i++) {

            String[] datos = pagos.get(i).split(";");
                json += "  {\n";
                json += "    \"id\":" + datos[0] + ",\n";
                json += "    \"id_cli\":" + datos[1] + ",\n";
                json += "    \"fecha\":" + datos[2] + ", \n";
                json += "    \"importe\":" + datos[3] + ", \n";
                json += "    \"litrod\":" + datos[5] + ",\n";
                json += "    \"combustible\":" + datos[5] + ",\n";
                json += "}";

                if(i < pagos.size() - 1) {
                    json = " , ";
                }
                json += "\n";
        }

        return json;
    }


}
