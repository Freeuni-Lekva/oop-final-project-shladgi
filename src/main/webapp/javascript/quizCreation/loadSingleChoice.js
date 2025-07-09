import { createSingleChoiceDiv, saveSingleChoiceQuestion } from './singleChoiceDiv.js';

document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('question-area');
    if (!root) {
        console.error("Missing question-area element");
        return;
    }

    // Create single question form
    const questionDiv = createSingleChoiceDiv();
    root.appendChild(questionDiv);

    // Create submit button
    const submitBtn = document.createElement('button');
    submitBtn.textContent = 'Submit Question';
    submitBtn.className = 'submit-btn';

    submitBtn.onclick = async () => {
        const quizId = 1; // Replace with actual quiz ID
        const msg = await saveSingleChoiceQuestion(questionDiv, quizId);

        if (msg.success) {
            alert("Question saved successfully!");
            // Optional: Reset form or redirect
            // questionDiv.querySelector('form').reset();
        }
    };

    root.appendChild(submitBtn);
});