import {getTextAnswerWhileTakingDiv} from "./textAnswerWhileTakingDiv.js";
import {getSingleChoiceWhileTakingDiv} from "./singleChoiceWhileTakingDiv.js";
import {getFillInBlanksWhileTakingDiv} from "./fillInBlanksWhileTakingDiv.js";
import {getMultiChoiceWhileTakingDiv} from "./multiChoiceWhileTakingDiv.js";
import {getMultiTextAnswerWhileTakingDiv} from "./multiTextAnswerWhileTakingDiv.js";
import {getFillInChoicesWhileTakingDiv} from "./fillInChoicesWhileTakingDiv.js";

const QType = {
    SingleChoice: "SingleChoice",
    MultiChoice: "MultiChoice",
    TextAnswer: "TextAnswer",
    MultiTextAnswer: "MultiTextAnswer",
    FillInBlanks: "FillInBlanks",
    FillChoices: "FillChoices"
};


export function getQuestionWhileTaking(data){
    switch (data.type){
        case QType.SingleChoice:
            return getSingleChoiceWhileTakingDiv(data);
        case QType.MultiChoice:
            return getMultiChoiceWhileTakingDiv(data);
        case QType.TextAnswer:
            return getTextAnswerWhileTakingDiv(data);
        case QType.MultiTextAnswer:
            return getMultiTextAnswerWhileTakingDiv(data);
        case QType.FillInBlanks:
            return getFillInBlanksWhileTakingDiv(data);
        case QType.FillChoices:
            return getFillInChoicesWhileTakingDiv(data);
    }



}
