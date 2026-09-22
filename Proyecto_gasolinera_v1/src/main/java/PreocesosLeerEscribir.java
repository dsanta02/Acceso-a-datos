import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class PreocesosLeerEscribir {

    ArrayList<Clientes> Clientes;
    ArrayList<Pagos> pagos;

    Scanner teclado;

    private Path archivosClientes;
    private Path archivosPagos;

    public PreocesosLeerEscribir(Scanner teclado) {

        this.teclado = teclado;

        Clientes = new ArrayList<>();
        pagos = new ArrayList<>();

        archivosClientes = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivoClientes.csv");
        archivosPagos = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivosPagos.csv");
    }

    public boolean guardarClientes() {


        try {

            ArrayList<String> lineas = new ArrayList<>();

            for (int i = 0; i < Clientes.size(); i++) {

                Clientes cliente = Clientes.get(i);

                String linea =
                        cliente.getId() + ";"
                                + cliente.getNombre() + ";"
                                + cliente.getTel() + ";"
                                + cliente.getMatricula();

                lineas.add(linea);
            }


            Files.write(archivosClientes, lineas);

        } catch (IOException e) {

            System.out.println(
                    "Error al guardar los clientes."
            );

        }

        return true;
    }

    public boolean guardarPago() {

        ArrayList<String> lineas = new ArrayList<>();

        try {

            for(int i = 0; i < pagos.size(); i ++) {


                String linea = pagos.get(i).getIdentificador() + "; " +
                        pagos.get(i).getIdentificadorCliente() + " ; " +
                        pagos.get(i).getFecha() + " ; " +
                        pagos.get(i).getFecha() + " ; " +
                        pagos.get(i).getImporte() + " ; " +
                        pagos.get(i).getLitros() + " ; " +
                        pagos.get(i).getLitros();

                lineas.add(linea);

            }

            Files.write(archivosPagos, lineas);

        }catch (IOException e) {

            System.out.println(e.getMessage());

        }
        return true;
    }



}
