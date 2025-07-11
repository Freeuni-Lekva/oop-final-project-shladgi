/**
 * Creates a fill-in-the-blanks question div with UI components.
 * @returns {HTMLDivElement} DOM element for fill-in-the-blanks question creation.
 */
export function getFillInBlanksDiv() {
    const container = document.createElement('div');
    container.className = 'fill-in-blanks-container';
    container.dataset.qtype = "FillInBlanks";

    // Delete button (X button in top-right corner)
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => container.remove();
    container.appendChild(deleteBtn);

    // Question input (textarea for multiline)
    const questionInput = document.createElement('textarea');
    questionInput.placeholder = 'Type the question here. Place cursor or select text and click "Add Blank" to insert blanks.';
    questionInput.className = 'question-input';
    questionInput.rows = 4;
    questionInput.required = true;
    container.appendChild(questionInput);

    // Add Blank Button
    const addBlankBtn = document.createElement('button');
    addBlankBtn.textContent = 'Add Blank';
    addBlankBtn.className = 'add-blank-btn';
    container.appendChild(addBlankBtn);

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
    exactMatchCheckbox.id = `fib-exact-match-${Math.random().toString(36).substr(2, 9)}`;
    exactMatchCheckbox.className = 'exact-match-checkbox';
    exactMatchCheckbox.checked = true;
    const exactMatchLabel = document.createElement('label');
    exactMatchLabel.htmlFor = exactMatchCheckbox.id;
    exactMatchLabel.textContent = 'Require exact match for answers (case and space sensitive)';
    exactMatchDiv.appendChild(exactMatchCheckbox);
    exactMatchDiv.appendChild(exactMatchLabel);
    container.appendChild(exactMatchDiv);

    // Answers container (for all blanks)
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';
    container.appendChild(answersContainer);

    // Function to add answer inputs
    function addAnswerInputs() {
        const answerSection = document.createElement('div');
        answerSection.className = 'answer-section';

        const header = document.createElement('div');
        header.className = 'answer-header';
        header.textContent = 'Correct Answers:';
        answerSection.appendChild(header);

        const answersList = document.createElement('div');
        answersList.className = 'answers-list';
        answerSection.appendChild(answersList);

        // Add first answer input
        addAnswerInput(answersList);

        // Add button to add more answers
        const addBtn = document.createElement('button');
        addBtn.textContent = '+ Add Another Answer';
        addBtn.className = 'add-answer-btn';
        addBtn.onclick = () => addAnswerInput(answersList);
        answerSection.appendChild(addBtn);

        // Delete button for this blank's answers
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Delete These Answers';
        deleteBtn.className = 'delete-answers-btn';
        deleteBtn.onclick = () => answerSection.remove();
        answerSection.appendChild(deleteBtn);

        answersContainer.appendChild(answerSection);
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

    // Handle adding blanks
    addBlankBtn.onclick = () => {
        const startPos = questionInput.selectionStart;
        const endPos = questionInput.selectionEnd;
        const selectedText = questionInput.value.substring(startPos, endPos);

        // Insert blank (_____) at selection (replacing any selected text)
        const newText = questionInput.value.substring(0, startPos) +
            '_____' +
            questionInput.value.substring(endPos);

        questionInput.value = newText;

        // Add answer inputs for this blank
        addAnswerInputs();

        // Place cursor after the new blank
        questionInput.selectionStart = questionInput.selectionEnd = startPos + 5;
        questionInput.focus();
    };

    return container;
}

/**
 * Validates and submits a fill-in-the-blanks question to the backend.
 * @param {HTMLElement} div - Question container element
 * @param {number} quizId - ID of the quiz
 * @returns {Promise<Object>} Object with success status and message
 */
export async function saveFillInBlanksQuestion(div, quizId) {
    const questionInput = div.querySelector('.question-input');
    const photoInput = div.querySelector('.photo-input');
    const pointsInput = div.querySelector('.points-input');
    const answersContainer = div.querySelector('.answers-container');
    const exactMatchCheckbox = div.querySelector('.exact-match-checkbox');

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

    // Count blanks and answer sections
    const blankCount = (questionInput.value.match(/_____/g) || []).length;
    const answerSections = answersContainer.querySelectorAll('.answer-section').length;

    if (blankCount === 0) {
        alert('Please add at least one blank to the question');
        return {success: false, message: 'No blanks in question'};
    }

    if (blankCount !== answerSections) {
        alert(`Number of blanks (${blankCount}) doesn't match number of answer sections (${answerSections})`);
        return {success: false, message: 'Blank/answer count mismatch'};
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
        alert('Please provide at least one answer for each blank and ensure all answer fields are filled');
        return {success: false, message: 'Missing or invalid answers'};
    }

    // Find blank positions
    const blankIndices = [];
    let pos = questionInput.value.indexOf('_____');
    while (pos !== -1) {
        blankIndices.push(pos);
        pos = questionInput.value.indexOf('_____', pos + 1);
    }

    try {
        const response = await fetch("/saveQuestion", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                type: "FillInBlanks",
                question: questionInput.value,
                blankIdx: blankIndices,
                correctAnswers: correctAnswers,
                exactMatch: exactMatchCheckbox.checked,
                quizId: quizId,
                photoUrl: photoInput.value.trim() || null,
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
