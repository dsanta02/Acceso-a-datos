package gasolinera.repository;

import gasolinera.model.Pagos;

import java.util.List;

public interface PagoRepository {

    List<Pagos> buscarPagos();

    Pagos guardar(Pagos pago);

    void reemplazarPagos(List<Pagos> pagos);
}
