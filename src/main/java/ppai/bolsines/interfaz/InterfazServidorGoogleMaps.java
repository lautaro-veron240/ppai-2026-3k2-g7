package ppai.bolsines.interfaz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ppai.bolsines.dto.BolsinMapaDTO;
import ppai.bolsines.dto.MapaDTO;

import java.util.List;

/**
 * «boundary» InterfazServidorGoogleMaps.
 * Arma la configuración del mapa (centro, zoom, marcadores y API key). El dibujo lo hace
 * la librería JavaScript de Google Maps en el navegador, que es la que se comunica con el
 * Servidor de Google Maps (getUbicacionMapa).
 */
@Component
public class InterfazServidorGoogleMaps {

    private static final double CENTRO_ARGENTINA_LAT = -34.0;
    private static final double CENTRO_ARGENTINA_LNG = -64.0;

    private final String apiKey;

    public InterfazServidorGoogleMaps(@Value("${google.maps.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    /** Gestor -> InterfazServidorGoogleMaps: obtenerMapa() */
    public MapaDTO obtenerMapa(List<BolsinMapaDTO> marcadores) {
        if (marcadores.isEmpty()) {
            return new MapaDTO(apiKey, CENTRO_ARGENTINA_LAT, CENTRO_ARGENTINA_LNG, 4, marcadores);
        }
        double lat = marcadores.stream().mapToDouble(BolsinMapaDTO::latitud).average().orElse(CENTRO_ARGENTINA_LAT);
        double lng = marcadores.stream().mapToDouble(BolsinMapaDTO::longitud).average().orElse(CENTRO_ARGENTINA_LNG);
        int zoom = marcadores.size() == 1 ? 7 : 5;
        return new MapaDTO(apiKey, lat, lng, zoom, marcadores);
    }
}
