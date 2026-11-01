(function () {
    var olhos = document.querySelectorAll(".olho");
    if (!olhos.length || window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        return;
    }

    document.addEventListener("mousemove", function (evento) {
        olhos.forEach(function (olho) {
            var caixa = olho.getBoundingClientRect();
            var dx = evento.clientX - (caixa.left + caixa.width / 2);
            var dy = evento.clientY - (caixa.top + caixa.height / 2);
            var angulo = Math.atan2(dy, dx);
            var alcance = Math.min(caixa.width, caixa.height) * 0.42;
            var distancia = Math.min(Math.hypot(dx, dy), alcance);
            olho.firstElementChild.style.transform =
                "translate(calc(-50% + " + (Math.cos(angulo) * distancia) + "px), calc(-50% + " +
                (Math.sin(angulo) * distancia) + "px))";
        });
    });
})();
