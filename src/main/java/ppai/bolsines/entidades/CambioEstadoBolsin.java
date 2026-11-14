package ppai.bolsines.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** «entity» CambioEstadoBolsin. Es el actual cuando no tiene fechaHoraFin. */
@Entity
public class CambioEstadoBolsin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Empleado responsable;

    protected CambioEstadoBolsin() { }

    public CambioEstadoBolsin(LocalDateTime fechaHoraInicio, Estado estado, Empleado responsable) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
        this.responsable = responsable;
    }

    /** Mensaje del diagrama de secuencia: Bolsin -> CambioEstadoBolsin: *esActual() */
    public boolean esActual() {
        return fechaHoraFin == null;
    }

    /** Mensaje del diagrama de secuencia: Bolsin -> CambioEstadoBolsin: sosEnviado() */
    public boolean sosEnviado() {
        return estado.sosEnviado();
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public Long getId() { return id; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public Estado getEstado() { return estado; }
}
