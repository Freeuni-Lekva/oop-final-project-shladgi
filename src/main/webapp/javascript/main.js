async function loadPage(page) {
    console.log(page);
    const p = await fetch('html/'+page +'.html').then(res => res.text());
    document.getElementById('currentPage').innerHTML = p;

}
loadPage('home');