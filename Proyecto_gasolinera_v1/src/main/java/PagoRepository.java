import java.util.List;

public interface PagoRepository {

    List<Pagos> buscarPagos();

    void guardar(Pagos pago);
}
