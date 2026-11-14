package ppai.bolsines.entidades;

import jakarta.persistence.*;

/** «entity» Rol */
@Entity
public class Rol {

    public static final String GERENTE_CM = "Gerente de Comisión Médica";
    public static final String ENCARGADO_BOLSINES = "Encargado de Bolsines";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    protected Rol() { }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /** Mensaje del diagrama de secuencia: Empleado -> Rol: esGCM() */
    public boolean esGCM() {
        return GERENTE_CM.equals(nombre);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
