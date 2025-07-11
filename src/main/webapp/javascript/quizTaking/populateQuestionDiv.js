import { highlightCorrectionSingleChoice } from "./singleChoiceWhileTakingDiv.js";
import {highlightCorrectionMultiChoice, populateMultiChoiceDiv} from "./multiChoiceWhileTakingDiv.js";
import {highlightCorrectionTextAnswer, populateTextAnswerDiv} from "./textAnswerWhileTakingDiv.js";
import {highlightCorrectionMultiTextAnswer, populateMultiTextAnswerDiv} from "./multiTextAnswerWhileTakingDiv.js";
import {highlightCorrectionFillInBlanks, populateFillInBlanksDiv} from "./fillInBlanksWhileTakingDiv.js";
import {highlightCorrectionFillInChoices, populateFillInChoicesDiv} from "./fillInChoicesWhileTakingDiv.js";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};

export function populateSingleChoiceDiv(div, questionData, userAnswer) {
    switch (questionData.type) {
        case QType.SingleChoice:
            populateSingleChoiceDiv(div, questionData, userAnswer);
            return;
        case QType.MultiChoice:
            populateMultiChoiceDiv(div, questionData, userAnswer);
            return;
        case QType.TextAnswer:
            populateTextAnswerDiv(div, questionData, userAnswer);
            return;
        case QType.MultiTextAnswer:
            populateMultiTextAnswerDiv(div, questionData, userAnswer);
            return;
        case QType.FillInBlanks:
            populateFillInBlanksDiv(div, questionData, userAnswer);
            return;
        case QType.FillChoices:
            populateFillInChoicesDiv(div, questionData, userAnswer);
            return;
    }
}
