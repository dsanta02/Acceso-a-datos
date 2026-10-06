import java.util.List;

public class GestionJson implements ProcesosLeerEscribir{


    @Override
    public boolean guardarClientes(List<Clientes> clientes) {
        return false;
    }

    @Override
    public boolean guardarPago(List<Pagos> pago) {
        return false;
    }

    @Override
    public List<Clientes> leerClientes() {
        return List.of();
    }

    @Override
    public List<Pagos> leerPagos() {
        return List.of();
    }

    @Override
    public boolean crearArchivo() {
        return false;
    }
}
