package ppai.bolsines.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** «entity» Bolsin */
@Entity
public class Bolsin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer numeroBolsin;
    private String numeroPrecinto;
    private LocalDate fecha;
    private Double peso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cm_origen_id")
    private ComisionMedica origen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cm_destino_id")
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bolsin_id")
    private List<CambioEstadoBolsin> cambioEstado = new ArrayList<>();

    protected Bolsin() { }

    public Bolsin(Integer numeroBolsin, String numeroPrecinto, LocalDate fecha, Double peso,
                  ComisionMedica origen, ComisionMedica destino) {
        this.numeroBolsin = numeroBolsin;
        this.numeroPrecinto = numeroPrecinto;
        this.fecha = fecha;
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;
    }

    /**
     * Mensaje del diagrama de secuencia: Gestor -> Bolsin: obtenerBolsinEnviadoCMOrigen()
     * Recorre sus cambios de estado (*esActual()) y al actual le pregunta sosEnviado().
     */
    public boolean obtenerBolsinEnviadoCMOrigen() {
        for (CambioEstadoBolsin cambio : cambioEstado) {
            if (cambio.esActual()) {
                return cambio.sosEnviado();
            }
        }
        return false;
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Bolsin: esTuCMOrigen() */
    public boolean esTuCMOrigen(ComisionMedica cm) {
        return cm != null && origen.getId().equals(cm.getId());
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Bolsin: getNumeroBolsin() */
    public Integer getNumeroBolsin() { return numeroBolsin; }

    /** Mensaje del diagrama de secuencia: Gestor -> Bolsin: obtenerCodigoCMOrigen() -> ComisionMedica: getCodigo() */
    public String obtenerCodigoCMOrigen() {
        return origen.getCodigo();
    }

    /** Mensaje del diagrama de secuencia: Gestor -> Bolsin(seleccionado): obtenerGCMDestino(). Devuelve la CM destino. */
    public ComisionMedica obtenerGCMDestino() {
        return destino;
    }

    public void agregarCambioEstado(CambioEstadoBolsin cambio) {
        for (CambioEstadoBolsin c : cambioEstado) {
            if (c.esActual()) c.setFechaHoraFin(cambio.getFechaHoraInicio());
        }
        cambioEstado.add(cambio);
    }

    public Long getId() { return id; }
    public String getNumeroPrecinto() { return numeroPrecinto; }
    public LocalDate getFecha() { return fecha; }
    public Double getPeso() { return peso; }
    public List<CambioEstadoBolsin> getCambioEstado() { return cambioEstado; }
}
