package ppai.bolsines.simulacion;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ppai.bolsines.entidades.Empleado;
import ppai.bolsines.entidades.Sesion;
import ppai.bolsines.entidades.Usuario;
import ppai.bolsines.repositorios.EmpleadoRepository;
import ppai.bolsines.repositorios.SesionRepository;
import ppai.bolsines.repositorios.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * FUERA DEL CU 36. Inicio de sesión, menú y cierre de sesión.
 * Crea la Sesion que después usa el GestorBolsines (Sesion.getUsuarioLogueado()).
 */
@Controller
public class InicioSesion {

    private final SesionRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;

    public InicioSesion(SesionRepository sesionRepository, UsuarioRepository usuarioRepository,
                        EmpleadoRepository empleadoRepository) {
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/")
    public String raiz() {
        return sesionActual().isPresent() ? "redirect:/menu" : "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    @Transactional
    public String iniciarSesion(@RequestParam("usuario") String nombreUsuario,
                                @RequestParam("contrasena") String contrasena, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findByNombre(nombreUsuario.trim());
        if (usuario.isEmpty() || !usuario.get().esContrasenaValida(contrasena)) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            model.addAttribute("usuarioIngresado", nombreUsuario);
            return "login";
        }
        cerrarSesionActual();
        sesionRepository.save(new Sesion(LocalDateTime.now(), usuario.get()));
        model.addAttribute("nombreUsuario", usuario.get().getNombre());
        return "accesoCorrecto";
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        Optional<Sesion> sesion = sesionActual();
        if (sesion.isEmpty()) {
            return "redirect:/login";
        }
        Usuario usuario = sesion.get().getUsuarioLogueado();
        model.addAttribute("nombreUsuario", usuario.getNombre());
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.esTuUsuario(usuario)) {
                model.addAttribute("nombreEmpleado", empleado.getNombre() + " " + empleado.getApellido());
                model.addAttribute("nombreCM", empleado.getCM().getNombre());
                model.addAttribute("nombreRol", empleado.getRol().getNombre());
                break;
            }
        }
        return "menu";
    }

    @PostMapping("/logout")
    @Transactional
    public String cerrarSesion() {
        cerrarSesionActual();
        return "redirect:/login?salio";
    }

    private Optional<Sesion> sesionActual() {
        return sesionRepository.findFirstByFechaHoraHastaIsNullOrderByFechaHoraDesdeDesc();
    }

    private void cerrarSesionActual() {
        sesionActual().ifPresent(s -> {
            s.cerrar(LocalDateTime.now());
            sesionRepository.save(s);
        });
    }
}
