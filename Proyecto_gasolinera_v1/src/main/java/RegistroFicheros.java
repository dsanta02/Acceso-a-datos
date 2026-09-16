import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegistroFicheros {

    ArrayList<Clientes> Clientes;
    ArrayList<Pagos> Pagos;

    Scanner teclado;

    private Path archivosClientes;
    private Path archivosPagos;

    public RegistroFicheros(Scanner teclado) {

        this.teclado = teclado;

        Clientes = new ArrayList<>();
        Pagos = new ArrayList<>();

        archivosClientes = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src");
        archivosPagos = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src");

    }

    public void crearArchivo() {

        try {
            if(!Files.exists(archivosClientes)) {
                Files.createFile(archivosClientes);
            }

            if(!Files.exists(archivosPagos)) {
                Files.createFile(archivosPagos);
            }

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void darAlta () {

        

    }



}
