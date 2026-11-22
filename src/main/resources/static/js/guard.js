(function () {

  if (/login\.html$/.test(location.pathname)) return;

  function sair(evento) {
    evento.preventDefault();
    fetch('/logout', { method: 'POST', credentials: 'same-origin' })
      .finally(function () { location.replace('login.html'); });
  }

  function preencher(usuario) {
    const nome = document.querySelector('.user-box strong');
    const avatar = document.querySelector('.user-box .avatar');
    if (nome) nome.textContent = usuario.nome;
    if (avatar) avatar.textContent = usuario.nome.trim().charAt(0).toUpperCase();

    const menu = document.querySelector('.sidebar nav');
    if (menu && !menu.querySelector('.sair')) {
      const link = document.createElement('a');
      link.href = 'login.html';
      link.className = 'sair';
      link.textContent = '⎋ Sair';
      link.addEventListener('click', sair);
      menu.appendChild(link);
    }
  }

  document.documentElement.style.visibility = 'hidden';
  fetch('/api/auth/me', { credentials: 'same-origin' })
    .then(function (r) {
      if (r.status === 401 || r.status === 403) {
        location.replace('login.html');
        return;
      }
      document.documentElement.style.visibility = '';
      return r.json().then(function (usuario) {
        if (document.readyState === 'loading') {
          document.addEventListener('DOMContentLoaded', function () { preencher(usuario); });
        } else {
          preencher(usuario);
        }
      });
    })
    .catch(function () {
      document.documentElement.style.visibility = '';
    });
})();
