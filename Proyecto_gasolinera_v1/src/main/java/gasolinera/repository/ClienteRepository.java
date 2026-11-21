package gasolinera.repository;

import gasolinera.model.Clientes;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    List<Clientes> buscarClientes();

    Optional<Clientes> buscarPorId(int id);

    boolean existeMatricula(String matricula);

    Clientes guardar(Clientes cliente);

    void reemplazarClientes(List<Clientes> clientes);
}
