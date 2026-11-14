package ppai.bolsines.interfaz;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ppai.bolsines.dto.*;
import ppai.bolsines.gestor.GestorBolsines;

import java.util.List;

/**
 * «boundary» PantallaConsultaBolsines.
 * En Spring MVC la pantalla es el controller + la vista HTML (Thymeleaf).
 * Los mensajes Gestor -> Pantalla del diagrama (mostrarCMUsuario, mostrarMapa, ...) se resuelven
 * con los valores de retorno del Gestor, como en la implementación de referencia de la cátedra.
 */
@Controller
@RequestMapping("/consultar-bolsines")
public class PantallaConsultaBolsines {

    private final GestorBolsines gestorBolsines;

    public PantallaConsultaBolsines(GestorBolsines gestorBolsines) {
        this.gestorBolsines = gestorBolsines;
    }

    /** Paso 1: EB -> Pantalla: opcConsultarBolsines() */
    @GetMapping
    public String opcConsultarBolsines(Model model) {
        habilitarPantalla(model);
        ResultadoConsultaDTO resultado;
        try {
            resultado = gestorBolsines.opcConsultarBolsines();
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "consultaBolsines";
        }
        mostrarCMUsuario(model, resultado.nombreCMUsuario());
        if (!resultado.hayBolsinesEnviados()) {
            model.addAttribute("sinBolsines", true);                   // A1
            return "consultaBolsines";
        }
        mostrarMapa(model, resultado.mapa(), resultado.bolsinesSinUbicacion());
        mostrarFiltros(model, resultado.filtroCMDestino());
        solicitarSeleccionBolsin(model);
        return "consultaBolsines";
    }

    private void habilitarPantalla(Model model) {
        model.addAttribute("titulo", "Consultar seguimiento de bolsines");
    }

    private void mostrarCMUsuario(Model model, String nombreCM) {
        model.addAttribute("nombreCMUsuario", nombreCM);
    }

    private void mostrarMapa(Model model, MapaDTO mapa, List<Integer> bolsinesSinUbicacion) {
        model.addAttribute("mapa", mapa);
        model.addAttribute("bolsinesSinUbicacion", bolsinesSinUbicacion);  // A3
    }

    private void mostrarFiltros(Model model, List<OpcionFiltroDTO> filtroCMDestino) {
        model.addAttribute("filtroCMDestino", filtroCMDestino);
    }

    private void solicitarSeleccionBolsin(Model model) {
        model.addAttribute("solicitarSeleccion", true);
    }

    /** Paso 7: EB -> Pantalla: tomarSeleccionBolsin() */
    @PostMapping("/seleccion")
    public String tomarSeleccionBolsin(@RequestParam("nroBolsin") int nroBolsin, Model model) {
        try {
            ConfirmacionCorreoDTO confirmacion = gestorBolsines.tomarSeleccionBolsin(nroBolsin);
            solicitarConfirmacionCorreo(model, confirmacion);
            return "confirmacionCorreo";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/consultar-bolsines";
        }
    }

    /** Paso 8: pregunta si se desea enviar el correo al GCM destino. */
    private void solicitarConfirmacionCorreo(Model model, ConfirmacionCorreoDTO confirmacion) {
        model.addAttribute("bolsin", confirmacion.bolsin());
        model.addAttribute("mapa", confirmacion.mapa());
    }

    /** Paso 9 (o A5): EB -> Pantalla: tomarConfirmacionCorreo() */
    @PostMapping("/confirmacion")
    public String tomarConfirmacionCorreo(@RequestParam("confirma") boolean confirma, Model model) {
        try {
            ResultadoNotificacionDTO resultado = gestorBolsines.tomarConfirmacionCorreo(confirma);
            informarEjecucionExitosa(model, resultado);
        } catch (RuntimeException e) {
            model.addAttribute("resultado", new ResultadoNotificacionDTO(false, e.getMessage()));
        }
        return "resultado";
    }

    private void informarEjecucionExitosa(Model model, ResultadoNotificacionDTO resultado) {
        model.addAttribute("resultado", resultado);
    }

    /** A4: el EB cancela la ejecución del caso de uso. */
    @GetMapping("/cancelar")
    public String cancelar() {
        gestorBolsines.finCU();
        return "redirect:/menu?cancelado";
    }
}
