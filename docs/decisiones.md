# Registro de decisiones (insumo para el documento de la entrega)

## Alcance
- Solo el flujo descripto del CU 36: GPS Tracker modelo XTR-4500L.
- Flujos alternativos implementados: A1, A2, A3, A4, A5.

## Arquitectura / tecnología
- Java 17 + Spring Boot 3.3.5 (lenguaje soportado por la cátedra; misma tecnología que la API mock del GPS).
- Aplicación web monolítica MVC: controller + vista Thymeleaf = Pantalla («boundary»); @Service = Gestor («control»); @Entity = entidades.
- Los mensajes Gestor -> Pantalla se resuelven como valores de retorno (DTO), igual que la implementación de referencia de la cátedra.
- GestorBolsines es @SessionScope: mantiene el estado del caso de uso por usuario (bolsines encontrados, bolsín seleccionado).

## Persistencia
- Modelo: Esquema de Persistencia (framework ORM JPA/Hibernate); las entidades no heredan de ninguna clase persistente.
- Motor: H2 embebida en archivo (sin instalación, persiste entre ejecuciones). ddl-auto=update.
- Mapeo: ManyToOne -> FK; OneToMany Bolsin->CambioEstadoBolsin con FK bolsin_id en cambio_estado_bolsin; OneToOne Empleado->Usuario.
- Bolsin tiene dos FK a ComisionMedica (cm_origen_id, cm_destino_id), como en el modelo de dominio (origen/destino).

## Patrón Adapter
- Target: IAdaptadorGPSTracker. Adapter: AdaptadorGPSTrackerXTR4500L. Adaptee: ServicioGPSTrackerXTR4500L. Client: InterfazGPSTracker.
- Firma común: obtenerLocalizacionBolsines(nroBolsin: int[], codigoCMOrigen: String[]): String[][] (arreglos paralelos).
- convertirRespuestaGPS (privado) convierte el JSON {"items":[...]} a filas [nro, "lat,long", fechaHora] y normaliza la fecha.
- Si el GPS no informa un bolsín (404/401/API caída) se omite de la matriz; el Gestor lo detecta y muestra A3.

## Otros patrones
- GRASP Experto: Bolsin.obtenerBolsinEnviadoCMOrigen(), Bolsin.esTuCMOrigen(), Empleado.esTuUsuario/esTuCM/sosGCM, Rol.esGCM, CambioEstadoBolsin.esActual/sosEnviado, Estado.sosEnviado.
- GRASP Controlador: GestorBolsines. Bajo acoplamiento: la pantalla recibe DTOs, no entidades.

## Diferencias con los diagramas (para Observaciones)
- GPS: se implementó solo XTR-4500L con la firma de arreglos (sin codigoCMDestino ni modeloGPS); el adaptador se crea con new(claveAPI) en la InterfazGPSTracker (en el diseño: crearAdaptador(claveAPI, modeloGPS)). Se quitó recibirDatos().
- Se usaron los nombres del diagrama de secuencia; difieren del diagrama de clases de análisis:
  - Gestor: buscarBolsinesEnviadosCMOrigen (clases: buscarBolsinesEnviados), buscarDireccionCorreoGCMDestino (clases: buscarDireccionCorreoCMDestino), obtenerLocalizacionBolsines (clases: getDatosLocalizacion), mapearBolsines (no está en clases).
  - Estado: sosEnviado (clases: esEnviado).
  - Bolsin: obtenerGCMDestino (clases: obtenerCMDestino); devuelve la CM destino.
  - Pantalla: mostrarMapa / mostrarFiltros / solicitarSeleccionBolsin (clases: mostrarMapaYFiltros).
- mostrarMapa/mostraMapa/mostraFiltros del Gestor se resuelven como retorno (mostrarMapa y mostrarFiltros en la Pantalla).
- Filtros por precinto y CM destino se aplican en la pantalla (JavaScript) sobre los bolsines ya ubicados.
- CU 31 simulado (log), la ERS indica Microsoft Graph.
- Inicio de sesión (fuera del CU): login con usuario/contraseña -> 'Acceso correcto' -> menú. Crea la Sesion que usa el Gestor (getUsuarioLogueado). Permite probar A1 con otro usuario.

## Datos de prueba de la API del GPS
- La API mock de la cátedra trae un solo bolsín por CM origen. Para mostrar varios bolsines en el mapa se agregaron
  los bolsines 113 a 118 (origen CM-CBA-01) en InMemoryBolsinRepository.init(), lugar que su README indica para
  cambiar los datos mock. No se modificaron endpoints, formatos, API keys ni errores.
- El archivo modificado y la explicación están en docs/api-gps/. Cada integrante que levante la API debe copiarlo.
- El 111 no se agregó a propósito (A3). En nuestra base: 112 Cerrado y 119 Recibido no aparecen (no están en movimiento).

## Pantallas (UX)
- Login -> "Acceso correcto" -> Menú -> Consulta -> Confirmación -> Resultado. Login y menú fuera del CU 36.
- Botones por color: verde = confirmar (Ingresar, Seleccionar, Sí enviar), rojo = cancelar/negativo (Cancelar, No enviar, Cerrar sesión), azul = navegación.
- Paso 8 (solicitarConfirmacionCorreo): además de los datos del bolsín, el Gestor pide a la InterfazServidorGoogleMaps un mapa centrado en el bolsín seleccionado (ConfirmacionCorreoDTO).
- Fondo con la imagen del moodboard del grupo; todo el contenido en un único panel para mantener la legibilidad.
- spring.thymeleaf.cache=false para ver cambios de las pantallas sin reiniciar.
