package ppai.bolsines.adaptador;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Patrón ADAPTER (GoF) - Adaptee.
 * Representa el servicio propio del dispositivo GPS Tracker modelo XTR-4500L, con su interfaz
 * incompatible: endpoint getBolsinLocation(apiKey, numeroBolsin, codigoComisionMedicaOrigen)
 * que devuelve un JSON con un arreglo "items".
 * Se conecta a la API mock de la cátedra (dsi-api-bolsines) que corre en el puerto 8080.
 */
public class ServicioGPSTrackerXTR4500L {

    private static final String URL_BASE = "http://localhost:8080/api/xtr-4500l";

    private final RestClient restClient = RestClient.create(URL_BASE);

    /**
     * GET /getBolsinLocation?numeroBolsin=..&codigoComisionMedicaOrigen=..  con header X-API-Key.
     * Lanza RestClientException si el GPS no puede informar (404, 401, API apagada, etc.).
     */
    public String getBolsinLocation(String apiKey, int numeroBolsin, String codigoComisionMedicaOrigen) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getBolsinLocation")
                        .queryParam("numeroBolsin", numeroBolsin)
                        .queryParam("codigoComisionMedicaOrigen", codigoComisionMedicaOrigen)
                        .build())
                .header("X-API-Key", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }
}
