function setupUserButtonListener() {
    const button = document.getElementById("userMenuItem");
    if (!button) return;

    button.addEventListener("click", (e) => {
        e.preventDefault();

        const friendsDiv = document.getElementById("friends");
        const statisticsDiv = document.getElementById("statistics");
        const userDiv = document.getElementById("user");

        if (friendsDiv) friendsDiv.style.display = "none";
        if (statisticsDiv) statisticsDiv.style.display = "none";
        if (userDiv) userDiv.style.display = "block";

        console.log("User section shown");
    });
}

document.addEventListener("DOMContentLoaded", setupUserButtonListener);
