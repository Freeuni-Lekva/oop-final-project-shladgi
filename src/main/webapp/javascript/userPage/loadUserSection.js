function setupUserButtonListener() {
    const button = document.getElementById("userMenuItem");
    if (!button) return;

    button.addEventListener("click", (e) => {
        e.preventDefault();

        document.getElementById("user").style.display = "block";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friends-container").style.display = "none";
        document.getElementById("friend-requests-container").style.display = "none"

        const statBtn = document.getElementById("statisticsMenuItem");
        const frnReqBtn = document.getElementById("friendRequestMenuItem");
        const frnBtn = document.getElementById("friendsMenuItem");

        if(button) button.classList.add("active");
        if(statBtn) statBtn.classList.remove("active");
        if(frnReqBtn) frnReqBtn.classList.remove("active");
        if(frnBtn) frnBtn.classList.remove("active");

    });
}

document.addEventListener("DOMContentLoaded", setupUserButtonListener);
