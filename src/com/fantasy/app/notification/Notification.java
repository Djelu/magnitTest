package com.fantasy.app.notification;

import com.fantasy.app.notification.errobj.ErrPairObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Djelu on 13.09.2017.
 */
public class Notification {

    private List<ErrPairObj> errors = new ArrayList<>();

    public void addError(String message, Throwable throwable){
        errors.add(new ErrPairObj(message, throwable));
    }

    public List<ErrPairObj> getErrors() {
        return errors;
    }

    public boolean hasErrors(){
        return errors.size() != 0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<errors.size(); i++){
            stringBuilder.append(i).append(". ").append(errors.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }

    public void printErrors(){
        System.out.println(String.format("Program can not continue to work becouse of:\n%s", errors));
        errors.clear();
    }

    public boolean hasErrorsThanPrint(){
        boolean hasErrors = errors.size() != 0;
        if(hasErrors){
            System.out.println(String.format("Program can not continue to work becouse of:\n%s", errors));
            errors.clear();
        }
        return hasErrors;
    }
}
