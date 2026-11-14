package ppai.bolsines.dto;

import java.util.List;

/** Configuración del mapa que arma la InterfazServidorGoogleMaps. */
public record MapaDTO(String apiKey, double centroLatitud, double centroLongitud, int zoom,
                      List<BolsinMapaDTO> marcadores) {

    public boolean tieneApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
