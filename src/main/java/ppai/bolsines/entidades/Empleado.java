package ppai.bolsines.entidades;

import jakarta.persistence.*;

/** «entity» Empleado */
@Entity
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne(optional = false)
    @JoinColumn(name = "comision_medica_id")
    private ComisionMedica comisionMedica;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    protected Empleado() { }

    public Empleado(String nombre, String apellido, String email, Rol rol, ComisionMedica comisionMedica, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.comisionMedica = comisionMedica;
        this.usuario = usuario;
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Empleado: *esTuUsuario() */
    public boolean esTuUsuario(Usuario usuarioLogueado) {
        return usuario != null && usuarioLogueado != null && usuario.getId().equals(usuarioLogueado.getId());
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Empleado: getCM() */
    public ComisionMedica getCM() {
        return comisionMedica;
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Empleado: esTuCM() */
    public boolean esTuCM(ComisionMedica cm) {
        return cm != null && comisionMedica.getId().equals(cm.getId());
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Empleado: sosGCM() -> Rol: esGCM() */
    public boolean sosGCM() {
        return rol.esGCM();
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Empleado: getEmail() */
    public String getEmail() { return email; }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Rol getRol() { return rol; }
    public Usuario getUsuario() { return usuario; }
}
