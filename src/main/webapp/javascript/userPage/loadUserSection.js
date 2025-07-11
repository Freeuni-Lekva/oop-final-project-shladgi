function setupUserButtonListener() {
    const button = document.getElementById("userMenuItem");
    if (!button) return;

    button.addEventListener("click", (e) => {
        e.preventDefault();

        document.getElementById("user").style.display = "block";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friends-container").style.display = "none";
        document.getElementById("friend-requests-container").style.display = "none"

        document.getElementById("userMenuItem").classList.add("active");
        document.getElementById("statisticsMenuItem").classList.remove("active");
        document.getElementById("friendRequestMenuItem").classList.remove("active");
        document.getElementById("friendsMenuItem").classList.remove("active");
    });
}

document.addEventListener("DOMContentLoaded", setupUserButtonListener);
