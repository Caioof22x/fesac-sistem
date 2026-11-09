(function () {
  const CHAVE = 'fesac_notas_lancadas';
  const seletor = document.getElementById('sala');
  const corpo = document.getElementById('lista');
  const botao = document.getElementById('salvar');
  const mensagem = document.getElementById('mensagem');
  let criterios = [];

  function mostrar(texto, sucesso) {
    mensagem.textContent = texto || '';
    mensagem.className = sucesso ? 'sucesso-login' : 'erro-login';
  }

  function lerLancadas() {
    try {
      return JSON.parse(localStorage.getItem(CHAVE)) || {};
    } catch (e) {
      return {};
    }
  }

  function guardarLancada(salaId, criterioId, valor) {
    const todas = lerLancadas();
    todas[salaId] = todas[salaId] || {};
    todas[salaId][criterioId] = valor;
    try {
      localStorage.setItem(CHAVE, JSON.stringify(todas));
    } catch (e) { /* sem armazenamento: segue sem lembrar */ }
  }

  function desenhar(manterMensagem) {
    const salaId = Number(seletor.value);
    const lancadas = lerLancadas()[salaId] || {};
    if (!manterMensagem) mostrar('');

    corpo.innerHTML = criterios.map(function (c) {
      const jaLancada = lancadas[c.id] !== undefined;
      return '<tr><td>' + Comum.esc(c.nome) + '</td><td>' + Comum.esc(c.pesoMaximo) + '</td><td>' +
        '<input class="small-input" type="number" min="0" max="' + Number(c.pesoMaximo) + '" step="any" ' +
        'data-criterio="' + c.id + '" aria-label="Nota em ' + Comum.esc(c.nome) + '" placeholder="0"' +
        (jaLancada ? ' value="' + Number(lancadas[c.id]) + '" disabled' : '') + '>' +
        (jaLancada ? ' <small>já lançada</small>' : '') + '</td></tr>';
    }).join('');

    const pendentes = criterios.some(function (c) { return lancadas[c.id] === undefined; });
    botao.disabled = !pendentes;
    if (!pendentes && !manterMensagem) {
      mostrar('Todas as notas desta sala já foram lançadas.', true);
    }
  }

  function coletar() {
    const campos = Array.from(corpo.querySelectorAll('input[data-criterio]:not([disabled])'));
    const envios = [];
    for (const campo of campos) {
      const criterio = criterios.find(function (c) { return c.id === Number(campo.dataset.criterio); });
      if (String(campo.value).trim() === '') {
        throw new Error('Preencha a nota de "' + criterio.nome + '" (use 0 se não houver pontos).');
      }
      const valor = Number(campo.value);
      if (!isFinite(valor) || valor < 0) {
        throw new Error('A nota de "' + criterio.nome + '" não pode ser negativa.');
      }
      if (valor > criterio.pesoMaximo) {
        throw new Error('A nota de "' + criterio.nome + '" não pode passar de ' + criterio.pesoMaximo + '.');
      }
      envios.push({ criterio: criterio, valor: valor });
    }
    return envios;
  }

  botao.addEventListener('click', async function () {
    const salaId = Number(seletor.value);
    let envios;
    try {
      envios = coletar();
    } catch (e) {
      mostrar(e.message);
      return;
    }
    if (!envios.length) return;

    botao.disabled = true;
    mostrar('Salvando...', true);
    let enviadas = 0;
    try {
      for (const envio of envios) {
        await Comum.chamar('/api/notas', {
          method: 'POST',
          body: JSON.stringify({ salaId: salaId, criterioId: envio.criterio.id, valor: envio.valor }),
        });
        guardarLancada(salaId, envio.criterio.id, envio.valor);
        enviadas++;
      }
      desenhar(true);
      mostrar('Notas salvas com sucesso.', true);
    } catch (e) {
      desenhar(true);
      mostrar((enviadas ? enviadas + ' nota(s) salva(s), mas deu erro: ' : '') + e.message);
    }
  });

  async function iniciar() {
    const dados = await Promise.all([Comum.chamar('/api/salas'), Comum.chamar('/api/criterios')]);
    const salas = dados[0];
    criterios = dados[1];

    if (!salas.length || !criterios.length) {
      seletor.innerHTML = '<option>—</option>';
      seletor.disabled = true;
      botao.disabled = true;
      corpo.innerHTML = '<tr><td colspan="3">Cadastre ao menos uma sala e um critério para lançar notas.</td></tr>';
      return;
    }

    seletor.innerHTML = salas.map(function (s) {
      return '<option value="' + s.id + '">' + Comum.esc(s.nome) + '</option>';
    }).join('');
    seletor.addEventListener('change', function () { desenhar(false); });
    desenhar(false);
  }

  iniciar().catch(function (e) { mostrar(e.message); });
})();
