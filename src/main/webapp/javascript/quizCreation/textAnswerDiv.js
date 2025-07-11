export function getTextAnswerDiv() {
    const div = document.createElement("div");
    div.classList.add("question-box");
    div.dataset.qtype = "TextAnswer";

    // Delete button (X button in top-right corner) - Updated to match single choice style
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => {
        if (confirm('Are you sure you want to delete this question?')) {
            div.remove();
        }
    };
    div.appendChild(deleteBtn);

    // Question text input
    const questionLabel = document.createElement("label");
    questionLabel.textContent = "Question Text:";
    const questionInput = document.createElement("input");
    questionInput.type = "text";
    questionInput.name = "questionText";
    questionInput.className = 'question-input';
    questionInput.required = true;
    questionLabel.appendChild(questionInput);

    // Image link input
    const imageLabel = document.createElement("label");
    imageLabel.textContent = "Image Link (optional):";
    const imageInput = document.createElement("input");
    imageInput.type = "text";
    imageInput.name = "imageLink";
    imageInput.className = 'photo-input';
    imageLabel.appendChild(imageInput);

    // Points input
    const pointsLabel = document.createElement("label");
    pointsLabel.textContent = "Points:";
    const pointsInput = document.createElement("input");
    pointsInput.className = 'points-input';
    pointsInput.type = "number";
    pointsInput.name = "points";
    pointsInput.min = "0";
    pointsInput.required = true;
    pointsLabel.appendChild(pointsInput);

    // Exact match checkbox
    const exactMatchLabel = document.createElement("label");
    exactMatchLabel.textContent = "Exact Match Required:";
    const exactMatchCheckbox = document.createElement("input");
    exactMatchCheckbox.type = "checkbox";
    exactMatchCheckbox.name = "exactMatch";
    exactMatchLabel.appendChild(exactMatchCheckbox);

    // Correct answers section
    const answersSection = document.createElement("div");
    answersSection.classList.add("correct-answers");

    const answersLabel = document.createElement("label");
    answersLabel.textContent = "Correct Answers:";

    const answersList = document.createElement("div");
    answersList.classList.add("answers-list");

    // Function to add a new answer input with a delete button
    const addAnswerInput = (defaultValue = "") => {
        const container = document.createElement("div");
        container.classList.add("answer-item");

        const input = document.createElement("input");
        input.type = "text";
        input.name = "correctAnswer";
        input.value = defaultValue;
        input.required = true;

        const deleteBtn = document.createElement("button");
        deleteBtn.type = "button";
        deleteBtn.className = 'delete-option-btn'; // Added class to match single choice style
        deleteBtn.textContent = 'Delete'; // Changed text to match
        deleteBtn.addEventListener("click", () => {
            // Prevent removing the last remaining answer
            if (answersList.childElementCount > 1) {
                container.remove();
            } else {
                alert("At least one correct answer is required.");
            }
        });

        container.appendChild(input);
        container.appendChild(deleteBtn);
        answersList.appendChild(container);
    };

    // Start with 1 answer input
    addAnswerInput();

    const addAnswerBtn = document.createElement("button");
    addAnswerBtn.type = "button";
    addAnswerBtn.classList.add("add-answer");
    addAnswerBtn.textContent = "Add Answer";
    addAnswerBtn.addEventListener("click", () => addAnswerInput());

    answersSection.appendChild(answersLabel);
    answersSection.appendChild(answersList);
    answersSection.appendChild(addAnswerBtn);

    // Append everything to the main div
    div.appendChild(questionLabel);
    div.appendChild(document.createElement("br"));
    div.appendChild(imageLabel);
    div.appendChild(document.createElement("br"));
    div.appendChild(pointsLabel);
    div.appendChild(document.createElement("br"));
    div.appendChild(exactMatchLabel);
    div.appendChild(document.createElement("br"));
    div.appendChild(answersSection);

    return div;
}


export async function saveTextAnswerQuestion(div, quizid) {
    const questionText = div.querySelector("input[name='questionText']").value.trim();
    const imageLink = div.querySelector("input[name='imageLink']").value.trim();
    const pointsStr = div.querySelector("input[name='points']").value.trim();
    const exactMatch = div.querySelector("input[name='exactMatch']").checked;

    const correctAnswers = Array.from(
        div.querySelectorAll("input[name='correctAnswer']")
    )
        .map(input => input.value.trim())
        .filter(value => value !== "");

    // Step 2: Validate input
    if (!questionText) {
        return { success: false, message: "Question text is required." };
    }

    if (pointsStr === "" || isNaN(pointsStr)) {
        return { success: false, message: "Points must be a valid number." };
    }

    const points = parseInt(pointsStr, 10);
    if (points < 0) {
        return { success: false, message: "Points cannot be negative." };
    }

    if (correctAnswers.length === 0) {
        return { success: false, message: "At least one correct answer is required." };
    }

    // Step 3: Prepare data
    const payload = {
        quizid: quizid,
        type: "TextAnswer",
        question: questionText,
        imageLink,
        points,
        exactMatch,
        correctAnswers
    };

    // Step 4: Send POST request to servlet
    try {
        const response = await fetch("/saveQuestion", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            return {
                success: false,
                message: `Server error: ${response.status}`
            };
        }

        const responseJson = await response.json();

        // Expecting: { success: true/false, message: "..." }
        return responseJson;

    } catch (error) {
        return {
            success: false,
            message: "Network error: " + error.message
        };
    }
}
