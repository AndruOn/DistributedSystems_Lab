package be.kuleuven.foodrestservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class FormatNotAcceptedAdvice {
    @ResponseBody
    @ExceptionHandler(FormatNotAcceptedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    String formatNotAcceptedHandler(FormatNotAcceptedException ex) {
        return ex.getMessage();
    }
}
