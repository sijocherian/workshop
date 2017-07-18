package dev.cjo.starter.ms.syndtarget.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author sijocherian
 */

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
