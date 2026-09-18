public class Clientes {

    private int id;
    private String nombre;
    private String tel;
    private String matricula;

    public Clientes(int id, String nombre, String tel, String matricula) {

        this.id = id;
        this.nombre = nombre.trim();
        this.tel = tel.trim();
        this.matricula = matricula.trim().toUpperCase();

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
}
