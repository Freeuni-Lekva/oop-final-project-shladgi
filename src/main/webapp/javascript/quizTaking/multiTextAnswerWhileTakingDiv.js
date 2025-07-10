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


export function evalAnswerMultiTextAnswer(div, questionid, userresultid, userid){


}