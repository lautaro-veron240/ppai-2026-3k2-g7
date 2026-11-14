package ppai.bolsines.gestor;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import ppai.bolsines.casosdeuso.NotificarUbicacionBolsin;
import ppai.bolsines.dto.*;
import ppai.bolsines.entidades.*;
import ppai.bolsines.interfaz.InterfazGPSTracker;
import ppai.bolsines.interfaz.InterfazServidorGoogleMaps;
import ppai.bolsines.repositorios.BolsinRepository;
import ppai.bolsines.repositorios.EmpleadoRepository;
import ppai.bolsines.repositorios.SesionRepository;

import java.util.*;

/**
 * «control» GestorBolsines - CU 36 Consultar seguimiento de bolsines.
 * Los métodos siguen el orden y los nombres del diagrama de secuencia.
 * Es @SessionScope: cada usuario tiene su propio gestor mientras dura el caso de uso.
 */
@Service
@SessionScope
public class GestorBolsines {

    // Colaboradores
    private final SesionRepository sesionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final BolsinRepository bolsinRepository;
    private final InterfazGPSTracker interfazGPSTracker;
    private final InterfazServidorGoogleMaps interfazServidorGoogleMaps;
    private final NotificarUbicacionBolsin notificarUbicacionBolsin;

    // Atributos del diagrama de clases
    private Usuario usuario;
    private ComisionMedica cmUsuario;
    private List<Bolsin> bolsinesEnviados = new ArrayList<>();
    private int[] nroBolsin = new int[0];
    private String[] codigoCMOrigen = new String[0];
    private String[][] datosLocalizacion = new String[0][];
    private List<BolsinMapaDTO> bolsinesMapeados = new ArrayList<>();
    private List<Integer> bolsinesSinUbicacion = new ArrayList<>();
    private MapaDTO mapa;
    private Bolsin bolsinSeleccionado;
    private BolsinMapaDTO datosBolsinSeleccionado;
    private Empleado empleadoGCMEncontrado;
    private String direccionCorreoCMDestino;

    public GestorBolsines(SesionRepository sesionRepository, EmpleadoRepository empleadoRepository,
                          BolsinRepository bolsinRepository, InterfazGPSTracker interfazGPSTracker,
                          InterfazServidorGoogleMaps interfazServidorGoogleMaps,
                          NotificarUbicacionBolsin notificarUbicacionBolsin) {
        this.sesionRepository = sesionRepository;
        this.empleadoRepository = empleadoRepository;
        this.bolsinRepository = bolsinRepository;
        this.interfazGPSTracker = interfazGPSTracker;
        this.interfazServidorGoogleMaps = interfazServidorGoogleMaps;
        this.notificarUbicacionBolsin = notificarUbicacionBolsin;
    }

    // ===================== Paso 1 a 6 =====================

    /** Pantalla -> Gestor: opcConsultarBolsines() */
    public ResultadoConsultaDTO opcConsultarBolsines() {
        finCU(); // arranca limpio
        String nombreCMUsuario = buscarCMUsuario();
        buscarBolsinesEnviadosCMOrigen();
        if (bolsinesEnviados.isEmpty()) {
            return ResultadoConsultaDTO.sinBolsines(nombreCMUsuario);   // A1
        }
        obtenerDatosBolsinEncontrado();
        obtenerLocalizacionBolsines();
        mapearBolsines();
        mapa = interfazServidorGoogleMaps.obtenerMapa(bolsinesMapeados);
        List<OpcionFiltroDTO> filtroCMDestino = habilitarSeleccionYFiltros();
        return new ResultadoConsultaDTO(nombreCMUsuario, true, mapa, filtroCMDestino, bolsinesSinUbicacion);
    }

