import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ficheros implements ProcesosLeerEscribir {

    Path archivosClientes;
    Path archivosPagos;

    public Ficheros() {


        archivosClientes = Paths.get("C:\\Users\\ACER\\Desktop\\prueba\\clientes.csv");
        archivosPagos = Paths.get("C:\\Users\\ACER\\Desktop\\prueba\\pagos.csv");

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
                        escapar(p.getCombustible());

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

                String[] datos = separar(linea);

                Clientes cliente = new Clientes(
                        Integer.parseInt(datos[0]),
                        datos[1],
                        datos[2],
                        datos[3]
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

                String[] datos = separar(linea);

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

    public String[] separar(String linea) {

        ArrayList<String> campos = new ArrayList<>();

        String campo = "";
        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {

            char caracter = linea.charAt(i);

            if (caracter == '"') {

                if (dentroComillas && i + 1 < linea.length()
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

        return campos.toArray(new String[0]);
    }

}



