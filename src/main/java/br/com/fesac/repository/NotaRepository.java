package br.com.fesac.repository;

import br.com.fesac.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    boolean existsBySalaId(Long salaId);

    boolean existsByCriterioId(Long criterioId);
}
