import {saveSingleChoiceQuestion} from "./singleChoiceDiv.js";
import {saveMultiChoiceQuestion} from "./multiChoiceDiv.js";
import {saveFillInBlanksQuestion} from "./fillInBlanksDiv.js";
import {saveMultiTextAnswerQuestion} from "./multiTextAnswerDiv.js"
import {saveTextAnswerQuestion} from "./textAnswerDiv.js";


const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};


export function saveQuestion(div, quizid, type){
    switch (type){
        case QType.SingleChoice:
            return saveSingleChoiceQuestion(div, quizid);
        case QType.MultiChoice:
            return saveMultiChoiceQuestion(div, quizid);
        case QType.TextAnswer:
            return saveTextAnswerQuestion(div, quizid);
        case QType.MultiTextAnswer:
            return saveMultiTextAnswerQuestion(div, quizid);
        case QType.FillInBlanks:
            return saveFillInBlanksQuestion(div, quizid);
        case QType.FillChoices:
            return saveFillInChoicesQuestion(div, quizid);
    }



}