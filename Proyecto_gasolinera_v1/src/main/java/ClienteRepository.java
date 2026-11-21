import java.util.List;

public interface ClienteRepository {

    List<Clientes> buscarClientes();

    boolean existeMatricula(String matricula);

    void guardar(Clientes cliente);
}
