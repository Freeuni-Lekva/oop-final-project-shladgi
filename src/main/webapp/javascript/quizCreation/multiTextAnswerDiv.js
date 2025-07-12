/**
 * Creates a multi-text-answer question div with UI components.
 * @returns {HTMLDivElement} DOM element for multi-text-answer question creation.
 */
//gio
export function getMultiTextAnswerDiv() {
    const container = document.createElement('div');
    container.className = 'multi-text-answer-container';
    container.dataset.qtype = "MultiTextAnswer";

    // Delete button (X button in top-right corner)
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => container.remove();
    container.appendChild(deleteBtn);

    // Question input (textarea for multiline)
    const questionInput = document.createElement('textarea');
    questionInput.placeholder = 'Type the question here...';
    questionInput.className = 'question-input';
    questionInput.rows = 4;
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
    exactMatchCheckbox.id = `mta-exact-match-${Math.random().toString(36).substr(2, 9)}`;
    exactMatchCheckbox.className = 'exact-match-checkbox';
    exactMatchCheckbox.checked = true;
    const exactMatchLabel = document.createElement('label');
    exactMatchLabel.htmlFor = exactMatchCheckbox.id;
    exactMatchLabel.textContent = 'Require exact match for answers (case and space sensitive)';
    exactMatchDiv.appendChild(exactMatchCheckbox);
    exactMatchDiv.appendChild(exactMatchLabel);
    container.appendChild(exactMatchDiv);

    // Ordered checkbox
    const orderedDiv = document.createElement('div');
    orderedDiv.className = 'ordered-option';
    const orderedCheckbox = document.createElement('input');
    orderedCheckbox.type = 'checkbox';
    orderedCheckbox.id = `mta-ordered-${Math.random().toString(36).substr(2, 9)}`;
    orderedCheckbox.className = 'ordered-checkbox';
    const orderedLabel = document.createElement('label');
    orderedLabel.htmlFor = orderedCheckbox.id;
    orderedLabel.textContent = 'Answers must be in correct order';
    orderedDiv.appendChild(orderedCheckbox);
    orderedDiv.appendChild(orderedLabel);
    container.appendChild(orderedDiv);

    // Answers container
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';
    container.appendChild(answersContainer);

    // Add initial answer section
    addAnswerSection(answersContainer);

    // Add Answer Section Button
    const addAnswerSectionBtn = document.createElement('button');
    addAnswerSectionBtn.textContent = '+ Add Answer Section';
    addAnswerSectionBtn.className = 'add-answer-section-btn';
    addAnswerSectionBtn.onclick = () => addAnswerSection(answersContainer);
    container.appendChild(addAnswerSectionBtn);

    return container;
}

function addAnswerSection(container) {
    const answerSection = document.createElement('div');
    answerSection.className = 'answer-section';

    const header = document.createElement('div');
    header.className = 'answer-header';
    header.textContent = `Correct Answers (Section ${container.children.length + 1})`;
    answerSection.appendChild(header);

    const answersList = document.createElement('div');
    answersList.className = 'answers-list';
    answerSection.appendChild(answersList);

    // Add first answer input
    addAnswerInput(answersList);

    // Add button to add more answers
    const addBtn = document.createElement('button');
    addBtn.textContent = '+ Add Possible Answer';
    addBtn.className = 'add-answer-btn';
    addBtn.onclick = () => addAnswerInput(answersList);
    answerSection.appendChild(addBtn);

    // Delete button for this answer section
    const deleteBtn = document.createElement('button');
    deleteBtn.textContent = 'Delete This Section';
    deleteBtn.className = 'delete-section-btn';
    deleteBtn.onclick = () => {
        if (container.children.length > 1) {
            answerSection.remove();
        } else {
            alert('At least one answer section is required');
        }
    };
    answerSection.appendChild(deleteBtn);

    container.appendChild(answerSection);
}

function addAnswerInput(container) {
    const answerGroup = document.createElement('div');
    answerGroup.className = 'answer-group';

    const input = document.createElement('input');
    input.type = 'text';
    input.placeholder = 'Enter correct answer...';
    input.required = true;
    answerGroup.appendChild(input);

    const removeBtn = document.createElement('button');
    removeBtn.textContent = '×';
    removeBtn.className = 'remove-answer-btn';
    removeBtn.onclick = () => {
        if (container.children.length > 1) {
            answerGroup.remove();
        } else {
            alert('At least one answer is required');
        }
    };
    answerGroup.appendChild(removeBtn);

    container.appendChild(answerGroup);
}

/**
 * Validates and submits a multi-text-answer question to the backend.
 * @param {HTMLElement} div - Question container element
 * @param {number} quizId - ID of the quiz
 * @returns {Promise<Object>} Object with success status and message
 */
export async function saveMultiTextAnswerQuestion(div, quizId) {
    const questionInput = div.querySelector('.question-input');
    const photoInput = div.querySelector('.photo-input');
    const pointsInput = div.querySelector('.points-input');
    const answersContainer = div.querySelector('.answers-container');
    const exactMatchCheckbox = div.querySelector('.exact-match-checkbox');
    const orderedCheckbox = div.querySelector('.ordered-checkbox');

    // Basic validation
    if (!questionInput.value.trim()) {
        alert('Please enter the question text');
        return {success: false, message: 'Missing question text'};
    }

    const points = parseInt(pointsInput.value) || 1;
    if (points < 1) {
        alert('Points must be at least 1');
        return {success: false, message: 'Invalid points value'};
    }

    // Collect all correct answers
    const correctAnswers = [];
    let allAnswersValid = true;

    answersContainer.querySelectorAll('.answer-section').forEach(section => {
        const answers = [];
        section.querySelectorAll('.answer-group input').forEach(input => {
            const answer = input.value.trim();
            if (!answer) {
                input.focus();
                allAnswersValid = false;
            }
            answers.push(answer);
        });

        if (answers.length === 0) {
            allAnswersValid = false;
        } else {
            correctAnswers.push(answers);
        }
    });

    if (!allAnswersValid) {
        alert('Please provide at least one answer for each section and ensure all answer fields are filled');
        return {success: false, message: 'Missing or invalid answers'};
    }

    // Get photo URL
    const photoUrl = photoInput.value.trim() || null;
    const exactMatch = exactMatchCheckbox.checked;
    const ordered = orderedCheckbox.checked;

    try {
        const response = await fetch("/saveQuestion", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                type: "MultiTextAnswer",
                question: questionInput.value,
                correctAnswers: correctAnswers,
                exactMatch: exactMatch,
                ordered: ordered,
                quizId: quizId,
                photoUrl: photoUrl,
                points: points
            })
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to save question');
        }

        return await response.json();
    } catch (error) {
        console.error('Error saving question:', error);
        alert(`Error: ${error.message}`);
        return {success: false, message: error.message};
    }
}
