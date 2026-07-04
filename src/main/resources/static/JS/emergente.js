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

    // BUSCADOR EN TIEMPO REAL
    const input = document.getElementById('inputBusqueda');
    const lista = document.getElementById('contenedorResultados');

    if (input) {
        input.addEventListener('input', function() {
            const texto = input.value.trim();

            if (texto.length < 2) {
                lista.classList.add('d-none');
                return;
            }

            fetch(`/api/productos/buscar?term=${encodeURIComponent(texto)}`)
                .then(response => response.json())
                .then(productos => {
                    lista.innerHTML = '';
                    if (productos.length > 0) {
                        productos.forEach(p => {
                            const item = document.createElement('div');
                            item.className = 'd-flex align-items-center p-2 border-bottom border-secondary result-item';
                            item.style.cursor = 'pointer';

                            item.innerHTML = `
                                    <img src="${p.imagenUrl || '/Imagenes/consolatarjeta.jpg'}" 
                                         style="width: 40px; height: 40px; object-fit: contain;" class="bg-white rounded p-1">
                                    <div class="ms-3">
                                        <div class="text-white small fw-bold">${p.nombre}</div>
                                        <div class="text-danger small fw-bold">S/ ${p.precio.toFixed(2)}</div>
                                    </div>
                                `;

                            item.addEventListener('click', function() {
                                window.location.href = `/producto/${p.id}`;
                            });

                            lista.appendChild(item);
                        });
                        lista.classList.remove('d-none');
                    } else {
                        lista.innerHTML = '<div class="p-3 text-secondary small text-center">No hay resultados</div>';
                        lista.classList.remove('d-none');
                    }
                })
                .catch(error => console.error('Error en fetch:', error));
        });

        document.addEventListener('click', (e) => {
            if (!input.contains(e.target) && !lista.contains(e.target)) {
                lista.classList.add('d-none');
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