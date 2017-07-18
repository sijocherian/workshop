package dev.cjo.starter.ms.syndtarget.service.model;

/**
 * @author sijocherian
 */
public class ErrorInfo {
    public final String error;
    public final String detail;

    public ErrorInfo(String errorCode, Exception ex) {
        this.error = errorCode;
        this.detail = ex.getLocalizedMessage();
    }
    public ErrorInfo(String errorCode, String msg) {
        this.error = errorCode;
        this.detail = msg;
    }
}
