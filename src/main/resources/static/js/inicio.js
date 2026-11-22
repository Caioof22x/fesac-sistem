(function () {
  const sair = document.getElementById('sair');

  sair.addEventListener('click', function () {
    fetch('/logout', { method: 'POST', credentials: 'same-origin' })
      .finally(function () { location.replace('login.html'); });
  });

  Promise.all([
    Comum.chamar('/api/salas'),
    Comum.chamar('/api/criterios'),
    Comum.chamar('/api/resultado'),
  ]).then(function (dados) {
    const ranking = dados[2];
    const avaliadas = ranking.filter(function (r) { return r.pontuacao > 0; }).length;
    const lideres = ranking.filter(function (r) { return r.posicao === 1 && r.pontuacao > 0; });

    document.getElementById('total-salas').textContent = dados[0].length;
    document.getElementById('total-criterios').textContent = dados[1].length;
    document.getElementById('salas-avaliadas').textContent = avaliadas;
    document.getElementById('lider').textContent = lideres.length
      ? lideres.map(function (r) { return r.sala; }).join(' e ')
      : '—';
  }).catch(function (e) {
    document.getElementById('mensagem').textContent = e.message;
  });
})();
