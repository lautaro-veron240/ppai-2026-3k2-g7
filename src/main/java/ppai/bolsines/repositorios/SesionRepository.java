package ppai.bolsines.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import ppai.bolsines.entidades.Sesion;

import java.util.Optional;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /** La sesión actual es la que no tiene fecha de cierre. */
    Optional<Sesion> findFirstByFechaHoraHastaIsNullOrderByFechaHoraDesdeDesc();
}
