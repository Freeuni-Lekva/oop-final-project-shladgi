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
        navLinks.innerHTML +=  getLi("notification" , "Notifications");
        navLinks.innerHTML +=  getLi("user" , userName);

        if (userType === "Admin") {
            navLinks.innerHTML += getLi("admin","Admin Panel");
        }
        navLinks.innerHTML+= '<form action="logout" method="post">\n' +
            '<button type="submit" class="btn btn-danger">Logout</button></form>';
    } else {
        navLinks.innerHTML += getLi("login","LogIn");
    }
});


function getLi(id,txt){
    return `<li class="nav-item" id=${id}>
            <a class="nav-link" href="/${id}">${txt}</a></li>`
}