function setupUserButtonListener() {
    const button = document.getElementById("userMenuItem");
    if (!button) return;

    button.addEventListener("click", (e) => {
        e.preventDefault();

        document.getElementById("friends").style.display = "none";
        document.getElementById("rating").style.display = "none";

        const userDiv = document.getElementById("user");
        userDiv.style.display = "block";

        console.log("User section shown");
    });
}

document.addEventListener("DOMContentLoaded", setupUserButtonListener);
