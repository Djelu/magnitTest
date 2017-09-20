package com.fantasy.app.notification.errobj;

/**
 * Created by Djelu on 13.09.2017.
 */
public class ErrPairObj {

    private String message;
    private Throwable throwable;

    public ErrPairObj(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return message;
    }

    public String allToString(){
        return message + "\n" + throwable.getMessage();
    }
}
