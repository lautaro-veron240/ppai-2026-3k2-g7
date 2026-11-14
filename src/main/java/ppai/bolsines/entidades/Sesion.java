package ppai.bolsines.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** «entity» Sesion. La sesión actual es la que no tiene fechaHoraHasta. */
@Entity
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraDesde;
    private LocalDateTime fechaHoraHasta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    protected Sesion() { }

    public Sesion(LocalDateTime fechaHoraDesde, Usuario usuario) {
        this.fechaHoraDesde = fechaHoraDesde;
        this.usuario = usuario;
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Sesion: getUsuarioLogueado() */
    public Usuario getUsuarioLogueado() {
        return usuario;
    }

    public void cerrar(LocalDateTime fechaHoraHasta) { this.fechaHoraHasta = fechaHoraHasta; }
    public Long getId() { return id; }
    public LocalDateTime getFechaHoraDesde() { return fechaHoraDesde; }
    public LocalDateTime getFechaHoraHasta() { return fechaHoraHasta; }
}
