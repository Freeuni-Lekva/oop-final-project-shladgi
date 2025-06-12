package objects.questions;

import java.util.ArrayList;
import java.util.List;

public class Answer<T>{
    private List<T> list;
    public Answer(List<T> indexes){
        this.list = indexes;
    }

    // for when there is only one choice or answer
    public T get(){
        return list.get(0);
    }

    public int getSize(){
        return list.size();
    }

    public T get(int index){
        return list.get(index);
    }

}
