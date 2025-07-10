export function getFillInChoicesWhileTakingDiv(data) {
    const container = document.createElement('div');
    container.className = 'question-container';

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

    // Process the question text with blanks
    const questionParts = splitQuestionWithBlanks(data.question, data.blanks);
    const questionText = document.createElement('div');
    questionText.className = 'question-text';

    questionParts.forEach((part, index) => {
        if (part.isBlank) {
            const select = document.createElement('select');
            select.style.margin = '0 5px';
            select.dataset.blankIndex = index;

            // Add options from choices
            if (data.choices && data.choices[index]) {
                data.choices[index].forEach((choice, choiceIndex) => {
                    const option = document.createElement('option');
                    option.value = choiceIndex;
                    option.textContent = choice;
                    select.appendChild(option);
                });
            }

            questionText.appendChild(select);
        } else {
            questionText.appendChild(document.createTextNode(part.text));
        }
    });

    container.appendChild(questionText);
    return container;
}

function splitQuestionWithBlanks(question, blanks) {
    const parts = [];
    let lastPos = 0;

    // Sort blanks to process in order
    const sortedBlanks = [...blanks].sort((a, b) => a - b);

    sortedBlanks.forEach(pos => {
        // Add text before blank
        if (pos > lastPos) {
            parts.push({
                text: question.substring(lastPos, pos),
                isBlank: false
            });
        }

        // Add blank
        parts.push({
            text: '',
            isBlank: true
        });

        lastPos = pos + 2; // Skip the __
    });

    // Add remaining text after last blank
    if (lastPos < question.length) {
        parts.push({
            text: question.substring(lastPos),
            isBlank: false
        });
    }

    return parts;
}