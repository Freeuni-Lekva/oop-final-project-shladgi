import {getQuestionWhileTaking} from "./questionWhileTaking.js";

document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const quizId = params.get("id");

    fetch("/quiz", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `id=${encodeURIComponent(quizId)}`
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                console.error("Error loading quiz" + data.message);
                return;
            }


            const questions = data.questions;

            // Use quiz info
            document.body.insertAdjacentHTML("afterbegin", `
            <h2>${data.title}</h2>
            <p>${data.description}</p>
        `);

            // Display each question
            questions.forEach((q, index) => {
                const questionDiv = getQuestionWhileTaking(q);

                questionDiv.classList.add("question");

                document.body.appendChild(questionDiv);
            });


        })
        .catch(error => {
            console.error("Fetch failed", error);
        });
});
