async function initStoreLocator(apiKey) {
    const CONFIGURATION = {
        "locations": [
            {"title":"Phantom","address1":"Tienda 210 - 211","address2":"C.C. La Rambla, San Borja 15036, Perú","coords":{"lat":-12.0888,"lng":-77.0049},"placeId":"ChIJK0wrAtTHBZERUpp3g0TyVc0"},
            {"title":"Phantom","address1":"C.C. Plaza Norte","address2":"Independencia 15311, Perú","coords":{"lat":-12.0068,"lng":-77.0591},"placeId":"ChIJo7Ur5N7PBZERUKUkMbg1DPc"},
            {"title":"Phantom","address1":"C.C. Mall Aventura Arequipa","address2":"Paucarpata 04002, Perú","coords":{"lat":-16.4168,"lng":-71.5140},"placeId":"ChIJf-qXZBpLQpERFOJCcp9oGWA"},
            {"title":"Phantom","address1":"C. C. Real Plaza","address2":"Chiclayo 14008, Perú","coords":{"lat":-6.7780,"lng":-79.8324},"placeId":"ChIJSa-oWSzvTJARBU_VHuoktCY"},
            {"title":"Phantom","address1":"Centro Comercial Real Plaza Salaverry","address2":"Jesús María 15076, Perú","coords":{"lat":-12.0899,"lng":-77.0520},"placeId":"ChIJN6dzRlTIBZERxCD9wHMEONM"}
        ],
        "mapOptions": {
            "center": {"lat": -12.0464, "lng": -77.0428},
            "zoom": 6
        },
        "mapsApiKey": apiKey,
        "capabilities": {
            "input": false, "autocomplete": false, "directions": false,
            "distanceMatrix": false, "details": false, "actions": false
        }
    };

    await customElements.whenDefined('gmpx-store-locator');
    const locator = document.querySelector('gmpx-store-locator');
    locator.configureFromQuickBuilder(CONFIGURATION);

}