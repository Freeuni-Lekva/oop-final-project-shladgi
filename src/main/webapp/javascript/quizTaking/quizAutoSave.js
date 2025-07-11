// Utility: Load saved answers and populate UI
import {populateQuestionDiv} from "./populateQuestionDiv.js";
import {evalAnswer} from "./answerSaveWhileTaking.js";

let autosaveFunctionId = null;
let isRunning = false;

export async function loadSavedAnswers(quizResultId) {
    try {
        console.log("load");
        const response = await fetch('/quizResult', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `id=${quizResultId}`
        });

        const data = await response.json();

        if (!data.ok) {
            console.error('Error loading quiz result:', data.message);
            return;
        }

        const questionPairs = data.questionAnswerPairs;

        questionPairs.forEach(pair => {
            const questionData = pair.questionData;
            const userAnswer = pair.userAnswer || null;

            const div = document.querySelector(`[data-question-id='${questionData.id}']`);
            if (div && userAnswer) {
                populateQuestionDiv(div, questionData, userAnswer);
            }
        });
    } catch (err) {
        console.error('Failed to load saved answers:', err);
    }
}



// Utility: Auto-save answers every N minutes
export function startAutoSave(intervalSeconds = 30, quizResultId, userId) {
    const saveInterval = intervalSeconds  * 1000;

     autosaveFunctionId = setInterval(async () => {
         isRunning = true;
        const container = document.getElementById('all-questions-container');
        if (!container) return;

        const questionDivs = container.children;

        await fetch('/delete-user-answers-for-result', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `resultId=${encodeURIComponent(quizResultId)}`
        });

        for (const div of questionDivs) {
            const questionId = parseInt(div.getAttribute('data-question-id'));
            const type = div.getAttribute('data-question-type');

            if (!isNaN(questionId) && type) {
                evalAnswer(type, div, questionId, quizResultId, userId, true);
            }
        }
        isRunning = false;
    }, saveInterval);

}

export function stopSavingGracefully(){
    if(autosaveFunctionId == null) return;
    const checkAndStop = () => {
        if (!isRunning) {
            clearInterval(autosaveFunctionId);
            console.log("Interval stopped gracefully.");
        } else {
            console.log("Waiting for running task to finish...");
            setTimeout(checkAndStop, 200); // check again shortly
        }
    };

    checkAndStop();
}