    /** Paso 2: busca la CM del usuario logueado. */
    private String buscarCMUsuario() {
        Sesion sesionActual = sesionRepository.findFirstByFechaHoraHastaIsNullOrderByFechaHoraDesdeDesc()
                .orElseThrow(() -> new IllegalStateException("No hay una sesión iniciada."));
        usuario = sesionActual.getUsuarioLogueado();
        Empleado empleadoLogueado = null;
        for (Empleado empleado : empleadoRepository.findAll()) {        // *esTuUsuario()
            if (empleado.esTuUsuario(usuario)) {
                empleadoLogueado = empleado;
                break;
            }
        }
        if (empleadoLogueado == null) {
            throw new IllegalStateException("El usuario logueado no está asociado a ningún empleado.");
        }
        cmUsuario = empleadoLogueado.getCM();
        return cmUsuario.getNombre();
    }

    /** Paso 3: bolsines en estado Enviado cuya CM origen es la del usuario logueado. */
    private void buscarBolsinesEnviadosCMOrigen() {
        bolsinesEnviados = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepository.findAll()) {             // loop [mientras haya bolsines]
            if (bolsin.obtenerBolsinEnviadoCMOrigen() && bolsin.esTuCMOrigen(cmUsuario)) {
                bolsinesEnviados.add(bolsin);
            }
        }
    }

    /** Arma los arreglos que se le pasan al GPS (posición i = mismo bolsín). */
    private void obtenerDatosBolsinEncontrado() {
        nroBolsin = new int[bolsinesEnviados.size()];
        codigoCMOrigen = new String[bolsinesEnviados.size()];
        for (int i = 0; i < bolsinesEnviados.size(); i++) {             // loop [mientras haya bolsines filtrados]
            Bolsin bolsin = bolsinesEnviados.get(i);
            nroBolsin[i] = bolsin.getNumeroBolsin();
            codigoCMOrigen[i] = bolsin.obtenerCodigoCMOrigen();
        }
    }

    /** Pasos 4 y 5: pide al GPS Tracker la localización (patrón Adapter detrás de la InterfazGPSTracker). */
    private void obtenerLocalizacionBolsines() {
        datosLocalizacion = interfazGPSTracker.obtenerLocalizacionBolsines(nroBolsin, codigoCMOrigen);
        Set<Integer> informados = new HashSet<>();
        for (String[] fila : datosLocalizacion) {
            informados.add(Integer.parseInt(fila[0]));
        }
        bolsinesSinUbicacion = new ArrayList<>();
        for (int nro : nroBolsin) {
            if (!informados.contains(nro)) {
                bolsinesSinUbicacion.add(nro);                          // A3
            }
        }
    }

    /** Separa la ubicación "lat,long" y arma los marcadores del mapa. */
    private void mapearBolsines() {
        bolsinesMapeados = new ArrayList<>();
        for (String[] fila : datosLocalizacion) {
            int nro = Integer.parseInt(fila[0]);
            String[] ubicacion = fila[1].split(",");
            Bolsin bolsin = buscarBolsinEnviado(nro);
            if (bolsin == null) continue;
            ComisionMedica cmDestino = bolsin.obtenerGCMDestino();
            bolsinesMapeados.add(new BolsinMapaDTO(nro, bolsin.getNumeroPrecinto(), cmDestino.getCodigo(),
                    cmDestino.getNombre(), Double.parseDouble(ubicacion[0]), Double.parseDouble(ubicacion[1]), fila[2]));
        }
    }

    /** Paso 6: habilita seleccionar un bolsín del mapa y filtrar por nro. de precinto o CM destino. */
    private List<OpcionFiltroDTO> habilitarSeleccionYFiltros() {
        Map<String, OpcionFiltroDTO> opciones = new TreeMap<>();
        for (BolsinMapaDTO b : bolsinesMapeados) {
            opciones.put(b.codigoCMDestino(), new OpcionFiltroDTO(b.codigoCMDestino(), b.nombreCMDestino()));
        }
        return new ArrayList<>(opciones.values());
    }

    // ===================== Pasos 7 y 8 =====================

    /** Pantalla -> Gestor: tomarSeleccionBolsin(nroBolsin) */
    public ConfirmacionCorreoDTO tomarSeleccionBolsin(int nroBolsinSeleccionado) {
        bolsinSeleccionado = buscarBolsinEnviado(nroBolsinSeleccionado);
        datosBolsinSeleccionado = bolsinesMapeados.stream()
                .filter(b -> b.nroBolsin() == nroBolsinSeleccionado).findFirst().orElse(null);
        if (bolsinSeleccionado == null || datosBolsinSeleccionado == null) {
            throw new IllegalArgumentException("El bolsín " + nroBolsinSeleccionado + " no está disponible en el mapa.");
        }
        return solicitarConfirmacionCorreo();
    }

    /** Paso 8: consulta si se desea enviar el correo al GCM destino (con el mapa del bolsín seleccionado). */
    private ConfirmacionCorreoDTO solicitarConfirmacionCorreo() {
        MapaDTO mapaBolsin = interfazServidorGoogleMaps.obtenerMapa(List.of(datosBolsinSeleccionado));
        return new ConfirmacionCorreoDTO(datosBolsinSeleccionado, mapaBolsin);
    }

    // ===================== Pasos 9 a 11 =====================

    /** Pantalla -> Gestor: tomarConfirmacionCorreo(confirma). Si no confirma -> A5. */
    public ResultadoNotificacionDTO tomarConfirmacionCorreo(boolean confirma) {
        if (bolsinSeleccionado == null) {
            throw new IllegalStateException("No hay un bolsín seleccionado.");
        }
        if (!confirma) {
            int nro = bolsinSeleccionado.getNumeroBolsin();
            finCU();
            return new ResultadoNotificacionDTO(false, "No se envió el correo para el bolsín " + nro + ".");  // A5
        }
        buscarDireccionCorreoGCMDestino();
        llamarCUNotificarUbicacionDeBolsin();
        ResultadoNotificacionDTO resultado = informarEjecucionExitosa();
        finCU();
        return resultado;
    }

    /** Paso 10: busca la dirección de correo del GCM de la CM destino. */
    private void buscarDireccionCorreoGCMDestino() {
        ComisionMedica cmDestino = bolsinSeleccionado.obtenerGCMDestino();
        empleadoGCMEncontrado = null;
        for (Empleado empleado : empleadoRepository.findAll()) {        // loop [mientras haya empleados]
            if (empleado.esTuCM(cmDestino) && empleado.sosGCM()) {
                empleadoGCMEncontrado = empleado;
                break;
            }
        }
        if (empleadoGCMEncontrado == null) {
            throw new IllegalStateException("No se encontró el Gerente de la CM destino " + cmDestino.getNombre() + ".");
        }
        direccionCorreoCMDestino = empleadoGCMEncontrado.getEmail();
    }

    /** Paso 11: <<include>> CU 31 Notificar ubicación de bolsín. */
    private void llamarCUNotificarUbicacionDeBolsin() {
        notificarUbicacionBolsin.ejecutar(direccionCorreoCMDestino, datosBolsinSeleccionado.nroBolsin(),
                datosBolsinSeleccionado.fechaHoraUltimaActualizacion(),
                datosBolsinSeleccionado.latitud(), datosBolsinSeleccionado.longitud());
    }

    private ResultadoNotificacionDTO informarEjecucionExitosa() {
        return new ResultadoNotificacionDTO(true, "Se notificó la ubicación del bolsín "
                + datosBolsinSeleccionado.nroBolsin() + " a " + direccionCorreoCMDestino + ".");
    }

    /** Fin del caso de uso (también se usa para A4: el EB cancela). */
    public void finCU() {
        bolsinesEnviados = new ArrayList<>();
        nroBolsin = new int[0];
        codigoCMOrigen = new String[0];
        datosLocalizacion = new String[0][];
        bolsinesMapeados = new ArrayList<>();
        bolsinesSinUbicacion = new ArrayList<>();
        mapa = null;
        bolsinSeleccionado = null;
        datosBolsinSeleccionado = null;
        empleadoGCMEncontrado = null;
        direccionCorreoCMDestino = null;
    }

    private Bolsin buscarBolsinEnviado(int nro) {
        for (Bolsin b : bolsinesEnviados) {
            if (b.getNumeroBolsin() == nro) return b;
        }
        return null;
    }
}
