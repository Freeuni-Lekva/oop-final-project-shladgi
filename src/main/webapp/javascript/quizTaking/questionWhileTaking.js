import {getTextAnswerWhileTakingDiv} from "./textAnswerWhileTakingDiv";
import {getSingleChoiceWhileTakingDiv} from "./singleChoiceWhileTakingDiv";
import {getFillInBlanksWhileTakingDiv} from "./fillInBlanksWhileTakingDiv";
import {getMultiChoiceWhileTakingDiv} from "./multiChoiceWhileTakingDiv";
import {getMultiTextAnswerWhileTakingDiv} from "./multiTextAnswerWhileTakingDiv";
import {getFillInChoicesWhileTakingDiv} from "./fillInChoicesWhileTakingDiv";

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
