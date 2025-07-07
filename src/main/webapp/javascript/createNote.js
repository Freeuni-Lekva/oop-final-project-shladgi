document.addEventListener("DOMContentLoaded", function () {
    // edzebs formas sadac action = createNote.
    const noteForm = document.querySelector('form[action="createNote"]');

    // Create the status message div, yuts aketebs
    const statusDiv = document.createElement("div");
    // es css unda iyos
    statusDiv.id = "statusMessage";
    statusDiv.style.marginBottom = "1rem";
    statusDiv.style.fontWeight = "bold";

    // Insert statusDiv at the top of the form (before the first input)
    noteForm.insertBefore(statusDiv, noteForm.firstChild);

    // Form submit handler
    noteForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const friendUsername = document.getElementById("noteFriend").value.trim();
        const message = document.getElementById("Message").value.trim();

   /*     // es ar unda eweros.
        if (friendUsername.length < 3 || message.length < 5) {
            statusDiv.textContent = "❌ Please enter a valid username and a longer message.";
            statusDiv.style.color = "red";
            return;
        }*/

        // Send the note via fetch (AJAX)
        fetch("createNote", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                FriendUsername: friendUsername,
                Message: message
            })
        })
            .then(response => response.json())// aq ukve dabrunebulia pasuxi da JSON-ad vkastavt
            .then(data => { // davarqvit data rac daabruna.
                if (data.success) {
                    statusDiv.textContent = "✅ Note sent successfully!";
                    statusDiv.style.color = "green";
                    noteForm.reset();
                } else {
                    statusDiv.textContent = "❌ " + data.message;
                    statusDiv.style.color = "red";
                }
            })
            .catch(error => {
                console.error("Error:", error);
                statusDiv.textContent = "❌ Server error.";
                statusDiv.style.color = "red";
            });
    });
});
