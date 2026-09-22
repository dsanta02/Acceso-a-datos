import java.util.ArrayList;
import java.util.List;

public interface ProcesosLeerEscribir {



    boolean guardarClientes(List<Clientes> clientes);

    boolean guardarPago(List<Pagos> pago);

    List<Clientes> leerClientes();

    List<Pagos> leerPagos();

    boolean crearArchivo();

}
