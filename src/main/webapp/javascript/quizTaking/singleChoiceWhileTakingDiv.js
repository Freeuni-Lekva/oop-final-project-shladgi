export function getSingleChoiceWhileTakingDiv(data) {
    // Validate required data
    if (!data || !data.question || !data.choices || data.choices.length === 0) {
        console.error("Invalid single choice question data");
        return document.createElement('div'); // Return empty div as fallback
    }

    const container = document.createElement('div');
    container.className = 'question-container single-choice-question';

    // Question text with aria-label
    const questionText = document.createElement('p');
    questionText.textContent = data.question;
    questionText.className = 'question-text';
    questionText.id = 'question-text-' + Math.random().toString(36).substr(2, 9);
    container.appendChild(questionText);

    // Optional image with alt text
    if (data.imageLink) {
        const imgContainer = document.createElement('div');
        imgContainer.className = 'question-image-container';

        const img = document.createElement('img');
        img.src = data.imageLink;
        img.alt = data.imageAltText || 'Question illustration';
        img.className = 'question-image';
        img.setAttribute('aria-describedby', questionText.id);
        imgContainer.appendChild(img);

        container.appendChild(imgContainer);
    }

    // Show weight if available
    if (data.weight !== undefined) {
        const weightInfo = document.createElement('p');
        weightInfo.textContent = `Weight: ${data.weight}`;
        weightInfo.className = 'question-weight';
        container.appendChild(weightInfo);
    }

  
    // Radio buttons for choices with proper accessibility
    const choiceList = document.createElement('div');
    choiceList.className = 'choice-list';
    choiceList.setAttribute('role', 'radiogroup');
    choiceList.setAttribute('aria-labelledby', questionText.id);

    data.choices.forEach((choice, index) => {
        const choiceId = 'choice-' + Math.random().toString(36).substr(2, 9);

        const label = document.createElement('label');
        label.className = 'choice-item';
        label.htmlFor = choiceId;

        const input = document.createElement('input');
        input.type = 'radio';
        input.id = choiceId;
        input.name = 'singleChoice';
        input.value = index;
        input.required = true; // At least one option must be selected
        input.setAttribute('aria-checked', 'false');
        input.setAttribute('role', 'radio');

        const span = document.createElement('span');
        span.textContent = choice;

        // Add event listener to update aria-checked
        input.addEventListener('change', function() {
            document.querySelectorAll(`input[name="${input.name}"]`).forEach(radio => {
                radio.setAttribute('aria-checked', radio.checked ? 'true' : 'false');
            });
        });

        label.appendChild(input);
        label.appendChild(span);
        choiceList.appendChild(label);
    });

    container.appendChild(choiceList);
    return container;
}

export function evalAnswerSingleChoice(div, questionid, userresultid, userid){

    return div;


}


