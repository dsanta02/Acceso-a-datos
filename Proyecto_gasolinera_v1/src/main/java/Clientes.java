public class Clientes {

    private String id;
    private String nombre;
    private int tel;
    private String matricula;

    public Clientes(String id, String nombre, int tel, String matricula) {

        this.id = id;
        this.nombre = nombre;
        this.tel = tel;
        this.matricula = matricula;

    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTel() {
        return tel;
    }

    public String getMatricula() {
        return matricula;
    }
}
