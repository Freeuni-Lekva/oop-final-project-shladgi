import {getQuizCreationDiv} from "./questionCreationDiv.js"
export function addQuestion() {
    // Get selected question type
    const select = document.getElementById('choices');
    const questionType = select.value;

    // Call your existing function that returns a div element for the question
    let questionDiv = getQuizCreationDiv(questionType);
    // Append the question div to questionContainer
    const container = document.getElementById('questionContainer');
    container.appendChild(questionDiv);
}
