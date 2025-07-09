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

    const navLinks = document.getElementById("nav-links");

    if (userType) {
        document.getElementById("nav-logo").href = "/home";
        navLinks.innerHTML +=  getLi("notifications" , "Notifications");
        navLinks.innerHTML +=  getLi("user" , userName);

        if (userType === "Admin") {
            navLinks.innerHTML += getLi("admin","Admin Panel");
        }
        navLinks.innerHTML+= '<form action="logout" method="post">\n' +
            '<button type="submit" class="btn btn-danger">Logout</button></form>';
        document.getElementById("nav-notifications").querySelector("a").innerHTML +=
            '<span id="notificationDot" style="display:none; color: red;">●</span>';
        checkNotifications(userid);
    } else {
        navLinks.innerHTML += getLi("login","LogIn");
    }
});


function getLi(id,txt){
    return `<li class="nav-item" id=nav-${id}>
            <a class="nav-link" href="/${id}">${txt}</a></li>`
}

function checkNotifications(userId){
    fetch("notifications",
        {method: "POST",
            headers :{
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body : new URLSearchParams({
                act: "hasUnseen",
                id : userId
            })
        }
    ).then(res => res.json())
        .then(data => {
            if (data.success && data.hasUnseen) {
                document.getElementById("notificationDot").style.display = "inline";
            }else{
                document.getElementById("notificationDot").style.display = "none";
            }

        }).catch(err => {console.log(err)}
        );
}