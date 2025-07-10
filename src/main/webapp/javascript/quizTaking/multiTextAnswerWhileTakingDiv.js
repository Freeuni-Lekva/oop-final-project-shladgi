export function getMultiTextAnswerWhileTakingDiv(data) {
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

    // Determine number of inputs needed
    const answerCount = data.correctAnswers?.length || data.expectedAnswers || 2; // Default to 2 if not specified

    // Create exactly the needed number of text inputs
    for (let i = 0; i < answerCount; i++) {
        const answerGroup = document.createElement('div');
        answerGroup.className = 'answer-group';

        const label = document.createElement('label');
        label.textContent = `Answer ${i + 1}:`;
        answerGroup.appendChild(label);

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'multi-text-answer';
        input.dataset.answerIndex = i;
        input.required = true;
        input.style.width = '100%';
        input.style.padding = '8px';
        input.style.margin = '5px 0';
        input.style.boxSizing = 'border-box';
        answerGroup.appendChild(input);

        container.appendChild(answerGroup);
    }

    return container;
}


export async function evalAnswerMultiTextAnswer(div, questionId, resultId, userId) {
    // Select all text inputs in the container
    const inputs = div.querySelectorAll('input[type="text"]');
    if (!inputs || inputs.length === 0) {
        console.error("No text inputs found for multi-text answer.");
        return { success: false, message: "No answer inputs found." };
    }

    // Collect non-empty trimmed answers
    const userAnswers = [];
    inputs.forEach(input => {
        const value = input.value.trim();
        if (value) {
            userAnswers.push(value);
        }
    });

    if (userAnswers.length === 0) {
        return { success: false, message: "Please enter at least one answer." };
    }

    const dataToSend = {
        userId: userId,
        questionId: questionId,
        resultId: resultId,
        userAnswer: {
            isString: true,              // Still treating as string-based answers
            choices: userAnswers         // An array of strings
        }
    };

    try {
        const response = await fetch('/evalAndSaveUserAnswer', {
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
        console.error("Error sending multi-text answer:", error);
        return { success: false, message: error.message };
    }
}
