import java.util.ArrayList;

public class GestionFicherosCSV implements ProcesosLeerEscribir {

    @Override
    public boolean guardarClientes() {
        return false;
    }

    @Override
    public boolean guardarPago() {
        return false;
    }

    @Override
    public ArrayList<Clientes> leerClientes() {
        return null;
    }

    @Override
    public ArrayList<Pagos> leerPagos() {
        return null;
    }

    @Override
    public boolean crearArchivo() {
        return false;
    }


}
