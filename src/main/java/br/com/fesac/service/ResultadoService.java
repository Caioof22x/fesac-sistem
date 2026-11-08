package br.com.fesac.service;

import br.com.fesac.dto.ResultadoResponse;
import br.com.fesac.model.Criterio;
import br.com.fesac.model.Nota;
import br.com.fesac.model.Sala;
import br.com.fesac.repository.CriterioRepository;
import br.com.fesac.repository.NotaRepository;
import br.com.fesac.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultadoService {

    private final SalaRepository salaRepository;
    private final CriterioRepository criterioRepository;
    private final NotaRepository notaRepository;

    @Transactional(readOnly = true)
    public List<ResultadoResponse> calcularResultadoFinal() {
        List<Sala> salas = salaRepository.findAll();
        List<Nota> notas = notaRepository.findAll();

        // Desempate: maior pontuação no critério de maior peso máximo
        Criterio criterioDesempate = criterioRepository.findAll().stream()
                .max(Comparator.comparing(Criterio::getPesoMaximo))
                .orElse(null);

        Map<Long, Double> totais = new HashMap<>();
        Map<Long, Double> pontosDesempate = new HashMap<>();
        for (Sala sala : salas) {
            totais.put(sala.getId(), 0.0);
            pontosDesempate.put(sala.getId(), 0.0);
        }

        for (Nota nota : notas) {
            Long salaId = nota.getSala().getId();
            totais.merge(salaId, nota.getValor(), Double::sum);
            if (criterioDesempate != null && nota.getCriterio().getId().equals(criterioDesempate.getId())) {
                pontosDesempate.merge(salaId, nota.getValor(), Double::sum);
            }
        }

        List<Sala> ordenadas = new ArrayList<>(salas);
        ordenadas.sort((a, b) -> {
            int comparacao = Double.compare(totais.get(b.getId()), totais.get(a.getId()));
            if (comparacao != 0) {
                return comparacao;
            }
            return Double.compare(pontosDesempate.get(b.getId()), pontosDesempate.get(a.getId()));
        });

        List<ResultadoResponse> ranking = new ArrayList<>();
        for (int i = 0; i < ordenadas.size(); i++) {
            Sala sala = ordenadas.get(i);
            int posicao = i + 1;
            if (i > 0) {
                Sala anterior = ordenadas.get(i - 1);
                boolean empatou = totais.get(sala.getId()).equals(totais.get(anterior.getId()))
                        && pontosDesempate.get(sala.getId()).equals(pontosDesempate.get(anterior.getId()));
                if (empatou) {
                    posicao = ranking.get(i - 1).posicao();
                }
            }
            ranking.add(new ResultadoResponse(posicao, sala.getId(), sala.getNome(), totais.get(sala.getId())));
        }
        return ranking;
    }
}
