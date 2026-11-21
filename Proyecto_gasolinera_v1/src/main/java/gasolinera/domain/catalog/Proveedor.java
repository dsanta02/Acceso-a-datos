package gasolinera.domain.catalog;

import gasolinera.domain.shared.ValidacionDominio;
public class Proveedor {

    private final int id;
    private final String codigo;
    private final String idFiscal;
    private final String razonSocial;
    private final String contacto;

    public Proveedor(int id, String codigo, String idFiscal,
                     String razonSocial, String contacto) {
        this.id = ValidacionDominio.idTecnico(id);
        this.codigo = ValidacionDominio.obligatorio("El código del proveedor", codigo, 20);
        this.idFiscal = ValidacionDominio.obligatorio("El identificador fiscal", idFiscal, 20);
        this.razonSocial = ValidacionDominio.obligatorio("La razón social", razonSocial, 120);
        this.contacto = ValidacionDominio.obligatorio("El contacto", contacto, 100);
    }

    public Proveedor(String codigo, String idFiscal, String razonSocial, String contacto) {
        this(0, codigo, idFiscal, razonSocial, contacto);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getIdFiscal() {
        return idFiscal;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getContacto() {
        return contacto;
    }
}
