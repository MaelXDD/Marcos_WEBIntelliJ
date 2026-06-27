function initMap() {
    // Coordenadas de la tienda
    const ubiPhantom = { lat: -12.046374, lng: -77.042793 };

    const map = new google.maps.Map(document.getElementById("map"), {
        center: ubiPhantom,
        zoom: 16,
        mapId: "DEMO_MAP_ID" // Opcional
    });

    // Marcador de la tienda
    new google.maps.Marker({
        position: ubiPhantom,
        map: map,
        title: "Phantom - Tienda de Videojuegos"
    });
}