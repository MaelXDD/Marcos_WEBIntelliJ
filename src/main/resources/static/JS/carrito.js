// Agregar productos al carrito
function agregarAlCarrito(btn) {
    const id = btn.dataset.id;
    const origen = btn.dataset.origen;

    // Datos del modal
    document.getElementById('modalNombre').textContent = btn.dataset.nombre;
    document.getElementById('modalPrecio').textContent = btn.dataset.precio;

    // Petición AJAX
    fetch(`/carrito/agregar/${id}?origen=${origen}`, {
        method: 'POST'
    })
        .then(response => response.text())
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}