window.Comum = (function () {

    async function chamar(url, opcoes) {
    let r;
    try {
      r = await fetch(url, Object.assign({
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
      }, opcoes));
    } catch (e) {
      throw new Error('Não foi possível falar com o servidor.');
    }
    if (r.status === 401 || r.status === 403) {
      location.replace('login.html');
      throw new Error('Sessão expirada. Entre novamente.');
    }
    if (!r.ok) {
      let mensagem = r.status === 400
        ? 'Confira os dados informados.'
        : 'Erro ' + r.status + ' ao falar com o servidor.';
      if (r.status === 500) {
        mensagem = 'Não foi possível concluir. Se já houver notas lançadas, o item não pode ser excluído.';
      } else if (r.status !== 400) {
        try {
          const dados = await r.json();
          if (dados && dados.message) mensagem = dados.message;
        } catch (e) { /* resposta sem JSON */ }
      }
      throw new Error(mensagem);
    }
    return r.status === 204 ? null : r.json();
  }
  function esc(texto) {
    return String(texto).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  return { chamar: chamar, esc: esc };
})();