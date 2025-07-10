let n = 0;

export function getSingleChoiceWhileTakingDiv(data){
    n++;
    const div = document.createElement("div");
    div.className = `single-choice-question${n}`;

    // For testing purposes, we just hardcode some content
    div.innerHTML = `
        <h4>Sample Question ${n} ${data.question}</h4>
        <div>
            <input type="radio" name="q1${n}" value="0">
            <label>Option 1</label>
        </div>
        <div>
            <input type="radio" name="q1${n}" value="1">
            <label>Option 2</label>
        </div>
    `;

    return div;


}


