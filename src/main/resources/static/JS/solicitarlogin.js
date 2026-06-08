 document.getElementById("btnPago")
    .addEventListener("click", function () {

    const authenticated =
    document.getElementById("isAuthenticated").value;

    if (authenticated === "true") {

    document.getElementById("formPago").submit();

} else {

    const modal = new bootstrap.Modal(
    document.getElementById("loginModal")
    );

    modal.show();
}
});
