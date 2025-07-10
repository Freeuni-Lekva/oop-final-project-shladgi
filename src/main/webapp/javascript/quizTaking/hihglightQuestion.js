import { highlightCorrectionSingleChoice } from "./singleChoiceWhileTakingDiv.js";
import { highlightCorrectionMultiChoice } from "./multiChoiceWhileTakingDiv.js";
import { highlightCorrectionTextAnswer } from "./textAnswerWhileTakingDiv.js";
import { highlightCorrectionMultiTextAnswer } from "./multiTextAnswerWhileTakingDiv.js";
import { highlightCorrectionFillInBlanks } from "./fillInBlanksWhileTakingDiv.js";
import { highlightCorrectionFillInChoices } from "./fillInChoicesWhileTakingDiv.js";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};

export function highlightQuestionDiv(div, evalResult, questionData) {
    switch (questionData.type) {
        case QType.SingleChoice:
            highlightCorrectionSingleChoice(div, evalResult, questionData);
        case QType.MultiChoice:
            highlightCorrectionMultiChoice(div, evalResult, questionData);
        case QType.TextAnswer:
            highlightCorrectionTextAnswer(div, evalResult, questionData);
        case QType.MultiTextAnswer:
            highlightCorrectionMultiTextAnswer(div, evalResult, questionData);
        case QType.FillInBlanks:
            highlightCorrectionFillInBlanks(div, evalResult, questionData);
        case QType.FillChoices:
            highlightCorrectionFillInChoices(div, evalResult, questionData);
    }
}
