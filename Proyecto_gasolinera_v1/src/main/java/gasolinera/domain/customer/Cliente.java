package gasolinera.domain.customer;

import gasolinera.domain.shared.ValidacionDominio;

public class Cliente {

    private final int id;
    private final Integer idClienteAntiguo;
    private final String codigoCliente;
    private final String nombre;
    private final String telefono;
    private final String email;
    private final String matricula;

    public Cliente(int id, Integer idClienteAntiguo, String codigoCliente, String nombre,
                   String telefono, String email, String matricula) {
        this.id = ValidacionDominio.idTecnico(id);
        if (idClienteAntiguo != null && idClienteAntiguo <= 0) {
            throw new IllegalArgumentException("El identificador antiguo debe ser positivo.");
        }
        this.idClienteAntiguo = idClienteAntiguo;
        this.codigoCliente = ValidacionDominio.obligatorio("El código de cliente",
                codigoCliente, 20);
        this.nombre = ValidacionDominio.obligatorio("El nombre", nombre, 100);
        this.telefono = ValidacionDominio.opcional("El teléfono", telefono, 20);
        this.email = ValidacionDominio.opcional("El email", email, 254);
        this.matricula = ValidacionDominio.opcional("La matrícula", matricula, 15);
        if (this.telefono == null && this.email == null) {
            throw new IllegalArgumentException("El cliente debe tener teléfono o email.");
        }
    }

    public Cliente(Integer idClienteAntiguo, String codigoCliente, String nombre,
                   String telefono, String email, String matricula) {
        this(0, idClienteAntiguo, codigoCliente, nombre, telefono, email, matricula);
    }

    public int getId() {
        return id;
    }

    public Integer getIdClienteAntiguo() {
        return idClienteAntiguo;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getMatricula() {
        return matricula;
    }
}
