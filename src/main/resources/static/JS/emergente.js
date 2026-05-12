document.addEventListener("DOMContentLoaded", function() {
    const elementoModal = document.getElementById('promoModal');
    if (elementoModal) {
        const myModal = new bootstrap.Modal(elementoModal);

        setTimeout(() => {
            myModal.show();
        }, 1000);
    }

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
                            lista.appendChild(item);
                        });
                        lista.classList.remove('d-none');
                    } else {
                        lista.innerHTML = '<div class="p-3 text-secondary small text-center">No hay resultados</div>';
                        lista.classList.remove('d-none');
                    }
                });
        });
    }

    document.addEventListener('click', (e) => {
        if (!input.contains(e.target) && !lista.contains(e.target)) {
            lista.classList.add('d-none');
        }
    });
});