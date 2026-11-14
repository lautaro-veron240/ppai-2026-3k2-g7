package ppai.bolsines.adaptador;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Patrón ADAPTER (GoF) - Adapter.
 * Adapta la interfaz del ServicioGPSTrackerXTR4500L (Adaptee) a IAdaptadorGPSTracker (Target).
 */
public class AdaptadorGPSTrackerXTR4500L implements IAdaptadorGPSTracker {

    private static final Logger log = LoggerFactory.getLogger(AdaptadorGPSTrackerXTR4500L.class);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String claveAPI;
    private final ServicioGPSTrackerXTR4500L servicio;
    private final ObjectMapper mapper = new ObjectMapper();

    /** new(claveAPI: String): AdaptadorGPSTrackerXTR4500L */
    public AdaptadorGPSTrackerXTR4500L(String claveAPI) {
        this.claveAPI = claveAPI;
        this.servicio = new ServicioGPSTrackerXTR4500L();
    }

    /**
     * loop [mientras haya bolsines]: llama al servicio propio del XTR-4500L por cada bolsín
     * y convierte cada respuesta al formato común.
     * Si el GPS no puede informar un bolsín, ese bolsín no aparece en el resultado (flujo alternativo A3).
     */
    @Override
    public String[][] obtenerLocalizacionBolsines(int[] nroBolsin, String[] codigoCMOrigen) {
        List<String[]> datosLocalizacion = new ArrayList<>();
        for (int i = 0; i < nroBolsin.length; i++) {
            try {
                String respuesta = servicio.getBolsinLocation(claveAPI, nroBolsin[i], codigoCMOrigen[i]);
                for (String[] fila : convertirRespuestaGPS(respuesta)) {
                    datosLocalizacion.add(fila);
                }
            } catch (RestClientException e) {
                log.warn("El GPS Tracker XTR-4500L no pudo informar la ubicación del bolsín {}: {}", nroBolsin[i], e.getMessage());
            }
        }
        return datosLocalizacion.toArray(new String[0][]);
    }

    /**
     * Convierte el JSON propio del XTR-4500L ({"items":[{numeroBolsin, latitud, longitud, fechaHoraUltimaActualizacion}]})
     * a la matriz común: [nroBolsin, "latitud,longitud", fechaHora]. Une latitud y longitud en un solo dato.
     */
    private String[][] convertirRespuestaGPS(String respuesta) {
        try {
            JsonNode items = mapper.readTree(respuesta).path("items");
            String[][] filas = new String[items.size()][];
            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                String nro = item.path("numeroBolsin").asText();
                String ubicacion = item.path("latitud").asDouble() + "," + item.path("longitud").asDouble();
                String fechaHora = LocalDateTime.parse(item.path("fechaHoraUltimaActualizacion").asText()).format(FORMATO_FECHA);
                filas[i] = new String[]{nro, ubicacion, fechaHora};
            }
            return filas;
        } catch (Exception e) {
            log.warn("Respuesta del GPS XTR-4500L con formato inesperado: {}", respuesta);
            return new String[0][];
        }
    }
}
