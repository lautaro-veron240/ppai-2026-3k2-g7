package ppai.bolsines.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import ppai.bolsines.entidades.Bolsin;

public interface BolsinRepository extends JpaRepository<Bolsin, Long> {
}
