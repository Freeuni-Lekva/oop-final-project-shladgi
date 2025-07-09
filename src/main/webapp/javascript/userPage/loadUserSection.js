function setupUserButtonListener() {
    const button = document.getElementById("userMenuItem");
    if (!button) return;

    button.addEventListener("click", (e) => {
        e.preventDefault();

        document.getElementById("user").style.display = "block";
        document.getElementById("statistics").style.display = "none";
        document.getElementById("friends-container").style.display = "none";
        document.getElementById("friend-requests-container").style.display = "none"
        console.log("User section shown");
    });
}

document.addEventListener("DOMContentLoaded", setupUserButtonListener);
