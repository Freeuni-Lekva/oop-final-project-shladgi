import { evalAnswerSingleChoice } from "./singleChoiceWhileTakingDiv.js";
import { evalAnswerMultiChoice } from "./multiChoiceWhileTakingDiv.js";
import { evalAnswerTextAnswer } from "./textAnswerWhileTakingDiv.js";
import { evalAnswerMultiTextAnswer } from "./multiTextAnswerWhileTakingDiv.js";
import { evalAnswerFillInBlanks } from "./fillInBlanksWhileTakingDiv.js";
import { evalAnswerFillInChoices } from "./fillInChoicesWhileTakingDiv.js";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};

export function evalAnswer(type, div, questionid, quizResultid, userid, save = true) {
    switch (type) {
        case QType.SingleChoice:
            return evalAnswerSingleChoice(div, questionid, quizResultid, userid, save);
        case QType.MultiChoice:
            return evalAnswerMultiChoice(div, questionid, quizResultid, userid, save);
        case QType.TextAnswer:
            return evalAnswerTextAnswer(div, questionid, quizResultid, userid, save);
        case QType.MultiTextAnswer:
            return evalAnswerMultiTextAnswer(div, questionid, quizResultid, userid, save);
        case QType.FillInBlanks:
            return evalAnswerFillInBlanks(div, questionid, quizResultid, userid, save);
        case QType.FillChoices:
            return evalAnswerFillInChoices(div, questionid, quizResultid, userid, save);
        default:
            console.warn("Unknown question type:", type);
            return null;
    }
}
