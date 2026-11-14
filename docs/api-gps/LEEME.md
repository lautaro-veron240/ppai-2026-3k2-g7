# Datos agregados a la API mock del GPS (dsi-api-bolsines)

Para que en el mapa se vean varios bolsines enviados desde Córdoba (CM-CBA-01),
se agregaron los bolsines 113 a 118 a los datos de prueba de la API de la cátedra
(su README indica que los datos mock se cambian en `InMemoryBolsinRepository.init()`).

## Cómo aplicarlo
Copiar `InMemoryBolsinRepository.java` de esta carpeta sobre:
```
dsi-api-bolsines/src/main/java/ar/edu/dsi/gpstracker/repository/InMemoryBolsinRepository.java
```
y reiniciar la API (`.\mvnw.cmd spring-boot:run`).

| Bolsín | Origen | Destino | Ubicación (en viaje) |
|---|---|---|---|
| 113 | CM-CBA-01 | CM-BA-01 | cerca de Villa María |
| 114 | CM-CBA-01 | CM-MDZ-03 | cerca de San Luis |
| 115 | CM-CBA-01 | CM-TUC-01 | cerca de Santiago del Estero |
| 116 | CM-CBA-01 | CM-ROS-02 | cerca de Marcos Juárez |
| 117 | CM-CBA-01 | CM-SLA-01 | cerca de Deán Funes |
| 118 | CM-CBA-01 | CM-NQN-01 | cerca de Río Cuarto |

El bolsín 111 NO se agrega a propósito: sirve para mostrar el flujo alternativo A3.
