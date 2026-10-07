import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Ficheros c = new Ficheros();

        aplicacionConsola aplicacion = new aplicacionConsola(sc, c);

        aplicacion.ejecutar();

        sc.close();
    }
}
