document.addEventListener("DOMContentLoaded", function() {
    
    const elementoModal = document.getElementById('promoModal');

    if (elementoModal) {
        const myModal = new bootstrap.Modal(elementoModal);
        
        setTimeout(() => {
            myModal.show();
        }, 1000);
    }
});