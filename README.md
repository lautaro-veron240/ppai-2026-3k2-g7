# PPAI 2026 – Bolsines – Grupo 7 (3K2)

**CU 36 – Consultar seguimiento de bolsines** · Flujo descripto con GPS Tracker modelo **XTR-4500L** · Patrón de diseño **Adapter** (GoF)

## Tecnologías
- Java 17 + Spring Boot 3.3.5
- Pantallas: Thymeleaf (HTML generado en el servidor) + Google Maps JavaScript API
- Persistencia: JPA / Hibernate + base H2 embebida en archivo (`./data/bolsinesdb`)
- GPS Tracker: API mock de la cátedra (`dsi-api-bolsines`, puerto 8080)

## Cómo levantarlo
Se necesitan **dos terminales**.

**Terminal 1 – API del GPS (la del profe)**
```
cd <carpeta>/dsi-api-bolsines
.\mvnw.cmd spring-boot:run
```

**Terminal 2 – Nuestra app**
```
cd <carpeta>/DSI-PPAI
.\mvnw.cmd spring-boot:run
```
Abrir **http://localhost:8081** e iniciar sesión (usuario `eb.cordoba`, contraseña `1234`).

La primera vez se crea la base H2 y se cargan los datos de prueba automáticamente.

## API key de Google Maps (no se sube a GitHub)
1. Copiar `config/local.properties.ejemplo` como `config/local.properties`.
2. Pegar la key en `google.maps.api-key=...`.
3. Reiniciar la app.

Sin key la app funciona igual: en lugar del mapa muestra la lista de bolsines.

## Datos de prueba y flujos para mostrar
| Usuario (contraseña 1234) | CM | Qué se ve |
|---|---|---|
| `eb.cordoba` (por defecto) | CM-CBA-01 Córdoba | **Flujo principal**: bolsín 101 en el mapa. **A3**: aviso de que el GPS no informó el bolsín 111 (no existe en la API del GPS). |
| `eb.jujuy` | CM-JUJ-01 Jujuy | **A1**: no hay bolsines enviados. |

- **A2**: en el filtro, escribir un precinto que no existe (ej. `P-999`).
- **A4**: botón *Cancelar*.
- **A5**: en la confirmación, *No enviar*.
- El correo del CU 31 está **simulado**: se ve en la consola (`[CU 31 - SIMULADO] Correo enviado`).

## Ver la base de datos
http://localhost:8081/h2-console · JDBC URL `jdbc:h2:file:./data/bolsinesdb` · usuario `sa` · sin contraseña.
Para empezar de cero, borrar la carpeta `data/` con la app apagada.

## Estructura
```
src/main/java/ppai/bolsines/
├── interfaz/      «boundary»  PantallaConsultaBolsines, InterfazGPSTracker, InterfazServidorGoogleMaps
├── gestor/        «control»   GestorBolsines
├── entidades/     «entity»    Bolsin, CambioEstadoBolsin, Estado, ComisionMedica, Empleado, Rol, Usuario, Sesion
├── adaptador/     Patrón Adapter: IAdaptadorGPSTracker, AdaptadorGPSTrackerXTR4500L, ServicioGPSTrackerXTR4500L
├── repositorios/  Acceso a datos (Spring Data JPA)
├── casosdeuso/    CU 31 Notificar ubicación de bolsín (simulado)
├── dto/           Datos que el gestor le devuelve a la pantalla
├── datos/         Carga de datos iniciales
└── simulacion/    Inicio de sesión, menú y cierre de sesión (fuera del CU)
```
