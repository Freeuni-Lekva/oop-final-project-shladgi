import { loadSessionValue } from "./getSessionInfo.js";


document.addEventListener("DOMContentLoaded", async ()=>{
    const navResponse = await fetch('nav.html');
    const navHtml = await navResponse.text();
    document.querySelector('nav').innerHTML = navHtml;

    const footerResponse = await fetch('footer.html');
    const footerHtml = await footerResponse.text();
    document.querySelector('footer').innerHTML = footerHtml;

    const userid = await loadSessionValue("userid");
    const userName = await loadSessionValue("username");
    const userType = await loadSessionValue("type");
    console.log(userType);
    const navLinks = document.getElementById("links");

    if (userType) {
        navLinks.innerHTML += `
            <li class="nav-item" id="user">
                <a class="nav-link" href="user.html">${userName}</a>
            </li>`;
        if (userType === "Admin") {
            navLinks.innerHTML += `
                <li class="nav-item" id="admin">
                    <a class="nav-link" href="admin.html">Admin Panel</a>
                </li>`;
        }
        navLinks.innerHTML+=
            `
                <li class="nav-item" id="logout">
                    <a class="nav-link" href="logout.html">Log Out</a>
                </li>`;
    } else {
        navLinks.innerHTML += getLi("login","Log In");
    }
});


function getLi(id,txt){
    return `<li class="nav-item" id=${id}>
            <a class="nav-link" href="/login">${txt}</a></li>`
}



