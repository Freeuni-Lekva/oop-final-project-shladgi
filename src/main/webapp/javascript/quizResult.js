import {loadSessionValue} from "./getSessionInfo.js";
import {fetchQuizResultData} from "./userQuizResultsDiv.js"
import {getUserQuizResultsDiv} from "./userQuizResultsDiv.js";
import {getTopPerformers} from "./getTopPerformers.js";

import {getQuestionWhileTaking} from "./quizTaking/questionWhileTaking.js";
import {populateQuestionDiv} from "./quizTaking/populateQuestionDiv.js";
import {highlightQuestionDiv} from "./quizTaking/hihglightQuestion.js";
import {evalAnswer} from "./quizTaking/answerSaveWhileTaking.js";

const quizResultId = parseInt(new URLSearchParams(window.location.search).get("id"));

fetchQuizResultData(quizResultId)
    .then(async data => {
        console.log(data);
            if(!data.ok || data.success === false) {
                    document.getElementById("resultHeader").textContent = data.message || "Error loading result";
                    document.getElementById("result-container").remove();
                    return;
            }

            // Display basic info
            document.getElementById("title").textContent = data.title;
            document.getElementById("totalscore").textContent = data.totalscore;
            document.getElementById("timetaken").textContent = formatTime(data.timetaken);
            document.getElementById("creationdate").textContent = new Date(data.creationdate).toLocaleString();

            // Display questions with highlighting
            if (data.questionAnswerPairs && data.questionAnswerPairs.length > 0) {
                    const questionsSection = document.createElement("section");
                    questionsSection.className = "mt-4";
                    questionsSection.innerHTML = "<h3>Your Answers</h3>";

                    const questionsContainer = document.createElement("div");
                    questionsContainer.className = "questions-container";

                    for (const pair of data.questionAnswerPairs) {
                            if (!pair.userAnswer || !pair.questionData) {
                                    console.warn("Skipping question - missing userAnswer or questionData", pair);
                                    continue;
                            }

                            try {
                                    // 1. Get empty question div
                                    const questionDiv = getQuestionWhileTaking(pair.questionData);

                                    // 2.
                                    populateQuestionDiv(questionDiv, pair.questionData, pair.userAnswer);

                                    // 3.
                                    const evaluationResult = await evalAnswer(
                                        pair.questionData.type,
                                        questionDiv,
                                        pair.questionData.id,
                                        quizResultId,
                                        data.userid,
                                        false
                                    );

                                    // 4. Highlight corrections
                                    highlightQuestionDiv( questionDiv, evaluationResult, pair.questionData);

                                    questionsContainer.appendChild(questionDiv);
                            } catch (error) {
                                    console.error("Error processing question:", error, pair);
                            }
                    }

                    questionsSection.appendChild(questionsContainer);
                    document.getElementById("result-container").appendChild(questionsSection);
            }

            // Load previous attempts
            const userId = await loadSessionValue("userid");
            if(!userId) {
                    const previousResults = document.getElementById("quiz-result-list");
                    previousResults.innerHTML = "<p>Please log in to see previous attempts</p>";
            } else {
                    const previousResults = document.getElementById("quiz-result-list");
                    const attemptsDiv = await getUserQuizResultsDiv(userId, data.quizid, quizResultId);
                    previousResults.replaceWith(attemptsDiv);
            }

            // Load top performers
            const topPerformersDiv = await getTopPerformers(data.quizid, null, 5);
            document.getElementById("top-performers-list").appendChild(topPerformersDiv);
    })
    .catch(error => {
            console.error("Error loading quiz result:", error);
            document.getElementById("resultHeader").textContent = "Error loading quiz result";
    });

function formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}m ${secs}s`;
}