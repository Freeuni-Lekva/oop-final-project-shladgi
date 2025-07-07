document.addEventListener("DOMContentLoaded", function () {
    const challengeForm = document.querySelector('form[action="challenge"]');

    if (!challengeForm) return;

    // Create and insert status div at the top of the form
    const statusDiv = document.createElement("div");
    statusDiv.id = "challengeStatus";
    statusDiv.style.marginBottom = "1rem";
    statusDiv.style.fontWeight = "bold";
    challengeForm.insertBefore(statusDiv, challengeForm.firstChild);

    challengeForm.addEventListener("submit", function (e) {
        e.preventDefault();

        const friendUsername = document.getElementById("challengeFriend").value.trim();
        const quizTitle = document.getElementById("quizTitle").value.trim();

   /*     if (friendUsername.length < 3 || quizTitle.length < 3) {
            statusDiv.textContent = "❌ Please enter valid friend username and quiz title.";
            statusDiv.style.color = "red";
            return;
        }*/

        fetch("challenge", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                FriendUsername: friendUsername,
                QuizTitle: quizTitle
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    statusDiv.textContent = "✅ Challenge sent successfully!";
                    statusDiv.style.color = "green";
                    challengeForm.reset();
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
