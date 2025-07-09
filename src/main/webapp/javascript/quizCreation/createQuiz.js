import {addQuestion} from "./questionAdd.js"
document.addEventListener("DOMContentLoaded", () => {
    const addButton = document.getElementById("addQuestin");
    addButton.addEventListener('click', addQuestion);



    const form = document.getElementById("quizForm");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = new FormData(form);

        // Convert checkboxes to "0" or "1"
        const checkboxes = ["random", "singlepage", "immediatecorrection", "practicemode"];
        checkboxes.forEach(name => {
            formData.set(name, formData.get(name) === "on" ? "1" : "0");
        });

        // Handle time limit conversion
        const hours = parseInt(formData.get("timelimithours") || "0", 10);
        const minutes = parseInt(formData.get("timelimitminutes") || "0", 10);
        const totalSeconds = (hours * 60 + minutes) * 60;
        formData.set("timelimit", isNaN(totalSeconds) || totalSeconds < 0 ? "-1" : totalSeconds.toString());

        // Optionally remove the hour/minute inputs from formData if not needed by backend
        formData.delete("timelimithours");
        formData.delete("timelimitminutes");

        // Send to servlet
        fetch("/createQuiz", {
            method: "POST",
            body: new URLSearchParams(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = `/startQuiz?id=${data.quizid}`; // Change as needed
                } else {
                    const error = document.getElementById("errorText");
                    error.textContent = data.message;
                }
            })
            .catch(error => {
                console.error("Fetch error:", error);
                alert("Server error occurred.");
            });
    });
});
