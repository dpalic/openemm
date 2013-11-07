function togglePreview(idElem2Hide, idElem2Show) {
    var imgPreview = document.getElementById(idElem2Hide);
    imgPreview.style.display = "none";
    Effect.toggle(idElem2Show, 'appear');
    return false;
}