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


function saveQuestion(type){
    switch (type){
        case QType.SingleChoice:
            return saveSingleChoiceQuestion();
        case QType.MultiChoice:
            return saveMultiChoiceQuestion();
        case QType.TextAnswer:
            return saveTextAnswerQuestion();
        case QType.MultiTextAnswer:
            return saveMultiTextAnswerQuestion();
        case QType.FillInBlanks:
            return saveFillInBlanksQuestion();
        case QType.FillChoices:
            return saveFillInChoicesQuestion();
    }



}