package dev.cjo.starter.ms.syndtarget.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TargetNotFoundException extends Exception { //extends RuntimeException

    public TargetNotFoundException(String message) {
        super(message);
    }


}
