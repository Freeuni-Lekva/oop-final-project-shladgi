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


export async function evalAnswerMultiChoice(div, questionId, resultId, userId) {
    // Get all checked checkboxes inside the div
    const checkedInputs = div.querySelectorAll('input[type="checkbox"]:checked');

    if (!checkedInputs || checkedInputs.length === 0) {
        return { success: false, message: "Please select at least one option." };
    }

    // Collect selected choice indexes
    const selectedChoices = Array.from(checkedInputs).map(input => parseInt(input.value));

    const dataToSend = {
        userId: userId,
        questionId: questionId,
        resultId: resultId,
        userAnswer: {
            isString: false,           // Not a text answer
            choices: selectedChoices  // List of selected indexes
        }
    };

    try {
        const response = await fetch('/saveAnswer', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dataToSend)
        });

        if (!response.ok) {
            const error = await response.json();
            return { success: false, message: error.message || 'Server error' };
        }

        const result = await response.json();
        return result;

    } catch (error) {
        console.error("Error sending multi-choice answer:", error);
        return { success: false, message: error.message };
    }
}
