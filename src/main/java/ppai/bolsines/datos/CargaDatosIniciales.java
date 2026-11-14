package ppai.bolsines.datos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ppai.bolsines.entidades.*;
import ppai.bolsines.repositorios.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga los datos de prueba la primera vez que se levanta la aplicación (base vacía).
 * Los números de bolsín y los códigos de CM coinciden con los que tiene precargados
 * la API mock del GPS Tracker de la cátedra (dsi-api-bolsines).
 */
@Component
public class CargaDatosIniciales implements CommandLineRunner {

    private final EstadoRepository estadoRepository;
    private final RolRepository rolRepository;
    private final ComisionMedicaRepository comisionMedicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final BolsinRepository bolsinRepository;
    private final SesionRepository sesionRepository;

    public CargaDatosIniciales(EstadoRepository estadoRepository, RolRepository rolRepository,
                               ComisionMedicaRepository comisionMedicaRepository, UsuarioRepository usuarioRepository,
                               EmpleadoRepository empleadoRepository, BolsinRepository bolsinRepository,
                               SesionRepository sesionRepository) {
        this.estadoRepository = estadoRepository;
        this.rolRepository = rolRepository;
        this.comisionMedicaRepository = comisionMedicaRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.bolsinRepository = bolsinRepository;
        this.sesionRepository = sesionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (estadoRepository.count() > 0) {
            return; // ya hay datos
        }

        // ---------- Estados (ámbito Bolsin) ----------
        Estado creado = estadoRepository.save(new Estado(Estado.AMBITO_BOLSIN, "Creado", "Bolsín generado"));
        Estado cerrado = estadoRepository.save(new Estado(Estado.AMBITO_BOLSIN, "Cerrado", "Bolsín precintado"));
        Estado enviado = estadoRepository.save(new Estado(Estado.AMBITO_BOLSIN, Estado.ENVIADO, "Bolsín retirado y en viaje"));
        Estado recibido = estadoRepository.save(new Estado(Estado.AMBITO_BOLSIN, "Recibido", "Bolsín recibido en la CM destino"));
        estadoRepository.save(new Estado(Estado.AMBITO_BOLSIN, "DeBaja", "Bolsín eliminado"));

        // ---------- Roles ----------
        Rol encargado = rolRepository.save(new Rol(Rol.ENCARGADO_BOLSINES, "Gestiona el envío y recepción de bolsines"));
        Rol gerente = rolRepository.save(new Rol(Rol.GERENTE_CM, "Gerente de la Comisión Médica"));

        // ---------- Comisiones médicas (mismos códigos que la API del GPS) ----------
        String[][] cms = {
                {"CM-CBA-01", "Comisión Médica Córdoba Capital", "Av. Colón 1050, Córdoba"},
                {"CM-CBA-02", "Comisión Médica Río Cuarto", "Constitución 850, Río Cuarto"},
                {"CM-ROS-02", "Comisión Médica Rosario", "Córdoba 1452, Rosario"},
                {"CM-MDZ-03", "Comisión Médica Mendoza", "San Martín 1200, Mendoza"},
                {"CM-BA-01", "Comisión Médica Buenos Aires", "Av. de Mayo 760, CABA"},
                {"CM-TUC-01", "Comisión Médica Tucumán", "24 de Septiembre 550, San Miguel de Tucumán"},
                {"CM-SLA-01", "Comisión Médica Salta", "Caseros 700, Salta"},
                {"CM-JUJ-01", "Comisión Médica Jujuy", "Belgrano 900, San Salvador de Jujuy"},
                {"CM-NQN-01", "Comisión Médica Neuquén", "Av. Argentina 300, Neuquén"},
                {"CM-MDQ-01", "Comisión Médica Mar del Plata", "Av. Independencia 2100, Mar del Plata"},
                {"CM-LPL-01", "Comisión Médica La Plata", "Calle 7 nro 1200, La Plata"},
        };
        Map<String, ComisionMedica> cm = new HashMap<>();
        for (String[] c : cms) {
            String slug = c[0].toLowerCase();
            cm.put(c[0], comisionMedicaRepository.save(
                    new ComisionMedica(c[0], c[1], c[2], slug + "@bolsines.gob.ar", "0800-555-0000")));
        }

        // ---------- Usuarios y empleados ----------
        LocalDate alta = LocalDate.of(2026, 3, 1);
        Usuario uCordoba = usuarioRepository.save(new Usuario("eb.cordoba", "1234", alta));
        Usuario uJujuy = usuarioRepository.save(new Usuario("eb.jujuy", "1234", alta));
        Empleado ebCordoba = empleadoRepository.save(
                new Empleado("Lucía", "Fernández", "lucia.fernandez@bolsines.gob.ar", encargado, cm.get("CM-CBA-01"), uCordoba));
        // EB de una CM que no tiene bolsines enviados (para mostrar el flujo alternativo A1)
        empleadoRepository.save(
                new Empleado("Martín", "Quispe", "martin.quispe@bolsines.gob.ar", encargado, cm.get("CM-JUJ-01"), uJujuy));
        // Un Gerente (GCM) por cada comisión médica
        for (ComisionMedica c : cm.values()) {
            String slug = c.getCodigo().toLowerCase();
            empleadoRepository.save(new Empleado("Gerente", c.getNombre().replace("Comisión Médica ", ""),
                    "gcm." + slug + "@bolsines.gob.ar", gerente, c, null));
        }

        // ---------- Bolsines (101 a 110 = los mismos de la API del GPS) ----------
        String[][] bolsines = {
                {"101", "CM-CBA-01", "CM-ROS-02"}, {"102", "CM-ROS-02", "CM-MDZ-03"},
                {"103", "CM-MDZ-03", "CM-BA-01"},  {"104", "CM-BA-01", "CM-CBA-01"},
                {"105", "CM-TUC-01", "CM-SLA-01"}, {"106", "CM-SLA-01", "CM-JUJ-01"},
                {"107", "CM-NQN-01", "CM-MDQ-01"}, {"108", "CM-MDQ-01", "CM-BA-01"},
                {"109", "CM-LPL-01", "CM-CBA-01"}, {"110", "CM-CBA-02", "CM-ROS-02"},
        };
        LocalDateTime base = LocalDateTime.of(2026, 3, 6, 9, 0);
        for (String[] b : bolsines) {
            crearBolsin(Integer.parseInt(b[0]), cm.get(b[1]), cm.get(b[2]), base, ebCordoba, creado, cerrado, enviado, null);
        }
        // 113 a 118: enviados desde Córdoba hacia distintos destinos (en viaje).
        // Para que el GPS los informe, también deben agregarse en la API mock (ver docs/datos-api-gps.md).
        String[][] desdeCordoba = {
                {"113", "CM-BA-01"}, {"114", "CM-MDZ-03"}, {"115", "CM-TUC-01"},
                {"116", "CM-ROS-02"}, {"117", "CM-SLA-01"}, {"118", "CM-NQN-01"},
        };
        for (String[] b : desdeCordoba) {
            crearBolsin(Integer.parseInt(b[0]), cm.get("CM-CBA-01"), cm.get(b[1]), base, ebCordoba, creado, cerrado, enviado, null);
        }
        // 111: enviado desde Córdoba pero NO existe en la API del GPS -> flujo alternativo A3
        crearBolsin(111, cm.get("CM-CBA-01"), cm.get("CM-MDZ-03"), base, ebCordoba, creado, cerrado, enviado, null);
        // 112: de Córdoba pero todavía Cerrado (no enviado) -> no debe aparecer en la consulta
        crearBolsin(112, cm.get("CM-CBA-01"), cm.get("CM-BA-01"), base, ebCordoba, creado, cerrado, null, null);
        // 119: de Córdoba que ya llegó (Recibido) -> no debe aparecer en la consulta (ya no está en movimiento)
        crearBolsin(119, cm.get("CM-CBA-01"), cm.get("CM-ROS-02"), base, ebCordoba, creado, cerrado, enviado, recibido);

        // ---------- Sesión iniciada (usuario de Córdoba) ----------
        sesionRepository.save(new Sesion(LocalDateTime.now(), uCordoba));
    }

    /** Crea un bolsín con su historia de estados: Creado -> Cerrado -> [Enviado] -> [Recibido]. */
    private void crearBolsin(int numero, ComisionMedica origen, ComisionMedica destino, LocalDateTime base,
                             Empleado responsable, Estado creado, Estado cerrado, Estado enviado, Estado recibido) {
        Bolsin bolsin = new Bolsin(numero, String.format("P-%06d", numero), base.toLocalDate(), 1500.0 + numero,
                origen, destino);
        bolsin.agregarCambioEstado(new CambioEstadoBolsin(base, creado, responsable));
        bolsin.agregarCambioEstado(new CambioEstadoBolsin(base.plusHours(2), cerrado, responsable));
        if (enviado != null) {
            bolsin.agregarCambioEstado(new CambioEstadoBolsin(base.plusHours(5), enviado, responsable));
        }
        if (recibido != null) {
            bolsin.agregarCambioEstado(new CambioEstadoBolsin(base.plusHours(20), recibido, null));
        }
        bolsinRepository.save(bolsin);
    }
}
