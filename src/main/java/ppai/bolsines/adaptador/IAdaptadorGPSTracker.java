package ppai.bolsines.adaptador;

/**
 * Patrón ADAPTER (GoF) - Target.
 * Interfaz común que espera el sistema para pedir la localización de los bolsines,
 * independientemente del modelo de GPS Tracker.
 * Devuelve una matriz con una fila por bolsín: [nroBolsin, "latitud,longitud", fechaHoraUltimaActualizacion]
 */
public interface IAdaptadorGPSTracker {

    String[][] obtenerLocalizacionBolsines(int[] nroBolsin, String[] codigoCMOrigen);
}
