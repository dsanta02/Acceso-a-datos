public class Clientes {

    private int id;
    private String nombre;
    private String tel;
    private String matricula;

    public Clientes(int id, String nombre, String tel, String matricula) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser positivo.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (tel == null || tel.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío.");
        }
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula no puede estar vacía.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.tel = tel.trim();
        this.matricula = matricula.trim().toUpperCase(java.util.Locale.ROOT);

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
        if (id <= 0 || nombre == null || nombre.isBlank()
                || tel == null || tel.isBlank()
                || matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Los datos del cliente no son válidos.");
        }
    }
}
