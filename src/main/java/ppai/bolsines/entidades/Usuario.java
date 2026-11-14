package ppai.bolsines.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

/** «entity» Usuario */
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;
    private String contrasena;
    private LocalDate fechaAlta;

    protected Usuario() { }

    public Usuario(String nombre, String contrasena, LocalDate fechaAlta) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.fechaAlta = fechaAlta;
    }

    /** Valida la contraseña ingresada en el inicio de sesión. */
    public boolean esContrasenaValida(String contrasenaIngresada) {
        return contrasena != null && contrasena.equals(contrasenaIngresada);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaAlta() { return fechaAlta; }
}
