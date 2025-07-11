/**
 * Creates a multi-choice question div with UI components.
 * @returns {HTMLDivElement} DOM element for multi-choice question creation.
 */
export function getMultiChoiceDiv() {
    const container = document.createElement('div');
    container.className = 'multi-choice-container';
    container.dataset.qtype = "MultiChoice";

    // Delete button (X button in top-right corner)
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => {
        if (confirm('Are you sure you want to delete this question?')) {
            container.remove();
            // If the last question is deleted, you might want to adjust UI
            if (document.querySelectorAll('.multi-choice-container').length === 0) {
                // Optionally add a placeholder or re-enable "add question" if all deleted
                //console.log("All multi-choice questions removed.");
            }
        }
    };
    container.appendChild(deleteBtn);

    // Question input
    const questionInput = document.createElement('input');
    questionInput.type = 'text';
    questionInput.placeholder = 'Enter question...';
    questionInput.className = 'question-input';
    questionInput.required = true;
    container.appendChild(questionInput);

    // Photo link input
    const photoInput = document.createElement('input');
    photoInput.type = 'url';
    photoInput.placeholder = 'Photo URL (optional)...';
    photoInput.className = 'photo-input';
    container.appendChild(photoInput);

    // Points input
    const pointsInput = document.createElement('input');
    pointsInput.type = 'number';
    pointsInput.placeholder = 'Points';
    pointsInput.className = 'points-input';
    pointsInput.min = '1';
    pointsInput.value = '1';
    pointsInput.required = true;
    container.appendChild(pointsInput);

    // Exact match checkbox
    const exactMatchDiv = document.createElement('div');
    exactMatchDiv.className = 'exact-match-option';
    const exactMatchCheckbox = document.createElement('input');
    exactMatchCheckbox.type = 'checkbox';
    exactMatchCheckbox.id = `exact-match-${Math.random().toString(36).substr(2, 9)}`;
    exactMatchCheckbox.className = 'exact-match-checkbox';
    const exactMatchLabel = document.createElement('label');
    exactMatchLabel.htmlFor = exactMatchCheckbox.id;
    exactMatchLabel.textContent = 'Require exact match for full points';
    exactMatchDiv.appendChild(exactMatchCheckbox);
    exactMatchDiv.appendChild(exactMatchLabel);
    container.appendChild(exactMatchDiv);


    // Answers container
    const answersDiv = document.createElement('div');
    answersDiv.className = 'answers-div';
    container.appendChild(answersDiv);

    // Adds an answer option row
    function addAnswerOption() {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'answer-option';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.className = 'correct-answer-checkbox';

        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = 'Answer text...';
        input.required = true;

        const deleteOptionBtn = document.createElement('button');
        deleteOptionBtn.textContent = 'Delete Option'; // Changed text for clarity
        deleteOptionBtn.className = 'delete-option-btn';
        deleteOptionBtn.onclick = () => {
            if (answersDiv.children.length > 2) { // At least 2 options required
                optionDiv.remove();
            } else {
                alert("At least 2 answer options are required for a multi-choice question.");
            }
        };

        optionDiv.appendChild(checkbox);
        optionDiv.appendChild(input);
        optionDiv.appendChild(deleteOptionBtn);
        answersDiv.appendChild(optionDiv);
    }

    // Add initial options
    addAnswerOption();
    addAnswerOption();

    // Add option button for individual question
    const addOptionBtn = document.createElement('button'); // Renamed to avoid conflict
    addOptionBtn.textContent = 'Add Answer Option';
    addOptionBtn.onclick = addAnswerOption;
    container.appendChild(addOptionBtn);

    return container;
}

/**
 * Validates and submits a multi-choice question to the backend.
 * @param {HTMLElement} div - Question container element
 * @param {number} quizId - ID of the quiz
 * @returns {Promise<Object>} Object with success status and message
 */
export async function saveMultiChoiceQuestion(div, quizId) {
    const questionInput = div.querySelector('.question-input');
    const photoInput = div.querySelector('.photo-input');
    const pointsInput = div.querySelector('.points-input');
    const answersDiv = div.querySelector('.answers-div');
    const answerOptions = answersDiv.querySelectorAll('.answer-option');
    const exactMatchCheckbox = div.querySelector('.exact-match-checkbox');

    // Validate question
    const question = questionInput?.value?.trim();
    if (!question) {
        //alert("Please enter a question text.");
        questionInput.focus();
        return {success: false, message: "Missing question text"};
    }

    // Validate points
    const points = parseInt(pointsInput?.value) || 1;
    if (points < 1) {
        //alert("Points must be at least 1.");
        pointsInput.focus();
        return {success: false, message: "Points must be at least 1."};
    }

    // Validate options
    if (answerOptions.length < 2) {
        //alert("At least 2 answer options are required.");
        return {success: false, message: "At least 2 answer options are required."};
    }

    const options = [];
    const correctIndexes = [];

    let allOptionsFilled = true;
    answerOptions.forEach((option, idx) => {
        const textInput = option.querySelector('input[type="text"]');
        const checkboxInput = option.querySelector('input[type="checkbox"]');

        const text = textInput?.value?.trim();
        const isChecked = checkboxInput?.checked;

        if (!text) {
            allOptionsFilled = false;
            textInput.focus();
            return;
        }

        options.push(text);
        if (isChecked) {
            correctIndexes.push(idx);
        }
    });

    if (!allOptionsFilled) {
        //alert("Please fill in all answer options.");
        return {success: false, message: "Please fill in all answer options."};
    }

    if (correctIndexes.length === 0) {
        //alert("Please select at least one correct answer.");
        return {success: false, message: "Please select at least one correct answer."};
    }

    const exactMatch = exactMatchCheckbox.checked;

    // Get photo URL
    const photoUrl = photoInput?.value?.trim() || null;

    try {
        const response = await fetch("/saveQuestion", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                type: "MultiChoice",
                question: question,
                options: options,
                correctIndexes: correctIndexes,
                exactMatch: exactMatch,
                quizId: quizId,
                photoUrl: photoUrl,
                points: points
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Server error');
        }

        const result = await response.json();
        return result;
    } catch (error) {
        console.error('Submission error:', error);
        //alert(`Error: ${error.message}`);
        return {success: false, message: error.message};
    }
}

//window.saveMultiChoiceQuestion = saveMultiChoiceQuestion;

