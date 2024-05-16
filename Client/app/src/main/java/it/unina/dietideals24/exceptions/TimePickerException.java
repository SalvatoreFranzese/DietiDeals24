package it.unina.dietideals24.exceptions;

public class TimePickerException extends Exception {
    public TimePickerException() {
    }

    public TimePickerException(String message) {
        super(message);
    }

    public TimePickerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimePickerException(Throwable cause) {
        super(cause);
    }

    public TimePickerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
