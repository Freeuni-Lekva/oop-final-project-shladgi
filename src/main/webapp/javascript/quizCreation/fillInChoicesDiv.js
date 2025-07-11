/**
 * Creates a fill-in-choices question div with UI components.
 * @returns {HTMLDivElement} DOM element for fill-in-choices question creation.
 */
let fillChoiceGlobalId = 0;

export function getFillInChoicesDiv() {
    const container = document.createElement('div');
    container.className = 'fill-in-choices-container';
    container.dataset.qtype = "FillChoices";


    const containerId = `fill-${fillChoiceGlobalId++}`;  // Unique ID for this question

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => container.remove();
    container.appendChild(deleteBtn);

    const questionInput = document.createElement('textarea');
    questionInput.placeholder = 'Type the question here. Select text and click "Add Choice Blank".';
    questionInput.className = 'question-input';
    questionInput.rows = 4;
    questionInput.required = true;
    container.appendChild(questionInput);

    const addBlankBtn = document.createElement('button');
    addBlankBtn.textContent = 'Add Choice Blank';
    addBlankBtn.className = 'add-blank-btn';
    container.appendChild(addBlankBtn);

    const photoInput = document.createElement('input');
    photoInput.type = 'url';
    photoInput.placeholder = 'Photo URL (optional)...';
    photoInput.className = 'photo-input';
    container.appendChild(photoInput);

    const pointsInput = document.createElement('input');
    pointsInput.type = 'number';
    pointsInput.placeholder = 'Points';
    pointsInput.className = 'points-input';
    pointsInput.min = '1';
    pointsInput.value = '1';
    pointsInput.required = true;
    container.appendChild(pointsInput);

    const exactMatchDiv = document.createElement('div');
    exactMatchDiv.className = 'exact-match-option';
    const exactMatchCheckbox = document.createElement('input');
    exactMatchCheckbox.type = 'checkbox';
    exactMatchCheckbox.className = 'exact-match-checkbox';
    exactMatchCheckbox.checked = true;
    const exactMatchLabel = document.createElement('label');
    exactMatchLabel.textContent = 'Require exact match';
    exactMatchDiv.appendChild(exactMatchCheckbox);
    exactMatchDiv.appendChild(exactMatchLabel);
    container.appendChild(exactMatchDiv);

    const choicesContainer = document.createElement('div');
    choicesContainer.className = 'choices-container';
    container.appendChild(choicesContainer);

    let blankCounter = 0;

    function addChoiceOptions() {
        const section = document.createElement('div');
        section.className = 'choice-section';
        section.dataset.blankIndex = blankCounter;

        const optionsContainer = document.createElement('div');
        optionsContainer.className = 'options-container';
        section.appendChild(optionsContainer);


        // Add two default options

        addChoiceOption(optionsContainer, section.dataset.blankIndex, containerId, true);

        addChoiceOption(optionsContainer, section.dataset.blankIndex, containerId, false);

        const addOptionBtn = document.createElement('button');
        addOptionBtn.textContent = '+ Add Option';
        addOptionBtn.className = 'add-option-btn';
        addOptionBtn.onclick = () => addChoiceOption(optionsContainer, section.dataset.blankIndex, containerId, false);
        section.appendChild(addOptionBtn);

        const deleteBlankBtn = document.createElement('button');
        deleteBlankBtn.textContent = 'Delete This Blank';
        deleteBlankBtn.className = 'delete-blank-btn';
        deleteBlankBtn.onclick = () => {
            if (choicesContainer.children.length > 1) {
                section.remove();
            } else {
                alert('At least one blank is required');
            }
        };
        section.appendChild(deleteBlankBtn);

        choicesContainer.appendChild(section);
        blankCounter++;
    }

    function addChoiceOption(container, blankIndex, containerId, isFirst) {
        const group = document.createElement('div');
        group.className = 'option-group';

        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = 'Option text...';
        input.required = true;
        group.appendChild(input);

        const radio = document.createElement('input');
        radio.type = 'radio';

        // Unique name per blank and question
        const radioName = `correct-option-${containerId}-${blankIndex}`;
        radio.name = radioName;
        radio.required = isFirst;
        if (isFirst) radio.checked = true;

        radio.addEventListener('change', () => {
            const allRadios = container.querySelectorAll(`input[type="radio"][name="${radioName}"]`);
            allRadios.forEach(r => {
                if (r !== radio) r.checked = false;
            });
        });

        group.appendChild(radio);

        const label = document.createElement('label');
        label.textContent = 'Correct';
        group.appendChild(label);

        const removeBtn = document.createElement('button');
        removeBtn.textContent = '×';
        removeBtn.className = 'remove-option-btn';
        removeBtn.onclick = () => {
            if (container.children.length > 2) {
                group.remove();
            } else {
                alert('At least two options are required');
            }
        };
        group.appendChild(removeBtn);

        container.appendChild(group);
    }

    addBlankBtn.onclick = () => {
        const startPos = questionInput.selectionStart;
        const endPos = questionInput.selectionEnd;

        const newText = questionInput.value.substring(0, startPos) +
            '________' +
            questionInput.value.substring(endPos);
        questionInput.value = newText;

        questionInput.selectionStart = questionInput.selectionEnd = startPos + 8;
        questionInput.focus();

        addChoiceOptions();
    };

    return container;
}



