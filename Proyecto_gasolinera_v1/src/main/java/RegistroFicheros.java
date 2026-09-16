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

        archivosClientes = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivoClientes.csv");
        archivosPagos = Paths.get("D:\\Users\\Alumno Mañana\\Desktop\\Gestion_Gasolina\\repo_gitHub\\Proyecto_gasolinera_v1\\src\\archivosPagos.csv");

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

        System.out.println("introduce un nombre: ");
        String nombre = teclado.nextLine();
        while(nombre.isEmpty()) {
            System.out.println("el nombre es obligatorio");
            System.out.println("nombre: ");
            nombre = teclado.nextLine();;
        }

        System.out.println("introduce el telefono: ");
        String telefono = teclado.nextLine();
        while(telefono.isEmpty()) {
            System.out.println("la matricula debe de ser obligatoria");
            System.out.println("matricula: ");
            telefono = teclado.nextLine();
        }

        System.out.println("introduce lña matricula: ");
        String matricula = teclado.nextLine();

        for(int i = 0; i < Clientes.size(); i ++) {

            if (Clientes.get(i).getMatricula().equals(matricula)){

                System.out.printf("no puede haber matriculas iguales");
                System.out.println("matricula: ");
                matricula = teclado.nextLine();

            }
        }

        int id = 1;

        for (int i = 0; i < Clientes.size(); i ++) {

            if(Clientes.get(i).getId() <= id) {

                id = Clientes.get(i).getId() + 1;

            }

        }




    }

    public void guardarDatos () {

        

    }



}
