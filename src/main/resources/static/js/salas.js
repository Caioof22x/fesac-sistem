(function () {
  const form = document.getElementById('form-sala');
  const campoNome = document.getElementById('nome');
  const lista = document.getElementById('lista');
  const erro = document.getElementById('erro');
  let salas = [];

  function mostrarErro(mensagem) {
    erro.textContent = mensagem || '';
  }

  function nomeValido(texto) {
    const nome = String(texto || '').trim();
    if (!nome) throw new Error('Informe o nome da sala.');
    if (nome.length > 100) throw new Error('O nome deve ter no máximo 100 caracteres.');
    return nome;
  }

  async function desenhar() {
    salas = await Comum.chamar('/api/salas');
    if (!salas.length) {
      lista.innerHTML = '<tr><td colspan="3">Nenhuma sala cadastrada.</td></tr>';
      return;
    }
    lista.innerHTML = salas.map(function (s, i) {
      return '<tr><td>' + (i + 1) + '</td><td>' + Comum.esc(s.nome) + '</td><td>' +
        '<button class="link" type="button" data-editar="' + s.id + '">Editar</button> ' +
        '<button class="link danger" type="button" data-excluir="' + s.id + '">Excluir</button></td></tr>';
    }).join('');
  }

  form.addEventListener('submit', async function (ev) {
    ev.preventDefault();
    mostrarErro('');
    try {
      const nome = nomeValido(campoNome.value);
      await Comum.chamar('/api/salas', { method: 'POST', body: JSON.stringify({ nome: nome }) });
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
        const atual = salas.find(function (s) { return s.id === id; });
        const novo = prompt('Novo nome da sala:', atual ? atual.nome : '');
        if (novo === null) return;
        await Comum.chamar('/api/salas/' + id, { method: 'PUT', body: JSON.stringify({ nome: nomeValido(novo) }) });
      } else if (botao.dataset.excluir) {
        if (!confirm('Excluir esta sala?')) return;
        await Comum.chamar('/api/salas/' + Number(botao.dataset.excluir), { method: 'DELETE' });
      }
      await desenhar();
    } catch (e) {
      mostrarErro(e.message);
    }
  });

  desenhar().catch(function (e) { mostrarErro(e.message); });
})();