/**
 * Validates and submits a fill-in-choices question to the backend.
 * @param {HTMLElement} div - Question container element
 * @param {number} quizId - ID of the quiz
 * @returns {Promise<Object>} Object with success status and message
 */
export async function saveFillInChoicesQuestion(div, quizId) {
    const questionInput = div.querySelector('.question-input');
    const photoInput = div.querySelector('.photo-input');
    const pointsInput = div.querySelector('.points-input');
    const choicesContainer = div.querySelector('.choices-container');

    const question = questionInput?.value;
    if (!question || question.trim() === '') {
        alert('Please enter the question text');
        return { success: false, message: 'Missing question text' };
    }

    const points = parseInt(pointsInput?.value) || 1;
    if (points < 1) {
        alert('Points must be at least 1');
        return { success: false, message: 'Invalid points value' };
    }

    const blankCount = (question.match(/\[BLANK_\d+\]/g) || []).length;
    const choiceSections = choicesContainer.querySelectorAll('.choice-section').length;



    const correctIndexes = [];
    const choices = [];
    const blankIndices = [];
    let pos = question.indexOf('[BLANK_');

    while (pos !== -1) {
        blankIndices.push(pos);
        pos = question.indexOf('[BLANK_', pos + 1);
    }

    const sections = choicesContainer.querySelectorAll('.choice-section');
    for (let index = 0; index < sections.length; index++) {
        const section = sections[index];
        const blankIndex = parseInt(section.dataset.blankIndex);
        const optionGroups = section.querySelectorAll('.option-group');
        const sectionChoices = [];
        let correctIndex = -1;

        for (let optionIndex = 0; optionIndex < optionGroups.length; optionIndex++) {
            const group = optionGroups[optionIndex];
            const input = group.querySelector('input[type="text"]');
            const radio = group.querySelector('input[type="radio"]');
            const optionText = input.value.trim();

            if (!optionText) {
                input.focus();
                alert(`Option text cannot be empty in blank ${index + 1}`);
                return { success: false, message: `Empty option in blank ${index + 1}` };
            }

            sectionChoices.push(optionText);
            if (radio.checked) {
                correctIndex = optionIndex;
            }
        }

        if (sectionChoices.length < 2) {
            alert(`At least two options are required for blank ${index + 1}`);
            return { success: false, message: `Not enough options in blank ${index + 1}` };
        }

        if (correctIndex === -1) {
            alert(`No correct option selected for blank ${index + 1}`);
            return { success: false, message: `Missing correct option for blank ${index + 1}` };
        }

        choices.push(sectionChoices);
        correctIndexes.push(correctIndex);
    }

    const photoUrl = photoInput?.value?.trim() || null;

    try {
        const response = await fetch("/saveQuestion", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify({
                type: "FillChoices",
                question: question,
                correctIndexes: correctIndexes,
                choices: choices,
                fillIndexes: blankIndices,
                quizId: quizId,
                photoUrl: photoUrl,
                points: points
            })
        });

        if (!response.ok) {
            const error = await response.json();
            return { success: false, message: error.message || 'Failed to save question' };
        }

        return await response.json();
    } catch (error) {
        console.error('Error saving question:', error);
        return { success: false, message: error.message || 'Unexpected error' };
    }
}
