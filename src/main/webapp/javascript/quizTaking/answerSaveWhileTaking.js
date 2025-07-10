import { evalAnswerSingleChoice } from "./singleChoiceWhileTakingDiv.js";
import { evalAnswerMultiChoice } from "./multiChoiceWhileTakingDiv.js";
import { evalAnswerTextAnswer } from "./textAnswerWhileTakingDiv.js";
import { evalAnswerMultiTextAnswer } from "./multiTextAnswerWhileTakingDiv.js";
import { evalAnswerFillInBlanks } from "./fillInBlanksWhileTakingDiv.js";
import { evalAnswerFillInChoices } from "./fillInChoicesWhileTakingDiv";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};

export function evalAnswer(type, div, questionid, userresultid, userid) {
    switch (type) {
        case QType.SingleChoice:
            return evalAnswerSingleChoice(div, questionid, userresultid, userid);
        case QType.MultiChoice:
            return evalAnswerMultiChoice(div, questionid, userresultid, userid);
        case QType.TextAnswer:
            return evalAnswerTextAnswer(div, questionid, userresultid, userid);
        case QType.MultiTextAnswer:
            return evalAnswerMultiTextAnswer(div, questionid, userresultid, userid);
        case QType.FillInBlanks:
            return evalAnswerFillInBlanks(div, questionid, userresultid, userid);
        case QType.FillChoices:
            return evalAnswerFillInChoices(div, questionid, userresultid, userid);
        default:
            console.warn("Unknown question type:", type);
            return null;
    }
}
