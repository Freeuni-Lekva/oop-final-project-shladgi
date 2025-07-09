/**
 * Creates a single choice question div with UI components.
 * @returns {HTMLDivElement} DOM element for question creation.
 */
export function getSingleChoiceDiv() {
    const container = document.createElement('div');
    container.className = 'single-choice-container';
    container.dataset.qtype = "SingleChoice";


    // Delete button (X button in top-right corner)
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => {
        if (confirm('Are you sure you want to delete this question?')) {
            container.remove();
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

    // Answers container
    const answersDiv = document.createElement('div');
    answersDiv.className = 'answers-div';
    container.appendChild(answersDiv);

    // Unique name for radio buttons
    const radioName = `correct-answer-${Math.random().toString(36).substr(2, 9)}`;

    // Adds an answer option row
    function addAnswerOption() {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'answer-option';

        const radio = document.createElement('input');
        radio.type = 'radio';
        radio.name = radioName;
        radio.required = answersDiv.children.length === 0;

        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = 'Answer text...';
        input.required = true;

        const deleteOptionBtn = document.createElement('button');
        deleteOptionBtn.textContent = 'Delete';
        deleteOptionBtn.className = 'delete-option-btn';
        deleteOptionBtn.onclick = () => {
            if (answersDiv.children.length > 2) {
                optionDiv.remove();
                if (radio.checked) {
                    const firstRadio = answersDiv.querySelector('input[type="radio"]');
                    if (firstRadio) firstRadio.checked = true;
                }
            } else {
                alert("At least 2 answer options are required.");
            }
        };

        optionDiv.appendChild(radio);
        optionDiv.appendChild(input);
        optionDiv.appendChild(deleteOptionBtn);
        answersDiv.appendChild(optionDiv);

        if (answersDiv.children.length === 1) {
            radio.checked = true;
        }
    }

    // Add initial options
    addAnswerOption();
    addAnswerOption();

    // Add option button
    const addBtn = document.createElement('button');
    addBtn.textContent = 'Add Option';
    addBtn.onclick = addAnswerOption;
    container.appendChild(addBtn);

    return container;
}

/**
 * Validates and submits a single question to the backend.
 * @param {HTMLElement} div - Question container element
 * @param {number} quizId - ID of the quiz
 * @returns {Promise<boolean>} true if successful
 */
export async function saveSingleChoiceQuestion(div, quizId) {
    const questionInput = div.querySelector('.question-input');
    const photoInput = div.querySelector('.photo-input');
    const pointsInput = div.querySelector('.points-input');
    const answersDiv = div.querySelector('.answers-div');
    const answerOptions = answersDiv.querySelectorAll('.answer-option');

    // Validate question
    const question = questionInput?.value?.trim();
    if (!question) {
        alert("Please enter a question text.");
        questionInput.focus();
        return {success:false, message:"Missing fields"};
    }

    // Validate points
    const points = parseInt(pointsInput?.value) || 1;
    if (points < 1) {
        alert("Points must be at least 1.");
        pointsInput.focus();
        return {success:false, message:"Points must be at least 1."};
    }

    // Validate options
    if (answerOptions.length < 2) {
        alert("At least 2 answer options are required.");
        return {success:false, message:"At least 2 answer options are required."};
    }

    const options = [];
    let correctIndex = -1;

    answerOptions.forEach((option, idx) => {
        const textInput = option.querySelector('input[type="text"]');
        const radioInput = option.querySelector('input[type="radio"]');

        const text = textInput?.value?.trim();
        const isChecked = radioInput?.checked;

        if (!text) {
            alert("Please fill in all answer options.");
            textInput.focus();
            return {success:false, message:"Please fill in all answer options."};
        }

        options.push(text);
        if (isChecked) correctIndex = idx;
    });

    if (correctIndex === -1) {
        alert("Please select the correct answer.");
        return {success:false, message:"Pleare select the correct answer"};
    }

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
                type: "SingleChoice",
                question: question,
                options: options,
                correctIndex: correctIndex,
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
        alert(`Error: ${error.message}`);
        return {success:false, message:error.message};
    }
}