package gasolinera.model;

public class Clientes {

    private int id;
    private String nombre;
    private String tel;
    private String matricula;

    public Clientes(int id, String nombre, String tel, String matricula) {
        validarDatos(nombre, tel, matricula);
        validarId(id);
        this.id = id;
        this.nombre = nombre.trim();
        this.tel = tel.trim();
        this.matricula = matricula.trim().toUpperCase(java.util.Locale.ROOT);
    }

    public Clientes(String nombre, String tel, String matricula) {
        validarDatos(nombre, tel, matricula);
        this.nombre = nombre.trim();
        this.tel = tel.trim();
        this.matricula = matricula.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private static void validarDatos(String nombre, String tel, String matricula) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (tel == null || tel.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío.");
        }
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula no puede estar vacía.");
        }
    }

    private static void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser positivo.");
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTel() {
        return tel;
    }

    public String getMatricula() {
        return matricula;
    }

    public void validar() {
        validarId(id);
        validarDatos(nombre, tel, matricula);
    }

    public void validarNuevo() {
        if (id != 0) {
            throw new IllegalArgumentException("Un cliente nuevo no debe tener ID asignado.");
        }
        validarDatos(nombre, tel, matricula);
    }
}
