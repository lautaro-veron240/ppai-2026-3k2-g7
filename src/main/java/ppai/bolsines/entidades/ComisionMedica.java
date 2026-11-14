package ppai.bolsines.entidades;

import jakarta.persistence.*;

/** «entity» ComisionMedica */
@Entity
public class ComisionMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;
    private String nombre;
    private String direccion;
    private String email;
    private String telefono;

    protected ComisionMedica() { }

    public ComisionMedica(String codigo, String nombre, String direccion, String email, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }

    /** Mensaje del diagrama de secuencia: getNombre() */
    public String getNombre() { return nombre; }

    /** Mensaje del diagrama de secuencia: Bolsin -> ComisionMedica: getCodigo() */
    public String getCodigo() { return codigo; }

    public Long getId() { return id; }
    public String getDireccion() { return direccion; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
}
