export function getFillInChoicesWhileTakingDiv(data) {
    const container = document.createElement('div');
    container.className = 'question-container';

    // Display the question text
    const questionText = document.createElement('div');
    questionText.className = 'question-text';
    questionText.textContent = data.question;
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

    // Create answer fields based on correctAnswers or choices
    const answersContainer = document.createElement('div');
    answersContainer.className = 'answers-container';

    // Determine how many answer fields to create
    const answerCount = data.correctAnswers?.length || data.choices?.length || 1;

    for (let i = 0; i < answerCount; i++) {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${i + 1}:`;
        answerGroup.appendChild(label);


        const select = document.createElement('select');
        select.style.margin = '0 5px';
        select.dataset.answerIndex = i;
        select.required = true;

        // Add default empty option
        const defaultOption = document.createElement('option');
        defaultOption.value = "";
        defaultOption.textContent = "-- Select --";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        select.appendChild(defaultOption);

        // Add options from choices (use choices[i] if available, otherwise empty)
        if (data.choices && data.choices[i]) {
            data.choices[i].forEach((choice, choiceIndex) => {
                const option = document.createElement('option');
                option.value = choiceIndex;
                option.textContent = choice;
                select.appendChild(option);
            });
        }

        answerGroup.appendChild(select);
        answersContainer.appendChild(answerGroup);
    }

    container.appendChild(answersContainer);
    return container;
}


export function evalAnswerFillInChoices(div, questionid, userresultid, userid){

}


