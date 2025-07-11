// javascript/quiz/checkBeforeQuiz.js
document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const quizId = params.get("id");

    const practice = params.get("practice");

    if (!quizId || practice === null) {

        showStartLink("Missing parameters.");
        return;
    }


    fetch(`/checkBeforeQuiz?id=${encodeURIComponent(quizId)}&practice=${encodeURIComponent(practice)}`)

        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                showStartLink("Access denied: " + data.message, quizId);
            }
        })
        .catch(error => {
            console.error("Error checking quiz access:", error);
            showStartLink("Server error.");
        });
});

function showStartLink(message, quizId) {

}
