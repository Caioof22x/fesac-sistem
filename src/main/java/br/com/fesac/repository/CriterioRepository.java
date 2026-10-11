package br.com.fesac.repository;

import br.com.fesac.model.Criterio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriterioRepository extends JpaRepository<Criterio, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
