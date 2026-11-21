package gasolinera.domain.staff;

import gasolinera.domain.shared.ValidacionDominio;
import java.util.Locale;

public class Funcion {

    private final int id;
    private final String codigo;
    private final String nombre;
    private final String descripcion;

    public Funcion(int id, String codigo, String nombre, String descripcion) {
        this.id = ValidacionDominio.idTecnico(id);
        this.codigo = ValidacionDominio.obligatorio("El código de función", codigo, 30)
                .toUpperCase(Locale.ROOT);
        this.nombre = ValidacionDominio.obligatorio("El nombre de función", nombre, 60);
        this.descripcion = ValidacionDominio.opcional("La descripción", descripcion, 200);
    }

    public Funcion(String codigo, String nombre, String descripcion) {
        this(0, codigo, nombre, descripcion);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
