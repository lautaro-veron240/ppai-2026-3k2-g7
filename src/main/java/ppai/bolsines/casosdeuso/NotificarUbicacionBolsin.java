package ppai.bolsines.casosdeuso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * CU 31 Notificar ubicación de bolsín (incluido desde el CU 36).
 * SIMULADO: no envía un correo real (la ERS indica Outlook / Microsoft Graph);
 * registra en el log el correo que se enviaría.
 */
@Component
public class NotificarUbicacionBolsin {

    private static final Logger log = LoggerFactory.getLogger(NotificarUbicacionBolsin.class);

    public boolean ejecutar(String direccionCorreo, int nroBolsin, String fechaHoraUltimaActualizacion,
                            double latitud, double longitud) {
        log.info("""
                [CU 31 - SIMULADO] Correo enviado
                  Para: {}
                  Asunto: Ubicacion del bolsin {}
                  Bolsin: {} | Ultima actualizacion: {} | Coordenadas: {}, {}""",
                direccionCorreo, nroBolsin, nroBolsin, fechaHoraUltimaActualizacion, latitud, longitud);
        return true;
    }
}
