document.addEventListener('DOMContentLoaded', () => {
    fetch('nav.html')
        .then(res => res.text())
        .then(data => {
            document.querySelector('nav').innerHTML = data;
        });
    fetch('footer.html')
        .then(res => res.text())
        .then(data => {
            document.querySelector('footer').innerHTML = data;
        });


});
