import {getSingleChoiceDiv} from "./singleChoiceDiv.js";
import {getMultiChoiceDiv} from "./multiChoiceDiv.js";
import {gettextAnswerDiv} from "./textAnswerDiv.js";
import {getMultiTextAnswerDiv} from "./multiTextAnswerDiv.js";
import {getFillInBlanksDiv} from "./fillInBlanksDiv.js";
import {getFillInChoicesDiv} from "./fillInChoicesDiv.js";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};


export function getQuizCreationDiv(type){
    switch (type){
        case QType.SingleChoice:
            return getSingleChoiceDiv();
        case QType.MultiChoice:
            return getMultiChoiceDiv();
        case QType.TextAnswer:
            return gettextAnswerDiv();
        case QType.MultiTextAnswer:
            return getMultiTextAnswerDiv();
        case QType.FillInBlanks:
            return getFillInBlanksDiv();
        case QType.FillChoices:
            return getFillInChoicesDiv();
    }



}