export function getMultiChoiceWhileTakingDiv(data) {
    const container = document.createElement('div');
    container.className = 'question-container';

    // Question text
    const questionText = document.createElement('p');
    questionText.textContent = data.question;
    questionText.className = 'question-text';
    container.appendChild(questionText);

    // Optional image
    if (data.imageLink) {
        const img = document.createElement('img');
        img.src = data.imageLink;
        img.alt = 'Question image';
        img.className = 'question-image';
        container.appendChild(img);
    }

    // Show weight
    if (data.weight !== undefined) {
        const weightInfo = document.createElement('p');
        weightInfo.textContent = `Weight: ${data.weight}`;
        weightInfo.className = 'question-weight';
        container.appendChild(weightInfo);
    }

    // Checkboxes for choices
    const choiceList = document.createElement('div');
    choiceList.className = 'choice-list';

    data.choices.forEach((choice, index) => {
        const label = document.createElement('label');
        label.className = 'choice-item';

        const input = document.createElement('input');
        input.type = 'checkbox';
        input.name = 'multiChoice';
        input.value = index;

        const span = document.createElement('span');
        span.textContent = choice;

        label.appendChild(input);
        label.appendChild(span);
        choiceList.appendChild(label);
    });

    container.appendChild(choiceList);
    return container;
}