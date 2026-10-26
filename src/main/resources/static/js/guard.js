(function () {

  if (/login\.html$/.test(location.pathname)) return;

  document.documentElement.style.visibility = 'hidden';
  fetch('/api/salas', { credentials: 'same-origin' })
    .then(function (r) {
      if (r.status === 401 || r.status === 403) {
        location.replace('login.html');
      } else {
        document.documentElement.style.visibility = '';
      }
    })
    .catch(function () {
      document.documentElement.style.visibility = '';
    });
})();