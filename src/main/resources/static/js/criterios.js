(function () {
  const form = document.getElementById('form-criterio');
  const campoNome = document.getElementById('nome');
  const campoMaximo = document.getElementById('maximo');
  const lista = document.getElementById('lista');
  const erro = document.getElementById('erro');
  let criterios = [];

  function mostrarErro(mensagem) {
    erro.textContent = mensagem || '';
  }

  function nomeValido(texto) {
    const nome = String(texto || '').trim();
    if (!nome) throw new Error('Informe o nome do critério.');
    if (nome.length > 100) throw new Error('O nome deve ter no máximo 100 caracteres.');
    return nome;
  }

  function maximoValido(texto) {
    const valor = Number(String(texto).replace(',', '.'));
    if (!isFinite(valor) || valor <= 0) throw new Error('A pontuação máxima deve ser maior que zero.');
    return valor;
  }

  async function desenhar() {
    criterios = await Comum.chamar('/api/criterios');
    if (!criterios.length) {
      lista.innerHTML = '<tr><td colspan="4">Nenhum critério cadastrado.</td></tr>';
      return;
    }
    lista.innerHTML = criterios.map(function (c, i) {
      return '<tr><td>' + (i + 1) + '</td><td>' + Comum.esc(c.nome) + '</td><td>' + Comum.esc(c.pesoMaximo) + '</td><td>' +
        '<button class="link" type="button" data-editar="' + c.id + '">Editar</button> ' +
        '<button class="link danger" type="button" data-excluir="' + c.id + '">Excluir</button></td></tr>';
    }).join('');
  }

  form.addEventListener('submit', async function (ev) {
    ev.preventDefault();
    mostrarErro('');
    try {
      const corpo = { nome: nomeValido(campoNome.value), pesoMaximo: maximoValido(campoMaximo.value) };
      await Comum.chamar('/api/criterios', { method: 'POST', body: JSON.stringify(corpo) });
      form.reset();
      await desenhar();
    } catch (e) {
      mostrarErro(e.message);
    }
  });

  lista.addEventListener('click', async function (ev) {
    const botao = ev.target.closest('button');
    if (!botao) return;
    mostrarErro('');
    try {
      if (botao.dataset.editar) {
        const id = Number(botao.dataset.editar);
        const atual = criterios.find(function (c) { return c.id === id; });
        const novoNome = prompt('Nome do critério:', atual ? atual.nome : '');
        if (novoNome === null) return;
        const novoMaximo = prompt('Pontuação máxima:', atual ? atual.pesoMaximo : '');
        if (novoMaximo === null) return;
        const corpo = { nome: nomeValido(novoNome), pesoMaximo: maximoValido(novoMaximo) };
        await Comum.chamar('/api/criterios/' + id, { method: 'PUT', body: JSON.stringify(corpo) });
      } else if (botao.dataset.excluir) {
        if (!confirm('Excluir este critério?')) return;
        await Comum.chamar('/api/criterios/' + Number(botao.dataset.excluir), { method: 'DELETE' });
      }
      await desenhar();
    } catch (e) {
      mostrarErro(e.message);
    }
  });

  desenhar().catch(function (e) { mostrarErro(e.message); });
})();