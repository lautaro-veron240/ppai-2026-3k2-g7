package ppai.bolsines.dto;

/** Datos primitivos de un bolsín para mostrar en el mapa (no se le pasa la entidad a la pantalla). */
public record BolsinMapaDTO(int nroBolsin, String numeroPrecinto, String codigoCMDestino, String nombreCMDestino,
                            double latitud, double longitud, String fechaHoraUltimaActualizacion) {
}
