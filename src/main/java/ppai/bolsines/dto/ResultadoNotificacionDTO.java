package ppai.bolsines.dto;

/** Resultado de tomarConfirmacionCorreo(). */
public record ResultadoNotificacionDTO(boolean correoEnviado, String mensaje) {
}
