package ppai.bolsines.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import ppai.bolsines.entidades.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
