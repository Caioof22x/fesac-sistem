(function () {
  const corpo = document.getElementById('lista');
  const vencedor = document.getElementById('vencedor');
  const mensagem = document.getElementById('mensagem');
  const medalhas = { 1: '🥇 ', 2: '🥈 ', 3: '🥉 ' };

  function pontos(valor) {
    return Number(valor).toLocaleString('pt-BR');
  }

  function mostrarVencedor(ranking) {
    const lideres = ranking.filter(function (r) { return r.posicao === 1; });
    if (!ranking.length || ranking[0].pontuacao === 0) {
      vencedor.innerHTML = '<span>🏆 Sala vencedora</span><strong>—</strong><small>Nenhuma nota lançada ainda</small>';
      return;
    }
    const titulo = lideres.length > 1 ? '🏆 Empate no primeiro lugar' : '🏆 Sala vencedora';
    const nomes = lideres.map(function (r) { return Comum.esc(r.sala); }).join(' e ');
    vencedor.innerHTML = '<span>' + titulo + '</span><strong>' + nomes + '</strong><small>' +
      pontos(lideres[0].pontuacao) + ' pontos</small>';
  }

  function desenhar(ranking) {
    mostrarVencedor(ranking);
    if (!ranking.length) {
      corpo.innerHTML = '<tr><td colspan="3">Nenhuma sala cadastrada.</td></tr>';
      return;
    }
    corpo.innerHTML = ranking.map(function (r) {
      const lider = r.posicao === 1 && r.pontuacao > 0;
      return '<tr' + (lider ? ' class="first"' : '') + '><td>' + (medalhas[r.posicao] || '') + r.posicao + 'º</td>' +
        '<td>' + Comum.esc(r.sala) + '</td>' +
        '<td>' + (lider ? '<strong>' + pontos(r.pontuacao) + '</strong>' : pontos(r.pontuacao)) + '</td></tr>';
    }).join('');
  }

  Comum.chamar('/api/resultado')
    .then(desenhar)
    .catch(function (e) {
      corpo.innerHTML = '';
      mensagem.textContent = e.message;
    });
})();
