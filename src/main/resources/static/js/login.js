const form = document.getElementById('form-login');
const erro = document.getElementById('erro');
const botao = document.getElementById('entrar');

form.addEventListener('submit', async (ev) => {
  ev.preventDefault();
  erro.textContent = '';
  const email = document.getElementById('email').value.trim();
  const senha = document.getElementById('senha').value;
  if (!email || !senha) {
    erro.textContent = 'Informe o e-mail e a senha.';
    return;
  }
  botao.disabled = true;
  botao.textContent = 'Entrando...';
  try {
    const resposta = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin',
      body: JSON.stringify({ email, senha }),
    });
    if (!resposta.ok) {
      let mensagem = 'Não foi possível entrar. Confira o e-mail e a senha.';
      try {
        const dados = await resposta.json();
        if (dados && dados.erro) mensagem = dados.erro;
      } catch (e) { /* resposta sem JSON */ }
      throw new Error(mensagem);
    }
    location.href = 'salas.html';
  } catch (e) {
    erro.textContent = e.message;
    botao.disabled = false;
    botao.textContent = 'Entrar';
  }
});
