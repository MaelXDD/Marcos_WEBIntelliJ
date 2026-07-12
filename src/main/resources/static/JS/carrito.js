function agregarAlCarrito(btn) {
    const id     = btn.dataset.id;
    const origen = btn.dataset.origen;

    fetch('/carrito/agregar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'productoId=' + id + '&origen=' + encodeURIComponent(origen)
    })
        .then(r => r.json())
        .then(data => {
            if (data.success) {
                document.getElementById('modalNombre').textContent = btn.dataset.nombre;
                document.getElementById('modalMarca').textContent  = btn.dataset.marca || '';
                document.getElementById('modalPrecio').textContent = 'S/ ' + btn.dataset.precio;

                const img = document.getElementById('modalImg');
                img.src = btn.dataset.imagen || '/Imagenes/consolatarjeta.jpg';
                img.onerror = () => { img.src = '/Imagenes/consolatarjeta.jpg'; };

                new bootstrap.Modal(document.getElementById('modalCarrito')).show();
                fetch('/carrito/count').then(r => r.text()).then(c => {
                    document.querySelectorAll('.badge-carrito').forEach(b => b.textContent = c);
                });
            } else {
                alert(data.message);
            }
        })
        .catch(err => console.error('Error:', err));
}