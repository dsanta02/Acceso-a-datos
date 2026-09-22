import java.util.ArrayList;

public interface ProcesosLeerEscribir {

    boolean guardarClientes();

    boolean guardarPago();

    ArrayList<Clientes> leerClientes();

    ArrayList<Pagos> leerPagos();

    boolean crearArchivo();

}
