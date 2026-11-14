package ppai.bolsines.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import ppai.bolsines.entidades.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombre(String nombre);
}
