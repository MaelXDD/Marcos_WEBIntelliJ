document.addEventListener("DOMContentLoaded", function() {

    //  MODAL PROMOCIÓN EMERGENTE
    const elementoModal = document.getElementById('promoModal');
    const hayLoginError = document.getElementById('loginError') !== null;

    if (elementoModal && !hayLoginError) {
        const myModal = new bootstrap.Modal(elementoModal);
        setTimeout(() => {
            myModal.show();
        }, 1000);
    }

    // MODAL LOGIN CON ERROR
    const loginModal = document.getElementById('loginModal');
    if (loginModal && hayLoginError) {
        const modal = new bootstrap.Modal(loginModal);
        modal.show();
    }

    // MODAL REGISTRO EXITOSO
    const registroExitosoModal = document.getElementById('registroExitosoModal');
    if (registroExitosoModal && registroExitosoModal.dataset.show === 'true') {
        const modal = new bootstrap.Modal(registroExitosoModal);
        modal.show();
    }

    // Barra de Busqueda
    const input = document.getElementById('inputBusqueda');
    const suggestionsBox = document.getElementById('suggestionsBox');
    const productGrid = document.getElementById('productGrid');

    if (input) {
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                const term = this.value.trim();
                if (term) {
                    window.location.href = window.location.origin + '/buscar?query=' + encodeURIComponent(term);
                }
            }
        });

        input.addEventListener('input', async function() {
            const term = this.value.trim();
            if (term.length < 1) {
                suggestionsBox.classList.add('d-none');
                return;
            }
            try {
                const response = await fetch(`/api/v1/productos/keywords?term=${encodeURIComponent(term)}`);
                const keywords = await response.json();
                suggestionsBox.innerHTML = '';

                if (keywords.length > 0) {
                    keywords.forEach(word => {
                        const item = document.createElement('div');

                        item.className = 'p-2 suggestion-item d-block text-dark';
                        item.style.cursor = 'pointer';
                        item.textContent = word;

                        item.addEventListener('mousedown', function(eventoDeClic) {
                            eventoDeClic.preventDefault();
                            eventoDeClic.stopPropagation();

                            const texto = word.toLowerCase().trim();
                            const categorias = ["consolas", "juegos", "perifericos", "tarjetas", "sillas"];

                            if (categorias.includes(texto)) {
                                window.location.assign(window.location.origin + `/categoria/${texto}`);
                            } else {
                                window.location.assign(window.location.origin + `/buscar?query=${encodeURIComponent(word)}`);
                            }
                        }, true);

                        suggestionsBox.appendChild(item);
                    });
                    suggestionsBox.classList.remove('d-none');
                } else {
                    suggestionsBox.classList.add('d-none');
                }
            } catch (error) { console.error('Error:', error); }
        });

        document.addEventListener('click', function(e) {
            const clickDentroDelInput = input.contains(e.target);
            const clickDentroDeSugerencias = suggestionsBox.contains(e.target);

            if (!clickDentroDelInput && !clickDentroDeSugerencias) {
                suggestionsBox.classList.add('d-none');
            }
        });
    }

    // MODAL COMPRA
    const compraModal = document.getElementById('compraModal');
    if (compraModal) {
        compraModal.addEventListener('show.bs.modal', function(event) {
            const boton = event.relatedTarget;
            document.getElementById('modalProductoNombre').textContent = boton.getAttribute('data-producto');
            document.getElementById('modalProductoPrecio').textContent = boton.getAttribute('data-precio');
        });
    }

});
// MOSTRAR/OCULTAR CONTRASEÑA
document.querySelectorAll('[data-toggle-password]').forEach(btn => {
    btn.addEventListener('click', function() {
        const inputId = this.getAttribute('data-toggle-password');
        const input = document.getElementById(inputId);
        const icon = this.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.replace('bi-eye', 'bi-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.replace('bi-eye-slash', 'bi-eye');
        }
    });
});