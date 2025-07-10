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

    // Create multiple text inputs
    // For simplicity, we'll create 4 inputs (assuming max 4 answers)
    for (let i = 0; i < 4; i++) {
        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = `Answer ${i + 1}`;
        input.style.width = '100%';
        input.style.padding = '8px';
        input.style.margin = '5px 0';
        input.style.boxSizing = 'border-box';
        container.appendChild(input);
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
        console.error("Error sending multi-text answer:", error);
        return { success: false, message: error.message };
    }
}
