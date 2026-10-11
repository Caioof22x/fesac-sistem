(function () {
  const form = document.querySelector('form.form-grid');
  const corpo = document.querySelector('table tbody');
  if (!form || !corpo) return;
  const campos = form.querySelectorAll('input');

  function renumerar() {
    corpo.querySelectorAll('tr').forEach((tr, i) => (tr.cells[0].textContent = i + 1));
  }

  form.addEventListener('submit', (ev) => {
    ev.preventDefault();
    const valores = Array.from(campos).map((c) => c.value.trim());
    if (valores.some((v) => !v)) {
      alert('Preencha todos os campos.');
      return;
    }
    const tr = document.createElement('tr');
    [''].concat(valores).forEach((texto) => {
      const td = document.createElement('td');
      td.textContent = texto;
      tr.appendChild(td);
    });
    const acoes = document.createElement('td');
    acoes.innerHTML = '<button class="link" type="button">Editar</button> <button class="link danger" type="button">Excluir</button>';
    tr.appendChild(acoes);
    corpo.appendChild(tr);
    renumerar();
    form.reset();
  });

  corpo.addEventListener('click', (ev) => {
    const botao = ev.target.closest('button');
    if (!botao) return;
    const tr = botao.closest('tr');
    if (botao.classList.contains('danger')) {
      if (confirm('Excluir este item?')) {
        tr.remove();
        renumerar();
      }
      return;
    }
    Array.from(tr.cells).slice(1, -1).forEach((celula) => {
      const novo = prompt('Novo valor:', celula.textContent);
      if (novo !== null && novo.trim()) celula.textContent = novo.trim();
    });
  });
})();
