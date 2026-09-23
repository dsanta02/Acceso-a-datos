import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ficheros implements ProcesosLeerEscribir {

    ArrayList<Clientes> listaCliente;
    ArrayList<Pagos> listaPagos;

    Path archivosClientes;
    Path archivosPagos;

    public Ficheros() {

        listaCliente = new ArrayList<>();
        listaPagos = new ArrayList<>();

        archivosClientes = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivoClientes.csv");
        archivosPagos = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivosPagos.csv");

    }

    @Override
    public boolean guardarClientes(List<Clientes> clientes) {

        ArrayList<String> datos = new ArrayList<>();

        try {

            for (int i = 0; i < clientes.size(); i++) {

                Clientes c = clientes.get(i);

                String linea = c.getId() + ";" +
                        c.getNombre() + ";" +
                        c.getTel() + ";" +
                        c.getMatricula();

                datos.add(linea);
            }

            Files.write(archivosClientes, datos);

            return true;

        } catch (IOException e) {

            System.out.println(e.getMessage());

            return false;
        }
    }

    @Override
    public boolean guardarPago(List<Pagos> pago) {

        ArrayList<String> datos = new ArrayList<>();

        try {
            for (int i = 0; i < pago.size(); i++) {

                Pagos p = pago.get(i);

                String linea = p.getIdentificador() + ";" +
                        p.getIdentificadorCliente() + ";" +
                        p.getFecha() + ";" +
                        p.getImporte() + ";" +
                        p.getLitros() + ";" +
                        p.getCombustible();

                datos.add(linea);
            }

            Files.write(archivosPagos, datos);
            return true;

        } catch (IOException e) {

            System.out.println(e.getMessage());

            return false;

        }


    }


    @Override
    public List<Clientes> leerClientes() {

        List<Clientes> lista = new ArrayList<>();

        try {

            List<String> lineas = Files.readAllLines(archivosClientes);

            for (int i = 0; i < lineas.size(); i++) {

                String linea = lineas.get(i);

                Clientes cliente = new Clientes(
                        Integer.parseInt(linea.split(";")[0]),
                        linea.split(";")[1],
                        linea.split(";")[2],
                        linea.split(";")[3]
                );

                lista.add(cliente);
            }

        } catch (IOException e) {

            System.out.println("Error al leer: " + e.getMessage());
        }

        return lista;
    }


    @Override
    public ArrayList<Pagos> leerPagos() {

        ArrayList<Pagos> lista = new ArrayList<>();

        try {

            List<String> lineas = Files.readAllLines(archivosPagos);

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (int i = 0; i < lineas.size(); i++) {

                String linea = lineas.get(i);

                String[] datos = linea.split(";");

                Pagos pago = new Pagos(
                        Integer.parseInt(datos[0]),
                        Integer.parseInt(datos[1]),
                        LocalDate.parse(datos[2], formato),
                        Double.parseDouble(datos[3].replace(",", ".")),
                        Double.parseDouble(datos[4].replace(",", ".")),
                        datos[5]
                );

                lista.add(pago);
            }

        } catch (IOException e) {

            System.out.println("Error al leer los pagos: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public boolean crearArchivo() {

        try {

            if (!Files.exists(archivosClientes.getParent())) {
                Files.createDirectories(archivosClientes.getParent());
            }

            if (!Files.exists(archivosClientes)) {
                Files.createFile(archivosClientes);
            }

            if (!Files.exists(archivosPagos)) {
                Files.createFile(archivosPagos);
            }

            return true;

        } catch (IOException e) {

            System.out.println("Error al crear los archivos.");
            return false;
        }
    }

    public String escapar(String texto) {

        texto = texto.replace("\"", "\"\"");

        return "\"" + texto + "\"";
    }

}



