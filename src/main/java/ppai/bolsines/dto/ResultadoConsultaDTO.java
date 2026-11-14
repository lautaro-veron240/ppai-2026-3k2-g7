package ppai.bolsines.dto;

import java.util.List;

/**
 * Lo que el Gestor le devuelve a la Pantalla después de opcConsultarBolsines().
 * (Los mensajes Gestor -> Pantalla del diagrama se resuelven como valores de retorno.)
 */
public record ResultadoConsultaDTO(String nombreCMUsuario, boolean hayBolsinesEnviados, MapaDTO mapa,
                                   List<OpcionFiltroDTO> filtroCMDestino, List<Integer> bolsinesSinUbicacion) {

    /** Flujo alternativo A1: no se encuentran bolsines en estado enviado. */
    public static ResultadoConsultaDTO sinBolsines(String nombreCMUsuario) {
        return new ResultadoConsultaDTO(nombreCMUsuario, false, null, List.of(), List.of());
    }
}
