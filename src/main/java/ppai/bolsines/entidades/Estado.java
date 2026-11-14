package ppai.bolsines.entidades;

import jakarta.persistence.*;

/** «entity» Estado. Los estados se distinguen por ámbito (Bolsin, Remito, Documentacion). */
@Entity
public class Estado {

    public static final String AMBITO_BOLSIN = "Bolsin";
    public static final String ENVIADO = "Enviado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ambito;
    private String nombre;
    private String descripcion;

    protected Estado() { }

    public Estado(String ambito, String nombre, String descripcion) {
        this.ambito = ambito;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /** Mensaje del diagrama de secuencia: CambioEstadoBolsin -> Estado: sosEnviado() */
    public boolean sosEnviado() {
        return esAmbitoBolsin() && ENVIADO.equals(nombre);
    }

    public boolean esAmbitoBolsin() {
        return AMBITO_BOLSIN.equals(ambito);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getAmbito() { return ambito; }
    public String getDescripcion() { return descripcion; }
}
