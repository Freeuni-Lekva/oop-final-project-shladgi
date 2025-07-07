document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const username = params.get("username");


    const menuItem = document.getElementById("userMenuItem");
    if (menuItem) {
        menuItem.textContent = username;
    }
    document.getElementById("friends").style.display = "none";
    document.getElementById("statistics").style.display = "none";
    document.getElementById("user").style.display = "block";
});
