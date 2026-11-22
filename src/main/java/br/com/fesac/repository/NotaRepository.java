package br.com.fesac.repository;

import br.com.fesac.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {

    boolean existsBySalaId(Long salaId);

    boolean existsByCriterioId(Long criterioId);

    boolean existsBySalaIdAndCriterioId(Long salaId, Long criterioId);

    List<Nota> findBySalaId(Long salaId);
}
