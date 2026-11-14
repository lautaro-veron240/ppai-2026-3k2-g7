package ppai.bolsines.interfaz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ppai.bolsines.adaptador.AdaptadorGPSTrackerXTR4500L;
import ppai.bolsines.adaptador.IAdaptadorGPSTracker;

/**
 * «boundary» InterfazGPSTracker - en el patrón Adapter cumple el rol de Client:
 * conoce solo la interfaz IAdaptadorGPSTracker.
 */
@Component
public class InterfazGPSTracker {

    private final String claveAPI;
    private IAdaptadorGPSTracker adaptador;

    public InterfazGPSTracker(@Value("${gps.xtr.api-key}") String claveAPI) {
        this.claveAPI = claveAPI;
    }

    /** Gestor -> InterfazGPSTracker: obtenerLocalizacionBolsines(nroBolsin, codigoCMOrigen): String[][] */
    public String[][] obtenerLocalizacionBolsines(int[] nroBolsin, String[] codigoCMOrigen) {
        adaptador = new AdaptadorGPSTrackerXTR4500L(claveAPI);   // new(claveAPI)
        return adaptador.obtenerLocalizacionBolsines(nroBolsin, codigoCMOrigen);
    }
}
