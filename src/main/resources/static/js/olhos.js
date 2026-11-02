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
            var alcance = Math.min(caixa.width, caixa.height) * 0.42;
            var distancia = Math.hypot(dx, dy);
            var escala = distancia > alcance ? alcance / distancia : 1;
            var ox = Math.max(-caixa.width * 0.18, dx * escala);
            var oy = Math.max(-caixa.height * 0.10, dy * escala);
            olho.firstElementChild.style.transform =
                "translate(calc(-50% + " + ox + "px), calc(-50% + " + oy + "px))";
        });
    });
})();
