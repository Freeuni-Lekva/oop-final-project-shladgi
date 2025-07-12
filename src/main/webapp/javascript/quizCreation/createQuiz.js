import {addQuestion} from "./questionAdd.js"
import {saveQuestion} from "./qustionSaving.js";
import {deleteQuiz} from "./quizDeletion.js";
import {checkAchievements} from "../achievement.js";

document.addEventListener("DOMContentLoaded", () => {
    const addButton = document.getElementById("addQuestion");
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


        const pointInputs = document.querySelectorAll('.points-input');

        let totalPoints = 0;

        pointInputs.forEach(input => {
            totalPoints += parseInt(input.value, 10);
        });

        formData.set("totalquestions", pointInputs.length.toString());
        formData.set("totalpoints", totalPoints.toString());
        // Send to servlet
        fetch("/createQuiz", {
            method: "POST",
            body: new URLSearchParams(formData)
        })
            .then(response => response.json())
            .then(async data => {

                if (data.success) {
                    const quizid = data.quizid;

                    const container = document.getElementById("questionContainer");
                    const questionDivs = container.children;
                    let failedSaving = false;
                    for (const div of questionDivs) {
                        const type = div.dataset.qtype;
                        if (type) {
                            const answer = await saveQuestion(div, quizid, type);
                            if(answer.success === false){
                                const error = document.getElementById("errorText");
                                error.textContent = answer.message;
                                deleteQuiz(quizid);
                                failedSaving = true;
                                break;
                            }


                        } else {
                            console.warn("Missing data-question-type on a question div.");
                        }
                    }
                    await checkAchievements(data.userid, "create")
                    // Then redirect (optional: only after all saveQuestion calls succeed)
                    if(!failedSaving) window.location.href = `/startQuiz?id=${quizid}`;
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
